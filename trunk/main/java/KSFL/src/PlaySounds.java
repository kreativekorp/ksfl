

import java.io.*;
import javax.sound.sampled.*;
import com.kreative.ksfl.*;
import com.kreative.rsrc.*;

public class PlaySounds {
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
					System.out.print("snd #"+id+((rsnd.name == null) ? "" : (" "+rsnd.name))+"...");
					byte[] stuff = fmt.convert(rsnd);
					if (stuff == null) {
						System.out.println(" CANNOT CONVERT");
					} else {
						playSound(stuff);
						System.out.println(" done");
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
	
	private static void playSound(byte[] stuff) {
		if (stuff != null) try {
			AudioInputStream st = AudioSystem.getAudioInputStream(new ByteArrayInputStream(stuff));
			AudioFormat fm = st.getFormat();
			DataLine.Info inf = new DataLine.Info(Clip.class, fm, ((int)st.getFrameLength()*fm.getFrameSize()));
			Clip c = (Clip)AudioSystem.getLine(inf);
			c.open(st);
			c.start();
			c.drain();
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
