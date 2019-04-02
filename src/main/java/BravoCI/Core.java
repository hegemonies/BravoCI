package BravoCI;

import BravoCI.Queue.Queue;
import BravoCI.Tests.Generator.BackendHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Core {
    public static void main(String... args) {
        int port = 9999;
        new Thread(new Queue(port)).start();
        new Thread(new BackendHandler()).start();

        SpringApplication.run(Core.class);
    }
}