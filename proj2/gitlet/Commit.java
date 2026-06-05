package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /** The message of this Commit. */
    private String message;
    private String date;
    private TreeMap<String, String> files;
    private String parent1;
    private String parent2;

    public Commit(String message, String date, String parent1, String parent2,
                  TreeMap<String, String> files) {
        this.message = message;
        this.date = date;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.files = files;
    }

    /** No-arg constructor for deserialization. */
    public Commit() {
        // no action
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public TreeMap<String, String> getFiles() {
        return files;
    }

    public String getParent1() {
        return parent1;
    }

    public String getParent2() {
        return parent2;
    }
}