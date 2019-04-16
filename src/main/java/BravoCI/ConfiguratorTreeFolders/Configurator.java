package BravoCI.ConfiguratorTreeFolders;


import java.io.File;
import java.nio.file.Paths;
import java.util.Date;

public class Configurator {
    private String username = System.getenv("USERNAME");
    private final String rootFolder = Paths.get(".").toAbsolutePath().normalize().toString();
    private final String reposFolder = String.format("%s/repos", rootFolder);
    private final String resultsFolder = String.format("%s/results", rootFolder);

    public Configurator() {
        System.out.println("Config:");
        System.out.println("root folder: " + rootFolder);
        System.out.println("repos folder: " + reposFolder);
        System.out.println("results folder: " + resultsFolder);

        if (username == null) {
            username = System.getenv("USER");
        }

        File _reposFolder = new File(reposFolder);
        if (!_reposFolder.exists()) {
            _reposFolder.mkdir();
        }

        File logsFolder = new File(resultsFolder);
        if (!logsFolder.exists()) {
            logsFolder.mkdir();
        }
    }

    public void configureUserFolders(String name, String repo, String commitName, String date) {
        File userFolder = new File(reposFolder + "/" + name);
        if (!userFolder.exists()) {
            userFolder.mkdir();
        }

        File userLogsFolder = new File(resultsFolder + "/" + name);
        if (!userLogsFolder.exists()) {
            userLogsFolder.mkdir();
        }

        File logsFolder = new File(resultsFolder+ "/" + name + "/" + repo);
        if (!logsFolder.exists()) {
            logsFolder.mkdir();
        }

        File concreteLogsFolder = new File( resultsFolder+ "/"
                + name + "/"
                + repo + "/"
                + commitName);
        if (!concreteLogsFolder.exists()) {
            concreteLogsFolder.mkdir();
        }

        File concrLogsFolder = new File(resultsFolder+ "/"
                + name + "/"
                + repo + "/"
                + commitName + "/"
                + date);
        if (!concrLogsFolder.exists()) {
            concrLogsFolder.mkdir();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public String getReposFolder() {
        return reposFolder;
    }

    public String getResultsFolder() {
        return resultsFolder;
    }
}
