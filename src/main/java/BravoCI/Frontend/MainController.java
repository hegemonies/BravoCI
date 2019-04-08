package BravoCI.Frontend;

import BravoCI.Queue.WrapperQueue;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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
import java.util.List;
import java.util.Properties;

@RestController
public class MainController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoOperations mongoOperations;

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

    @RequestMapping("/")
    public String hello() {
        return "hello";
    }

    @RequestMapping("/add")
    public String addUser(@RequestParam(name = "name", required = true) String name,
                          @RequestParam(name = "repo", required = true) String repository) {
        try {
            Git git = Git.cloneRepository()
                    .setURI("https://github.com/" + name + "/" + repository + ".git")
                    .setDirectory(new File("/home/sandra/repos/" + name + "/" +repository + "/"))
                    .call();

            if (userRepository.findAll().contains(new User(name))) {
                User u = mongoOperations.findOne(Query.query(Criteria.where("name").is(name)), User.class);
                assert u != null;
                u.addRepository(repository);
                userRepository.save(u);
            } else {
                userRepository.save(new User(name, repository));
            }

            WrapperQueue.addToQueue(name, repository, socket);

            git.close();
        } catch (GitAPIException exception) {
            System.out.println(exception.getMessage());
            return "Invalid data: Name or Repository";
        }

        return "OK";
    }

    @RequestMapping("/showAll")
    public List<User> showAll() {
        return userRepository.findAll();
    }

    @RequestMapping("/search")
    private User search(@RequestParam(name = "name", required = true) String name) {
        return mongoOperations.findOne(Query.query(Criteria.where("name").is(name)), User.class);
    }
}
