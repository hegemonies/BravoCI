package BravoCI;

import BravoCI.Queue.Queue;
import BravoCI.Tests.Generator.BackendHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Core {
    private final int port = 9999;

    Core() {
        new Thread(new Queue(port)).start();
        new Thread(new ZooKeeper(1)).start();
    }

    public static void main(String... args) {
        SpringApplication.run(Core.class);
    }
}