package StarterDockerImages;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String... args) {
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        // 1) взять запрос на тестирование из очереди
        // подразумевается что локальный реп уже обновлен
        // пример взятого запроса из БД
//        String requst = "hegemonies/LearningJava/TC";
        String request = "/home/dan/git/LearningJava/TC";

        // пример расшареной папки
//        String shareVolume = "~/repos/" + requst +":/home";
        String shareVolume = request +":/home";

        // 2) запуск генератора скрипта из json файла
        // не забудь про проверку на исключения
        // new GeneratorScripts().exec();

        // пример docker образа
        String UsingDockerImage = "dan/my-container-gradle:1";

        // создать docker контейнер
        CreateContainerResponse container = dockerClient.createContainerCmd(UsingDockerImage)
                .withBinds(Bind.parse(shareVolume)).exec();
        // запустить docker контейнер
        dockerClient.startContainerCmd(container.getId()).exec();

        // подождать завершения тестов todo: как?

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // забрать логи
        File file = new File("/home/dan/git/LearningJava/TC/logs.txt");

        System.out.println(file.getName());

        try (FileReader reader = new FileReader(file)) {
            char[] buffer = new char[(int)file.length()];
            reader.read(buffer);
            String raw = new String(buffer);

            System.out.println("logs:\n" + raw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
