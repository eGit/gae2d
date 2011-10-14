package org.fonteditor.instructions;

import org.fonteditor.elements.paths.FEPath;
import org.fonteditor.elements.paths.FEPathList;
import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.options.display.DisplayOptions;
import org.fonteditor.utilities.callback.CallBack;
import org.fonteditor.utilities.log.Log;

import com.jgraph.gaeawt.java.awt.Point;

public class InstructionStream implements InstructionConstants {
  private int[] i;
  private int[] new_array;
  private int number = 0;
  private FEPointList fepointlist; // needs to go in the cache...
  private FEPathList fepathlist; // needs to go in the cache...
  private FEPath fep; //  try to get rid of this...

  private int instruction_pointer;

  private boolean quit = false;
  private boolean needs_remaking = true; // needs to go in the cache...

  private static final int INCREMENT = 16; // need generic collection...

  public InstructionStream() {
    instruction_pointer = 0;
    number = 10;
    i = new int[number];
    resetPointAndPathLists();
  }

  public void resetPointAndPathLists() {
    fepointlist = new FEPointList();
    fepathlist = new FEPathList();
    //Log.log("resetPointAndPathLists");
  }

  int getNextInstruction() {
    return i[instruction_pointer++];
  }

  public void add(int ins) {
    if (instruction_pointer >= number) {
      makeMore();
    }

    i[instruction_pointer++] = ins;
  }

  public void dragStroke(Point from, Point to, int ip, DisplayOptions gdo) {
    int dx = gdo.getCoords().rescaleX(to.x - from.x);
    int dy = gdo.getCoords().rescaleY(to.y - from.y);

    translateStroke(ip, dx, dy);
  }

  public void dragPoint(Point from, Point to, int ip, DisplayOptions gdo) {
    int dx = gdo.getCoords().rescaleX(to.x - from.x);
    int dy = gdo.getCoords().rescaleY(to.y - from.y);

    translatePoint(ip, dx, dy);
  }

  void translatePoint(int ip, int dx, int dy) {
    i[ip] += dx;
    ip++;
    i[ip] += dy;
    ip++;
  }

  void scalePoint(int ip, final float fx, final float fy) {
    i[ip] *= fx;
    ip++;
    i[ip] *= fy;
    ip++;
  }

  void setPoint(int ip, final int x, final int y) {
    i[ip++] = x;
    i[ip++] = y;
  }

  public void setPoint(final FEPoint p, final int x, final int y) {
    setPoint(p.getInstructionPointer(), x, y);
  }

  public void translateAll(final int dx, final int dy) {
    fepointlist.executeOnEachPoint(new CallBack() {
      public void callback(Object o) {
        FEPoint p = (FEPoint) o;

        translatePoint(p.getInstructionPointer(), dx, dy);
      }
    });
  }

  public void scaleAll(final float fx, final float fy) {
    fepointlist.executeOnEachPoint(new CallBack() {
      public void callback(Object o) {
        FEPoint p = (FEPoint) o;

        scalePoint(p.getInstructionPointer(), fx, fy);
      }
    });
  }

  private void invalidatePolygons() {
    fepathlist.executeOnEachPath(new CallBack() {
      public void callback(Object o) {
        FEPath p = (FEPath) o;

        p.invalidatePolygons();
      }
    });
  }

  public void executeToMakeGlyph(boolean tracing) {
    //Log.log("executeToMakeGlyph");
    invalidatePolygons();
    quit = false;
    instruction_pointer = 0;
    InstructionArray.setLogging(tracing);
    do {
      int instruction_number = i[instruction_pointer++];
      //Log.log("instruction_pointer" + instruction_pointer + " instruction_number: " + instruction_number);
      InstructionArray.getIns(instruction_number).execute(this);
    } while (!quit);
    InstructionArray.setLogging(false);
  }

  private void copy(InstructionStream is_out, int ip) {
    //int moves = 0;

    quit = false;
    instruction_pointer = ip;
    do {
      int type = i[instruction_pointer++];

      //if (type == MOVE_TO) {
      //moves++;
      //}
      //Log.log("type:" + type);
      //Log.log("InstructionArray.iea[type]:" + InstructionArray.iea[type]);
      InstructionArray.getIns(type).copy(this, is_out);
    } while (!quit);
    //Log.log("in: " + (instruction_pointer - ip));
    //Log.log("out: " + (is_out.number - out_start));
  }

  private void translateStroke(int ip, int dx, int dy) {
    quit = false;
    instruction_pointer = ip;
    do {
      // ***very*** crude...
      int type = i[instruction_pointer++];

      if (type == CLOSE_PATH) {
        quit = true;
      } else {
        InstructionArray.getIns(type).translate(this, dx, dy);
      }
    } while (!quit);
  }

  private void makeMore() {
    new_array = new int[number + INCREMENT];
    System.arraycopy(i, 0, new_array, 0, number);
    i = new_array;
    number += INCREMENT;
  }

  public boolean isInNeedOfRemaking() {
    return needs_remaking;
  }

  public void setRemakeFlag(boolean needs_remaking) {
    this.needs_remaking = needs_remaking;
  }

  public void add(FEPath p, InstructionStream is_out) {
    copy(is_out, p.getInstructionPointer());
  }

  /**
   * **** Don't call this directly ****
   */
  public FEPathList getFEPathList() {
    return fepathlist;
  }

  /**
   * **** Don't call this directly ****
   */
  public FEPointList getFEPointList() {
    return fepointlist;
  }

  void translateOnePoint(int dx, int dy) {
    i[instruction_pointer++] += dx;
    i[instruction_pointer++] += dy;
  }

  public int getInstructionAt(int offset) {
    return i[offset];
  }

  public void setInstructionAt(int offset, int value) {
    i[offset] = value;
  }

  public void deleteInstructionsAt(int offset, int length) {
    System.arraycopy(i, offset + length, i, offset, number - offset - length);
    number = number - length;
  }

  public void setInstructionPointer(int instruction_pointer) {
    this.instruction_pointer = instruction_pointer;
  }

  public int getInstructionPointer() {
    return instruction_pointer;
  }

  void setFep(FEPath fep) {
    this.fep = fep;
  }

  FEPath getFep() {
    return fep;
  }

  void setQuit(boolean quit) {
    this.quit = quit;
  }

  boolean isQuitting() {
    return quit;
  }

  public int getIndexOfCurveContainingPoint(FEPoint point) {
    int index_of_point = point.getInstructionPointer();
    instruction_pointer = 0;
    int type;

    do {
      int return_value = instruction_pointer;
      type = i[instruction_pointer++];

      instruction_pointer += InstructionArray.getIns(type).numberOfCoordinates();
      if (instruction_pointer > index_of_point) {
        return return_value;
      }
    } while (type != END_GLYPH);

    throw new RuntimeException("Point not found");
  }

  public void dump() {
    instruction_pointer = 0;
    int type;

    do {
      type = i[instruction_pointer++];
      Log.log("INS:" + InstructionArray.getIns(type));
      instruction_pointer += InstructionArray.getIns(type).numberOfCoordinates();
    } while (type != END_GLYPH);
  }
}

//  void translateStroke(int ip, int dx, int dy) {
//    translate(ip, dx, dy);
////    Instruction ins;
////    do {
////      ins = i[ip++];
////      if ((ins.type == Instruction.POINT_X)) {
////        ins.value += dx;
////      }
////
////      if ((ins.type == Instruction.POINT_Y)) {
////        ins.value += dy;
////      }
////    } while (ins.type != Instruction.CLOSE_PATH);
//  }

//  public void executeToMakeGlyph() {
//    invalidatePolygons();
//    quit = false;
//    instruction_pointer = 0;
//
//    do {
//      int instruction_number = i[instruction_pointer++];
//      //Log.log("instruction_pointer" + instruction_pointer + " instruction_number: " + instruction_number);
//      InstructionArray.getIns(instruction_number).execute(this);
//    } while (!quit);
//  }
