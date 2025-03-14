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
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

@Command(
    name = "download",
    description = "Download a specific version of Java"
)
public class JavaDownloadCommand implements Runnable {

    @Parameters(index = "0", description = "The version of Java to download")
    private String version;

    @Parameters(index = "1", description = "The system architecture (e.g., x64, aarch64)")
    private String architecture;

    @Override
    public void run() {
        String userHome = System.getProperty("user.home");
        Path targetPath = Path.of(userHome, ".beehive", "java", "adoptium-" + version + "-" + architecture);
        Path tempDir = Path.of(userHome, ".beehive", "tmp");

        try {
            Files.createDirectories(tempDir);
            Path tempFile = tempDir.resolve("java.tar.gz");

            Files.createDirectories(targetPath.getParent());
            URL url = new URL("https://api.adoptium.net/v3/binary/latest/" + version + "/ga/linux/" + architecture + "/jdk/hotspot/normal/adoptium");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (var inputStream = connection.getInputStream()) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                extractTarGz(tempFile, targetPath);
                System.out.println("Java adoptium " + version + " for " + architecture + " downloaded to " + targetPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to download Java: " + e.getMessage());
        }
    }

    private void extractTarGz(Path tarGzPath, Path targetDir) throws IOException {
        try (var inputStream = new java.util.zip.GZIPInputStream(Files.newInputStream(tarGzPath));
             var tarInputStream = new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(inputStream)) {
            org.apache.commons.compress.archivers.tar.TarArchiveEntry entry;
            while ((entry = tarInputStream.getNextTarEntry()) != null) {
                Path entryPath = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(tarInputStream, entryPath, StandardCopyOption.REPLACE_EXISTING);
                    setFilePermissions(entry, entryPath);
                    if ((entry.getMode() & 0100) != 0) { // Check if the executable bit is set
                        System.out.println("Extracted: " + entryPath + " with executable permissions");
                    }
                }
            }
        }

        // Check if a single directory was extracted
        try (var stream = Files.list(targetDir)) {
            var directories = stream.filter(Files::isDirectory).toList();
            if (directories.size() == 1) {
                Path singleDir = directories.get(0);
                try (var innerStream = Files.list(singleDir)) {
                    for (Path path : innerStream.toList()) {
                        Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(path);
                        Files.move(path, targetDir.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                        Files.setPosixFilePermissions(targetDir.resolve(path.getFileName()), permissions);
                        if (permissions.contains(PosixFilePermission.OWNER_EXECUTE)) {
                            System.out.println("Moved: " + path + " to " + targetDir.resolve(path.getFileName()) + " with executable permissions");
                        }
                    }
                }
                Files.delete(singleDir);
            }
        }
    }

    private void setFilePermissions(org.apache.commons.compress.archivers.tar.TarArchiveEntry entry, Path entryPath) {
        File file = entryPath.toFile();
        file.setReadable((entry.getMode() & 0400) != 0);
        file.setWritable((entry.getMode() & 0200) != 0);
        file.setExecutable((entry.getMode() & 0100) != 0);
    }

    private void setFilePermissions(Set<PosixFilePermission> permissions, Path entryPath) throws IOException {
        Files.setPosixFilePermissions(entryPath, permissions);
    }
}
