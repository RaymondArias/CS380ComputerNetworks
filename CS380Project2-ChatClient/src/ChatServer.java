import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
	public static ArrayList<String> lines = new ArrayList<>();
	public static int index = 0;

	public static void main(String[] args) throws IOException {
		lines.add("Test");
		

		try (ServerSocket server = new ServerSocket(2000)) {
			System.out.println(server.getInetAddress());
			while (true) {
				Socket socket = server.accept();
				System.out.println(socket.getLocalSocketAddress());
				Runnable newSocket = new Runnable() {
					

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							InputStream is = socket.getInputStream();
							InputStreamReader isr = new InputStreamReader(is, "UTF-8");
							BufferedReader br = new BufferedReader(isr);
							String test = br.readLine();
							//lines.add(br.readLine());
							
							
							
							OutputStream os = socket.getOutputStream();
							PrintStream out = new PrintStream(os);
							out.println(test);
							socket.close();
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						

					}

				};
				Thread newThread = new Thread(newSocket);
				newThread.start();
			}

		}
	}

}
