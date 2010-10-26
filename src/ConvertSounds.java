

import java.io.*;
import com.kreative.ksfl.*;
import com.kreative.rsrc.*;

public class ConvertSounds {
	public static void main(String[] args) {
		Format fmt = Format.WAV;
		for (String arg : args) {
			if (arg.equalsIgnoreCase("-wav") || arg.equalsIgnoreCase("-wave")) fmt = Format.WAV;
			else if (arg.equalsIgnoreCase("-aif") || arg.equalsIgnoreCase("-aiff")) fmt = Format.AIFF;
			else try {
				File bf = new File(arg);
				File f = new File(new File(bf, "..namedfork"), "rsrc");
				MacResourceProvider rp = new MacResourceFile(f, "r", MacResourceFile.CREATE_NEVER);
				short[] ids = rp.getIDs(KSFLConstants.snd);
				for (short id : ids) {
					SoundResource rsnd = rp.get(KSFLConstants.snd, id).shallowRecast(SoundResource.class);
					System.out.print("snd #"+id+((rsnd.name == null || rsnd.name.trim().length() == 0) ? "" : (" "+rsnd.name.trim()))+"...");
					byte[] stuff = fmt.convert(rsnd);
					if (stuff == null) {
						System.out.println(" CANNOT CONVERT");
					} else {
						File outf = new File(bf.getParentFile(), bf.getName()+" #"+id+((rsnd.name == null || rsnd.name.trim().length() == 0) ? "" : (" "+rsnd.name.trim().replace('/', ':')))+"."+fmt.name().toLowerCase());
						try {
							FileOutputStream out = new FileOutputStream(outf);
							out.write(stuff);
							out.close();
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
	
	private static enum Format {
		WAV, AIFF;
		public byte[] convert(SoundResource r) {
			switch (this) {
			case WAV: return r.toWav();
			case AIFF: return r.toAiff();
			default: return null;
			}
		}
	}
}
