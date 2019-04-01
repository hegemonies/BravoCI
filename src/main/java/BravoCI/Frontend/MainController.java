package BravoCI.Frontend;

import BravoCI.Queue.WrapperQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @RequestMapping("/")
    public String hello() {
        return "hello";
    }

    @RequestMapping("/add")
    public String addUser(@RequestParam(name = "name", required = true) String name,
                          @RequestParam(name = "repo", required = true) String repository) {
        if (userRepository.findAll().contains(new User(name))) {
            User u = mongoOperations.findOne(Query.query(Criteria.where("name").is(name)), User.class);
            assert u != null;
            u.addRepository(repository);
            userRepository.save(u);
        } else {
            userRepository.save(new User(name, repository));
        }

        WrapperQueue.addToQueue(name, repository);

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
