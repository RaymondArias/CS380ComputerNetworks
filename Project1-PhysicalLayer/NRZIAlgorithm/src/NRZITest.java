
public class NRZITest {
	public static int LAST_BIT = -99;
	public static void main(String []args)
	{
		int []bits = {0, 0, 2, 0};
		double preamble = 1.0;
		
		System.out.printf("%x", NRZIdecoding(bits, preamble));
		
	}
	
	
	public static int NRZIdecoding(int []bits, double preamble)
	{
		int retVal = 0;
		boolean signal = true;
		for(int i = 0; i < bits.length; i++)
		{
			if (NRZITest.LAST_BIT == -99)
			{
				if(bits[i] > preamble)
				{
					signal = true;
					retVal = retVal << 1;
					retVal += 1;
					NRZITest.LAST_BIT = 1;
				
				}
				else
				{
					signal = false;
					retVal = retVal << 1;
					NRZITest.LAST_BIT = 0;
				}
			}
			else if (bits[i] > preamble)
			{
				if(NRZITest.LAST_BIT == 1)
				{
					signal = false;
					retVal = retVal << 1;
					NRZITest.LAST_BIT = 0;
				}
				else
				{
					signal = true;
					retVal = retVal << 1;
					retVal += 1;
					
					NRZITest.LAST_BIT = 1;
				}
			}
			else if(bits[i] < preamble)
			{
				if(NRZITest.LAST_BIT == 1)
				{
					signal = true;
					retVal = retVal << 1;
					retVal += 1;
					NRZITest.LAST_BIT = 1;
				}
				else
				{
					signal = false;
					retVal = retVal << 1;
					NRZITest.LAST_BIT = 0;
				}
			}
			
		}
		return retVal;
	}

}
