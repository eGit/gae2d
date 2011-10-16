package java2d;

import java.io.*;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

import com.jgraph.gaeawt.java.awt.Font;
import com.jgraph.gaeawt.java.awt.Graphics2D;
import com.jgraph.gaeawt.java.awt.font.FontRenderContext;
import com.jgraph.gaeawt.java.awt.image.BufferedImage;

public class ClipImage
{
	public static BufferedImage clipImageTest() throws IOException, ImageReadException
	{
		int w = 600;
		int h = 600;
		BufferedImage imageARGB = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		String filename = "Venus of Urbino.jpg";
		InputStream in = ClipImage.class.getResourceAsStream(filename);
		final BufferedImage image = Sanselan.getBufferedImage(in);
		in.close();

		Graphics2D g2 = imageARGB.createGraphics();
		g2.rotate(-Math.PI / 12, image.getWidth() / 2, image.getHeight() / 2);
		String s = "bella";
		Font font = new Font("Serif", Font.PLAIN, 192);
		FontRenderContext frc = g2.getFontRenderContext();
		//GlyphVector gv = font.createGlyphVector(frc, s);
		//Shape clippingShape = gv.getOutline(10, 200);
		// FIXME when font rendering implemented
		//g2.clip(clippingShape);
		g2.drawImage(image, 0, 0, null);
		
		return imageARGB;
	}
}