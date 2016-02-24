import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// System.out.println("Connected to chat room");
		// System.out.println("Enter user name: ");
		Socket socket = new Socket("cs380.codebank.xyz", 38002);
		Scanner input = new Scanner(System.in);
		String message = input.nextLine();
		String address = socket.getInetAddress().getHostAddress();
		OutputStream out = socket.getOutputStream();
		PrintStream stream = new PrintStream(out);
		stream.println(message);

		Runnable readData = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					InputStream is;
					try {
						is = socket.getInputStream();
						InputStreamReader isr = new InputStreamReader(is, "UTF-8");
						BufferedReader br = new BufferedReader(isr);
						String test = br.readLine();
						System.out.println(test);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		};

		Thread thread = new Thread(readData);
		// thread.run();
		 thread.start();

		while (true) {

			message = input.nextLine();
			stream.println(message);
			if(message.equals("quit"))
				break;
			// System.out.print(br.readLine())
		}

	}
}
