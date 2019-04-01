package BravoCI.Frontend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WrapperQueue {
    private static final String host = "127.0.0.1";
    private static final int port = 9999;

    public static void addToQueue(String name, String repository) {
        try {
            Socket socket = new Socket(host, port);
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter out = new PrintWriter(outputStream);

            String request = "SET:" + name + "/" + repository;

            out.println(request);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFromQueue() {
        String answer = null;

        try {
            Socket socket = new Socket(host, port);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            PrintWriter out = new PrintWriter(outputStream);
            Scanner in = new Scanner(inputStream);

            out.println("GET");
            out.flush();

            answer = in.next();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return answer;
    }
}
