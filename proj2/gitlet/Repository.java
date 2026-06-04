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

    public static void Init(){
        if(GITLET_DIR.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        String message="initial commit";
        Commit c=new Commit(message,formatdate(new Date(0)),null,null,new TreeMap<String,String>());
        File master=join(BRANCHES_DIR,"master");
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
    private static String commit(Commit c){
        String name=sha1("commit",c.message,c.date,c.parent1==null?"":c.parent1,c.parent2==null?"":c.parent2,c.files.toString());
        File f=join(COMMITS_DIR,name);
        writeObject(f,c);
        return name;
    }
    public static void Add(String filename){
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
    public static void Commit(String message){
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
    public static void Checkout1(String name){
        Commit c=readObject(join(COMMITS_DIR,getheadhash()),Commit.class);
        if(c.files.get(name)==null){
            System.out.println("File does not exist in that commit.");
            return;
        }
        String text=readContentsAsString(join(BLOBS_DIR,c.files.get(name)));
        writeContents(join(CWD,name),text);
    }
    public static void Checkout2(String commitid,String filename) {
       String findcommitid=findcommit(commitid);
        if(findcommitid==null){
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit c=readObject(join(COMMITS_DIR,findcommitid),Commit.class);
        if(c.files.get(filename)==null){
            System.out.println("File does not exist in that commit.");
            return;
        }
        loadfile(filename,c.files.get(filename));
    }
    private static String findcommit(String commitid){
        File f=join(COMMITS_DIR,commitid);
        if(f.exists()){
            return commitid;
        }
        else{
           List<String>commits=plainFilenamesIn(COMMITS_DIR);
           for(String s:commits){
               if(s.startsWith(commitid)){
                  return s;
               }
           }
        }
        return null;
    }
    public static void Checkout3(String branchname){
        File f=join(BRANCHES_DIR,branchname);
        if(!f.exists()){
            System.out.println("No such branch exists.");
            return;
        }
        if(branchname.equals(readContentsAsString(HEAD))){
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Commit c= readObject(join(COMMITS_DIR,readContentsAsString(join(BRANCHES_DIR,branchname))),Commit.class);
        TreeMap<String,String>addstorage=readObject(ADDSTORAGE,TreeMap.class);
        TreeSet<String>delstorage=readObject(DELSTORAGE,TreeSet.class);
        boolean flag=true;
        for(String s:plainFilenamesIn(CWD)){
            if(c.files.get(s)!=null){
                if(addstorage.get(s)==null&&readObject(join(COMMITS_DIR,getheadhash()),Commit.class).files.get(s)==null){
                    flag=false;
                    break;
                }
                if(delstorage.contains(s)){
                    flag=false;
                    break;
                }
            }
        }
        if(!flag){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
        }
        writeContents(HEAD,branchname);
        addstorage.clear();
        delstorage.clear();
        writeObject(ADDSTORAGE,addstorage);
        writeObject(DELSTORAGE,delstorage);
        for(String s:c.files.keySet()){
            File newfile=join(CWD,s);
            writeContents(newfile,readContentsAsString(join(BLOBS_DIR,c.files.get(s))));
        }
    }
    private static void loadfile(String filename,String blobname) {
        File f=join(CWD,filename);
        String text=readContentsAsString(join(BLOBS_DIR,blobname));
        writeContents(f,text);
    }
    public static void Global_log(){
        List<String> commits=plainFilenamesIn(COMMITS_DIR);
        for(String s:commits){
            Commit c=readObject(join(COMMITS_DIR,s),Commit.class);
            System.out.println("===");
            System.out.println("commit "+s);
            if(c.parent2!=null){
                System.out.println("Merge: "+c.parent1+c.parent2);
            }
            System.out.println("Date: "+c.date);
            System.out.println(c.message);
            System.out.println();
        }
    }
    public static void Find(String target){
        List<String> commits=plainFilenamesIn(COMMITS_DIR);
        boolean flag=false;
        for(String s:commits){
            Commit c=readObject(join(COMMITS_DIR,s),Commit.class);
            if(c.message.equals(target)){
                System.out.println(s);
                flag=true;
            }
        }
        if(!flag){
            System.out.println("Found no commit with that message.");
        }
    }
    public static void Status(){
        System.out.println("=== Branches ===");
        List<String> branches=plainFilenamesIn(BRANCHES_DIR);
        Collections.sort(branches);
        String headbranch=readContentsAsString(HEAD);
        for(String branch:branches){
            if(branch.equals(headbranch)){
                System.out.print("*");
            }
            System.out.println(branch);
        }
        TreeMap<String,String>addstorage=readObject(ADDSTORAGE,TreeMap.class);
        TreeSet<String>delstorage=readObject(DELSTORAGE,TreeSet.class);
        System.out.println();
        System.out.println("=== Staged Files ===");
        for(String filename:addstorage.keySet()){
            System.out.println(filename);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for(String filename:delstorage){
            System.out.println(filename);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        TreeSet<String>notstaged=new TreeSet<>();
        Commit headcommit=readObject(join(COMMITS_DIR,getheadhash()),Commit.class);
        for(String s:headcommit.files.keySet()){
            File f=join(CWD,s);
            if(!f.exists()){
                if(!delstorage.contains(s)){
                    notstaged.add(s);
                }
                continue;
            }
            String filetext=readContentsAsString(f);
            if(headcommit.files.get(s)!=null&&!filetext.equals(readContentsAsString(join(BLOBS_DIR,headcommit.files.get(s))))&&addstorage.get(s)==null)
                notstaged.add(s);
        }
        for(String s:addstorage.keySet()){
            File f=join(CWD,s);
            if(!f.exists()){
                notstaged.add(s);
                continue;
            }
            if(!readContentsAsString(f).equals(readContentsAsString(join(BLOBS_DIR,addstorage.get(s)))))notstaged.add(s);
        }
        for(String s:notstaged){
            System.out.println(s);
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
       TreeSet<String>nottrack=getuntracked();
        for(String s:nottrack){
            System.out.println(s);
        }
        System.out.println();
    }
    private static TreeSet<String> getuntracked(){
        Commit headcommit=readObject(join(COMMITS_DIR,getheadhash()),Commit.class);
        TreeSet<String>nottrack=new TreeSet<>();
        TreeMap<String,String>addstorage=readObject(ADDSTORAGE,TreeMap.class);
        TreeSet<String>delstorage=readObject(DELSTORAGE,TreeSet.class);
        for(String s:plainFilenamesIn(CWD)){
            File f=join(CWD,s);
            if(f.isDirectory())continue;
            if(delstorage.contains(s)){
                nottrack.add(s);
                continue;
            }
            if(headcommit.files.get(s)==null&&addstorage.get(s)==null){
                nottrack.add(s);
                continue;
            }
        }
        return nottrack;
    }
    public static void Branch(String newbranchname){
        List<String>branches=plainFilenamesIn(BRANCHES_DIR);
        if(branches.contains(newbranchname)){
            System.out.println("A branch with that name already exists.");
            return;
        }
        File newbranchfile=join(BRANCHES_DIR,newbranchname);
        writeContents(newbranchfile,getheadhash());
    }
    public static void Rm_branch(String branchname){
        if(!plainFilenamesIn(BRANCHES_DIR).contains(branchname)){
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if(branchname.equals(readContentsAsString(HEAD))){
            System.out.println("Cannot remove the current branch.");
            return;
        }
        join(BRANCHES_DIR,branchname).delete();
    }
    public static void Reset (String commitid){
        String findcommitid=findcommit(commitid);
        if(findcommitid==null){
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit c=readObject(join(COMMITS_DIR,findcommitid),Commit.class);
        TreeSet<String>untracked=getuntracked();
        for(String s:c.files.keySet()){
            if(untracked.contains(s)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }
        Commit currentCommit = readObject(join(COMMITS_DIR, getheadhash()), Commit.class);
        for (String s : currentCommit.files.keySet()) {
            if (!c.files.containsKey(s)) {
                File f = join(CWD, s);
                if (f.exists()) {
                    restrictedDelete(f);
                }
            }
        }

        for(String s:c.files.keySet()){
            loadfile(s,c.files.get(s));
        }
        writeContents(join(BRANCHES_DIR,readContentsAsString(HEAD)),findcommitid);
        clearstorage();
        return;
    }
    private static void clearstorage(){
        writeObject(ADDSTORAGE,new TreeMap<String,String>());
        writeObject(DELSTORAGE,new TreeSet<String>());
    }
    public static void Merge(String branch2) {
        // 检查暂存区是否有未提交的更改
        TreeMap<String, String> addstorage = readObject(ADDSTORAGE, TreeMap.class);
        TreeSet<String> delstorage = readObject(DELSTORAGE, TreeSet.class);
        if (!addstorage.isEmpty() || !delstorage.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        // 检查分支有效性
        if (readContentsAsString(HEAD).equals(branch2)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        if (!join(BRANCHES_DIR, branch2).exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        String name1 = getheadhash();                          // 当前分支头
        String name2 = readContentsAsString(join(BRANCHES_DIR, branch2)); // 给定分支头
        String name3 = getSplitPoint(name1, name2);            // 分裂点

        if (name3 == null) {
            // 理论上不会发生，但保留兜底
            return;
        }

        // 情况1：给定分支是当前分支的祖先
        if (name3.equals(name2)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        // 情况2：当前分支快进
        if (name3.equals(name1)) {
            System.out.println("Current branch fast-forwarded.");
            // 快进相当于直接 reset 到给定分支的头部
            Reset(name2);  // Reset 内部会处理工作目录、暂存区和分支指针
            return;
        }

        // 加载三个关键提交
        Commit c1 = readObject(join(COMMITS_DIR, name1), Commit.class);
        Commit c2 = readObject(join(COMMITS_DIR, name2), Commit.class);
        Commit c3 = readObject(join(COMMITS_DIR, name3), Commit.class);
        TreeMap<String, String> files1 = c1.files;
        TreeMap<String, String> files2 = c2.files;
        TreeMap<String, String> files3 = c3.files;

        // ---------- 第一步：检查未追踪文件冲突 ----------
        TreeSet<String> affectedFiles = new TreeSet<>();
        // 收集所有可能被合并操作覆盖/创建/删除的文件
        for (String f : files2.keySet()) {
            // 给定分支中与分裂点不同的文件（会被用来覆盖或冲突写入）
            if (files3.get(f) == null || !files3.get(f).equals(files2.get(f))) {
                affectedFiles.add(f);
            }
        }
        for (String f : files1.keySet()) {
            // 当前分支未修改但给定分支删除的文件（将被删除）
            if (!files2.containsKey(f) && files3.containsKey(f) && files1.get(f).equals(files3.get(f))) {
                affectedFiles.add(f);
            }
        }
        // 给定分支新增的文件（肯定受影响）
        for (String f : files2.keySet()) {
            if (!files3.containsKey(f)) {
                affectedFiles.add(f);
            }
        }

        TreeSet<String> untracked = getuntracked();
        for (String f : affectedFiles) {
            if (untracked.contains(f)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        // ---------- 第二步：实际合并 ----------
        boolean conflict = false;
        Set<String> allFiles = new TreeSet<>();
        allFiles.addAll(files1.keySet());
        allFiles.addAll(files2.keySet());
        allFiles.addAll(files3.keySet());

        for (String s : allFiles) {
            String s1 = files1.get(s); // 当前分支的 blob
            String s2 = files2.get(s); // 给定分支的 blob
            String s3 = files3.get(s); // 分裂点的 blob

            // ---- 文件在分裂点存在 ----
            if (s3 != null) {
                // 两分支均存在且都修改，且修改不同 → 冲突
                if (s1 != null && s2 != null && !s1.equals(s3) && !s2.equals(s3) && !s1.equals(s2)) {
                    conflict = true;
                    String blobHash = writeConflictFile(s, s1, s2);
                    addstorage.put(s, blobHash);
                }
                // 只有给定分支修改 → 使用给定分支版本
                else if (s1 != null && s2 != null && s1.equals(s3) && !s2.equals(s3)) {
                    loadfile(s, s2);
                    addstorage.put(s, s2);
                }
                // 只有当前分支修改 → 保持不动（nothing）
                else if (s1 != null && s2 != null && !s1.equals(s3) && s2.equals(s3)) {
                    // do nothing
                }
                // 当前分支删除，给定分支修改 → 冲突
                else if (s1 == null && s2 != null && !s2.equals(s3)) {
                    conflict = true;
                    String blobHash = writeConflictFile(s, null, s2);
                    addstorage.put(s, blobHash);
                }
                // 给定分支删除，当前分支未修改 → 删除并暂存删除
                else if (s2 == null && s1 != null && s1.equals(s3)) {
                    join(CWD, s).delete();
                    delstorage.add(s);
                }
                // 当前分支删除，给定分支未修改 → 保持删除（可能工作目录有未追踪文件已检查过，这里不操作）
                else if (s1 == null && s2 != null && s2.equals(s3)) {
                    // nothing
                }
                // 两分支都删除 → 保持删除
                else if (s1 == null && s2 == null) {
                    // nothing
                }
                // 其他情况（如s1==null && s2==null 已处理，s1!=null && s2==null && s1!=s3 → 冲突）
                else if (s2 == null && s1 != null && !s1.equals(s3)) {
                    conflict = true;
                    String blobHash = writeConflictFile(s, s1, null);
                    addstorage.put(s, blobHash);
                }
            }
            // ---- 文件在分裂点不存在 ----
            else {
                // 仅在给定分支新增 → 检出并暂存
                if (s1 == null && s2 != null) {
                    loadfile(s, s2);
                    addstorage.put(s, s2);
                }
                // 仅在当前分支新增 → 保持不动
                else if (s1 != null && s2 == null) {
                    // nothing
                }
                // 两分支都新增且内容不同 → 冲突
                else if (s1 != null && s2 != null && !s1.equals(s2)) {
                    conflict = true;
                    String blobHash = writeConflictFile(s, s1, s2);
                    addstorage.put(s, blobHash);
                }
                // 两分支都新增且内容相同 → nothing
            }
        }

        // 保存暂存区状态（之后的提交会清空，这里先持久化）
        writeObject(ADDSTORAGE, addstorage);
        writeObject(DELSTORAGE, delstorage);

        // ---------- 第三步：自动合并提交 ----------
        // 如果合并后没有任何暂存更改，按规范应报错（与普通 commit 行为一致）
        if (addstorage.isEmpty() && delstorage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            // 回滚已经修改的工作目录文件吗？规范未要求，直接返回。
            return;
        }

        // 构造合并提交的文件快照（基于当前分支）
        TreeMap<String, String> mergedFiles = new TreeMap<>(c1.files);
        for (String file : addstorage.keySet()) {
            mergedFiles.put(file, addstorage.get(file));
        }
        for (String file : delstorage) {
            mergedFiles.remove(file);
        }

        String currentBranch = readContentsAsString(HEAD);
        String mergeMsg = "Merged " + branch2 + " into " + currentBranch + ".";
        Commit mergeCommit = new Commit(mergeMsg, formatdate(new Date()), name1, name2, mergedFiles);
        String newHash = commit(mergeCommit);

        // 移动当前分支指针
        writeContents(join(BRANCHES_DIR, currentBranch), newHash);

        // 清空暂存区
        addstorage.clear();
        delstorage.clear();
        writeObject(ADDSTORAGE, addstorage);
        writeObject(DELSTORAGE, delstorage);

        // 冲突提示
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }
    private static String getsplitdot(String branch1,String branch2){
       ArrayDeque <String>q=new ArrayDeque<>();
       TreeSet<String>commits1=new TreeSet<>();
       commits1.add(readContentsAsString(join(BRANCHES_DIR,branch1)));
       q.push(readContentsAsString(join(BRANCHES_DIR,branch1)));
       while(!q.isEmpty()){
           String s=q.getFirst();
           q.pop();
           Commit c=readObject(join(COMMITS_DIR,s),Commit.class);
           if(c.parent1!=null&&!commits1.contains(c.parent1)){
               commits1.add(c.parent1);
               q.addLast(c.parent1);
           }
           if(c.parent2!=null&&!commits1.contains(c.parent2)){
               commits1.add(c.parent2);
               q.addLast(c.parent2);
           }
       }
       q.push(readContentsAsString(join(BRANCHES_DIR,branch2)));
        while(!q.isEmpty()){
            String s=q.getFirst();
            if(commits1.contains(s))return s;
            q.pop();
            Commit c=readObject(join(COMMITS_DIR,s),Commit.class);
            if(c.parent1!=null){
                q.addLast(c.parent1);
            }
            if(c.parent2!=null){
                q.addLast(c.parent2);
            }
        }
        return null;
    }
    // 读取 blob 的文件内容字符串
    private static String readBlobContent(String blobHash) {
        return readContentsAsString(join(BLOBS_DIR, blobHash));
    }

    // 写入冲突文件，并返回其 blob 哈希
    private static String writeConflictFile(String filename, String blobHashCurrent, String blobHashGiven) {
        String curContent = (blobHashCurrent == null) ? "" : readBlobContent(blobHashCurrent);
        String givenContent = (blobHashGiven == null) ? "" : readBlobContent(blobHashGiven);
        String conflictContent = "<<<<<<< HEAD\n" + curContent +
                "=======\n" + givenContent + ">>>>>>>\n";
        writeContents(join(CWD, filename), conflictContent);
        // 计算冲突内容的 blob 哈希并写入 blob 文件
        String blobName = sha1("blob", conflictContent);
        writeContents(join(BLOBS_DIR, blobName), conflictContent);
        return blobName;
    }
    private static String getSplitPoint(String hash1, String hash2) {
        // 第一次遍历：收集 hash1 的所有祖先（用 BFS 或 DFS，无所谓）
        Set<String> ancestors1 = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        ancestors1.add(hash1);
        queue.add(hash1);
        while (!queue.isEmpty()) {
            String s = queue.removeFirst();
            Commit c = readObject(join(COMMITS_DIR, s), Commit.class);
            if (c.parent1 != null && ancestors1.add(c.parent1))
                queue.addLast(c.parent1);
            if (c.parent2 != null && ancestors1.add(c.parent2))
                queue.addLast(c.parent2);
        }
        // 第二次遍历：从 hash2 开始 BFS，找第一个出现在 ancestors1 中的节点
        queue.clear();
        queue.addLast(hash2);
        while (!queue.isEmpty()) {
            String s = queue.removeFirst();
            if (ancestors1.contains(s))
                return s;
            Commit c = readObject(join(COMMITS_DIR, s), Commit.class);
            if (c.parent1 != null) queue.addLast(c.parent1);
            if (c.parent2 != null) queue.addLast(c.parent2);
        }
        return null;
    }
}
