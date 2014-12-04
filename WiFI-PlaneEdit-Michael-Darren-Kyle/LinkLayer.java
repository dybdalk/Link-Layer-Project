package wifi;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;

import rf.RF;
/**
 * Michael Villasenor
 * Kyle Dybdal
 * Darren Chu
 */



/**
 * Use this layer as a starting point for your project code.  See {@link Dot11Interface} for more
 * details on these routines.
 * @author richards
 */
public class LinkLayer implements Dot11Interface {
   private RF theRF;           // You'll need one of these eventually
   private short ourMAC;       // Our MAC address


private PrintWriter output; // The output stream we'll write to
   private Receiver receiver;
   private byte[] recvData;
   private long SIFS = 100;
   private long DIFS = SIFS + 200*2;
   private final static long timeoutLength = 0;
   private short sequenceNumber;
   private short seq
   

   /**
    * Constructor takes a MAC address and the PrintWriter to which our output will
    * be written.
    * @param ourMAC  MAC address
    * @param output  Output stream associated with GUI
    */
   public LinkLayer(short ourMAC, PrintWriter output) {
      this.ourMAC = ourMAC;
      this.output = output;      
      theRF = new RF(null, null);
      sequenceNumber = 0;
      
      receiver = new Receiver(theRF, ourMAC);
      (new Thread(receiver)).start();
      output.println("LinkLayer: Constructor ran.");
   }

   /**
    * Send method takes a destination, a buffer (array) of data, and the number
    * of bytes to send.  See docs for full description.
    */
   public int send(short dest, byte[] data, int len) {
      output.println("LinkLayer: Sending "+len+" bytes to "+dest); //Brad's Stuff
      while(theRF.inUse()){ //are we clear for liftoff?
    	  try {
			Thread.sleep(DIFS); //sleep until for a while, then check to see if we are clear for liftoff
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); //in case we can't sleep (monsters in the closet?)
		}
      }
      
      Frame sendFrame = new Frame((short)0, (short)0, sequenceNumber); //Construct the basis of a frame, and provide control bits
      sendFrame.setDestAddr(dest); //Where are we sending to?
      sendFrame.setSrcAddr(ourMAC); //Who are we?
      byte[] tempData = new byte[len]; //create a place to put all the stuff we want to send.
      for (int ii=0; ii<len; ii++) //loop through all the data we got and only send as much as we were asked to
      {
    	  tempData[ii] = data[ii];
      }
      sendFrame.setData(tempData, len); //construct the frame with only the data we want to send
      sendFrame.setCRC(111); //set the CRC to 111 because we are not dealing with this yet
      theRF.transmit(sendFrame.makeFrame()); //send the finished packet
      sequenceNumber += sendFrame.getData().length;
      long timeoutTime = System.currentTimeMillis() + timeoutLength;
      return sendFrame.getData().length; //get the length of what we sent and return it!
   }
   
   /**
    * Recv method blocks until data arrives, then writes it an address info into
    * the Transmission object.  See docs for full description.
    */
   public int recv(Transmission t) {
	  
	   output.println("LinkLayer: Pretending to block on recv()");
	   //byte[] received = theRF.receive(); //save the received bytes to a byte array for displaying and parsing later
	   recvData = receiver.getHeader();
	   if(recvData != null){
		   t.setBuf(recvData);	   
		   output.println("We got this: "+t.getBuf().toString()); //show the user what we received
		   //TODO Created Transmission object
		   Frame localFrameAccess = new Frame();
       byte[] controlFromReceivedPacket=localFrameAccess.extractControl(recvData);
       if ((controlFromReceivedPacket[0]>>5)==1)
       {
          //If ACK matches sequence number of packet we just sent, then go for it
          //Still to be implemented
          //Sorry Brad



       }
		   //TODO Add more parameters for what a "bad" packet is, below
		   if(recvData.length != -1)
		   {
	 
				ByteBuffer bb = ByteBuffer.allocate(2); // grab the control bytes
				bb.order(ByteOrder.LITTLE_ENDIAN);
				bb.put(recvData[2]);
				bb.put(recvData[3]);
				short recvCtrl = bb.getShort(0);
				short ackSeqNum = (short) (recvCtrl & (short)4095); // bitwise and with 0000111111111111 to get the sequence number from the control packet
			   Frame ACKFrame = new Frame((short)1, (short)0, ackSeqNum);
			   ACKFrame.setDestAddr(t.getDestAddr());
			   ACKFrame.setSrcAddr(ourMAC);
			   ACKFrame.setCRC(111);
			   
			   long sleepTime = (long) (SIFS); 
			   theRF.transmit(ACKFrame.makeFrame());

				// Sleep
				try
				{
					Thread.sleep(sleepTime); //go to sleep!
				}
				catch (InterruptedException e)
				{
					e.printStackTrace(); //Tell me why you can't sleep
				}
			   	   
		   }
	   }
	   
	   //TODO Transmission object return
	   return latestData; //old: recvData.length
   }

   /**
    * Returns a current status code.  See docs for full description.
    */
   public int status() {
      output.println("LinkLayer: Faking a status() return value of 0");
      return 0;
   }

   /**
    * Passes command info to your link layer.  See docs for full description.
    */
   public int command(int cmd, int val) {
      output.println("LinkLayer: Sending command "+cmd+" with value "+val);
      return 0;
   }
   
   public short getOurMAC() {
	return ourMAC;
}
}
