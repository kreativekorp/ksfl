
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.kreative.ksfl.KSFLConstants;
import com.kreative.rsrc.MacResourceFile;
import com.kreative.rsrc.MacResourceProvider;
import com.kreative.rsrc.PictureResource;

public class ConvertPictures {
	public static void main(String[] args) {
		String fmt = "png";
		for (String arg : args) {
			if (arg.equalsIgnoreCase("-png")) fmt = "png";
			else if (arg.equalsIgnoreCase("-jpg")) fmt = "jpg";
			else if (arg.equalsIgnoreCase("-jpeg")) fmt = "jpg";
			else if (arg.equalsIgnoreCase("-gif")) fmt = "gif";
			else if (arg.equalsIgnoreCase("-bmp")) fmt = "bmp";
			else if (arg.equalsIgnoreCase("-wbmp")) fmt = "wbmp";
			else try {
				File bf = new File(arg);
				File f = new File(new File(bf, "..namedfork"), "rsrc");
				MacResourceProvider rp = new MacResourceFile(f, "r", MacResourceFile.CREATE_NEVER);
				short[] ids = rp.getIDs(KSFLConstants.PICT);
				for (short id : ids) {
					PictureResource pict = rp.get(KSFLConstants.PICT, id).shallowRecast(PictureResource.class);
					System.out.print("PICT #"+id+((pict.name == null || pict.name.trim().length() == 0) ? "" : (" "+pict.name.trim()))+"...");
					BufferedImage image = (BufferedImage)pict.toImage();
					if (image == null) {
						System.out.println(" CANNOT CONVERT");
					} else {
						File outf = new File(bf.getParentFile(), bf.getName()+" #"+id+((pict.name == null || pict.name.trim().length() == 0) ? "" : (" "+pict.name.trim().replace('/', ':')))+"."+fmt);
						try {
							ImageIO.write(image, fmt, outf);
							System.out.println(" done");
						} catch (IOException ioe) {
							System.out.println(" CANNOT WRITE");
						}
					}
				}
				rp.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
}
