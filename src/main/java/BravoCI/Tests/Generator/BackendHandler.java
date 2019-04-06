package BravoCI.Tests.Generator;

import BravoCI.Queue.WrapperQueue;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.github.dockerjava.api.model.HostConfig.newHostConfig;

public class BackendHandler implements Runnable {
    private final DockerClient dockerClient = DockerClientBuilder.getInstance().build();
    private final String USERNAME = System.getenv("USERNAME");
    private final String pathLocaleFolder = String.format("/home/%s/repos", USERNAME);
    private final String pathLogs = String.format("/home/%s/results", USERNAME);
    private String username;
    private String repo;
    private File folderLocale;
    private File userFolder;
    private File logsFolder;
    private File userLogsFolder;
    private String localeVolume;
    private String shareVolume;

    @Override
    public void run() {
        String request;
        while (true) {
            request = WrapperQueue.getFromQueue();

            if (!request.equals("EMPTY")) {
                username = request.split(":")[0];
                repo = request.split(":")[1];

                System.out.println("thread [" + Thread.currentThread().getId() + "] getting task for "
                        + username + " " + repo);

                this.prepareToTesting();
                this.startTesting();

                System.out.println("thread [" + Thread.currentThread().getId() + "] finish task for "
                        + username + " " + repo);

                this.afterTesting();
            } else {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void prepareToTesting() {
        // set up folders
        folderLocale = new File(pathLocaleFolder);
        if (!folderLocale.exists()) {
            folderLocale.mkdir();
        }

        userFolder = new File(pathLocaleFolder + "/" + username);
        if (!userFolder.exists()) {
            userFolder.mkdir();
        }

        logsFolder = new File(pathLogs);
        if (!logsFolder.exists()) {
            logsFolder.mkdir();
        }

        userLogsFolder = new File(pathLogs + "/" + username);
        if (!userLogsFolder.exists()) {
            userLogsFolder.mkdir();
        }

        File userLogsFolder = new File(pathLogs + "/" + username + "/" + repo);
        if (!userLogsFolder.exists()) {
            userLogsFolder.mkdir();
        }

        localeVolume = pathLocaleFolder + "/" + username + "/" + repo;
        shareVolume = localeVolume +":/media";

        // cloning repo from github
        String url_repo = "https://github.com/" + username + "/" + repo;

        ProcessBuilder processBuilder = new ProcessBuilder("./clone_repo.sh",
                url_repo,
                pathLocaleFolder + "/" + username);

        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startTesting() {
        // generate bravo.sh from bravo.json
        Content content = null;
        try {
            content = Generator.readJSON(localeVolume);
            Generator.scriptGeneration(content, localeVolume);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String compiler = "bravo_" + Objects.requireNonNull(content).getConfig().getCompiler();

        // running tests inside the docker container

        boolean imageExist = false;
        String imageTag = null;
        List<Image> images = dockerClient.listImagesCmd()
                .withImageNameFilter(compiler)
                .exec();

        for (Image image : images) {
            String[] imageTags = image.getRepoTags();

            if (imageTags != null) {
                for (String cur_imageTag : imageTags) {
                    if (cur_imageTag.equals(compiler + ":latest")) {
                        imageExist = true;
                        imageTag = cur_imageTag;
                    }
                }
            }
        }

        if (imageExist) {
            // create a running container with bind mounts
            CreateContainerResponse container = dockerClient.createContainerCmd(imageTag)
                    .withHostConfig(newHostConfig().withBinds(Bind.parse(shareVolume)))
                    .exec();
            dockerClient.startContainerCmd(container.getId()).exec();

            boolean containerFinish = false;
            while (!containerFinish) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<Container> exited_containers = dockerClient.listContainersCmd()
                        .withShowAll(true)
                        .withStatusFilter(Arrays.asList("exited"))
                        .exec();

                for (Container _container : exited_containers) {
                    if (_container.getId().equals(container.getId())) {
                        containerFinish = true;
                    }
                }
            }
        } else {
            writeToLog("The necessary image does not exist");
        }

        File logs = new File(localeVolume + "/logs.txt");

        try (FileReader reader = new FileReader(logs)) {
            char[] buffer = new char[(int) logs.length()];
            reader.read(buffer);
            String raw = new String(buffer);
            System.out.println("logs: ");
            System.out.println(raw);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logs.renameTo(new File(pathLogs + "/" + username + "/" + repo + "/logs.txt"));

        // delete repo
        try {
            FileUtils.deleteDirectory(new File(localeVolume));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void afterTesting() {
        username = null;
        repo = null;
        folderLocale = null;
        userFolder = null;
        logsFolder = null;
        userLogsFolder = null;
        localeVolume = null;
        shareVolume = null;
    }

    private void writeToLog(String string) {
        File file = new File(Paths.get(".").toAbsolutePath().normalize().toString() +"/logs.txt");

        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(string);
            fileWriter.flush();

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
