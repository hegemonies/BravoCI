package BravoCI;

import BravoCI.Tests.Generator.BackendHandler;

public class ZooKeeper implements Runnable {
    private int capacity;

    public ZooKeeper(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void run() {
        for (int i = 0; i < capacity; i++) {
            new Thread(new BackendHandler()).start();
        }

    }
}
