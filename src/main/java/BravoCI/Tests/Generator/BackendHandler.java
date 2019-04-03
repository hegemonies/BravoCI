package BravoCI.Tests.Generator;

import BravoCI.Queue.WrapperQueue;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.List;

public class BackendHandler implements Runnable {
    DockerClient dockerClient = DockerClientBuilder.getInstance().build();
    String pathLocaleFolder = "/repos";
    String username;
    String repo;
    File folderLocale;
    File userFolder;
    String localeVolume;
    String shareVolume;

    @Override
    public void run() {
        String request;
        while (true) {
            request = WrapperQueue.getFromQueue();
            username = request.split(":")[0];
            repo = request.split(":")[1];

            if (!request.equals("EMPTY")) {
                this.prepareToTesting();
                this.startTesting();
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
        folderLocale = new File(pathLocaleFolder);
        if (!folderLocale.exists()) {
            folderLocale.mkdir();
        }

        userFolder = new File(pathLocaleFolder + "/" + username);
        if (!userFolder.exists()) {
            userFolder.mkdir();
        }

        localeVolume = pathLocaleFolder + "/" + username + "/" + repo;
        shareVolume = localeVolume +":/media";

        // todo need clone repo from github using scripts
    }

    private void startTesting() {
        Content content = null;
        try {
            content = Generator.readJSON(localeVolume);
            Generator.scriptGeneration(content, localeVolume);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String compiler = content.getConfig().getCompiler();

        boolean imageExist = false;
        String imageTag = null;
        List<Image> images = dockerClient.listImagesCmd()
                .withImageNameFilter(compiler)
                .exec(); // todo: check for performance

        for (Image image : images) { // todo need refactor
            if (image.getRepoTags().equals(compiler)) {
                imageExist = true;
                imageTag = image.getRepoTags()[0];
            }
        }

        if (imageExist) {
            CreateContainerResponse container = dockerClient.createContainerCmd(imageTag)
                    .withBinds(Bind.parse(shareVolume))
                    .exec();
            dockerClient.startContainerCmd(container.getId()).exec();

            List<Container> exited_containers = dockerClient.listContainersCmd()
                    .withShowAll(true)
                    .withStatusFilter("exited")
                    .exec();

            boolean containerFinish = false;
            while (!containerFinish) {
                for (Container _container : exited_containers) {
                    if (_container.getId().equals(container.getId())) {
                        containerFinish = true;
                    }
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            File logs = new File(localeVolume + "/logs.txt");
            logs.renameTo(new File("/results/" + username + "/" + repo + "/logs.txt"));
        } else {
            writeToLog("The necessary image does not exist");
        }
    }

    private void afterTesting() {
        username = null;
        repo = null;
        folderLocale = null;
        userFolder = null;
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
