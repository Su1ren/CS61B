package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Helper.*;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  Since the operations are all based on the filesystem,
 *  this class contains only static public interfaces for operations.
 *
 *  Another word, we don't need to maintain a Repository object.
 *  @author Suiren
 */
public class Repository {
    /**
     * The structure of .gitlet directory:
     * Referring to Git in practice.
     * -.gitlet
     *   |-objects
     *      |-commits
     *      |-blobs
     *   |-refs
     *      |-heads
     *          |-master
     *          |-others
     *   |-HEAD
     *   |-index (stage area)
     *
     * NOTICE:
     * 1. .gitlet, objects, refs, and heads are created as init() executes.
     * 2. HEAD is created as the first commit happens in init().
     * 3. index is created as the first file added into stage area.
     *
     * This class takes charge of branch management, few interactions with files.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The directory containing commits and blobs. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");

    /** The directory containing commits nodes in objects directory. */
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");

    /** The directory containing blob nodes in objects directory. */
    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");

    /** The directory containing references to branches */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");

    /** The directory containing references to the tips of branches */
    public static final File BRANCH_HEADS_DIR = join(REFS_DIR, "heads");

    /** The HEAD file, pointing to the tip of the current branch. */
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    /** The default branch MASTER. */
    public static final String DEFAULT_BRANCH = "master";

    /** The stage area file.  */
    public static final File STAGE_AREA = join(GITLET_DIR, "index");

    /** Prefix of branch name. */
    public static final String BRANCH_PREFIX = "refs/heads/";

    /**
     * In initialization of repository.
     * 1.We create a .gitlet directory if it doesn't exist.
     *   Throw exception if .gitlet directory already exists.
     * 2.Setup directories for commits, blobs and heads.
     * 3.Construct the default branch MASTER.
     * 4.Create and update HEAD file with the initial commit ID.
     * 5.Create initial commit, then update heads record in heads directory.
     */
    public static void init() {
        if (GITLET_DIR.exists()) {
            throw new GitletException("A Gitlet version-control system already exists in the current directory.");
        }

        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        REFS_DIR.mkdir();
        BRANCH_HEADS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        setCurrentBranch(DEFAULT_BRANCH);
        initCommit();
    }

    /**
     * Create an initial commit node,
     * Save it as a file in Objects directory.
     * Then update the heads infos in refs directory.
     */
    private static void initCommit() {
        Commit init = new Commit();
        init.save();
        setBranchHeadCommit(DEFAULT_BRANCH, init.getID());
    }

    /**
     * The current branch is recorded in HEAD file.
     * So we can modify the current branch easily.
     * @param branch the name of the current branch
     */
    private static void setCurrentBranch(String branch) {
        writeContents(HEAD_FILE,  BRANCH_PREFIX + branch);
    }

    /**
     * Chain the current commit node into the branch head directory.
     * @param branch the name of the branch
     * @param id the ID of the current commit
     */
    private static void setBranchHeadCommit(String branch, String id) {
        setBranchHead(join(BRANCH_HEADS_DIR, branch), id);
    }

    /**
     * Write the commit ID into the branch head file in heads directory.
     * @param file branch head file should be written.
     * @param id the ID of the current commit, also the written content.
     */
    private static void setBranchHead(File file, String id) {
        writeContents(file, id);
    }

    /**
     * Add a file to the stage area.
     * Staging an already-staged file will overwrite the previous entry.
     * If the current working version is identical to the version in the current commit,
     * Do not stage it to be added, and remove it from the staging area if it is already there.
     * The file will no longer be staged for removal if it was at the time of the command.
     * <p>
     * For commit to track, files are stored as blobs in the blobs directory after addition.
     * @param fileName the name of the file to add into the stage area
     */
    public static void add(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            throw new GitletException("File does not exist.");
        }
        StageArea.addFile(file);
        // createBlob(file);
    }

    /**
     * Commit the files in StageArea to the current branch.
     * Copy the HEAD commit and modify its message and date.
     * Chain the new commit node into the branch and save in objects directory.
     * Modify the HEAD file to point to the new commit node.
     * If the stage area is empty both addition and removal, throw exception.
     * <p>
     * After commit, stage area must be cleared, and the index file cleared.
     * @param message the message of the new commit
     */
    public static void commit(String message) {
        StageArea stage = readObject(STAGE_AREA, StageArea.class);

        if (message.isEmpty()) {
            throw new GitletException("Please enter a commit message.");
        }
        if (stage.getAddStage().isEmpty() && stage.getRemoveStage().isEmpty()) {
            throw new GitletException("No changes added to the commit.");
        }

//        Commit curCommit = Repository.getCurrentCommit();
//        List<String> parent = new ArrayList<>();
//        parent.add(curCommit.getID());
//        Commit newCommit = new Commit(message, new Date(), parent, curCommit.getTrack());

        Commit newCommit = chainCommitAfterCurrent(message);
        modifyTrack(newCommit, stage);
        newCommit.save(); // in objects directory

        stage.clearStage();
        writeObject(STAGE_AREA, stage); // in index file
        setCurrentBranch(getCurrentBranchName()); // in HEAD file
        setBranchHeadCommit(getCurrentBranchName(), newCommit.getID()); // in refs/heads directory
    }

    /**
     * Chain the new commit node into the branch.
     * @param message the message of the new commit
     * @return the new commit node in the branch.
     */
    private static Commit chainCommitAfterCurrent(String message) {
        Commit cur = Repository.getCurrentCommit();
        List<String> parent = new ArrayList<>();
        parent.add(cur.getID());
        return new Commit(message, new Date(), parent, cur.getTrack());
    }

    /**
     * Modify the track of the new commit node.
     * According to the stage area.
     * @param newCommit the new commit node need modification.
     * @param stage the stage area
     */
    private static void modifyTrack(Commit newCommit, StageArea stage) {
        for (String path: stage.getAddStage().keySet()) {
            newCommit.getTrack().put(path, stage.getAddStage().get(path));
        }
        for (String path: stage.getRemoveStage()) {
            newCommit.getTrack().remove(path);
        }
    }

    /**
     * Get the last commit node in the current branch.
     * 1. Search in the HEAD file to locate the current branch.
     * 2. Get the commit hash from heads directory.
     * 3. Get the commit node from objects/commits directory.
     * @return the last commit node
     */
    public static Commit getCurrentCommit() {
        String headPath = readContentsAsString(HEAD_FILE);
        File curBranchHead = join(GITLET_DIR, headPath);
        String commitHash = readContentsAsString(curBranchHead);

        return getCommitFromID(commitHash);
    }

    /**
     * Get current branch head from HEAD file.
     * @return the current branch head in heads directory.
     */
    private static String getCurrentBranch() {
        return readContentsAsString(HEAD_FILE);
    }

    /**
     * Get the commit node from objects/commits directory.
     * @param id the hashed commit node.
     * @return the commit node
     */
    private static Commit getCommitFromID(String id) {
        File dst = getObjectCommitFile(id);
        return readObject(dst, Commit.class);
    }

    /**
     * Unstage the file if it is currently in the stage area for adding.
     * If the file is ALREADY tracked in the current commit, stage it for removal,
     * And remove it in the working directory.
     * <p>
     * If the file is neither staged in addition nor tracked in head commit,
     * Print error message.
     * @param fileName the name of the file.
     */
    public static void rm(String fileName) {
        StageArea stage = readObject(STAGE_AREA, StageArea.class);

        if (stage == null) {
            throw new GitletException("No .gitlet directory here.");
        }

        File file = join(CWD, fileName);
        Commit cur = getCurrentCommit();
//        if (stage.getAddStage().containsKey(fileName)) {
//            stage.getAddStage().remove(fileName);
//        } else if (cur.getTrack().containsKey(fileName)) {
//            stage.getRemoveStage().add(fileName);
//            restrictedDelete(file);
//        } else {
//            stage.getRemoveStage().add(fileName);
//            throw new GitletException("No reason to remove the file.");
//        }
        if (stage.getAddStage().containsKey(fileName)) {
            stage.getAddStage().remove(fileName);
        } else {
            stage.getRemoveStage().add(fileName);
            if (cur.getTrack().containsKey(fileName)) {
                restrictedDelete(file);
            } else {
                throw new GitletException("No reason to remove the file.");
            }
        }

        writeObject(STAGE_AREA, stage);
    }

    /**
     * Print the information of the current information of the repository.
     * Start at the current head commit, and go back until the branch to initial commit.
     * Ignore the second parent commit by default.
     * The path forms a set of commit nodes, called commit history.
     * For each node in history, display its id, time and message.
     * Time pattern should follow a specific format like:
     * <p></p>
     * === <br>
     * Date: Wed Dec 31 16:00:00 1969 -0800 <br>
     *
     * If a commit node is created from merge, the pattern is as below:<br>
     * === <br>
     * commit 3e8bf1d794ca2e9ef8a4007275acf3751c7170ff <br>
     * Merge: 4975af1 2c1ead1 <br>
     * Date: Sat Nov 11 12:30:00 2017 -0800 <br>
     * Merged development into master.
     */
    public static void log() {
        Commit cur = getCurrentCommit();
        while (!cur.getParents().isEmpty()) {
            displayCommit(cur);
            cur = getCommitFromID(cur.getParents().get(0)); // getCommit()
        }
        displayCommit(cur);
    }

    /**
     * Display commit information.
     * @param commit the commit node
     */
    private static void displayCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getID());
        if (commit.getParents().size() > 1) {
            System.out.println("Merge: " + commit.getParents().get(0).substring(0, 7) + " "
                    + commit.getParents().get(1).substring(0, 7));
        }
        System.out.println("Date: " + timeConvert(commit.getDate()));
        System.out.println(commit.getMessage());
        System.out.println();
    }

    /**
     * Print all commit information in the repository, on every branch.
     * For simplicity, the order of the commits doesn't matter.
     */
    public static void globalLog() {
        List<String> commits = plainFilenamesIn(Repository.COMMITS_DIR);
        if (commits.isEmpty()) {
            throw new GitletException("No commits yet.");
        }
        for (String id : commits) {
            displayCommit(getCommitFromID(id));
        }
    }

    /**
     * Find out the IDs of all commits that have the given commit message.
     * Display the IDs of matching commits on separate line.
     * If no such commit exists, print error message.
     */
    public static void find(String message) {
        List<String> commits = plainFilenamesIn(Repository.COMMITS_DIR);
        if (commits.isEmpty()) {
            throw new GitletException("Found no commit with that message.");
        }
        boolean found = false;
        for (String id : commits) {
            Commit node = getCommitFromID(id);
            if (node.getMessage().equals(message)) {
                System.out.println(node.getID());
                found = true;
            }
        }
        if (!found) {
            throw new GitletException("Found no commit with that message.");
        }
    }

    /**
     * Display what branches currently exist, and marks the current branch with *.
     * Also displays What files have been staged for addition or removal.
     * Format as below:<br>
     * === Branches ===
     * *master
     * other-branch
     *
     * === Staged Files ===
     * wug.txt
     * wug2.txt
     *
     * === Removed Files ===
     * goodbye.txt
     *
     * === Modifications Not Staged For Commit ===
     * junk.txt (deleted)
     * wug3.txt (modified)
     *
     * === Untracked Files ===
     * random.stuff
     *
     * The last two sections are extra credit.
     * Feel free to leave them blank.
     */
    public static void status() {
        displayBranches();
        displayStage();
        displayModificationNotStaged();
        displayUntracked();
    }

    /**
     * Display all existing branches.
     * The current branch is marked with *.
     */
    private static void displayBranches() {
        System.out.println("=== Branches ===");
        // System.out.println("*" + getCurrentBranchName());
        List<String> heads = plainFilenamesIn(BRANCH_HEADS_DIR);
        if (heads.isEmpty()) {
            throw new GitletException("No branches yet.");
        }
        for (String branch : heads) {
            if (branch.equals(getCurrentBranchName())) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();
    }

    /**
     * Get the current branch name.
     */
    private static String getCurrentBranchName() {
        String cur = getCurrentBranch();
        File file = join(cur);
        return file.getName();
    }

    /**
     * Print all files that have been staged for addition or removal.
     */
    private static void displayStage() {
        StageArea stage = readObject(STAGE_AREA, StageArea.class);

        System.out.println("=== Staged Files ===");
        for (String file : stage.getAddStage().keySet()) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String file : stage.getRemoveStage()) {
            System.out.println(file);
        }
        System.out.println();
    }

    /**
     * Display what files in directory have been modified but not staged for commit.
     * That is:
     * 1. Tracked in current commit, changed in working directory, but not staged;
     * 2. Staged in addition, but with different contents than in working directory;
     * 3. Stage in addition, but removed in working directory;
     * 4. Not staged for removal, but tracked in the commit and deleted in working directory.
     */
    private static void displayModificationNotStaged() {
        List<String> files = plainFilenamesIn(Repository.CWD);
        System.out.println("=== Modifications Not Staged For Commit ===");
        Map<String, String> tracking = getCurrentCommit().getTrack();
        StageArea stage = readObject(STAGE_AREA, StageArea.class);
        PriorityQueue<String> q = new PriorityQueue<>();

        for (String path : files) {
            File file = join(CWD, path);
            Blob blob = new Blob(file);
            if (tracking.containsKey(path) &&
                    !tracking.get(path).equals(blob.getID()) &&
                    !stage.getAddStage().containsKey(path)) {
                q.add(path + " (modified)");
            } else if (stage.getAddStage().containsKey(path) && !stage.getAddStage().get(path).equals(blob.getID())) {
                q.add(path + " (modified)");
            }
        }

        for (String file : stage.getAddStage().keySet()) {
            if (!files.contains(file)) {
                q.add(file + " (deleted)");
            }
        }

        for (String file : tracking.keySet()) {
            if (!stage.getRemoveStage().contains(file) && !files.contains(file)) {
                q.add(file + " (deleted)");
            }
        }

        for (String path : q) {
            System.out.println(path);
        }
        System.out.println();
    }

    /**
     * Display untracked files, which are in the working directory at present.
     * But neither staged for addition nor tracked.
     * Also including files staged for removal, but then re-created without Gitlet's knowledge.
     * Ignore any subdirectories.
     */
    private static void displayUntracked() {
        List<String> files = plainFilenamesIn(Repository.CWD);
        Map<String, String> tracking = getCurrentCommit().getTrack();
        StageArea stage = readObject(STAGE_AREA, StageArea.class);

        System.out.println("=== Untracked Files ===");
        for (String file : files) {
            if (!tracking.containsKey(file) && !stage.getAddStage().containsKey(file)) {
                System.out.println(file);
            }
            if (stage.getRemoveStage().contains(file)) {
                System.out.println(file);
            }
        }
        System.out.println();
    }

    /**
     * Create a new branch with the given name.
     * And points it at the current head commit.
     * The branch is just a name for a reference to a commit node.
     * Notice that the operation will not instantly switch to the new branch, HEAD unchanging.
     * If the BRANCH operation is not ever called, the branch will stay on MASTER branch.
     * If the named branch already exists, print error message.
     * @param name the name of the new branch
     */
    public static void branch(String name) {
        if (plainFilenamesIn(BRANCH_HEADS_DIR).contains(name)) {
            throw new GitletException("A branch with that name already exists.");
        }

        File newBranch = join(BRANCH_HEADS_DIR, name);
        try {
            newBranch.createNewFile();
            writeContents(newBranch, getCurrentCommit().getID());
        } catch (IOException e) {
            throw error("Cannot create branch.");
        }
    }

    /**
     * Delete the branch with given name.
     * This operation will not delete commits created under the branch.
     * If the given branch doesn't exist, abort and print error message.
     * If you try to delete the currently working branch, also abort.
     * @param name the name of branch to delete.
     */
    public static void rmBranch(String name) {
        if (!plainFilenamesIn(BRANCH_HEADS_DIR).contains(name)) {
            throw new GitletException("A branch with that name does not exist.");
        }
        if (name.equals(getCurrentBranchName())) {
            throw new GitletException("Cannot remove the current branch.");
        }

        File dstBranch = join(Repository.BRANCH_HEADS_DIR, name);
        restrictedDelete(dstBranch);
    }

    /**
     * Take all files in the head commit of the given branch and put them in the CWD.
     * If one file exists in working directory, overwrite it.
     * Then switch the HEAD to the given branch.
     * Any files tracked in the current branch but not present in the checkout branch will be removed.
     * After all, the stage will be clear unless the check-out branch is the current branch.
     * 1. If the no branch with that name exists, print error message.
     * 2. If the given branch is the current branch, print error message.
     * 3. If a working file is untracked in the current branch and would be overwritten by checkout,
     * print "There is an untracked file in the way; delete it, or add and commit it first." And exit.
     * The check should take precedence before anything else. Do not change CWD.
     */
    public static void checkoutBranch(String branch) {
        if (!plainFilenamesIn(BRANCH_HEADS_DIR).contains(branch)) {
            throw new GitletException("No such branch exists.");
        }
        if (branch.equals(getCurrentBranchName())) {
            throw new GitletException("No need to checkout the current branch.");
        }
        List<String> files = plainFilenamesIn(CWD);
        Commit curCommit = getCurrentCommit();
        setCurrentBranch(branch);
        Commit checkoutCommit = getCurrentCommit();

        for (String file : files) {
            if (!curCommit.getTrack().containsKey(file) && checkoutCommit.getTrack().containsKey(file)) {
                throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        checkoutHelper(curCommit, checkoutCommit);
    }

    /**
     * Take charge of the files adding, overwriting and removal.
     */
    private static void checkoutHelper(Commit curCommit, Commit checkoutCommit) {
        StageArea stage = readObject(STAGE_AREA, StageArea.class);
        if (!curCommit.getID().equals(checkoutCommit.getID())) {
            for (String file : checkoutCommit.getTrack().keySet()) {
                String blobHash = curCommit.getTrack().get(file);
                overwriteFile(file, blobHash);
            }
            for (String file : curCommit.getTrack().keySet()) {
                if (!checkoutCommit.getTrack().containsKey(file)) {
                    restrictedDelete(join(CWD, file));
                }
            }
        }
        stage.clearStage();
        writeObject(STAGE_AREA, stage);
    }

    /**
     * Overwrite a file according to the given blob hash.
     */
    private static void overwriteFile(String file, String blobHash) {
        Blob blob = readObject(join(BLOBS_DIR, blobHash), Blob.class);
        File newFile = join(CWD,file);
        try {
            newFile.createNewFile();
            writeContents(newFile, (Object) blob.getContent());
        } catch (IOException e) {
            throw error("File creation failed in checkout.");
        }
    }

    /**
     * Take the version of the files as it exists in the head commit.
     * Put it in the CWD.
     * Overwrite the version of the file in the CWD.
     * The new version is not staged.
     * If the file doesn't exist in the previous commit, print error message.
     */
    public static void checkoutFile(String path) {
        Commit curCommit = getCurrentCommit();

        if (!curCommit.getTrack().containsKey(path)) {
            throw new GitletException("File does not exist in that commit.");
        } else {
            String blobHash = curCommit.getTrack().get(path);
            overwriteFile(path, blobHash);
        }
    }

    /**
     * Take the version of the files as it exists in the given commitID.
     * Put it in the CWD.
     * Overwrite the version of the file in the CWD.
     * The new version is not staged.
     * If no commit with that ID exists, print error message.
     * If the file doesn't exist in the given commit, print error message.
     */
    public static void checkoutFileFromCommit(String commitID, String path) {
        if (!plainFilenamesIn(COMMITS_DIR).contains(commitID)) {
            throw new GitletException("No commit with that id exists");
        }
        Commit dstCommit = getCommitFromID(commitID);
        if (!dstCommit.getTrack().containsKey(path)) {
            throw new GitletException("File does not exist in that commit.");
        }
        overwriteFile(path, dstCommit.getTrack().get(path));
    }

    /**
     * Checkout all the files tracked by the given commitID.
     * Remove tracked files that are not present in the given commitID.
     * Also moves the current branch's head to that commit node.
     * After all, the stage area will be cleared.
     * This command is essentially a CHECKOUT of a commit that also changes the current branch head.
     * If no commit with that ID exists, print error message.
     * If a working file is untracked in the current branch and would be overwritten, print:
     * "There is an untracked file in the way; delete it, or add and commit it first" then exit.
     */
    public static void reset(String commitID) {
        if (!plainFilenamesIn(COMMITS_DIR).contains(commitID)) {
            throw new GitletException("No commit with that id exists");
        }
        Commit curCommit = getCurrentCommit();
        Commit dstCommit = getCommitFromID(commitID);
        for (String file : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            if (!curCommit.getTrack().containsKey(file)) {
                if (dstCommit.getTrack().containsKey(file)) {
                    throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first");
                } else {
                    rm(file);
                }
            }
        }
        checkoutHelper(curCommit, dstCommit);
        setBranchHeadCommit(getCurrentBranchName(), commitID);
    }
}
