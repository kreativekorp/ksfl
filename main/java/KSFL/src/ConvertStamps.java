
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;

import com.kreative.ksfl.KSFLConstants;
import com.kreative.rsrc.MacResourceFile;
import com.kreative.rsrc.MacResourceProvider;
import com.kreative.rsrc.PictureResource;

public class ConvertStamps {
	public static void main(String[] args) {
		int org = 16000;
		boolean trns = false;
		String fmt = "png";
		boolean dimg = false;
		boolean simg = true;
		boolean spnx = false;
		for (String arg : args) {
			if (arg.equalsIgnoreCase("-c")) org = 16000;
			else if (arg.equalsIgnoreCase("-o")) org = 18000;
			else if (arg.equalsIgnoreCase("-w")) trns = false;
			else if (arg.equalsIgnoreCase("-t")) trns = true;
			else if (arg.equalsIgnoreCase("-png")) fmt = "png";
			else if (arg.equalsIgnoreCase("-jpg")) fmt = "jpg";
			else if (arg.equalsIgnoreCase("-jpeg")) fmt = "jpg";
			else if (arg.equalsIgnoreCase("-gif")) fmt = "gif";
			else if (arg.equalsIgnoreCase("-bmp")) fmt = "bmp";
			else if (arg.equalsIgnoreCase("-wbmp")) fmt = "wbmp";
			else if (arg.equalsIgnoreCase("-d")) { dimg = true;  simg = false; spnx = false; }
			else if (arg.equalsIgnoreCase("-p")) { dimg = false; simg = true;  spnx = false; }
			else if (arg.equalsIgnoreCase("-s")) { dimg = false; simg = true;  spnx = true;  }
			else if (arg.equalsIgnoreCase("-a")) { dimg = true;  simg = true;  spnx = true;  }
			else try {
				File bf = new File(arg);
				File rf = new File(new File(bf, "..namedfork"), "rsrc");
				MacResourceProvider rp = new MacResourceFile(rf, "r", MacResourceFile.CREATE_NEVER);
				boolean stm = rp.contains(KSFLConstants.PICT, (short)(org +  01));
				boolean anm = rp.contains(KSFLConstants.PICT, (short)(org +  51))
				           && rp.contains(KSFLConstants.PICT, (short)(org + 101))
				           && rp.contains(KSFLConstants.PICT, (short)(org + 151));
				if (stm) {
					System.out.print(arg + "...");
					int w = 32 * 14 * 8;
					int h = (anm ? (32 * 4) : 32);
					int t = BufferedImage.TYPE_INT_ARGB;
					BufferedImage img = new BufferedImage(w, h, t);
					int[] rgb = new int[w * h];
					for (int i = 0; i < rgb.length; i++) rgb[i] = (trns ? 0 : -1);
					img.setRGB(0, 0, w, h, rgb, 0, w);
					rgb = new int[31 * 31];
					for (int y = 0, f = 0; f < (anm ? 4 : 1); f++, y += 32) {
						for (int x = 0, p = 0; p < 8; p++) {
							PictureResource pict = rp.get(KSFLConstants.PICT, (short)(org + 50*f + p + 1)).shallowRecast(PictureResource.class);
							BufferedImage image = (BufferedImage)pict.toImage();
							for (int i = 0; i < 14; i++, x += 32) {
								image.getRGB(i * 32, 0, 31, 31, rgb, 0, 31);
								if (trns) makeTransparent(rgb);
								img.setRGB(x, y, 31, 31, rgb, 0, 31);
							}
						}
					}
					try {
						if (dimg) {
							File out = new File(bf.getParentFile(), bf.getName() + ".d");
							out.mkdir();
							int[] irgb = new int[32 * h];
							BufferedImage iimg = new BufferedImage(32, h, t);
							for (int i = 0, n = 14 * 8, x = 0; i < n; i++, x += 32) {
								img.getRGB(x, 0, 32, h, irgb, 0, 32);
								iimg.setRGB(0, 0, 32, h, irgb, 0, 32);
								String iname = "000" + i;
								iname = iname.substring(iname.length() - 3);
								File iout = new File(out, iname + "." + fmt);
								ImageIO.write(iimg, fmt, iout);
							}
						}
						if (simg) {
							File out = new File(bf.getParentFile(), bf.getName() + "." + fmt);
							ImageIO.write(img, fmt, out);
						}
						if (spnx) {
							File out = new File(bf.getParentFile(), bf.getName() + ".spnx");
							PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(out), "UTF-8"), true);
							printSPNX(pw, bf.getName(), anm);
							pw.close();
						}
						System.out.println(" done");
					} catch (IOException ioe) {
						System.out.println(" CANNOT WRITE");
					}
				}
				rp.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	
	private static void makeTransparent(int[] src) {
		int[] buf = new int[33 * 33];
		for (int i = 0; i < buf.length; i++) buf[i] = -1;
		for (int sy = 0, by = 33, y = 0; y < 31; y++, sy += 31, by += 33) {
			for (int sx = sy, bx = by + 1, x = 0; x < 31; x++, sx++, bx++) {
				buf[bx] = src[sx];
			}
		}
		List<Integer> Q = new LinkedList<Integer>();
		Q.add(0);
		while (!Q.isEmpty()) {
			int i = Q.remove(0);
			if (isWhite(buf[i])) {
				int w = i;
				int e = i;
				while (((w % 33) >  0) && isWhite(buf[w - 1])) w--;
				while (((e % 33) < 32) && isWhite(buf[e + 1])) e++;
				for (int x = w; x <= e; x++) buf[x] = 0;
				if ((i / 33) >  0) {
					for (int x = w; x <= e; x++) {
						if (isWhite(buf[x - 33])) Q.add(x - 33);
					}
				}
				if ((i / 33) < 32) {
					for (int x = w; x <= e; x++) {
						if (isWhite(buf[x + 33])) Q.add(x + 33);
					}
				}
			}
		}
		for (int sy = 0, by = 33, y = 0; y < 31; y++, sy += 31, by += 33) {
			for (int sx = sy, bx = by + 1, x = 0; x < 31; x++, sx++, bx++) {
				src[sx] = buf[bx];
			}
		}
	}
	
	private static boolean isWhite(int pixel) {
		return (pixel & 0xFEFEFEFE) == 0xFEFEFEFE;
	}
	
	private static void printSPNX(PrintWriter out, String name, boolean anm) {
		name = name.trim();
		if (name.contains("-")) name = name.substring(name.indexOf("-") + 1).trim();
		if (name.contains(".")) name = name.substring(0, name.lastIndexOf(".")).trim();
		name = name.replaceAll("([a-z])([0-9])", "$1 $2");
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE sprite-sheet PUBLIC \"-//Kreative//DTD SpriteInfo 1.0//EN\" \"http://www.kreativekorp.com/dtd/spnx.dtd\">");
		out.println("<sprite-sheet name=\"" + xmls(name) + "\"");
		out.println("              intent=\"" + (anm ? "animated" : "rubber") + "-stamps\">");
		out.println();
		out.println("\t<slice sx=\"0\" sy=\"0\"");
		out.println("\t       cw=\"31\" ch=\"31\"");
		out.println("\t       chx=\"15\" chy=\"15\"");
		out.println("\t       cdx=\"32\" cdy=\"32\"");
		out.println("\t       cols=\"" + (14 * 8) + "\" rows=\"" + (anm ? 4 : 1) + "\"");
		out.println("\t       order=\"" + (anm ? "ttb-ltr" : "ltr-ttb") + "\"/>");
		out.println();
		if (anm) {
			for (int i = 0, n = 14 * 8, x = 0; i < n; i++, x += 4) {
				out.println("\t<sprite-set index=" + rpad(x) + "><sprite index=" + rpad(x) + " count=\"4\"/></sprite-set>");
			}
		} else {
			out.println("\t<sprite index=\"0\" count=\"" + (14 * 8) + "\"/>");
		}
		out.println();
		out.println("</sprite-sheet>");
	}
	
	private static String xmls(String s) {
		return s.replaceAll("&", "&amp;")
		        .replaceAll("<", "&lt;")
		        .replaceAll(">", "&gt;")
		        .replaceAll("\"", "&quot;");
	}
	
	private static String rpad(int v) {
		String s = "\"" + v + "\"";
		while (s.length() < 5) s = " " + s;
		return s;
	}
}
