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

    @RequestMapping("/showAll")
    public List<User> showAll() {
        return userRepository.findAll();
    }
}
