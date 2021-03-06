package BravoCI.Queue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WrapperQueue {
    public static void addToQueue(String name, String repository, String commitInfo, Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter out = new PrintWriter(outputStream);

            String request = "SET:" + name + "/" + repository + "/" + commitInfo;

            out.println(request);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFromQueue(Socket socket) {
        String answer = null;

        try {
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
