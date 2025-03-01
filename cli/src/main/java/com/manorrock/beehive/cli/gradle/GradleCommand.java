package com.manorrock.beehive.cli.gradle;

import picocli.CommandLine.Command;

@Command(
    name = "gradle",
    description = "Gradle related commands",
    subcommands = {
        GradleDownloadCommand.class,
        GradleListCommand.class,
        GradleRemoveCommand.class
    }
)
public class GradleCommand {
    
}
