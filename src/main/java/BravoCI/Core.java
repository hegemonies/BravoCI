package BravoCI;

import BravoCI.Queue.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Core {
    public static void main(String... args) {
        int port = 9999;
        new Queue(port).up();

        SpringApplication.run(Core.class);
    }
}