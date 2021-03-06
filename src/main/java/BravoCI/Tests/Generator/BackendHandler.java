package BravoCI.Tests.Generator;

import BravoCI.AppConfig;
import BravoCI.ConfiguratorTreeFolders.Configurator;
import BravoCI.Frontend.CommitInfo;
import BravoCI.Frontend.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import BravoCI.Queue.WrapperQueue;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;
import org.apache.commons.io.FileUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.github.dockerjava.api.model.HostConfig.newHostConfig;

@Component
public class BackendHandler implements Runnable {
    private final DockerClient dockerClient = DockerClientBuilder.getInstance().build();
    private Socket socket = null;
    private String username;
    private String repo;
    private String commitInfo;
    private String date;
    private String localeVolumeDocker;
    private String shareVolumeDocker;
    private String logsFolder;
    private Configurator configurator = new Configurator();
    private String pathToTargetFile;
    private MongoOperations mongoOperations;

    public BackendHandler() {}

    public BackendHandler(String host, int port) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        mongoOperations = context.getBean(MongoOperations.class);

        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String request;
        while (true) {
            try {
                request = WrapperQueue.getFromQueue(this.socket);

                if (!request.equals("EMPTY")) {
                    String[] tmps = request.split(":");
                    username = tmps[0];
                    repo = tmps[1];
                    commitInfo = tmps[2];
                    date = tmps[3];

                    System.out.println("thread [" + Thread.currentThread().getId() + "] getting task for "
                            + username + " " + repo + " " + commitInfo + " " + date);

                    this.prepareToTesting();
                    this.startTesting();

                    System.out.println("thread [" + Thread.currentThread().getId() + "] finish task for "
                            + username + " " + repo + " " + commitInfo + " " + date);

                    this.afterTesting();
                } else {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    private void prepareToTesting() {
//        configurator.configureUserFolders(username, repo, commitInfo);
        localeVolumeDocker = configurator.getReposFolder() + "/" + username + "/" + repo;
        shareVolumeDocker = localeVolumeDocker +":/media";
        logsFolder = configurator.getResultsFolder() + "/" + username + "/" + repo + "/" + commitInfo + "/" + date;
    }

    private void startTesting() {
        try {
            // generate bravo.sh from bravo.json
            Content content = null;
            try {
                content = Generator.readJSON(localeVolumeDocker);
                Generator.scriptGeneration(content, localeVolumeDocker);
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
                        .withHostConfig(newHostConfig().withBinds(Bind.parse(shareVolumeDocker)))
			            .withUser("1013:1013")
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

            // move logs
            File logs = new File(localeVolumeDocker + "/logs.txt");
            String resultPathLogsFile = logsFolder + "/logs.txt";
            logs.renameTo(new File(resultPathLogsFile));

            String targetFile = content.getConfig().getTargetFile();
            setPathToTargetFile(targetFile);

            // move targer file
            File _targerFile = new File(pathToTargetFile);
            String[] tmps = pathToTargetFile.split("/");
            String targerFilename = tmps[tmps.length - 1];
            String resultPathTargerFile = logsFolder + "/" + targerFilename;
            _targerFile.renameTo(new File(resultPathTargerFile));

            User u = mongoOperations.findOne(Query.query(Criteria.where("name").is(username)), User.class);
            assert u != null;
            CommitInfo new_commitInfo = u.getCommit(repo, commitInfo.split(":")[0]);
            String result = resultPathLogsFile
                    + ":"
                    + resultPathTargerFile;
            new_commitInfo.setResult(result);
            new_commitInfo.setCheck(true);
            u.changeCommit(repo, new_commitInfo);

            mongoOperations.save(u);
        } finally {
            // delete repo
            // try {
            //     FileUtils.deleteDirectory(new File(localeVolumeDocker));
            // } catch (IOException e) {
            //     e.printStackTrace();
            // }
        }
    }

    private void afterTesting() {
        username = null;
        repo = null;
    }

    private void writeToLog(String string) {
        File file = new File(logsFolder + "/logs.txt");

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

    private void processFilesFromFolder(File folder, String filename) {
        File[] folderEntries = folder.listFiles();

        assert folderEntries != null;

        for (File entry : folderEntries)  {
            if (entry.isDirectory())  {
                processFilesFromFolder(entry, filename);
            } else {
                if (entry.getName().contains(filename)) {
                    try {
                        this.pathToTargetFile = entry.getCanonicalPath();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void setPathToTargetFile(String filename) {
        System.out.println("LOOK HERE: " + configurator.getReposFolder() + "/" + username + "/" + repo);
        File thisFolder = new File(configurator.getReposFolder() + "/" + username + "/" + repo);
        processFilesFromFolder(thisFolder, filename);
        System.out.println("!!!!!filename: " + filename);
        System.out.println("!!!!!pathToTargetFile: " + pathToTargetFile);
    }
}
