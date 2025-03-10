package com.manorrock.beehive.cli.node;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Command(
    name = "download",
    description = "Download a specific Node.js version"
)
public class NodeDownloadCommand implements Runnable {

    @Parameters(index = "0", description = "The version of Node.js to download")
    private String version;

    @Override
    public void run() {
        String userHome = System.getProperty("user.home");
        Path targetPath = Path.of(userHome, ".beehive", "node", "node-v" + version);
        Path tempDir = Path.of(userHome, ".beehive", "tmp");

        try {
            Files.createDirectories(tempDir);
            Path tempFile = tempDir.resolve("node.zip");

            Files.createDirectories(targetPath.getParent());
            URL url = new URL("https://nodejs.org/dist/v" + version + "/node-v" + version + "-win-x64.zip");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (var inputStream = connection.getInputStream()) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                extractZip(tempFile, targetPath);
                System.out.println("Node.js " + version + " downloaded to " + targetPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to download Node.js: " + e.getMessage());
        }
    }

    private void extractZip(Path zipPath, Path targetDir) throws IOException {
        try (var zipInputStream = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path entryPath = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zipInputStream, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipInputStream.closeEntry();
            }
        }
    }
}
