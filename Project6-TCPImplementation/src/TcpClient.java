import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class TcpClient {
	public static void main(String []args) throws UnknownHostException, IOException
	{
		try(Socket socket = new Socket("cs380.codebank.xyz", 38006))
		{
			OutputStream out = socket.getOutputStream();
			InputStream in = socket.getInputStream();
			Random rand = new Random();
			byte []serverPacket = new byte[20];
			//Establish a connection
			int sequenceNum = rand.nextInt();
			int ack = 0;
			int dataSize = 0;
			byte flag = 0x02;
			byte []synPacket = makePacket(tcpPacket(dataSize, sequenceNum, ack, flag));
			out.write(synPacket);
			
			//first response from server
			for(int i = 0; i < 4; i++)
			{
				System.out.printf("%x",in.read());
			}
			//first TCPheader
			in.read(serverPacket);
			ack = serverPacket[4] & 0xff;
			ack = (ack << 8) ^ (serverPacket[5] & 0xff);
			ack = (ack << 8) ^ (serverPacket[6] & 0xff);
			ack = (ack << 8) ^ (serverPacket[7] & 0xff);
			
			sequenceNum += 1;
			ack += 1;
			flag = 0x12;
			byte []secondPacket = makePacket(tcpPacket(dataSize, sequenceNum, ack, flag));
			out.write(secondPacket);
			System.out.println();
			for(int i = 0; i < 4; i++)
			{
				System.out.printf("%x", in.read());
			}
			System.out.println();
			dataSize = 1;
			flag = 0x12;
			for(int i = 0; i < 12; i++)
			{
				sequenceNum += dataSize;
				dataSize *= 2;
				byte [] packet = makePacket(tcpPacket(dataSize, sequenceNum, ack, flag));
				out.write(packet);
				for(int j = 0; j < 4; j ++)
				{
					System.out.printf("%x", in.read());
				}
				System.out.println();
			}
			
			//Close TCP connection
			flag = 0x01;
			dataSize = 0;
			byte []finishPacket = makePacket(tcpPacket(dataSize, sequenceNum, ack, flag));
			out.write(finishPacket);
			for(int i = 0; i < 4; i++)
			{
				System.out.printf("%x", in.read());
			}
			serverPacket = new byte[20];
			in.read(serverPacket);
			System.out.println();
			if(serverPacket[13] == 0x10)
			{
				in.read(serverPacket);
			}
			flag = 0x10;
			finishPacket = makePacket(tcpPacket(dataSize, sequenceNum, ack, flag));
			out.write(finishPacket);
			//System.out.println();
			for(int i = 0; i < 4; i++)
			{
				System.out.printf("%x", in.read());
			}
			
			System.out.println("\nConnection Closed");
			
		}
		
	}
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
	public static byte[] makePacket(byte[] tcpPacket)
	{
		byte[] packet = new byte[20 + tcpPacket.length];
		byte firstBits = (byte) ((tcpPacket.length + 20) >> 8);
		byte secondBits = (byte) ((tcpPacket.length + 20) & 0xffff);
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
		packet[9] = 0x06; //TCP Protocol
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
		
		for(int i = 0; i < tcpPacket.length; i++)
		{
			packet[20 + i] = tcpPacket[i];
		}
		
		//System.out.printf("%x",checkSum);
		
		
		return packet;
	}
	public static byte[] tcpPacket(int dataSize, int seqNum, int ack, byte flag)
	{
		byte []tcpPacket = new byte[20 + dataSize];
		byte []randData = new byte[dataSize];
		Random random = new Random();
		random.nextBytes(randData);
		
		
		tcpPacket[0] = 0x1a; //src port
		tcpPacket[1] = 0x33; //src port
		tcpPacket[2] = 0x1a; //dst port
		tcpPacket[3] = 0x33; //dst port
		tcpPacket[4] = (byte) (seqNum >> 24); //sequence number
		tcpPacket[5] = (byte) ((seqNum >> 16) & 0x00ff); //sequence number
		tcpPacket[6] = (byte) ((seqNum >> 8) & 0x00ff);	//sequence number
		tcpPacket[7] = (byte) (seqNum & 0x00ff); //sequence number
		tcpPacket[8] = (byte) (ack >> 24); //acknowledgement
		tcpPacket[9] = (byte) ((ack >> 16) & 0x00ff); //acknowledgement
		tcpPacket[10] = (byte) ((ack >> 8) & 0x00ff); //acknowledgement
		tcpPacket[11] = (byte) (ack & 0x00ff); //acknowledgement
		tcpPacket[12] = 0x50; //header length and zero
		tcpPacket[13] = flag; //zero and flags
		tcpPacket[14] = 0x00; //advertised window
		tcpPacket[15] = 0x00; //advertised window
		tcpPacket[16] = 0x00; //checksum
		tcpPacket[17] = 0x00; //checksum
		tcpPacket[18] = 0x00; //urgPtr
		tcpPacket[19] = 0x00; //ugPrt
		for(int i = 0; i < randData.length; i++)
		{
			tcpPacket[20 + i] = randData[i];
		}
		int tcpLength = tcpPacket.length;
		byte []pseudoHeader = pseudoHeader(tcpPacket);
		
		short checkSum = checkSum(pseudoHeader, pseudoHeader.length/2);
		byte firstBits = (byte) (checkSum >> 8);
		byte secondBits = (byte) (checkSum & 0xffff);
		tcpPacket[16] = firstBits;
		tcpPacket[17] = secondBits;
		
		
		return tcpPacket;
	}
	public static byte []pseudoHeader(byte []tcpPacket)
	{
		int tcpLength = tcpPacket.length;
		byte []pseudoHeader = new byte[tcpLength + 12];
		pseudoHeader[0] = 0x0a; //source IP
		pseudoHeader[1] = 0x6e; //source Ip
		pseudoHeader[2] = 0x24; //source IP
		pseudoHeader[3] = 0x13; //source IP
		pseudoHeader[4] = 0x34; //dest ip
		pseudoHeader[5] = 0x21; //dest ip
		pseudoHeader[6] = (byte) 0x83; //dest ip
		pseudoHeader[7] = 0x10; //dest ip
		pseudoHeader[8] = 0x00; //reserved
		pseudoHeader[9] = 0x06; //tcp Protocol
		pseudoHeader[10] = (byte) (tcpLength >> 8); //length
		pseudoHeader[11] = (byte) (tcpLength & 0x00ff); //length
		for(int i = 0; i < tcpLength; i++)
		{
			pseudoHeader[12 + i] = tcpPacket[i];
		}
		
		return pseudoHeader;
		
		
	}
	
	

}
