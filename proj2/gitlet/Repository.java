package gitlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    public static final File HEAD = join(GITLET_DIR, "head");
    public static final File ADDSTORAGE = join(GITLET_DIR, "addstorage");
    public static final File DELSTORAGE = join(GITLET_DIR, "delstorage");

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        String message = "initial commit";
        Commit c = new Commit(message, formatdate(new Date(0)), null, null,
                new TreeMap<String, String>());
        File master = join(BRANCHES_DIR, "master");
        writeContents(master, commit(c));
        writeContents(HEAD, "master");
        writeObject(ADDSTORAGE, new TreeMap<String, String>());
        writeObject(DELSTORAGE, new TreeSet<String>());
    }

    private static String formatdate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z",
                Locale.US);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    private static String commit(Commit c) {
        String name = sha1("commit", c.getMessage(), c.getDate(),
                c.getParent1() == null ? "" : c.getParent1(),
                c.getParent2() == null ? "" : c.getParent2(),
                c.getFiles().toString());
        File f = join(COMMITS_DIR, name);
        writeObject(f, c);
        return name;
    }

    public static void add(String filename) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        File file = join(CWD, filename);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        String filetext = readContentsAsString(file);
        TreeMap<String, String> addstorage = readObject(ADDSTORAGE, TreeMap.class);
        TreeSet<String> delstorage = readObject(DELSTORAGE, TreeSet.class);
        File commitfile = join(COMMITS_DIR, getheadhash());
        Commit curcommit = readObject(commitfile, Commit.class);
        String blobname = sha1("blob", filetext);
        if (curcommit.getFiles().get(filename) != null
                && curcommit.getFiles().get(filename).equals(blobname)) {
            if (delstorage.contains(filename)) {
                delstorage.remove(filename);
            }
            return;
        }
        File blobfile = join(BLOBS_DIR, blobname);
        writeContents(blobfile, filetext);
        addstorage.put(filename, blobname);
        writeObject(ADDSTORAGE, addstorage);
        if (delstorage.contains(filename)) {
            delstorage.remove(filename);
        }
        writeObject(DELSTORAGE, delstorage);
    }

    private static String getheadhash() {
        return readContentsAsString(join(BRANCHES_DIR,
                readContentsAsString(HEAD)));
    }

    public static void rm(String filename) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        File file = join(CWD, filename);
        TreeMap<String, String> addstorage = readObject(ADDSTORAGE, TreeMap.class);
        TreeSet<String> delstorage = readObject(DELSTORAGE, TreeSet.class);
        if (!addstorage.containsKey(filename)
                && !readObject(join(COMMITS_DIR, getheadhash()), Commit.class)
                .getFiles().containsKey(filename)) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if (addstorage.containsKey(filename)) {
            addstorage.remove(filename);
        }
        if (readObject(join(COMMITS_DIR, getheadhash()), Commit.class)
                .getFiles().containsKey(filename)) {
            delstorage.add(filename);
            file.delete();
        }
        writeObject(ADDSTORAGE, addstorage);
        writeObject(DELSTORAGE, delstorage);
    }

    public static void commit(String message) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        TreeMap<String, String> addstorage = readObject(ADDSTORAGE, TreeMap.class);
        TreeSet<String> delstorage = readObject(DELSTORAGE, TreeSet.class);
        if (addstorage.isEmpty() && delstorage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        String headhash = getheadhash();
        Commit headcommit = readObject(join(COMMITS_DIR, headhash), Commit.class);
        TreeMap<String, String> headfiles = headcommit.getFiles();
        TreeMap<String, String> newfiles = new TreeMap<>();
        for (String key : addstorage.keySet()) {
            String value = addstorage.get(key);
            newfiles.put(key, value);
        }
        for (String key : headfiles.keySet()) {
            if (delstorage.contains(key)) {
                continue;
            }
            if (newfiles.containsKey(key)) {
                continue;
            }
            newfiles.put(key, headfiles.get(key));
        }
        String date = formatdate(new Date());
        Commit newcommit = new Commit(message, date, headhash, null, newfiles);
        String newhash = commit(newcommit);
        writeContents(join(BRANCHES_DIR, readContentsAsString(HEAD)), newhash);
        addstorage.clear();
        delstorage.clear();
        writeObject(ADDSTORAGE, addstorage);
        writeObject(DELSTORAGE, delstorage);
    }

    public static void log() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        String name = getheadhash();
        Commit c = readObject(join(COMMITS_DIR, name), Commit.class);
        while (true) {
            System.out.println("===");
            System.out.println("commit " + name);
            if (c.getParent2() != null) {
                System.out.println("Merge: " + c.getParent1() + " "
                        + c.getParent2());
            }
            System.out.println("Date: " + c.getDate());
            System.out.println(c.getMessage());
            System.out.println();
            if (c.getParent1() == null) {
                break;
            }
            name = c.getParent1();
            c = readObject(join(COMMITS_DIR, name), Commit.class);
        }
    }

    public static void checkout1(String name) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        Commit c = readObject(join(COMMITS_DIR, getheadhash()), Commit.class);
        if (c.getFiles().get(name) == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String text = readContentsAsString(join(BLOBS_DIR,
                c.getFiles().get(name)));
        writeContents(join(CWD, name), text);
    }

    public static void checkout2(String commitid, String filename) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        String findcommitid = findcommit(commitid);
        if (findcommitid == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit c = readObject(join(COMMITS_DIR, findcommitid), Commit.class);
        if (c.getFiles().get(filename) == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        loadfile(filename, c.getFiles().get(filename));
    }

    private static String findcommit(String commitid) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return null;
        }
        File f = join(COMMITS_DIR, commitid);
        if (f.exists()) {
            return commitid;
        } else {
            List<String> commits = plainFilenamesIn(COMMITS_DIR);
            for (String s : commits) {
                if (s.startsWith(commitid)) {
                    return s;
                }
            }
        }
        return null;
    }

    public static void checkout3(String branchname) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        File f = join(BRANCHES_DIR, branchname);
        if (!f.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        if (branchname.equals(readContentsAsString(HEAD))) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Commit c = readObject(join(COMMITS_DIR,
                        readContentsAsString(join(BRANCHES_DIR, branchname))),
                Commit.class);
        TreeMap<String, String> addstorage = readObject(ADDSTORAGE, TreeMap.class);
        TreeSet<String> delstorage = readObject(DELSTORAGE, TreeSet.class);
        TreeSet<String> delist = new TreeSet<>();
        boolean flag = true;
        for (String s : plainFilenamesIn(CWD)) {
            if (c.getFiles().get(s) != null) {
                if (addstorage.get(s) == null
                        && readObject(join(COMMITS_DIR, getheadhash()),
                        Commit.class).getFiles().get(s) == null) {
                    flag = false;
                    break;
                }
                if (delstorage.contains(s)) {
                    flag = false;
                    break;
                }
            } else {
                if (readObject(join(COMMITS_DIR, getheadhash()),
                        Commit.class).getFiles().get(s) != null) {
                    delist.add(s);
                }
            }
        }
        if (!flag) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            return;
        }
        for (String s : delist) {
            join(CWD, s).delete();
        }
        writeContents(HEAD, branchname);
        addstorage.clear();
        delstorage.clear();
        writeObject(ADDSTORAGE, addstorage);
        writeObject(DELSTORAGE, delstorage);
        for (String s : c.getFiles().keySet()) {
            File newfile = join(CWD, s);
            writeContents(newfile,
                    readContentsAsString(join(BLOBS_DIR, c.getFiles().get(s))));
        }
    }

    private static void loadfile(String filename, String blobname) {
        File f = join(CWD, filename);
        String text = readContentsAsString(join(BLOBS_DIR, blobname));
        writeContents(f, text);
    }

    public static void globalLog() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        for (String s : commits) {
            Commit c = readObject(join(COMMITS_DIR, s), Commit.class);
            System.out.println("===");
            System.out.println("commit " + s);
            if (c.getParent2() != null) {
                System.out.println("Merge: " + c.getParent1() + " "
                        + c.getParent2());
            }
            System.out.println("Date: " + c.getDate());
            System.out.println(c.getMessage());
            System.out.println();
        }
    }

    public static void find(String target) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        boolean flag = false;
        for (String s : commits) {
            Commit c = readObject(join(COMMITS_DIR, s), Commit.class);
            if (c.getMessage().equals(target)) {
                System.out.println(s);
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        System.out.println("=== Branches ===");
        List<String> branches = plainFilenamesIn(BRANCHES_DIR);
        Collections.sort(branches);
        String headbranch = readContentsAsString(HEAD);
        for (String branch : branches) {
            if (branch.equals(headbranch)) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        TreeMap<String, String> addstorage = readObject(ADDSTORAGE, TreeMap.class);
        TreeSet<String> delstorage = readObject(DELSTORAGE, TreeSet.class);
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String filename : addstorage.keySet()) {
            System.out.println(filename);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String filename : delstorage) {
            System.out.println(filename);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        TreeSet<String> notstaged = new TreeSet<>();
        Commit headcommit = readObject(join(COMMITS_DIR, getheadhash()), Commit.class);
        for (String s : headcommit.getFiles().keySet()) {
            File f = join(CWD, s);
            if (!f.exists()) {
                if (!delstorage.contains(s)) {
                    notstaged.add(s);
                }
                continue;
            }
            String filetext = readContentsAsString(f);
            if (headcommit.getFiles().get(s) != null
                    && !filetext.equals(readContentsAsString(
                    join(BLOBS_DIR, headcommit.getFiles().get(s))))
                    && addstorage.get(s) == null) {
                notstaged.add(s);
            }
        }
        for (String s : addstorage.keySet()) {
            File f = join(CWD, s);
            if (!f.exists()) {
                notstaged.add(s);
                continue;
            }
            if (!readContentsAsString(f).equals(
                    readContentsAsString(join(BLOBS_DIR, addstorage.get(s))))) {
                notstaged.add(s);
            }
        }
        for (String s : notstaged) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        TreeSet<String> nottrack = getuntracked();
        for (String s : nottrack) {
            System.out.println(s);
        }
        System.out.println();
    }

    private static TreeSet<String> getuntracked() {
        Commit headcommit = readObject(join(COMMITS_DIR, getheadhash()), Commit.class);
        TreeSet<String> nottrack = new TreeSet<>();
        TreeMap<String, String> addstorage = readObject(ADDSTORAGE, TreeMap.class);
        TreeSet<String> delstorage = readObject(DELSTORAGE, TreeSet.class);
        for (String s : plainFilenamesIn(CWD)) {
            File f = join(CWD, s);
            if (f.isDirectory()) {
                continue;
            }
            if (delstorage.contains(s)) {
                nottrack.add(s);
                continue;
            }
            if (headcommit.getFiles().get(s) == null
                    && addstorage.get(s) == null) {
                nottrack.add(s);
                continue;
            }
        }
        return nottrack;
    }

    public static void branch(String newbranchname) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        List<String> branches = plainFilenamesIn(BRANCHES_DIR);
        if (branches.contains(newbranchname)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        File newbranchfile = join(BRANCHES_DIR, newbranchname);
        writeContents(newbranchfile, getheadhash());
    }

    public static void rmBranch(String branchname) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        if (!plainFilenamesIn(BRANCHES_DIR).contains(branchname)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (branchname.equals(readContentsAsString(HEAD))) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        join(BRANCHES_DIR, branchname).delete();
    }

    public static void reset(String commitid) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        String findcommitid = findcommit(commitid);
        if (findcommitid == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit c = readObject(join(COMMITS_DIR, findcommitid), Commit.class);
        TreeSet<String> untracked = getuntracked();
        for (String s : c.getFiles().keySet()) {
            if (untracked.contains(s)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }
        Commit currentCommit = readObject(join(COMMITS_DIR, getheadhash()),
                Commit.class);
        for (String s : currentCommit.getFiles().keySet()) {
            if (!c.getFiles().containsKey(s)) {
                File f = join(CWD, s);
                if (f.exists()) {
                    restrictedDelete(f);
                }
            }
        }
        for (String s : c.getFiles().keySet()) {
            loadfile(s, c.getFiles().get(s));
        }
        writeContents(join(BRANCHES_DIR, readContentsAsString(HEAD)),
                findcommitid);
        clearstorage();
    }

    private static void clearstorage() {
        writeObject(ADDSTORAGE, new TreeMap<String, String>());
        writeObject(DELSTORAGE, new TreeSet<String>());
    }

    public static void merge(String branch2) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        TreeMap<String, String> addstorage = readObject(ADDSTORAGE, TreeMap.class);
        TreeSet<String> delstorage = readObject(DELSTORAGE, TreeSet.class);
        if (!addstorage.isEmpty() || !delstorage.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        if (readContentsAsString(HEAD).equals(branch2)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        if (!join(BRANCHES_DIR, branch2).exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        String name1 = getheadhash();
        String name2 = readContentsAsString(join(BRANCHES_DIR, branch2));
        String name3 = getSplitPoint(name1, name2);

        if (name3 == null) {
            return;
        }
        if (name3.equals(name2)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (name3.equals(name1)) {
            System.out.println("Current branch fast-forwarded.");
            reset(name2);
            return;
        }

        Commit c1 = readObject(join(COMMITS_DIR, name1), Commit.class);
        Commit c2 = readObject(join(COMMITS_DIR, name2), Commit.class);
        Commit c3 = readObject(join(COMMITS_DIR, name3), Commit.class);
        TreeMap<String, String> files1 = c1.getFiles();
        TreeMap<String, String> files2 = c2.getFiles();
        TreeMap<String, String> files3 = c3.getFiles();

        TreeSet<String> affectedFiles = new TreeSet<>();
        for (String f : files2.keySet()) {
            if (files3.get(f) == null || !files3.get(f).equals(files2.get(f))) {
                affectedFiles.add(f);
            }
        }
        for (String f : files1.keySet()) {
            if (!files2.containsKey(f) && files3.containsKey(f)
                    && files1.get(f).equals(files3.get(f))) {
                affectedFiles.add(f);
            }
        }
        for (String f : files2.keySet()) {
            if (!files3.containsKey(f)) {
                affectedFiles.add(f);
            }
        }

        TreeSet<String> untracked = getuntracked();
        for (String f : affectedFiles) {
            if (untracked.contains(f)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }

        boolean conflict = false;
        Set<String> allFiles = new TreeSet<>();
        allFiles.addAll(files1.keySet());
        allFiles.addAll(files2.keySet());
        allFiles.addAll(files3.keySet());

        for (String s : allFiles) {
            String s1 = files1.get(s);
            String s2 = files2.get(s);
            String s3 = files3.get(s);

            if (s3 != null) {
                if (s1 != null && s2 != null && !s1.equals(s3)
                        && !s2.equals(s3) && !s1.equals(s2)) {
                    conflict = true;
                    String blobHash = writeConflictFile(s, s1, s2);
                    addstorage.put(s, blobHash);
                } else if (s1 != null && s2 != null && s1.equals(s3)
                        && !s2.equals(s3)) {
                    loadfile(s, s2);
                    addstorage.put(s, s2);
                } else if (s1 != null && s2 != null && !s1.equals(s3)
                        && s2.equals(s3)) {
                    // do nothing
                } else if (s1 == null && s2 != null && !s2.equals(s3)) {
                    conflict = true;
                    String blobHash = writeConflictFile(s, null, s2);
                    addstorage.put(s, blobHash);
                } else if (s2 == null && s1 != null && s1.equals(s3)) {
                    join(CWD, s).delete();
                    delstorage.add(s);
                } else if (s1 == null && s2 != null && s2.equals(s3)) {
                    // nothing
                } else if (s1 == null && s2 == null) {
                    // nothing
                } else if (s2 == null && s1 != null && !s1.equals(s3)) {
                    conflict = true;
                    String blobHash = writeConflictFile(s, s1, null);
                    addstorage.put(s, blobHash);
                }
            } else {
                if (s1 == null && s2 != null) {
                    loadfile(s, s2);
                    addstorage.put(s, s2);
                } else if (s1 != null && s2 == null) {
                    // nothing
                } else if (s1 != null && s2 != null && !s1.equals(s2)) {
                    conflict = true;
                    String blobHash = writeConflictFile(s, s1, s2);
                    addstorage.put(s, blobHash);
                }
            }
        }

        writeObject(ADDSTORAGE, addstorage);
        writeObject(DELSTORAGE, delstorage);

        if (addstorage.isEmpty() && delstorage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }

        TreeMap<String, String> mergedFiles = new TreeMap<>(c1.getFiles());
        for (String file : addstorage.keySet()) {
            mergedFiles.put(file, addstorage.get(file));
        }
        for (String file : delstorage) {
            mergedFiles.remove(file);
        }

        String currentBranch = readContentsAsString(HEAD);
        String mergeMsg = "Merged " + branch2 + " into " + currentBranch + ".";
        Commit mergeCommit = new Commit(mergeMsg, formatdate(new Date()),
                name1, name2, mergedFiles);
        String newHash = commit(mergeCommit);

        writeContents(join(BRANCHES_DIR, currentBranch), newHash);

        addstorage.clear();
        delstorage.clear();
        writeObject(ADDSTORAGE, addstorage);
        writeObject(DELSTORAGE, delstorage);

        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private static String getsplitdot(String branch1, String branch2) {
        ArrayDeque<String> q = new ArrayDeque<>();
        TreeSet<String> commits1 = new TreeSet<>();
        commits1.add(readContentsAsString(join(BRANCHES_DIR, branch1)));
        q.push(readContentsAsString(join(BRANCHES_DIR, branch1)));
        while (!q.isEmpty()) {
            String s = q.getFirst();
            q.pop();
            Commit c = readObject(join(COMMITS_DIR, s), Commit.class);
            if (c.getParent1() != null && !commits1.contains(c.getParent1())) {
                commits1.add(c.getParent1());
                q.addLast(c.getParent1());
            }
            if (c.getParent2() != null && !commits1.contains(c.getParent2())) {
                commits1.add(c.getParent2());
                q.addLast(c.getParent2());
            }
        }
        q.push(readContentsAsString(join(BRANCHES_DIR, branch2)));
        while (!q.isEmpty()) {
            String s = q.getFirst();
            if (commits1.contains(s)) {
                return s;
            }
            q.pop();
            Commit c = readObject(join(COMMITS_DIR, s), Commit.class);
            if (c.getParent1() != null) {
                q.addLast(c.getParent1());
            }
            if (c.getParent2() != null) {
                q.addLast(c.getParent2());
            }
        }
        return null;
    }

    private static String readBlobContent(String blobHash) {
        return readContentsAsString(join(BLOBS_DIR, blobHash));
    }

    private static String writeConflictFile(String filename,
                                            String blobHashCurrent, String blobHashGiven) {
        String curContent = (blobHashCurrent == null) ? ""
                : readBlobContent(blobHashCurrent);
        String givenContent = (blobHashGiven == null) ? ""
                : readBlobContent(blobHashGiven);
        String conflictContent = "<<<<<<< HEAD\n" + curContent
                + "=======\n" + givenContent + ">>>>>>>\n";
        writeContents(join(CWD, filename), conflictContent);
        String blobName = sha1("blob", conflictContent);
        writeContents(join(BLOBS_DIR, blobName), conflictContent);
        return blobName;
    }

    private static String getSplitPoint(String hash1, String hash2) {
        Set<String> ancestors1 = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        ancestors1.add(hash1);
        queue.add(hash1);
        while (!queue.isEmpty()) {
            String s = queue.removeFirst();
            Commit c = readObject(join(COMMITS_DIR, s), Commit.class);
            if (c.getParent1() != null && ancestors1.add(c.getParent1())) {
                queue.addLast(c.getParent1());
            }
            if (c.getParent2() != null && ancestors1.add(c.getParent2())) {
                queue.addLast(c.getParent2());
            }
        }
        queue.clear();
        queue.addLast(hash2);
        while (!queue.isEmpty()) {
            String s = queue.removeFirst();
            if (ancestors1.contains(s)) {
                return s;
            }
            Commit c = readObject(join(COMMITS_DIR, s), Commit.class);
            if (c.getParent1() != null) {
                queue.addLast(c.getParent1());
            }
            if (c.getParent2() != null) {
                queue.addLast(c.getParent2());
            }
        }
        return null;
    }
}