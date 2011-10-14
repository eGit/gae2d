package org.fonteditor.utilities.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A class containing static methods which load images -
 * including images within jar files...
 * <p>
 * @author Tim Tyler
 * @version 1.0
 */

public class FileLoader
{
	//  private static FileLoader file_loader;

	/**
	 * FileLoader, constructor.
	 * <p>
	 * The constructor is private.
	 * There should not be any instances of this
	 * class created outside the private one used
	 * in the class.
	 */

	//private FileLoader() {}

	/**
	 * static initialization.
	 * <p>
	 * Create an instance of this class for later use...
	 */

	//static {
	//file_loader = new FileLoader();
	//}

	/**
	 * Read a file into a byte array from wherever RootClass is.
	 * <p>
	 * Loads a specified file, either from the currect directory,
	 * Or from inside the relevant jar file, whichever is appropriate.
	 */
	static byte[] getByteArray(String name) throws IOException
	{
		return null; // FIXME getByteArray(FE.class, name);
	}

	/**
	 * Read a file into a byte array from wherever RootClass is.
	 * <p>
	 * Loads a specified file, either from the currect directory,
	 * Or from inside the relevant jar file, whichever is appropriate.
	 */
	public static byte[] getByteArray(Class c, String name) throws IOException
	{
		// Log.log("Class: " + c + " - " + name);
		InputStream in = c.getResourceAsStream(name);

		if (in == null)
		{
			throw new IOException("Failed to find file: " + c + " - " + name);
		}

		ByteArrayOutputStream bytes;

		bytes = new ByteArrayOutputStream();
		int array_size = 1024; // choose a size...
		byte[] array = new byte[array_size];
		int rb;

		while ((rb = in.read(array, 0, array_size)) > -1)
		{
			bytes.write(array, 0, rb);
		}

		bytes.close();
		in.close();
		return bytes.toByteArray();
	}
}
