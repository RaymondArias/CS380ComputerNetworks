import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

public class UdpClient {
	public static short checkSum(byte [] buf, int count)
	{
		long sum = 0;
		int counter = 0;
		while(count-- != 0)
		{
			int num = ((buf[counter] & 0xff) << 8);
			num = (num ^ (buf[counter + 1] & 0xff));
			counter += 2;
			sum += num;
			
			if((sum &0xffff0000) != 0)
			{
				sum &= 0xffff;
				sum ++;
			}
		}
		return (short) ~(sum & 0xffff);
	}
	public static byte[] ip4Header()
	{
		byte []byteValue = new byte[19];
		byteValue[0] = 0x00; //TOS
		byteValue[1] = 0x00; //Length part 1
		byteValue[2] = 0x18; //Length part 2
		byteValue[3] = 0x00; //Indent part1
		byteValue[4] = 0x00; //Indent part2
		byteValue[5] = 0x40; //flag and offsetpart1
		byteValue[6] = 0x00; //offset part2
		byteValue[7] = 0x32; //TTL
		byteValue[8] = 0x11; //UDP Protocol
		byteValue[9] = 0x00; //checksum
		byteValue[10] = 0x00; //check sum
		byteValue[11] = 0x0a; //source IP
		byteValue[12] = 0x6e; //source Ip
		byteValue[13] = 0x24; //source IP
		byteValue[14] = 0x13; //source IP
		byteValue[15] = 0x34; //dest ip
		byteValue[16] = 0x21; //dest ip
		byteValue[17] = (byte) 0x83; //dest ip
		byteValue[18] = 0x10; //dest ip
		//System.out.printf("%x",checkSum);
		
		return byteValue;
		
	}
	public static byte []updHeader(int port, int dataSize)
	{
		int length = 8 + dataSize;
		
		byte[] packet = null;
		byte[] randomData = new byte[dataSize];
		packet = new byte[8 + dataSize];
		packet[0] = (byte) (port >> 8); //srcPort
		packet[1] = (byte) (port & 0x00ff); //srcPort
		packet[2] = (byte) (port >> 8); //dstPort
		packet[3] = (byte) (port & 0x00ff); // dstPort
		packet[4] = (byte) (length >> 8); //length
		packet[5] = (byte)(length); //length
		packet[6] = 0x00; //checksum
		packet[7] = 0x00; //checksum
		Random r = new Random();
		r.nextBytes(randomData);
		for(int i = 0; i < dataSize; i++)
		{
			packet[i + 8] = randomData[i];
		}
		byte []pseudoHeader = new byte[packet.length + 12];
		pseudoHeader[0] = 0x0a; //source IP
		pseudoHeader[1] = 0x6e; //source Ip
		pseudoHeader[2] = 0x24; //source IP
		pseudoHeader[3] = 0x13; //source IP
		pseudoHeader[4] = 0x34; //dest ip
		pseudoHeader[5] = 0x21; //dest ip
		pseudoHeader[6] = (byte) 0x83; //dest ip
		pseudoHeader[7] = 0x10; //dest ip
		pseudoHeader[8] = 0x00; 
		pseudoHeader[9] = 0x11; //udp
		pseudoHeader[10] = packet[4];
		pseudoHeader[11] = packet[5];
		for(int i = 0; i < packet.length; i++)
		{
			pseudoHeader[i + 12] = packet[i];
		}
		short checkSum = checkSum(pseudoHeader, pseudoHeader.length/2);
		byte firstBits = (byte) (checkSum >> 8);
		byte secondBits = (byte) (checkSum & 0xffff);
		packet[6] = firstBits;
		packet[7] = secondBits;
		
		
		return packet;
		
	}
	public static byte[] makePacket(byte[] udpPacket)
	{
		byte[] packet = new byte[20 + udpPacket.length];
		byte firstBits = (byte) ((udpPacket.length + 20) >> 8);
		byte secondBits = (byte) ((udpPacket.length + 20) & 0xffff);
		//byte []byteValue = new byte[19];
		packet[0] = 0x45;
		packet[1] = 0x00; //TOS
		packet[2] = firstBits; //Length part 1
		packet[3] = secondBits; //Length part 2
		packet[4] = 0x00; //Indent part1
		packet[5] = 0x00; //Indent part2
		packet[6] = 0x40; //flag and offsetpart1
		packet[7] = 0x00; //offset part2
		packet[8] = 0x32; //TTL
		packet[9] = 0x11; //UDP Protocol
		packet[10] = 0x00; //checksum
		packet[11] = 0x00; //check sum
		packet[12] = 0x0a; //source IP
		packet[13] = 0x6e; //source Ip
		packet[14] = 0x24; //source IP
		packet[15] = 0x13; //source IP
		packet[16] = 0x34; //dest ip
		packet[17] = 0x21; //dest ip
		packet[18] = (byte) 0x83; //dest ip
		packet[19] = 0x10; //dest ip
		short checkSum = checkSum(packet, packet.length/2);
		firstBits = (byte) (checkSum >> 8);
		secondBits = (byte) (checkSum & 0xffff);
		packet[10] = firstBits;
		packet[11] = secondBits;
		
		for(int i = 0; i < udpPacket.length; i++)
		{
			packet[20 + i] = udpPacket[i];
		}
		
		//System.out.printf("%x",checkSum);
		
		
		return packet;
	}
	public static void main(String[]args) throws UnknownHostException, IOException
	{
		try(Socket socket = new Socket("cs380.codebank.xyz", 38005))
		{
			OutputStream out = socket.getOutputStream();
			InputStream in = socket.getInputStream();
			byte []outBytes = new byte[20];
			outBytes[0] = 0x45;
			byte []tempByte = ip4Header();
			
			for(int i = 0; i < tempByte.length; i++)
			{
				outBytes[i + 1] = tempByte[i]; 
			}
			out.write(outBytes[0]);
			short checkSum = checkSum(outBytes, outBytes.length/2);
			byte firstBits = (byte) (checkSum >> 8);
			byte secondBits = (byte) (checkSum & 0xffff);
			tempByte[9] = firstBits;
			tempByte[10] = secondBits;
			byte []udpData = {(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef};
			
			out.write(tempByte);
			out.write(udpData[0]);
			out.write(udpData[1]);
			out.write(udpData[2]);
			out.write(udpData[3]);
			for(int i = 0; i < 4; i++)
			{
				System.out.printf("%x", in.read());
			}
			//Get the port number
			int port = in.read();
			port = port << 8;
			port = port ^ in.read();
			//System.out.printf("\n%x\n", port);
			System.out.println();
			int dataSize = 1;
			long []roundTripTime = new long[12];
			for(int i = 0; i < 12; i++)
			{
				dataSize *= 2;
				byte [] packet = makePacket(updHeader(port, dataSize));
				
				out.write(packet);
				long send = System.currentTimeMillis();
				for(int j = 0; j < 4; j++)
				{
					System.out.printf("%x", in.read());
					
				}
				long receive = System.currentTimeMillis();
				roundTripTime[i] = receive - send; 
				System.out.println(" " + (receive - send));
			}
			Arrays.sort(roundTripTime);
			System.out.println("Median rtt = " + roundTripTime[6]);
		}
				
	}
	

}
