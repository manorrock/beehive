package com.manorrock.beehive.cli.node;

import picocli.CommandLine.Command;

@Command(
    name = "node",
    description = "Node.js related commands",
    subcommands = {
        NodeDownloadCommand.class,
        NodeListCommand.class,
        NodeRemoveCommand.class
    }
)
public class NodeCommand {
    
}
