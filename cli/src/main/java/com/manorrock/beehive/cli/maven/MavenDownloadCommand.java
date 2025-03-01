package com.manorrock.beehive.cli.maven;

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
    description = "Download a specific version of Maven"
)
public class MavenDownloadCommand implements Runnable {

    @Parameters(index = "0", description = "The version of Maven to download")
    private String version;

    @Override
    public void run() {
        String userHome = System.getProperty("user.home");
        Path targetPath = Path.of(userHome, ".beehive", "maven", version);

        try {
            Files.createDirectories(targetPath.getParent());
            URL url = new URL("https://example.com/maven/" + version + "/download");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (var inputStream = connection.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Maven " + version + " downloaded to " + targetPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to download Maven: " + e.getMessage());
        }
    }
}
