package gitlet;

import jdk.jshell.execution.Util;

import java.io.File;
import static gitlet.Utils.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File COMMITS_DIR = join(GITLET_DIR,"commits");

    public static final File BLOBS_DIR =join(GITLET_DIR,"blobs");

    public static final File BRANCHES_DIR =join(GITLET_DIR,"branches");

    public static final File HEAD=join(GITLET_DIR,"head");

    public static final File ADDSTORAGE=join(GITLET_DIR,"addstorage");

    public static final File DELSTORAGE=join(GITLET_DIR,"delstorage");

    public static void Init() throws IOException {
        if(GITLET_DIR.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        HEAD.createNewFile();
        ADDSTORAGE.createNewFile();
        DELSTORAGE.createNewFile();
        String message="initial commit";
        Commit c=new Commit(message,formatdate(new Date(0)),null,null,new TreeMap<String,String>());
        File master=join(BRANCHES_DIR,"master");
        master.createNewFile();
        writeContents(master,commit(c));
        writeContents(HEAD,"master");
        writeObject(ADDSTORAGE,new TreeMap<String,String>());
        writeObject(DELSTORAGE,new TreeSet<String>());
    }
    private static String formatdate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }
    private static String commit(Commit c) throws IOException {
        String name=sha1("commit",c.message,c.date,c.parent1==null?"":c.parent1,c.parent2==null?"":c.parent2,c.files.toString());
        File f=join(COMMITS_DIR,name);
        f.createNewFile();
        writeObject(f,c);
        return name;
    }
    public static void Add(String filename) throws IOException {
        if(!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        File file=join(CWD,filename);
        if(!file.exists()){
            System.out.println("File does not exist.");
            return;
        }
        String filetext=readContentsAsString(file);
        TreeMap <String,String>addstorage=readObject(ADDSTORAGE,TreeMap.class);
        TreeSet<String> delstorage=readObject(DELSTORAGE,TreeSet.class);
        File commitfile=join(COMMITS_DIR,getheadhash());
        Commit curcommit=readObject(commitfile,Commit.class);
        String blobname=sha1("blob",filetext);
        if(curcommit.files.get(filename)!=null&&curcommit.files.get(filename).equals(blobname)){
            return;
        }
        File blobfile=join(BLOBS_DIR,blobname);
        blobfile.createNewFile();
        writeContents(blobfile,filetext);
        addstorage.put(filename,blobname);
        writeObject(ADDSTORAGE,addstorage);
        if(delstorage.contains(filename)){
            delstorage.remove(filename);
            writeObject(DELSTORAGE,delstorage);
        }
    }
    private static String getheadhash(){
        return readContentsAsString(join(BRANCHES_DIR,readContentsAsString(HEAD)));
    }
    public static void Rm(String filename){
        if(!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        File file=join(CWD,filename);
        TreeMap<String,String>addstorage=readObject(ADDSTORAGE,TreeMap.class);
        TreeSet<String>delstorage=readObject(DELSTORAGE,TreeSet.class);
        if(addstorage.containsKey(filename)){
            addstorage.remove(filename);
        }
        if(readObject(join(COMMITS_DIR,getheadhash()),Commit.class).files.containsKey(filename)){
            delstorage.add(filename);
            file.delete();
        }
        writeObject(ADDSTORAGE,addstorage);
        writeObject(DELSTORAGE,delstorage);
    }
    public static void Commit(String message) throws IOException {
        if(!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        if(message.isEmpty()){
            System.out.println("Please enter a commit message.");
            return;
        }
        TreeMap<String,String>addstorage=readObject(ADDSTORAGE,TreeMap.class);
        TreeSet<String>delstorage=readObject(DELSTORAGE,TreeSet.class);
        if(addstorage.isEmpty()&&delstorage.isEmpty()){
            System.out.println("No changes added to the commit.");
            return;
        }
        String headhash=getheadhash();
        Commit headcommit=readObject(join(COMMITS_DIR,headhash),Commit.class);
        TreeMap<String,String>headfiles=headcommit.files;
        TreeMap<String,String>newfiles=new TreeMap<>();
        for(String key:addstorage.keySet()){
            String value=addstorage.get(key);
            newfiles.put(key,value);
        }
        for(String key:headfiles.keySet()){
            if(delstorage.contains(key))continue;
            if(newfiles.containsKey(key))continue;
            newfiles.put(key,headfiles.get(key));
        }
        String date=formatdate(new Date());
        Commit newcommit=new Commit(message,date,headhash,null,newfiles);
        String newhash=commit(newcommit);
        writeContents(join(BRANCHES_DIR,readContentsAsString(HEAD)),newhash);
        addstorage.clear();
        delstorage.clear();
        writeObject(ADDSTORAGE,addstorage);
        writeObject(DELSTORAGE,delstorage);

    }
    public static void Log(){
        if(!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        String name=getheadhash();
        Commit c=readObject(join(COMMITS_DIR,name),Commit.class);
        while(true){
            System.out.println("===");
            System.out.println("commit "+name);
            if(c.parent2!=null){
                System.out.println("Merge: "+c.parent1+c.parent2);
            }
            System.out.println("Date: "+c.date);
            System.out.println(c.message);
            System.out.println();
            if(c.parent1==null)break;
            name=c.parent1;
            c=readObject(join(COMMITS_DIR,name),Commit.class);
        }
    }
    public static void Checkout1(String name) throws IOException {
        Commit c=readObject(join(COMMITS_DIR,getheadhash()),Commit.class);
        if(c.files.get(name)==null){
            System.out.println("File does not exist in that commit.");
            return;
        }
        String text=readContentsAsString(join(BLOBS_DIR,c.files.get(name)));
        writeContents(join(CWD,name),text);
    }
    public static void Checkout2(String commitid,String filename) throws IOException {
        Commit c=new Commit();
        File f=join(COMMITS_DIR,commitid);
        if(f.exists()){
            c=readObject(f,Commit.class);
        }
        else{
            boolean flag=false;
            List<String> commits=plainFilenamesIn(COMMITS_DIR);
            for(String s:commits){
                if(s.startsWith(commitid)){
                    c=readObject(join(COMMITS_DIR,s),Commit.class);
                    flag=true;
                    break;
                }
            }
            if(!flag){
                System.out.println("No commit with that id exists.");
                return;
            }
        }
        if(c.files.get(filename)==null){
            System.out.println("File does not exist in that commit.");
            return;
        }
        loadfile(filename,c.files.get(filename));
    }
    public static void Checkout3(String branchname){

    }
    private static void loadfile(String filename,String blobname) throws IOException {
        File f=join(CWD,filename);
        if(!f.exists()) f.createNewFile();
        String text=readContentsAsString(join(BLOBS_DIR,blobname));
        writeContents(f,text);
    }
}
