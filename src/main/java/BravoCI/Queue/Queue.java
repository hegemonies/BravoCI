package BravoCI.Queue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class Queue {
    private ArrayBlockingQueue<Package> queue = new ArrayBlockingQueue<Package>(100, true);
    private ServerSocket socket;
    private int port;

    public Queue(int port) {
        this.port = port;
    }

    public void up() {
        try {
            socket = new ServerSocket(port);
            System.out.println("Server upped");
            handler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handler() {
        while (true) {
            try {
                Socket client = socket.accept();

                System.out.println("New connection: " + client.toString());

                new Thread(new UserHandler(client, queue)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String... args) {
        int port = 9999;
        new Queue(port).up();
    }
}
