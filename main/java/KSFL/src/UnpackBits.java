import java.io.*;

public class UnpackBits {
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
					unpackBits(in, System.out);
					in.close();
				}
			}
		} else {
			unpackBits(System.in, System.out);
		}
		System.out.flush();
	}
	
	private static int parseInt(String s) {
		try { return Integer.parseInt(s); }
		catch (NumberFormatException e) { return 0; }
	}
	
	private static void unpackBits(InputStream in, OutputStream out) throws IOException {
		while (true) {
			int h = in.read();
			if (h < 0) {
				break;
			} else if (h < 128) {
				int c = h + 1;
				while (c-- > 0) {
					int d = in.read();
					if (d < 0) break;
					out.write(d);
				}
			} else {
				int c = 257 - h;
				int d = in.read();
				if (d < 0) break;
				while (c-- > 0) {
					out.write(d);
				}
			}
		}
	}
}
