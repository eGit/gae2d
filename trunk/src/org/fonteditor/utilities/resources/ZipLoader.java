package org.fonteditor.utilities.resources;

/**
 * A class containing static methods which load images -
 * including images within jar files...
 * <p>
 * @author Tim Tyler
 * @version 1.0
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipLoader {
  private static ZipLoader zip_loader;

  /**
   * ZipLoader, constructor.
   * <p>
   * The constructor is private.
   * There should not be any instances of this
   * class created outside the private one used
   * in the class.
   */
  private ZipLoader() { }

  /**
   * static initialization.
   * <p>
   * Create an instance of this class for later use...
   */
  static {
    zip_loader = new ZipLoader();
  }

  /**
   * Get an image.
   * <p>
   * Loads a specified file, either from the currect directory,
   * Or from inside the relevant jar file, whichever is appropriate.
   */
  static byte[] getByteArray(String archive, String name) {
    // Log.log("Starting to load "+ name + ".");
    byte[] array;
    byte[] array2;

    ByteArrayOutputStream bytes;
    
    try {
      InputStream in = null; // FIXME FE.class.getResourceAsStream(archive);
      if (in == null) {
        throw new IOException("Failed to find file: " + name);
      }

      bytes = new ByteArrayOutputStream();
      int array_size = 1024; // choose a size...
      array = new byte[array_size];
      int rb;

      while ((rb = in.read(array, 0, array_size)) > -1) {
        bytes.write(array, 0, rb);
      }
      bytes.close();
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    ByteArrayOutputStream bytes2;

    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes.toByteArray());
      ZipInputStream zis = new ZipInputStream(bais);

      ZipEntry entry;
      do {
        entry = zis.getNextEntry();
      } while (!entry.getName().equals(name));

      bytes2 = new ByteArrayOutputStream();
      int array2_size = 1024; // choose a size...
      array2 = new byte[array2_size];
      int rb;

      while ((rb = zis.read(array2, 0, array2_size)) > -1) {
        bytes2.write(array2, 0, rb);
      }

      bytes2.close();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    return bytes2.toByteArray();
  }
}
