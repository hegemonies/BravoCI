package BravoCI.Queue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

public class UserHandler implements Runnable {
    private final Socket socket;
    private ArrayBlockingQueue<Package> queue;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private PrintWriter out;
    private Scanner in;

    public UserHandler(Socket socket, ArrayBlockingQueue<Package> queue) {
        this.socket = socket;
        this.queue = queue;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            out = new PrintWriter(outputStream);
            in = new Scanner(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (in.hasNext()) {
                    String request = in.next();

                    System.out.println("queue request: " + request);

                    if (request.length() >= 3) {
                        String action = request.substring(0, 3);

                        System.out.println("action: " + action);

                        if (action.equals("GET")) {
                            int queueSize = queue.size();
                            System.out.println("size of queue: " + queueSize);

                            if (queueSize > 0) {
                                System.out.println("answer for GET: " + queue.peek().toString());

                                Package tmpPackage = queue.poll();
                                if (tmpPackage != null) {
                                    out.println(tmpPackage.toString());
                                    out.flush();
                                }
                            } else {
                                String answer = "EMPTY";
                                out.println(answer);
                                out.flush();
                            }
                        } else if (action.equals("SET")) {
                            String content = request.substring(4);

                            if (content.length() >= 3 && content.contains("/")) {
                                System.out.println("content: " + content);

                                String[] userInfo = content.split("/");
                                String userName = userInfo[0];
                                String userRepository = userInfo[1];
                                String commitInfo = userInfo[2];

                                queue.add(new Package(userName, userRepository, commitInfo));
                            }
                        }
                    }
                }
            } catch (Exception exc) {
                System.out.println(exc.getMessage());
            }
        }
    }
}
