//package com.michael.MyUtils;
//
//import edu.lu.uni.serval.git.exception.GitRepositoryNotFoundException;
//import edu.lu.uni.serval.git.exception.NotValidGitRepositoryException;
//import edu.lu.uni.serval.git.travel.GitRepository;
//import edu.lu.uni.serval.utils.FileHelper;
//import org.eclipse.jgit.api.Git;
//import org.eclipse.jgit.api.ResetCommand;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.eclipse.jgit.lib.ObjectId;
//import org.eclipse.jgit.lib.Repository;
//import org.eclipse.jgit.revwalk.RevCommit;
//import org.eclipse.jgit.revwalk.RevWalk;
//import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//public class ResetGitRepos {
//    public static void main(String [] args) throws GitAPIException, IOException, NotValidGitRepositoryException, GitRepositoryNotFoundException {
////        String gitRoot = javaBasePath + eachProjectName + "/.git";
////        StringBuilder result = new StringBuilder();
////        try{
////            GitRepository gitRepository = ResetGitRepos.openRepository(javaBasePath,eachProjectName,gitRoot);
////            List<RevCommit> commits = gitRepository.getAllCommits(false);
//////            System.out.println(commits.size());
////            ArrayList<String> commitIDs = new ArrayList<>();
////            for(RevCommit commit:commits){
////                commitIDs.add(commit.getName());
////            }
////            ResetGitRepos.removeGitLock(eachProjectName, javaBasePath);
////            ResetGitRepos.rollReposToLatestCommit(gitRoot, commits.get(0).getName(), commitIDs, commits);
////        }
////        catch (Exception e){
////            continue;
////        }
//    }
//    public static void removeGitLock(String gitRoot) {
//    /*
//        check if index file is locked, if it is, delete index.lock
//    */
//        String lockFile = gitRoot + "/" + "index.lock";
//        if (FileHelper.isValidPath(lockFile)) {
//            FileHelper.deleteFile(lockFile);
//            System.out.println("delete lockFile:" + lockFile);
//        }
//    }
//
//    public static GitRepository openRepository(String basePath,String projectName,String gitRoot) throws NotValidGitRepositoryException, GitRepositoryNotFoundException, IOException {
//        String revisedPath = basePath +"\\" + projectName + File.separator + "Rev";
//        String previousPath = basePath +"\\" + projectName + File.separator + "Pre";
//        FileHelper.createDirectory(revisedPath);
//        FileHelper.createDirectory(previousPath);
//        GitRepository gitRepository = new GitRepository(gitRoot,revisedPath,previousPath);
//        gitRepository.open();
//        return gitRepository;
//    }
//    public static void rollReposToLatestCommit(String gitRoot, String commitID,List<String> commitIDs, List<RevCommit>commits) throws NotValidGitRepositoryException, GitRepositoryNotFoundException, IOException, GitAPIException {
//
//        Boolean isRollBack;
//        int index = commitIDs.indexOf(commitID);
//        System.out.println("roll back to the "+index+"-th commit");
//        if(index!=-1){
//            RevCommit commit = commits.get(index);
////            System.out.println("commit.getId():"+commit.getId());
//            isRollBack = rollBackPreRevision(gitRoot, commit.getId());
//            System.out.println(isRollBack);
//        }
//    }
//
//    public static void rollReposToSpecificCommit(String gitRoot, String commitID,List<String> commitIDs, List<RevCommit>commits) throws NotValidGitRepositoryException, GitRepositoryNotFoundException, IOException, GitAPIException {
//
//        Boolean isRollBack;
//        int index = commitIDs.indexOf(commitID);
////        System.out.println("roll back to the "+index+"-th commit");
//        if(index!=-1){
//            RevCommit commit = commits.get(index);
////            System.out.println("commit.getId():"+commit.getId());
//            isRollBack = rollBackPreRevision(gitRoot, commit.getId());
////            System.out.println(isRollBack);
//        }
//    }
//    public static void rollReposToSpecificBeforeCommit(String gitRoot, String commitID,List<String> commitIDs, List<RevCommit>commits) throws NotValidGitRepositoryException, GitRepositoryNotFoundException, IOException, GitAPIException {
//
//        Boolean isRollBack;
//        int index = commitIDs.indexOf(commitID);
//        System.out.println("roll back to the "+index+"-th commit");
//        if(index!=-1){
//            RevCommit commit = commits.get(index);
//            System.out.println("commit.getId():"+commit.getId());
//            RevCommit beforeCommit = commit.getParent(0);
//            System.out.println("beforeCommit.getId():"+beforeCommit.getId());
//            isRollBack = rollBackPreRevision(gitRoot, beforeCommit.getId());
//            System.out.println(isRollBack);
//        }
//    }
//
//    public static String rollReposToSpecificCommitAndGetID(String gitRoot, String commitID,List<String> commitIDs, List<RevCommit>commits) throws NotValidGitRepositoryException, GitRepositoryNotFoundException, IOException, GitAPIException {
//
//        Boolean isRollBack;
//        int index = commitIDs.indexOf(commitID);
//        RevCommit parentCommit = null;
//        System.out.println(index);
//        if(index!=-1){
//            RevCommit commit = commits.get(index);
//            System.out.println("commit.getId():"+commit.getId());
//            parentCommit = commit.getParent(0);
//            System.out.println("parent commit.getId():"+parentCommit.getId());
//            isRollBack = rollBackPreRevision(gitRoot, parentCommit.getId());
//            System.out.println(isRollBack);
//        }
//        if(parentCommit!=null)
//            return parentCommit.getName();
//        else
//            return "";
//
//    }
//
////    public void rollAllReposToSpecificCommit(Map<String,Integer> reposAndLongestDuration) throws NotValidGitRepositoryException, GitRepositoryNotFoundException, IOException, GitAPIException {
////        List<String> projectList = readList(Configuration4InCon.JAVA_REPO_NAMES_FILE);
////        List<String> errorRepoList = new ArrayList<>();
////        for(String project:projectList){
////            int longestDuration = reposAndLongestDuration.get(project);
////            String gitRoot = Configuration4InCon.JAVA_REPOS_PATH + project + "/.git";
////            GitRepository gitRepository = new GitRepository(gitRoot);
////            gitRepository.open();
////
////            try{
////                List<RevCommit> commits = gitRepository.getAllCommits(false);
////                Boolean isRollBack;
////                isRollBack = rollBackPreRevision(gitRoot, commits.get(longestDuration).getId());
////                System.out.println(longestDuration);
////                System.out.println(isRollBack);
////
////            }
////            catch(org.eclipse.jgit.api.errors.CheckoutConflictException e){
////                e.printStackTrace();
////                errorRepoList.add(project);
////                continue;
////            }
////            catch(org.eclipse.jgit.api.errors.NoHeadException e1){
////                e1.printStackTrace();
////                errorRepoList.add(project);
////                continue;
////            }
////            catch(org.eclipse.jgit.api.errors.JGitInternalException e2){
////                System.err.println(project);
////                e2.printStackTrace();
////                errorRepoList.add(project);
////                continue;
////            }
////        }
////        System.out.println(errorRepoList);
////    }
////
////    public void rollAllReposToSpecificCommit(int initialOrLatest) throws NotValidGitRepositoryException, GitRepositoryNotFoundException, IOException, GitAPIException {
////        List<String> projectList = readList(Configuration4InCon.JAVA_REPO_NAMES_FILE);
////        List<String> errorRepoList = new ArrayList<>();
////        for(String project:projectList){
////            String gitRoot = Configuration4InCon.JAVA_REPOS_PATH + project + "/.git";
////            GitRepository gitRepository = new GitRepository(gitRoot);
////            gitRepository.open();
////
////            try{
////                List<RevCommit> commits = gitRepository.getAllCommits(false);
////                Boolean isRollBack;
////                if(initialOrLatest==0){
////                    isRollBack = rollBackPreRevision(gitRoot, commits.get(0).getId());
////                }
////                else{
////                    isRollBack = rollBackPreRevision(gitRoot, commits.get(commits.size() - 1).getId());
////                }
////                System.out.println(isRollBack);
////
////
////            }
////            catch(org.eclipse.jgit.api.errors.CheckoutConflictException e){
////                e.printStackTrace();
////                errorRepoList.add(project);
////                continue;
////            }
////            catch(org.eclipse.jgit.api.errors.NoHeadException e1){
////                e1.printStackTrace();
////                errorRepoList.add(project);
////                continue;
////            }
////        }
////        System.out.println(errorRepoList);
////    }
////
////
////    public void rollRepoToSpecificCommit(){
////        //        String gitRoot = "/home1/michael/BadMethodName/ctakes/.git";
//////        String gitRoot = "/home1/michael/BadMethodName/JavaRepos/ofbiz/.git";
//////        GitRepository gitRepository = new GitRepository(gitRoot);
//////        gitRepository.open();
//////        List<RevCommit> commits = gitRepository.getAllCommits(false);
//////        System.out.println(commits.get(0).getId());
//////        System.out.println(commits.get(1).getId());
//////        System.out.println(commits.get(2).getId());
//////        System.out.println(commits.get(commits.size()-1).getId());
//////        System.out.println(commits.get(commits.size()-2).getId());
//////        Boolean isRollBack = rollBackPreRevision(gitRoot,commits.get(0).getId());
//////        System.out.println(isRollBack);
//////        int num = resetGitRepos.getEachRepoJavaFilesNumber("/home1/michael/BadMethodName/JavaRepos/ofbiz");
//////        System.out.println(num);
////    }
////    public void getReposJavaFilesNumber(){
////        ArrayList<Integer> numList = new ArrayList<>();
////        List<String> projectList = readList(Configuration4InCon.JAVA_REPO_NAMES_FILE);
////        HashMap<String,Integer> reposAndJavaFileNumber = new HashMap<>();
////
////        for(String project:projectList){
////            String projectPath = Configuration4InCon.JAVA_REPOS_PATH + project;
//////            System.out.println(projectPath);
////            numList.add(getEachRepoJavaFilesNumber(projectPath));
////            reposAndJavaFileNumber.put(project,getEachRepoJavaFilesNumber(projectPath));
////        }
//////        System.out.println(numList);
////        int cnt = 0;
////        for(int i:numList){
////            if(i==0){
////                cnt++;
////            }
////        }
////        System.out.println(numList.size());
////        System.out.println(cnt);
////        Map<String,Integer> reposAndJavaFileNumber1 = new MapSorter().sortByValueAscending(reposAndJavaFileNumber);
//////        for(Map.Entry<String,Integer> entry:reposAndJavaFileNumber1.entrySet()){
//////            System.out.println(entry.getKey()+":"+entry.getValue());
//////        }
////        List<String> inValidReposList = new ArrayList<>();
////        for(Map.Entry<String,Integer> entry:reposAndJavaFileNumber1.entrySet()){
////            if(entry.getValue()==0){
////                inValidReposList.add(entry.getKey());
////            }
////        }
////        //serialized object
//////        try {
////////            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("success1000MethodInfoAndBodyMap.txt"));
////////            ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream("methodInfoAndBodyMap.txt"));
////////            ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream("successMethodInfoAndBodyMap.txt"));
////////            ObjectOutputStream oos3 = new ObjectOutputStream(new FileOutputStream("successGetterMethodInfoAndBodyMap.txt"));
//////            ObjectOutputStream oos4 = new ObjectOutputStream(new FileOutputStream("/tmp/inValidReposList.txt"));
////////            oos.writeObject(success1000MethodInfoAndBodyMap);
////////            oos1.writeObject(methodInfoAndBodyMap);
////////            oos2.writeObject(successMethodInfoAndBodyMap);
////////            oos3.writeObject(successGetterMethodInfoAndBodyMap);
//////            oos4.writeObject(inValidReposList);
//////        } catch (IOException e) {
//////            e.printStackTrace();
//////        }
////
////
////
////    }
////    public void printReposJavaFilesNumber(){
////        List<String> projectList = readList(Configuration4InCon.JAVA_REPO_NAMES_FILE);
////        for(String project:projectList){
////            String projectPath = Configuration4InCon.JAVA_REPOS_PATH + project;
////            printEachRepoJavaFilesNumber(projectPath);
////        }
////    }
////    public static List<String> readList(String fileName) {
////        List<String> list = new ArrayList<>();
////        String content = FileHelper.readFile(fileName);
////        BufferedReader reader = new BufferedReader(new StringReader(content));
////        try {
////            String line = null;
////            while ((line = reader.readLine()) != null) {
////                list.add(line);
////            }
////            reader.close();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        return list;
////    }
////    private void printEachRepoJavaFilesNumber(String filePath){
////        List<File> list = FileHelper.getAllFiles(filePath,"java");
////        System.out.println(list.size());
////    }
////    private int getEachRepoJavaFilesNumber(String filePath){
////        List<File> list = FileHelper.getAllFiles(filePath,"java");
//////        System.out.println(list.size());
////        return list.size();
////    }
////    public static boolean rollBackPreRevision(String gitRoot, String revision) throws IOException, GitAPIException {
////
////        Git git = Git.open(new File(gitRoot));
////
////        Repository repository = git.getRepository();
////
////        RevWalk walk = new RevWalk(repository);
////        ObjectId objId = repository.resolve(revision);
////        RevCommit revCommit = walk.parseCommit(objId);
//////        String preVision = revCommit.getParent(0).getName();
////        String thisVision = revCommit.getName();
////        git.reset().setMode(ResetCommand.ResetType.HARD).setRef(thisVision).call();
////        repository.close();
////        return true;
////    }
//
//    public static boolean rollBackPreRevision(String gitRoot, ObjectId revision) throws IOException, GitAPIException {
//        try{
//            removeGitLock(gitRoot);
//            Git git = Git.open(new File(gitRoot));
//
//            Repository repository = git.getRepository();
//
//            RevWalk walk = new RevWalk(repository);
//            RevCommit revCommit = walk.parseCommit(revision);
////        String preVision = revCommit.getParent(0).getName();
//            String thisVision = revCommit.getName();
//            git.reset().setMode(ResetCommand.ResetType.HARD).setRef(thisVision).call();
//            repository.close();
//            return true;
//        }
//        catch(Exception e){
//            e.printStackTrace();
//            return false;
//        }
//
//    }
//
//    public static void pullMaster(String gitRoot) throws IOException, GitAPIException {
//        Git git = Git.open(new File(gitRoot));
//        git.pull()
//                .setRemote("origin")                      // 远程名称
//                .setRemoteBranchName("master")            // 远程分支
//                .setCredentialsProvider( new UsernamePasswordCredentialsProvider( "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIDbYSGQGoYHE9CUE82MV0G2agNnc7u/K7g1KAjLXmpOw wangtaiming@bit.edu.cn", "" ) )
//         .call();
//    }
//}
