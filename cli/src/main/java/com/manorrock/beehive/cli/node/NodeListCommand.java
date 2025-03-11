package com.manorrock.beehive.cli.node;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import picocli.CommandLine.Command;

@Command(
    name = "list",
    description = "List installed Node.js versions"
)
public class NodeListCommand implements Runnable {
    
    @Override
    public void run() {
        Path nodeDirectory = Paths.get(System.getProperty("user.home"), ".beehive", "node");
        
        if (!Files.exists(nodeDirectory)) {
            System.out.println("No Node.js versions installed");
            return;
        }
        
        try {
            boolean hasVersions = false;
            System.out.println("Installed Node.js versions:");
            
            try (Stream<Path> paths = Files.list(nodeDirectory)) {
                for (Path path : paths.toList()) {
                    if (Files.isDirectory(path)) {
                        hasVersions = true;
                        String versionName = path.getFileName().toString();
                        System.out.println("  " + versionName);
                    }
                }
            }
            
            if (!hasVersions) {
                System.out.println("  No Node.js versions installed");
            }
        } catch (Exception e) {
            System.err.println("Error listing Node.js versions: " + e.getMessage());
        }
    }
}
