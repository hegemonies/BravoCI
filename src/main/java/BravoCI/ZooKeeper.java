package BravoCI;

import BravoCI.ConfiguratorTreeFolders.Configurator;
import BravoCI.Tests.Generator.BackendHandler;

public class ZooKeeper implements Runnable {
    private String host;
    private int port;
    private int capacity;

    public ZooKeeper(String host, int port, int capacity) {
        this.host = host;
        this.port = port;
        this.capacity = capacity;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < capacity; i++) {
            new Thread(new BackendHandler(host, port)).start();
        }

    }
}
