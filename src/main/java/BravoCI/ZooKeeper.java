package BravoCI;

import BravoCI.Tests.Generator.BackendHandler;

public class ZooKeeper {
    private int capacity;

    public ZooKeeper(int capacity) {
        this.capacity = capacity;
    }

    public void start() {
        for (int i = 0; i < capacity; i++) {
            new Thread(new BackendHandler()).start();
        }
    }
}
