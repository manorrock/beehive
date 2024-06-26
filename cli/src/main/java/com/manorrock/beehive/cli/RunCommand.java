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

import java.io.File;
import java.lang.System.Logger;
import static java.lang.System.Logger.Level.INFO;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * The run command.
 *
 * <p>
 * This command will initiate a run either locally or remotely and echo the
 * output to the local terminal. It will use the build command to build if it
 * was not previously build.
 * </p>
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@Command(name = "run", mixinStandardHelpOptions = true)
public class RunCommand implements Callable<Integer> {

    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = System.getLogger(RunCommand.class.getName());

    /**
     * Stores the file/directory to run.
     */
    @Parameters(index = "0",
            description = "The file/directory to run. When not supplied the current directory will be used.")
    private List<String> file;

    /**
     * Stores the image name (if running on a container runtime).
     */
    @Option(names = "--image", description = "The image name.")
    private String imageName;

    /**
     * Stores the runtime.
     */
    @Option(names = "--runtime", description = "The execution runtime (e.g. Docker, Kubernetes).", defaultValue = "docker")
    private String runtime;

    /**
     * Stores the timeout.
     */
    @Option(names = "--timeout", description = "The timeout before aborting the run.")
    private long timeout = 600;

    /**
     * Stores the timeout unit.
     */
    @Option(names = "--timeout-unit", description = "The timeout unit (e.g. seconds, minutes, hours, days).")
    private String timeoutUnit = "seconds";
    
    /**
     * Call the command.
     *
     * @return 0 when completed successfully.
     * @throws Exception when a serious error occurs.
     */
    @Override
    public Integer call() throws Exception {
        if (file == null) {
            imageName = new File("").getCanonicalFile().getName();
        } else {
            imageName = file.get(0);
        }
        if (runtime != null) {
            switch (runtime.toLowerCase()) {
                case "docker":
                    return runOnDocker(imageName);
                default:
                    break;
            }
        }
        return 0;
    }

    /**
     * Run the given image locally using Docker.
     *
     * @param imageName the image name.
     */
    private int runOnDocker(String imageName) throws Exception {
        LOGGER.log(INFO, "Running " + imageName);
        ProcessBuilder builder = new ProcessBuilder();
        ArrayList<String> processArguments = new ArrayList<>();
        processArguments.add("docker");
        processArguments.add("run");
        processArguments.add(imageName);
        Process process = builder.command(processArguments).inheritIO().start();
        process.waitFor(timeout, TimeUnit.valueOf(timeoutUnit.toUpperCase()));
        return process.exitValue();
    }
}
