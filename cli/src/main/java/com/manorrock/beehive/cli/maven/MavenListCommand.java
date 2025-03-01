package com.manorrock.beehive.cli.maven;

import picocli.CommandLine.Command;
import java.io.File;

@Command(
    name = "list",
    description = "List locally available versions of Maven"
)
public class MavenListCommand implements Runnable {

    @Override
    public void run() {
        File mavenHome = new File(System.getProperty("user.home") + "/.beehive/maven");
        File[] versions = mavenHome.listFiles();
        if (versions != null) {
            for (File version : versions) {
                if (version.isDirectory()) {
                    System.out.println(version.getName());
                }
            }
        } else {
            System.out.println("No Maven versions found.");
        }
    }
}
