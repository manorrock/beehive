package com.manorrock.beehive.cli.java;

import picocli.CommandLine.Command;
import java.io.File;

@Command(
    name = "list",
    description = "List locally available versions of Java"
)
public class JavaListCommand implements Runnable {

    @Override
    public void run() {
        File javaHome = new File(System.getProperty("java.home"));
        File[] vendors = javaHome.listFiles();
        if (vendors != null) {
            for (File vendor : vendors) {
                if (vendor.isDirectory()) {
                    File[] versions = vendor.listFiles();
                    if (versions != null) {
                        for (File version : versions) {
                            if (version.isDirectory()) {
                                System.out.println(vendor.getName() + " - " + version.getName());
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("No Java versions found.");
        }
    }
}
