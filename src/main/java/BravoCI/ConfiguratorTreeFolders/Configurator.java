package BravoCI.ConfiguratorTreeFolders;


import java.io.File;
import java.util.Date;

public class Configurator {
    private final String username = System.getenv("USERNAME");
    private final String rootFolder = String.format("/home/%s/bravoci", username);
    private final String reposFolder = String.format("%s/repos", rootFolder);
    private final String resultsFolder = String.format("%s/results", rootFolder);

    public Configurator() {
        File _reposFolder = new File(reposFolder);
        if (!_reposFolder.exists()) {
            _reposFolder.mkdir();
        }

        File logsFolder = new File(resultsFolder);
        if (!logsFolder.exists()) {
            logsFolder.mkdir();
        }
    }

    public void configureUserFolders(String name, String repo, String commitName) {
        File userFolder = new File(reposFolder + "/" + name);
        if (!userFolder.exists()) {
            userFolder.mkdir();
        }

        File userLogsFolder = new File(resultsFolder + "/" + name);
        if (!userLogsFolder.exists()) {
            userLogsFolder.mkdir();
        }

        File logsFolder = new File(resultsFolder+ "/"
                + name + "/"
                + repo + "/"
                + commitName);
        if (!logsFolder.exists()) {
            logsFolder.mkdir();
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
