package wifi;

public class Packets 
{
		private short frameType;
		private short retry;
		private short sequenceNum;
		private short packet;
		
		public Packets()
		{	
			retry = 0; //retry can equal 0 | 1
			frameType = 0; //frameType can equal 0 - 5
			sequenceNum = 0xFFF; //sequenceNum = 12 bits of 0 | 1
		}
		
		private int controlField()
		{
			frameType = (short) (frameType << 13);
			retry = (short) (retry << 12);
			//sequenceNum = (short) (sequenceNum << 7);
			packet = (short) (sequenceNum | frameType | retry); 
			return packet;
		}
		
		//http://stackoverflow.com/questions/10025358/create-4-bytes-from-16-bits
}
