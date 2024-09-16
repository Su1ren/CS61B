package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import static gitlet.Repository.STAGE_AREA;
import static gitlet.Utils.*;

public class StageArea implements Serializable {
    /**
     * The Stage Area
     * Contains the file record added by add operation.
     * And the file record removed by rm operation.
     */

    /**
     * The addition stage area, choose Map for better search time.
     * Key: file name, Value: SHA-1 ID
     */
    private final Map<String, String> addStage = new HashMap<>();
    /** The removal stage area, Set is OK. Element: file name */
    private final Set<String> removeStage = new HashSet<>();

    public Map<String, String> getAddStage() {
        return this.addStage;
    }

    public Set<String> getRemoveStage() {
        return this.removeStage;
    }

    /**
     * Add a file from add operation to StageArea.
     * If the added file is identical to the one in the last commit tracked.
     * Compare if the SHA-1 ID is the same.
     * If the added file is not tracked in the cur commit,
     * include it in addStage and save the new blob.
     * If the file is tracked, but the SHA-1 ID is different,
     * overwrite it in addStage and save the latest version blob.
     * If the file is in the removeStage, remove it from removeStage.
     * After all, write the modified stage into the STAGE_AREA.
     *
     * @param srcFile the source file from working directory.
     */
    public static void addFile(File srcFile) {
        if (!STAGE_AREA.exists()) {
            try {
                STAGE_AREA.createNewFile();
                writeObject(STAGE_AREA, new StageArea());
            } catch (IOException e) {
                throw error("Cannot create stage area file.");
            }
        }

        Blob blob = new Blob(srcFile);
        Commit curCommit = Repository.getCurrentCommit();
        StageArea stage = readObject(STAGE_AREA, StageArea.class);

        if (curCommit.getTrack().containsKey(srcFile.getName())) {
            if (!curCommit.getTrack().get(srcFile.getName()).equals(blob.getID())) {
                stage.getAddStage().put(srcFile.getName(), blob.getID());
                blob.save();
            }
            if (stage.getRemoveStage().contains(srcFile.getName())) {
                stage.getRemoveStage().remove(srcFile.getName());
            }
        } else {
            blob.save();
            stage.getAddStage().put(srcFile.getName(), blob.getID());
        }

        writeObject(STAGE_AREA, stage);
    }

    public void clearStage() {
        addStage.clear();
        removeStage.clear();
    }

    public boolean isClear() {
        return addStage.isEmpty() && removeStage.isEmpty();
    }

    public static StageArea getStageArea() {
        return readObject(STAGE_AREA, StageArea.class);
    }
}
