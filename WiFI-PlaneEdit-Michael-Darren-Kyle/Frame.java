package wifi;

import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * Michael Villasenor Kyle Dybdal Darren Chu
 */

public class Frame {
	private short frameType;
	private short retry;
	private short sequenceNum;
	private short packet;
	private byte[] control = new byte[2];
	private byte[] destAddr = new byte[2];
	private byte[] srcAddr = new byte[2];
	private byte[] data;
	private byte[] crc = new byte[4];
	private byte[] theFrame;

	/**
	 * Constructor
	 * 
	 * @param frameType
	 * @param retry
	 * @param sequenceNum
	 */
	public Frame(short frameType, short retry, short sequenceNum) {
		this.frameType = frameType;
		this.retry = retry;
		this.sequenceNum = sequenceNum;
	}
	public Frame (){
		
	}

	void constructControl() {
		frameType = (short) (frameType << 13);
		retry = (short) (retry << 12);
		// sequenceNum = (short) (sequenceNum << 7);
		packet = (short) (sequenceNum | frameType | retry);
		BitSet controlBits = convertBits(packet);
		control = controlBits.toByteArray();
	}

	void setDestAddr(short mac) {
		destAddr = ByteBuffer.allocate(2).putShort(mac).array();
	}

	void setSrcAddr(short src) {
		srcAddr = ByteBuffer.allocate(2).putShort(src).array();
	}

	void setData(byte[] dataIn, int len) {
		data = new byte[len];
		for (int ii=0; ii<len; ii++)
		{
			data[ii] = dataIn[ii];
		}
	}

	void setCRC(int crcIn) {
		crc = ByteBuffer.allocate(4).putInt(crcIn).array();
	}

	byte[] getControl() {
		return control;
	}

	byte[] getDestAddr() {
		return destAddr;
	}

	byte[] getSrcAddr() {
		return srcAddr;
	}

	byte[] getData() {
		return data;
	}

	byte[] getCRC() {
		return crc;
	}

	/**
	 * take all the sections of the frame and put them in one byte array
	 * 
	 * @return
	 */
	byte[] makeFrame() {
		int index = 0;
		theFrame = new byte[data.length + 10]; // frame is the size of the data
												// plus the 10 bytes required for
												// all packets

		// these for each loops step through each section that goes into the
		// frame and consolidates them into one byte array
		for (byte b : control) {
			theFrame[index] = b;
			index++;
		}

		for (byte c : destAddr) {
			theFrame[index] = c;
			index++;
		}

		for (byte d : srcAddr) {
			theFrame[index] = d;
			index++;
		}

		for (byte e : data) {
			theFrame[index] = e;
			index++;
		}

		for (byte f : crc) {
			theFrame[index] = f;
			index++;
		}

		return theFrame;
	}

	/**
	 * takes a short and returns a bitset representing that value
	 * 
	 * @param value
	 * @return BitSet version of the value
	 */
	private BitSet convertBits(short value) {
		BitSet bits = new BitSet();
		int index = 0;
		while (value != (short) 0) { // continue until all bits have been
										// accounted for
			if (value % (short) 2 != 0) { // if there's not a zero in the last
											// bit, set the corresponding bit in
											// the bitset to 1
				bits.set(index);
			}
			index++;
			value >>>= 1; // check the next bit
		}
		return bits;
	}
	
	//TODO added remote Frame data extraction method
	
	byte[] extractControl(byte[] inBuf)
	{
		int index=0;
		private byte[] extractedControl = new byte[2];
		
		for (byte b : control) {
			extractedControl[index] = inBuf[index];
			index++;
		}
		
		return extractedControl;
	}
	
	byte[] extractDest(byte[] inBuf)
	{
		int index=2;
		private byte[] extractedDest = new byte[2];
		
		for (byte b : destAddr) {
			extractedDest[index] = inBuf[index];
			index++;
		}
		
		return extractedDest;
	}
	
	byte[] extractSrc(byte[] inBuf)
	{
		int index=4;
		private byte[] extractedSrc = new byte[2];
		
		for (byte b : srcAddr) {
			extractedSrc[index] = inBuf[index];
			index++;
		}
		return extractedSrc;
	}
	
	byte[] extractData(byte[] inBuf)
	{
		int index=6;
		private byte[] extractedData = new byte[2039];
		
		for (byte b : data) {
			extractedData[index] = inBuf[index];
			index++;
		}
		return extractedData;
	}
	
	byte[] extractCRC(byte[] inBuf)
	{
		int index=2045;
		private byte[] extractedCRC = new byte[4];
		
		for (byte b : crc) {
			extractedCRC[index] = inBuf[index];
			index++;
		}
		return extractedCRC;
	}
}	