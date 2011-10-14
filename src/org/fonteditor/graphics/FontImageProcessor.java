package org.fonteditor.graphics;

import org.fonteditor.utilities.log.Log;

/*
 * Process glyph images...
 * 
 * ToDo
 * ====
 * Add more image processing functions...
 */
  /**
   * Graphics utility class.
   * Used mainly when scaling oversampled fonts down to produce anti-aliasing.
   */

public class FontImageProcessor {

  /**
   * Makes images smaller (by integer scale factors only)...
   */
  public static GreyByteArrayTranslated fontScaleToByteArray(ImageWrapper tti, int nx, int ny) {
    int w = tti.getWidth();
    int h = tti.getHeight();
    int original_w = w;

    // needs to die...
    while ((h >= ny) && isRowEmpty(tti, h - ny, h)) {
      h -= ny;
    }

    // needs to die...
    while ((w >= nx) && isColumnEmpty(tti, w - nx, w)) {
      w -= nx;
    }

    int offset_y = 0;

    while ((offset_y < h) && isRowEmpty(tti, offset_y * ny, (offset_y + 1) * ny)) {
      offset_y += 1;
      h -= ny;
    }

    if (w == 0) {
      w = nx;
    }

    if (h == 0) {
      h = ny;
    }

    return fontScaleHelper2(tti, nx, ny, w, h, offset_y, original_w);
  }

  private static GreyByteArrayTranslated fontScaleHelper2(ImageWrapper tti, int nx, int ny, int w, int h, int offset_y, int original_w) {
    int w2 = w / nx;
    int h2 = h / ny;

    if (w2 == 0) {
      w2 = 1;
    }

    if (h2 == 0) {
      h2 = 1;
    }

    byte[] bytes_out = new byte[w2 * h2];
    int[] pixels_in = imageToArray(tti);

    for (int i2 = 0; i2 < w2; i2++) {
      for (int j2 = 0; j2 < h2; j2++) {
        int rootx = i2 * nx;
        int rooty = (j2 + offset_y) * ny;
        int b_total = 0;
        int cnt = nx * ny;

        for (int i3 = 0; i3 < nx; i3++) {
          for (int j3 = 0; j3 < ny; j3++) {
            int pix = pixels_in[rootx + i3 + original_w * (rooty + j3)];

            b_total += (pix & 0xff);
          }
        }

        b_total = b_total / cnt;

        bytes_out[i2 + w2 * j2] = (byte) b_total;
      }
    }

    GreyByteArray gba = new GreyByteArray(bytes_out, w2, h2);

    return new GreyByteArrayTranslated(gba, offset_y);
  }

  private static boolean isColumnEmpty(ImageWrapper tti, int x1, int x2) {
    int w = tti.getWidth();
    int h = tti.getHeight();
    int[] pixels = imageToArray(tti);

    for (int y = h; --y >= 0;) {
      for (int x = x1; x < x2; x++) {
        if ((pixels[x + y * w] & 0xFF) != 0xFF) {
          return false;
        }
      }
    }

    return true;
  }

  private static boolean isRowEmpty(ImageWrapper tti, int y1, int y2) {
    int w = tti.getWidth();
    int[] pixels = imageToArray(tti);

    for (int x = w; --x >= 0;) {
      for (int y = y1; y < y2; y++) {
        if ((pixels[x + y * w] & 0xFF) != 0xFF) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Scale - makes images smaller by powers of two only...
   * scale factors = 0,0 -> normal size...
   */

  public static ImageWrapper scaleDown(ImageWrapper tti, int log2_x_scale, int log2_y_scale) {
    int x_scale = 1 << log2_x_scale;
    int y_scale = 1 << log2_y_scale;

    if ((x_scale == 1) && (y_scale == 1)) {
      return tti;
    }

    int w = tti.getWidth();
    int h = tti.getHeight();
    int w2 = w >>> log2_x_scale;
    int h2 = h >>> log2_y_scale;
    int log_both = log2_x_scale + log2_y_scale;

    if (w2 == 0) {
      w2 = 1;
    }

    if (h2 == 0) {
      h2 = 1;
    }

    int[] pixels_out = new int[w2 * h2];
    int[] pixels4 = imageToArray(tti);
    int root_x;
    int root_y;
    int root_i = 0;
    int root_i2 = 0;

    root_y = 0;
    int offset_to_next_line = w - x_scale;

    for (int j2 = 0; j2 < h2; j2++) {
      root_x = 0;
      for (int i2 = 0; i2 < w2; i2++) {
        int r_total = 0;
        int g_total = 0;
        int b_total = 0;

        root_i2 = root_x + w * root_y; // optimise...
        for (int j3 = 0; j3 < y_scale; j3++) {
          for (int i3 = 0; i3 < x_scale; i3++) {
            int pix = pixels4[root_i2++];

            b_total += (pix & 0xff);
            g_total += (pix & 0xff00);
            r_total += (pix & 0xff0000);
          }

          root_i2 += offset_to_next_line;
        }

        pixels_out[root_i++] = 0xFF000000 | ((b_total >>> log_both) & 0xFF) | ((g_total >>> log_both) & 0xFF00) | ((r_total >>> log_both) & 0xFF0000);
        root_x += x_scale;
      }

      root_y += y_scale;
    }

    return new ImageWrapper(pixels_out, w2, h2);
  }

  // has side effects...
  // grab pixels into an array...
  private static int[] imageToArray(ImageWrapper i) {
    if (i == null) {
      // throw exception... FIZ
      Log.log("Null pointer problems in ImageProcessor");
      return null;
    } else {
      return i.getArray();
    }
  }

  /**
   * Scale - makes images smaller...
   * scale factors = 1024 => normal size...
   */
  private static ImageWrapper zzzscaleSlow(ImageWrapper tti, int x_scale, int y_scale) {
    int w = tti.getWidth();
    int h = tti.getHeight();
    int w2 = (w * x_scale) >> 10;
    int h2 = (h * y_scale) >> 10;

    if (w2 == 0) {
      w2 = 1;
    }

    if (h2 == 0) {
      h2 = 1;
    }

    int[] pixels_out = new int[w2 * h2]; // out
    int[] pixels4 = imageToArray(tti);

    // arrays for easy access...

    int[][] r = new int[w][h]; // in
    int[][] g = new int[w][h]; // in
    int[][] b = new int[w][h]; // in
    int[][] a = new int[w][h]; // in

    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        int pix = pixels4[i + w * j];
        int alpha = pix >>> 24;

        b[i][j] = (pix & 0xff) * alpha;
        g[i][j] = ((pix >> 8) & 0xff) * alpha;
        r[i][j] = ((pix >> 16) & 0xff) * alpha;
        a[i][j] = alpha;
      }
    }
    int x;
    int y;
    int x2;
    int y2;
    int x_inc;
    int y_inc;

    //int maxx, maxy;
    //maxx = w << 10;
    //maxy = h << 10;
    x_inc = (1 << 20) / x_scale;
    y_inc = (1 << 20) / y_scale;
    for (int j2 = 0; j2 < h2; j2++) {
      y = (j2 << 20) / y_scale;
      for (int i2 = 0; i2 < w2; i2++) {
        x = (i2 << 20) / x_scale;
        int r_total = 0;
        int g_total = 0;
        int b_total = 0;
        int a_total = 0;
        int cnt = 0;

        for (int j3 = 0; j3 < 4; j3++) {
          y2 = y + ((j3 * y_inc) >> 2);
          for (int i3 = 0; i3 < 4; i3++) {
            x2 = x + ((i3 * x_inc) >> 2);
            if ((x2 >> 10) < w) {
              if ((y2 >> 10) < h) {
                int alpha = a[x2 >> 10][y2 >> 10];

                b_total += b[x2 >> 10][y2 >> 10] * alpha;
                g_total += g[x2 >> 10][y2 >> 10] * alpha;
                r_total += r[x2 >> 10][y2 >> 10] * alpha;
                a_total += alpha;
                cnt++;
              }
            }
          }
        }

        int divisor = a_total;

        if (divisor != 0) {
          r_total = (r_total >> 8) / divisor;
          g_total = (g_total >> 8) / divisor;
          b_total = (b_total >> 8) / divisor;
        } else {
          r_total = 0;
          g_total = 0;
          b_total = 0;
        }
        a_total = a_total / cnt;
        pixels_out[i2 + w2 * j2] = b_total | g_total << 8 | r_total << 16 | a_total << 24;
      }
    }
    return new ImageWrapper(pixels_out, w2, h2);
  }
}

//  /**
//   * Makes images smaller (by integer scale factors only)...
//   */
//  public static ImageWrapperTranslated fontScale(ImageWrapper tti, int nx, int ny) {
//    int w = tti.getWidth();
//    int h = tti.getHeight();
//    int original_w = w;
//
//    // needs to die...
//    while ((h >= ny) && isRowEmpty(tti, h - ny, h)) {
//      h -= ny;
//    }
//
//    // needs to die...
//    while ((w >= nx) && isColumnEmpty(tti, w - nx, w)) {
//      w -= nx;
//    }
//
//    int offset_y = 0;
//
//    while ((offset_y < h) && isRowEmpty(tti, offset_y * ny, (offset_y + 1) * ny)) {
//      offset_y += 1;
//      h -= ny;
//    }
//
//    if (w == 0) {
//      w = nx;
//    }
//
//    if (h == 0) {
//      h = ny;
//    }
//
//    return fontScaleHelper(tti, nx, ny, w, h, offset_y, original_w);
//  }
//
//  private static ImageWrapperTranslated fontScaleHelper(ImageWrapper tti, int nx, int ny, int w, int h, int offset_y, int original_w) {
//    int w2 = w / nx;
//    int h2 = h / ny;
//
//    if (w2 == 0) {
//      w2 = 1;
//    }
//
//    if (h2 == 0) {
//      h2 = 1;
//    }
//
//    int[] pixels_out = new int[w2 * h2];
//    int[] pixels4 = imageToArray(tti);
//
//    for (int i2 = 0; i2 < w2; i2++) {
//      for (int j2 = 0; j2 < h2; j2++) {
//        int rootx = i2 * nx;
//        int rooty = (j2 + offset_y) * ny;
//        int b_total = 0;
//        int cnt = nx * ny;
//
//        for (int i3 = 0; i3 < nx; i3++) {
//          for (int j3 = 0; j3 < ny; j3++) {
//            int pix = pixels4[rootx + i3 + original_w * (rooty + j3)];
//
//            b_total += (pix & 0xff);
//          }
//        }
//        b_total = b_total / cnt;
//        int transparent = (b_total == 0xFF) ? 0 : 0xFF000000;
//
//        //transparent = 0xFF000000;
//        pixels_out[i2 + w2 * j2] = b_total | b_total << 8 | b_total << 16 | transparent;
//      }
//    }
//
//    ImageWrapper img = new ImageWrapper(pixels_out, w2, h2);
//
//    return new ImageWrapperTranslated(img, offset_y);
//  }
