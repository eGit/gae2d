package org.fonteditor.utilities.resources;

/**
 * A class containing static methods which load images -
 * including images within jar files...
 * <p>
 * @author Tim Tyler
 * @version 1.12
 */

import java.applet.Applet;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.harmony.awt.gl.image.ByteArrayDecodingImageSource;
import org.apache.harmony.awt.gl.image.OffscreenImage;
import org.fonteditor.graphics.ImageWrapper;
import org.fonteditor.utilities.general.For;

import com.jgraph.gaeawt.java.awt.image.BufferedImage;

public class ImageLoader
{

	/**
	 * ImageLoader, constructor.
	 * <p>
	 * The constructor is private.
	 * There should not be any instances of this
	 * class created outside the private one used
	 * in the class.
	 **/
	private ImageLoader()
	{
		//...
	}

	/**
	 * Get an image.
	 * <p>
	 * Loads a specified image, either from the currect directory,
	 * Or from inside the relevant jar file, whichever is appropriate.
	 **/
	public static ImageWrapper getImage(Class cls, String name)
	{
		InputStream in;
		ImageWrapper image;
		//boolean ispng;

		//if (!Rockz.directory_separator.equals("/")) {
		//name = StringParser.searchAndReplace(name, "/", Rockz.directory_separator);
		//}

		// Log.log("Loading: " + name + ".");

		byte[] byte_array;
		int byte_array_size;

		try
		{
			//Log.log("Starting to load: " + name);

			in = cls.getResourceAsStream(name);

			//in = ImageLoader.class.getResourceAsStream(name);
			if (in == null)
			{
				throw new RuntimeException("Problem locating image file: "
						+ name);
			}

			// Thanks to Karl Schmidt for the followig code...
			ByteArrayOutputStream bytes;

			bytes = new ByteArrayOutputStream();
			byte_array_size = 1024; // choose a size...
			byte_array = new byte[byte_array_size];

			int rb;

			while ((rb = in.read(byte_array, 0, byte_array_size)) > -1)
			{
				bytes.write(byte_array, 0, rb);
			}

			bytes.close();

			byte_array = bytes.toByteArray();

			image = null; // FIXME new ImageWrapper(new BufferedImage(new ByteArrayDecodingImageSource(byte_array, 0, byte_array.length)));

			in.close();

			//Log.log("Finished loading: " + name + ".");

			return image;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		//Log.log("BAD EXIT: "+ name);

		return null;
	}
}
