package com.manorrock.beehive.cli.maven;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;

@Command(
    name = "remove",
    description = "Remove a locally installed version of Maven"
)
public class MavenRemoveCommand implements Runnable {

    @Parameters(index = "0", description = "The version of Maven to remove")
    private String version;

    @Override
    public void run() {
        File mavenVersionDir = new File(System.getProperty("user.home") + "/.beehive/maven/" + version);
        if (mavenVersionDir.exists() && mavenVersionDir.isDirectory()) {
            deleteDirectory(mavenVersionDir);
            System.out.println("Maven version " + version + " removed successfully.");
        } else {
            System.out.println("Maven version " + version + " not found.");
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
