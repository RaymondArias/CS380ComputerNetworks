
import java.net.Socket;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoServer {

    public static void main(String[] args) throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(22222)) {

            while (true) {
                Socket socket = serverSocket.accept();

                Runnable newTask = () -> {

                    try {
                        InputStream is = null;

                        String address = socket.getInetAddress().getHostAddress();
                        System.out.printf("Client connected: %s%n", address);
                        is = socket.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                        BufferedReader br = new BufferedReader(isr);
                        String echo = br.readLine();
                        OutputStream os = socket.getOutputStream();
                        PrintStream out = new PrintStream(os);
                        out.printf(echo);
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                };
                Thread newThread = new Thread(newTask);
                newThread.start();

            }
        }
    }

}
