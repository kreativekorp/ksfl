import java.io.*;

public class PackBits {
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			boolean parseOptions = true;
			int skip = 0;
			int i = 0; while (i < args.length) {
				String arg = args[i++];
				if (parseOptions && arg.startsWith("-")) {
					if (arg.equals("--")) {
						parseOptions = false;
					} else if (arg.equals("-s") && i < args.length) {
						skip = parseInt(args[i++]);
					} else {
						System.err.println("Invalid option: " + arg);
					}
				} else {
					File file = new File(arg);
					InputStream in = new FileInputStream(file);
					in.skip(skip);
					packBits(in, System.out);
					in.close();
				}
			}
		} else {
			packBits(System.in, System.out);
		}
		System.out.flush();
	}
	
	private static int parseInt(String s) {
		try { return Integer.parseInt(s); }
		catch (NumberFormatException e) { return 0; }
	}
	
	private static void packBits(InputStream in, OutputStream out) throws IOException {
		ByteArrayOutputStream dataRun = new ByteArrayOutputStream();
		int repeatByte = -1, repeatCount = -1;
		while (true) {
			int h = in.read();
			if (h < 0) {
				break;
			} else if (h == repeatByte) {
				repeatCount++;
			} else {
				if (repeatCount >= 3) {
					packData(dataRun.toByteArray(), out);
					dataRun = new ByteArrayOutputStream();
					packRepeat(repeatByte, repeatCount, out);
				} else {
					for (int i = 0; i < repeatCount; i++) {
						dataRun.write(repeatByte);
					}
				}
				repeatByte = h;
				repeatCount = 1;
			}
		}
		if (repeatCount >= 3) {
			packData(dataRun.toByteArray(), out);
			dataRun = new ByteArrayOutputStream();
			packRepeat(repeatByte, repeatCount, out);
		} else {
			for (int i = 0; i < repeatCount; i++) {
				dataRun.write(repeatByte);
			}
		}
		packData(dataRun.toByteArray(), out);
	}
	
	private static void packData(byte[] data, OutputStream out) throws IOException {
		int dataPtr = 0, dataLen = data.length;
		while (dataLen >= 128) {
			out.write(128 - 1);
			out.write(data, dataPtr, 128);
			dataPtr += 128;
			dataLen -= 128;
		}
		if (dataLen > 0) {
			out.write(dataLen - 1);
			out.write(data, dataPtr, dataLen);
		}
	}
	
	private static void packRepeat(int data, int count, OutputStream out) throws IOException {
		while (count >= 128) {
			out.write(257 - 128);
			out.write(data);
			count -= 128;
		}
		if (count > 0) {
			out.write(257 - count);
			out.write(data);
		}
	}
}
