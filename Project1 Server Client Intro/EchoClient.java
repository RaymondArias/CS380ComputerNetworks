
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public final class EchoClient {

    public static void main(String[] args) throws Exception {
        Scanner userInput = new Scanner(System.in);
        String message;
        while (true) {
            try (Socket socket = new Socket("localhost", 22222)) {

                message = userInput.nextLine();
                OutputStream os = socket.getOutputStream();
                PrintStream out = new PrintStream(os);
                System.out.println("Client >> " +message);
                out.println(message);
                
                
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                System.out.println("Server >> " + br.readLine());

            }
        }
    }
}
