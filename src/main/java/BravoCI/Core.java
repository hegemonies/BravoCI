package BravoCI;

import BravoCI.ConfiguratorTreeFolders.Configurator;
import BravoCI.Queue.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

@SpringBootApplication
@Import(AppConfig.class)
public class Core {
    private static String host;
    private static int port;
    private final int countAnimals = 1;

    Core() {
        new Thread(new Queue(port)).start();
        new Thread(new ZooKeeper(host, port, countAnimals)).start();
    }

    public static void main(String... args) {
        System.out.println("from Core" + Paths.get(".").toAbsolutePath().normalize().toString());
        try {
            Properties properties = new Properties();
            String propPath = new File(".").getAbsolutePath();
            propPath = propPath.substring(0, propPath.length() - 1) + "src/main/resources/queue.properties";
            properties.load(new FileReader(propPath));

            host = properties.getProperty("queue-host");
            String tmp_port = properties.getProperty("queue-port");
            port = Integer.parseInt(tmp_port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SpringApplication.run(Core.class);
    }
}