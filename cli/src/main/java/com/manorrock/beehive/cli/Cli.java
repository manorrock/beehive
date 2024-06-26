/*
 * Copyright (c) 2002-2024, Manorrock.com. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.manorrock.beehive.cli;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * The Beehive CLI.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@Command(name = "bee", mixinStandardHelpOptions = true,
        subcommands = {
            RunCommand.class
        },
        versionProvider = CliVersionProvider.class)
public class Cli implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        return 0;
    }

    public static void main(String[] arguments) {
        if (arguments.length > 0 && arguments[0].equals("run")) {
            System.exit(new CommandLine(new Cli()).execute(arguments));
        } else if (arguments.length > 0 && arguments[0].equals("build")) {
            System.out.println("Build:)");
        } else if (arguments.length > 0 && arguments[0].equals("deploy")) {
            System.out.println("Deploy;)");
        } else if (arguments.length > 0 && arguments[0].equals("undeploy")) {
            System.out.println("Undeploy :(");
        }
    }
}
