package com.manorrock.beehive.cli.gradle;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;

@Command(
    name = "remove",
    description = "Remove a locally installed version of Gradle"
)
public class GradleRemoveCommand implements Runnable {

    @Parameters(index = "0", description = "The version of Gradle to remove")
    private String version;

    @Override
    public void run() {
        File gradleVersionDir = new File(System.getProperty("user.home") + "/.beehive/gradle/" + version);
        if (gradleVersionDir.exists() && gradleVersionDir.isDirectory()) {
            deleteDirectory(gradleVersionDir);
            System.out.println("Gradle version " + version + " removed successfully.");
        } else {
            System.out.println("Gradle version " + version + " not found.");
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
