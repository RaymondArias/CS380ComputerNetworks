
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class InputClient {

    public static void main(String[] args) throws UnknownHostException, IOException {
        while (true) {

            try {
                Socket socket = new Socket("cs380.codebank.xyz", 38002);
                //System.out.println("Connected to server");
                InputStream in = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(in, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                

		if(in.available() != 0)
                {
//String input = br.read();
                    System.out.println(in.read());
                }

                /*
                 * while (true) { System.out.println(br.readLine()); }
                 */
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
