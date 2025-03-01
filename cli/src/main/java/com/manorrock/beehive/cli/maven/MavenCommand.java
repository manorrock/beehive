package com.manorrock.beehive.cli.maven;

import picocli.CommandLine.Command;

@Command(
    name = "maven",
    description = "Maven related commands",
    subcommands = {
        MavenDownloadCommand.class,
        MavenListCommand.class,
        MavenRemoveCommand.class
    }
)
public class MavenCommand {
    
}
