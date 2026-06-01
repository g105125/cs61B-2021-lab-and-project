package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.TreeMap;

import gitlet.Repository;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    public Commit(String message,String date,String parent1, String parent2, TreeMap<String,String> files){
        this.message=message;
        this.parent1=parent1;
        this.parent2=parent2;
        this.files=files;
        this.date=date;
    }
    /** The message of this Commit. */
    public String message;

    public String date;

    public TreeMap<String,String> files;

    public String parent1;

    public String parent2;

    public Commit() {
        return;
    }

    /* TODO: fill in the rest of this class. */
}
