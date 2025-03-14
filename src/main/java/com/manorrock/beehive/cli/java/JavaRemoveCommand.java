package com.manorrock.beehive.cli.java;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;

@Command(
    name = "remove",
    description = "Remove a locally installed version of Java"
)
public class JavaRemoveCommand implements Runnable {

    @Parameters(index = "0", description = "The version of Java to remove")
    private String version;

    @Override
    public void run() {
        File javaVersionDir = new File(System.getProperty("user.home") + "/.beehive/java/" + version); // Updated path
        if (javaVersionDir.exists() && javaVersionDir.isDirectory()) {
            deleteDirectory(javaVersionDir);
            System.out.println("Java version " + version + " removed successfully.");
        } else {
            System.out.println("Java version " + version + " not found.");
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}
