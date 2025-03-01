package com.manorrock.beehive.cli.gradle;

import picocli.CommandLine.Command;
import java.io.File;

@Command(
    name = "list",
    description = "List locally available versions of Gradle"
)
public class GradleListCommand implements Runnable {

    @Override
    public void run() {
        File gradleHome = new File(System.getProperty("user.home") + "/.beehive/gradle");
        File[] versions = gradleHome.listFiles();
        if (versions != null) {
            for (File version : versions) {
                if (version.isDirectory()) {
                    System.out.println(version.getName());
                }
            }
        } else {
            System.out.println("No Gradle versions found.");
        }
    }
}
