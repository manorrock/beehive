package com.manorrock.beehive.cli.java;

import picocli.CommandLine.Command;

@Command(
    name = "java",
    description = "Java related commands",
    subcommands = {
        JavaDownloadCommand.class,
        JavaListCommand.class,
        JavaRemoveCommand.class
    }
)
public class JavaCommand {
    
}