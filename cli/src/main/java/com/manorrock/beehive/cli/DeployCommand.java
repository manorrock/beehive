/*
 *  Copyright (c) 2002-2024, Manorrock.com. All Rights Reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      1. Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *
 *      3. Neither the name of the copyright holder nor the names of its 
 *         contributors may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package com.manorrock.beehive.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * The deploy command.
 *
 * <p>
 * This command will deploy the image to a target runtime. It will try to use
 * sensible defaults. If it fails to determine the defaults it will suggest a
 * course of action.
 * </p>
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@Command(name = "deploy", mixinStandardHelpOptions = true)
public class DeployCommand implements Callable<Integer> {

    /**
     * Stores the file/directory to execute the command against.
     */
    @Parameters(index = "0",
            description = "The file/directory to build. When not supplied the"
            + "current directory will be used.")
    protected List<String> file;
    
    /**
     * Stores the application name.
     */
    @Option(names = "--name", description = "The application name")
    protected String name;

    /**
     * Stores the verbose flag.
     */
    @Option(names = {"-v", "--verbose"}, description = "Output more verbose.")
    protected boolean verbose = false;    
    /**
     * Stores the image name.
     */
    @Option(names = "--image", description = "The image name.")
    private String imageName;

    /**
     * Stores the runtime.
     */
    @Option(names = "--runtime", description = "The execution runtime (e.g. Docker, Kubernetes).", defaultValue = "docker")
    private String runtime;

    /**
     * Stores the skip build.
     */
    @Option(names = {"--skip-build"}, description = "Skip the build")
    private boolean skipBuild = false;

    /**
     * Stores the timeout.
     */
    @Option(names = "--timeout", description = "The timeout before aborting the deploy.")
    private long timeout = 600;

    /**
     * Stores the timeout unit.
     */
    @Option(names = "--timeout-unit", description = "The timeout unit (e.g. seconds, minutes, hours, days).")
    private String timeoutUnit = "seconds";

    /**
     * Stores the working directory.
     */
    private File workingDirectory;

    /**
     * Call the command.
     *
     * @return 0 when completed successfully.
     * @throws Exception when a serious error occurs.
     */
    @Override
    public Integer call() throws Exception {
        if (imageName != null) {
        } else if (file == null) {
            imageName = new File("").getCanonicalFile().getName();
        } else {
            imageName = new File(file.get(0)).getCanonicalFile().getName();
        }
        if (file != null) {
            workingDirectory = new File(file.get(0));
        }
        determineName();
        if (runtime != null) {
            switch (runtime.toLowerCase()) {
                case "docker":
                    return deployOnDocker(imageName);
                default:
                    break;
            }
        }
        return 0;
    }

    /**
     * Deploy the given image locally using Docker.
     *
     * @param imageName the image name.
     */
    private int deployOnDocker(String imageName) throws Exception {

        if (!skipBuild) {
            DockerBuilder docker = new DockerBuilder();
            docker.setImageName(imageName);
            docker.setTimeout(timeout);
            docker.setTimeoutUnit(timeoutUnit);
            if (workingDirectory != null) {
                docker.setWorkingDirectory(workingDirectory);
            }
            docker.build();
        }

        System.out.println("[Deployer] Starting deployment of '" + imageName + "' image");

        ProcessBuilder builder = new ProcessBuilder();
        ArrayList<String> processArguments = new ArrayList<>();
        processArguments.add("docker");
        processArguments.add("run");
        processArguments.add("--rm");
        processArguments.add("--name");
        processArguments.add(name);
        processArguments.add("-d");
        processArguments.add("-p");
        processArguments.add("8080:8080");
        processArguments.add(imageName);
        if (verbose) {
            builder = builder.inheritIO();
        }
        Process process = builder.command(processArguments).start();
        process.waitFor(timeout, TimeUnit.valueOf(timeoutUnit.toUpperCase()));

        if (process.exitValue() == 0) {
            System.out.println("[Deployer] Application is available on http://localhost:8080");
        }

        return process.exitValue();
    }

    /**
     * Determine the name.
     *
     * @throws Exception when a serious error occurs.
     */
    protected void determineName() throws Exception {
        if (name == null && file == null) {
            name = new File("").getCanonicalFile().getName();
        } else if (name == null) {
            name = new File(file.get(0)).getCanonicalFile().getName();
        }
        System.out.println("[Common] Determined application name to be '" + name + "'");
    }
    
}
