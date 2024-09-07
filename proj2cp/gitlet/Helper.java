package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static gitlet.Utils.*;

public class Helper {
    /**
     * Convert the pattern of Date to the required.
     *
     * @param date original date pattern
     * @return target Date information String
     */
    public static String timeConvert(Date date) {
        DateFormat re = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return re.format(date);
    }

    /**
     * Retrieve the blob node File from its ID in Objects/blobs directory.
     */
    public static File getObjectBlobFile(String id) {
        return join(Repository.BLOBS_DIR, id);
    }

    /**
     * Retrieve the commit node File from its ID in Objects/commits directory.
     */
    public static File getObjectCommitFile(String id) {
        return join(Repository.COMMITS_DIR, id);
    }


//    /**
//     * Retrieve the file Object from its ID in Objects directory.
//     * @return The file Object
//     */
//    public static File getObjectFile(String id) {
//        String directory = getObjectsDirectory(id);
//        String filename = getFileName(id);
//        return join(Repository.OBJECTS_DIR, directory, filename);
//    }
//
//    /** Similar to Git in practice. */
//    private static String getObjectsDirectory(String id) {
//        return id.substring(0, 2);
//    }
//
//    private static String getFileName(String id) {
//        return id.substring(2);
//    }

    /**
     * Save the Objects as files in Objects directory.
     * If the commits or blobs directory doesn't exist, create it.
     */
    public static void saveObjectFile(File file, Serializable obj) {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdir();
        }
        writeObject(file, obj);
    }

    public static void main(String[] args) {
        List<String> files = plainFilenamesIn(Repository.CWD);
        for (String file : files) {
            System.out.println(file);
        }
    }
}
