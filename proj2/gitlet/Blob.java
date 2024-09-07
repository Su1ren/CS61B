package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Helper.*;
import static gitlet.Utils.readContents;
import static gitlet.Utils.sha1;

/**
 * Blob represents a block of file content.
 * Containing a file with its relative path, a generated ID, and its content.
 * We distinguish the blobs by their IDs.
 * IDs are hashed by SHA-1.
 *
 * Blobs are used to store the content of the files when reading files.
 * And write the content of the files when writing files.
 */
public class Blob implements Serializable {
    /** The name of the source file in the working directory. */
    private String path;
    /** The content of the blob. */
    private byte[] content;
    /** The generated ID of the blob. */
    private String ID;
    /** The corresponding file record of the blob in objects directory. */
    private File file;

    /**
     * Create a blob from a file in working directory to blobs directory.
     * @param srcFile the file in working directory
     */
    public Blob(File srcFile) {
        this.content = readContents(srcFile);
        this.path = srcFile.getName();
        this.ID = sha1(this.path, this.content);
        this.file = getObjectBlobFile(this.ID);
    }

    public String getPath() {
        return this.path;
    }

    public String getID() {
        return this.ID;
    }

    public byte[] getContent() {
        return this.content;
    }

    /**
     * Save the blob node as a file in Objects directory.
     */
    public void save() {
        saveObjectFile(this.file, this);
    }

    public File getFile() {
        return this.file;
    }
}
