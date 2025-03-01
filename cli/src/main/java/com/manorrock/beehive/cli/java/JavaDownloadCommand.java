package com.manorrock.beehive.cli.java;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.net.URL;
import java.net.HttpURLConnection;

@Command(
    name = "download",
    description = "Download a specific version of Java"
)
public class JavaDownloadCommand implements Runnable {

    @Parameters(index = "0", description = "The vendor of the Java version")
    private String vendor;

    @Parameters(index = "1", description = "The version of Java to download")
    private String version;

    @Override
    public void run() {
        String userHome = System.getProperty("user.home");
        Path targetPath = Path.of(userHome, ".beehive", "java", vendor + "-" + version);

        try {
            Files.createDirectories(targetPath.getParent());
            URL url = new URL("https://example.com/java/" + vendor + "/" + version + "/download");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (var inputStream = connection.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Java " + vendor + " " + version + " downloaded to " + targetPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to download Java: " + e.getMessage());
        }
    }
}
