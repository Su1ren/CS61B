package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Helper.*;
import static gitlet.Utils.readContents;
import static gitlet.Utils.sha1;

/** Represents a gitlet commit object.
 *  A commit object contains:
 *  1. Message of this commit.
 *  2. Date of this commit proceeds.
 *  3. Father Node this commit changed from.
 *  4. Pointer to the blobs of this commit.
 *
 *  Commit should be serializable for branch management.
 *  @author Suiren
 */
public class Commit implements Serializable {
    /**
     * Message: The message of this Commit
     * Date: The date of this Commit, in the form of a Date object
     * parents: The hashed code of the previous commit nodes in the branch
     * blobIDs: The IDs of blobs to hold.
     * ID: The generated ID of this Commit node
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The date of this Commit. */
    private Date date;
    /** Parents are at most 2. */
    private List<String> parents;
    /** Pointers to the blobs of this Commit, unbounded. Use hashmap to scale down search time. Key: file path, Value: SHA-1 */
    private Map<String, String> blobIDs;
    /** The ID of this Commit. */
    private String ID;
    /** The corresponding file record of this Commit. */
    private File file;
    /** Whether this commit starts a branching. */
    private boolean branchFrom = false;

    public Commit(String message, Date date, List<String> parent, Map<String, String> files) {
        this.message = message;
        this.date = date;
        this.parents = parent;
        this.blobIDs = files;
        this.ID = generateID();
        this.file = getObjectCommitFile(this.ID);
    }

    /**
     * Default initial commit constructor
     */
    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.parents = new ArrayList<>();
        this.blobIDs = new HashMap<>();
        this.ID = generateID();
        this.file = getObjectCommitFile(this.ID);
    }

    public String getID() {
        return this.ID;
    }

    public Map<String, String> getTrack() {
        return this.blobIDs;
    }

    public List<String> getParents() {
        return this.parents;
    }

    public Date getDate() {
        return this.date;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean hasBranchSplit() {
        return this.branchFrom;
    }

    public void setBranchSplit(boolean split) {
        this.branchFrom = split;
    }

    private String generateID() {
        return sha1(this.message, timeConvert(this.date), this.parents.toString(), this.blobIDs.toString());
    }

    public void recomputeID() {
        this.ID = generateID();
    }

    public boolean hasSecondParent() {
        return this.parents.size() > 1;
    }

    /**
     * Save the commit node as a file in Objects/commits directory
     * Chain the commit node to its parents
     */
    public void save() {
        saveObjectFile(this.file, this);
    }
}
