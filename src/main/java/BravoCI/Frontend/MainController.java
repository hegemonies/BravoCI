package BravoCI.Frontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String hello() {
        return "hello";
    }

    @RequestMapping("/add")
    public String addUser(@RequestParam(name = "Name", required = true) String Name) {
        userRepository.save(new User(Name));

        return "OK";
    }

    @RequestMapping("/addCommit")
    public String addCommit(@RequestParam(name = "Name", required = true) String Name,
                            @RequestParam(name = "Commit", required = true) String Commit) {
        User user = search(Name);

        if (user == null) {
            return "bad";
        }

        user.addCommit(new CommitInfo(Commit, null, false));

        userRepository.save(user);

        return "good";
    }

    @RequestMapping("/showAll")
    public List<User> showAll() {
        return userRepository.findAll();
    }

    private User search(String name) {
        for (User user : userRepository.findAll()) {
            if (user.getName().equals(name)) {
                return user;
            }
        }

        return null;
    }
}
