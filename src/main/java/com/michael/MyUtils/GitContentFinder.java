package com.michael.MyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GitContentFinder {
    public static String findFirstCommit(String filePath, String targetContent) {
        String path_bath = "D:/Git/Git/git-bash.exe";
        String path_script = "cd E:/CodeAdaptation/Zhang/dataset/Top100Repositories/";
        String path_output = "E:/CodeAdaptation/CCAHelper/output/result.txt";
        ProcessBuilder processBuilder = new ProcessBuilder();
//        String command = path_script + " && ./find-first-content.sh \"" + filePath + "\" \"" + targetContent + "\" > \"" + path_output + "\"";
        String command = path_script + " && ./find-first-content.sh " + filePath + " " + targetContent + " > " + path_output + " ; sleep 10";
        processBuilder.command(path_bath, "-c", command);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                content.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("error: " + exitCode);
            }
            return content.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}