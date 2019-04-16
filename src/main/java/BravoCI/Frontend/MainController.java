package BravoCI.Frontend;

import BravoCI.ConfiguratorTreeFolders.Configurator;
import BravoCI.Queue.WrapperQueue;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@RestController
public class MainController {
    @Autowired
    private MongoOperations mongoOperations;

    private Configurator configurator = new Configurator();
    private Socket socket;

    public MainController() {
        try {
            Properties properties = new Properties();
            String propPath = new File(".").getAbsolutePath();
            propPath = propPath.substring(0, propPath.length() - 1) + "src/main/resources/queue.properties";
            properties.load(new FileReader(propPath));

            String host = properties.getProperty("queue-host");
            String tmp_port = properties.getProperty("queue-port");
            int port = Integer.parseInt(tmp_port);

            socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/add")
    public String addUser(@RequestParam(name = "name", required = true) String name,
                          @RequestParam(name = "repo", required = true) String repository) {
        String reposFolder = configurator.getReposFolder() + "/" + name + "/" + repository;
        String date = new Date().toString().replace(" ", "_").replace(":", "-");
        Git git = null;

        try {
            git = Git.cloneRepository()
                .setURI("https://github.com/" + name + "/" + repository + ".git")
                .setDirectory(new File(reposFolder))
                .call();

            Iterable<RevCommit> logs = git.log().call();
            String lastCommitName = logs.iterator().next().getName();

            configurator.configureUserFolders(name, repository, lastCommitName, date);

            if (mongoOperations.findAll(User.class).contains(new User(name))) {
                User u = mongoOperations.findOne(Query.query(Criteria.where("name").is(name)), User.class);
                assert u != null;
                u.addCommit(repository,
                        new CommitInfo(lastCommitName,
                                date,
                        "",
                        false));
                u.addRepository(repository);
                mongoOperations.save(u);
            } else {
                User u = new User(name, repository);
                u.addCommit(repository, new CommitInfo(lastCommitName,
                        date,
                        "",
                        false));
                mongoOperations.save(u);
            }

            WrapperQueue.addToQueue(name,
                    repository,
                    lastCommitName + ":" + date,
                    socket);

        } catch (GitAPIException exception) {
            new File(reposFolder).delete();
            exception.printStackTrace();
            return "Invalid data: Name or Repository";
        } finally {
            assert git != null;
            git.close();
        }

        return "Your request is being processed";
    }

    @RequestMapping("/showAll")
    public List<User> showAll() {
        return mongoOperations.findAll(User.class);
    }

    @RequestMapping("/search")
    public User search(@RequestParam(name = "name", required = true) String name) {
        return mongoOperations.findOne(Query.query(Criteria.where("name").is(name)), User.class);
    }

    @RequestMapping("/searchrepo")
    public List<User> searchRepository(@RequestParam(name = "name", required = true) String name) {
        return mongoOperations.find(Query.query(Criteria.where("repositories.name").is(name)), User.class);
    }
}
