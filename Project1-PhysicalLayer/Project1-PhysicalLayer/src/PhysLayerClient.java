import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class PhysLayerClient {
	public static int LAST_BIT = -99;
	public static boolean signal = true;

	public static void main(String[] args) throws UnknownHostException, IOException {
		double preambleAverage = 0;
		byte[] decodedBytes = new byte[32];
		int index = 0;
		try (Socket socket = new Socket("cs380.codebank.xyz", 38001)) {
			System.out.println("Connected to server");
			InputStream input = socket.getInputStream();
			int inputVal = 0;
			for (int i = 0; i < 64; i++) 
			{
				inputVal = input.read();
				preambleAverage += inputVal;	
			}
			preambleAverage = preambleAverage / 64;
			System.out.println("Baseline established from preamble: "+preambleAverage);
			for (int i = 0; i < 32; i++) {
				int []readBits = new int[5];
				for(int j = 0; j < readBits.length; j++)
				{
					readBits[j] = input.read();
				}
				int decodedByte1 = bitConversion(NRZIdecoding(readBits, preambleAverage));
				for(int j = 0; j < readBits.length; j++)
				{
					readBits[j] = input.read();
					
				}
				int decodedByte2 = bitConversion(NRZIdecoding(readBits, preambleAverage));
				decodedByte1 = decodedByte1 << 4;
				int combinedByte = decodedByte1 ^ decodedByte2; 
				decodedBytes[index] = (byte) combinedByte;
				index++;
			}
			System.out.print("Received 32 bytes: ");
			for(int i = 0; i < decodedBytes.length; i++)
			{
				System.out.printf("%x",decodedBytes[i]);
			}
			OutputStream os = socket.getOutputStream();
			os.write(decodedBytes);

			if (input.read() == 1) {
				System.out.println("\nResponse Good");
			}
			else
			{
				System.out.println("\n Response Failed");
				
			}
			
		}
		System.out.println("Disconnected from server");

	}
	/**
	 * Reads signal encoded using NRZI
	 * and returns bits representation
	 * @param bits
	 * @param preamble
	 * @return
	 */
	public static int NRZIdecoding(int []bits, double preamble)
	{
		int retVal = 0;
		for(int i = 0; i < bits.length; i++)
		{
			if (PhysLayerClient.LAST_BIT == -99)
			{
				if(bits[i] > preamble)
				{
					signal = true;
					retVal = retVal << 1;
					retVal += 1;
					PhysLayerClient.LAST_BIT = 1;
				
				}
				else
				{
					signal = false;
					retVal = retVal << 1;
					PhysLayerClient.LAST_BIT = 0;
				}
			}
			else if (bits[i] > preamble)
			{
				if(signal)
				{
					signal = true;
					retVal = retVal << 1;
					PhysLayerClient.LAST_BIT = 0;
				}
				else
				{
					signal = true;
					retVal = retVal << 1;
					retVal += 1;
					
					PhysLayerClient.LAST_BIT = 1;
				}
			}
			else if(bits[i] < preamble)
			{
				if(signal)
				{
					signal = false;
					retVal = retVal << 1;
					retVal ++;
					PhysLayerClient.LAST_BIT = 1;
				}
				else
				{
					signal = false;
					retVal = retVal << 1;
					PhysLayerClient.LAST_BIT = 0;
				}
			}
			
		}
		return retVal;
	}
	/**
	 * Translates 5 bit values into 4 bits 
	 * using the 4b/5b table
	 * @param value
	 * @return
	 */
	public static int bitConversion(int value) {
		//System.out.println(Integer.toBinaryString(value));
		int retVal = 0b00000000;
		if (value == 0b00011110) {
			return 0b00000000;
		} else if (value == 0b00001001) {
			return 0b00000001;
		}
		else if (value == 0b00010100) {
			return 0b00000010;
		} 
		else if (value == 0b00010101) {
			return 0b00000011;
		} 
		else if (value == 0b00001010) {
			return 0b00000100;
		} 
		else if (value == 0b00001011) {
			return 0b00000101;
		} 
		else if (value == 0b00001110) {
			return 0b00000110;
		} 
		else if (value == 0b00001111) {
			return 0b00000111;
		} 
		else if (value == 0b00010010) {
			return 0b00001000;
		} 
		else if (value == 0b00010011) {
			return 0b00001001;
		} 
		else if (value == 0b00010110) {
			return 0b00001010;
		} 
		else if (value == 0b00010111) {
			return 0b00001011;
		} 
		else if (value == 0b00011010) {
			return 0b00001100;
		} 
		else if (value == 0b00011011) {
			return 0b00001101;
		} 
		else if (value == 0b00011100) {
			return 0b00001110;

		} else if (value == 0b00011101) {
			return 0b00001111;
		}

		return retVal;

	}
}
