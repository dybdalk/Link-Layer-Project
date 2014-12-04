package wifi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import rf.RF;

/**
 * Michael Villasenor
 * Kyle Dybdal
 * Darren Chu
 */

public class Receiver implements Runnable{
	
	private RF theRF;           // You'll need one of these eventually
	ArrayBlockingQueue<byte[]> incomingPackets;
	private byte[] received;
	private short ourMAC;
	
	
	public Receiver(RF theRF, short ourMAC)
	{
		this.theRF = theRF; //set passed RF to the local RF variable
		this.ourMAC = ourMAC;
		incomingPackets = new ArrayBlockingQueue<byte[]>(128);
	}
	
	public void run()
	{
		System.out.println("Reader is alive and well"); //We made it to the receiving zone - nice!
		while (true) //Run until quit by the user or the RF running time runs out as specified in the RF library.
		{
			received = theRF.receive(); //save the received bytes to a byte array for displaying and parsing later
			//System.out.println("We got this: "+received.toString());
			ByteBuffer bb = ByteBuffer.allocate(2); // grab the dest addr from the received packet (this should really be done in the frame class)
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.put(received[2]);
			bb.put(received[3]);
			short destAddr = bb.getShort(0);
			if(destAddr == ourMAC){ // if the dest addr form the receive packet matches our mac address, we keep it
				incomingPackets.add(received); 
			}
		}
	}
	
	/**
	 * pops the top of the queue off and returns it
	 * @return the head of the queue
	 */
	public byte[] getHeader(){
		if(!incomingPackets.isEmpty()){
			return incomingPackets.remove();
		}
		else{
			return null;
		}
	}
}