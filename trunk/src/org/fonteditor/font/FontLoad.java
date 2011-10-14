package org.fonteditor.font;

import org.fonteditor.instructions.Instruction;
import org.fonteditor.instructions.InstructionArray;
import org.fonteditor.instructions.InstructionConstants;
import org.fonteditor.utilities.resources.ResourceLoader;

public class FontLoad implements InstructionConstants {
  private static int offset;
  private static int last_point_x = 0;
  private static int last_point_y = 0;
 // private static String font_name;

  public static FEFont load(String font_name) {
    //Log.log("Load font file:" + font_name);
    //this.font_name = font_name;
    byte[] ba = ResourceLoader.getByteArray("font.zip", "fonts/", font_name);

    //Log.log("Load font file:OUT");
    //Log.log("Length:" + ba.length);
    return load(ba, font_name);
  }

  public static FEFont load(byte[] ba, String font_name) {
    offset = 0;
    int min = 32; // ?
    int max = 128; // ?
    FEFont font = new FEFont(font_name, min, max);

    for (int i = min; i < max; i++) {
      FEGlyph glyph = loadGlyph(font, ba);

      font.getGlyphArray().add(glyph, i);
    }
    return font;
  }

  public static FEGlyph loadGlyph(FEFont font, byte[] ba) {
    //    int number = 256;
    FEGlyph glyph = new FEGlyph(font, -1); // !?!?

    resetPoints();
    boolean quit = false;

    do {
      int ins = ba[offset++];

      if (ins == END_GLYPH) {
        quit = true;
      }
      if (ins == GLYPH_NUMBER) {
        offset++; // Mistakenly *assumes* byte!!!!???? LATER
      } else if (ins != GLYPH_NEXT) {
        // do nothing...
        Instruction instruction = InstructionArray.getIns(ins);

        //Log.log("instruction:" + instruction.name);
        //Log.log("number_of_coordinates:" + instruction.numberOfCoordinates());
        glyph.getInstructionStream().add(ins);
        for (int i = 0; i < instruction.numberOfCoordinates(); i++) {
          int v = getValue(ba);

          if ((i & 1) == 0) {
            last_point_x = (last_point_x + v) & 0xFFFF;
            v = last_point_x;
          } else {
            last_point_y = (last_point_y + v) & 0xFFFF;
            v = last_point_y;
          }
          glyph.getInstructionStream().add(v);
        }
      }
    } while (!quit);
    
    glyph.resetRemakeFlag();
    return glyph;
  }

  static int getValue(byte[] ba) {
    int v = ba[offset++] & 0xFF;

    if (v != 0x80) {
      return v << 8;
    }
    
    int v2 = ba[offset++] & 0xFF;

    if (v2 == 0x00) {
      return 0x8000;
    }
    
    int v3 = ba[offset++] & 0xFF;

    return (v3 << 8) | v2;
  }

  private static void resetPoints() {
    last_point_x = 0;
    last_point_y = 0;
  }

  static void setOffset(int offset) {
    FontLoad.offset = offset;
  }

  static int getOffset() {
    return offset;
  }

  static void setLastPointX(int last_point_x) {
    FontLoad.last_point_x = last_point_x;
  }

  static int getLastPointX() {
    return last_point_x;
  }

  static void setLastPointY(int last_point_y) {
    FontLoad.last_point_y = last_point_y;
  }

  static int getLastPointY() {
    return last_point_y;
  }
}