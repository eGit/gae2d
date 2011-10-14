package org.fonteditor.instructions;

import org.fonteditor.elements.curves.CurveBezierCubic;
import org.fonteditor.elements.curves.CurveBezierQuadratic;
import org.fonteditor.elements.curves.CurveStraightLine;
import org.fonteditor.elements.paths.FEPath;
import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.utilities.general.For;
import org.fonteditor.utilities.log.Log;

public abstract class InstructionArray implements InstructionConstants {
  private static Instruction[] iea;
  private static boolean trace = false;
  private static boolean logging = false;
  private static int last_point_x = 0;
  private static int last_point_y = 0;
  private static FEPoint current_point = new FEPoint(-1, -1);

  static {
    iea = new Instruction[MAX_NUMBER];
    iea[ERROR] = new Instruction(ERROR, "Error") {
      public void execute(InstructionStream is) {
        if (InstructionArray.needsLogStatements()) {
          logExecution();
        }
        For.get(is);
      }

      public void copy(InstructionStream is_in, InstructionStream is_out) {
        For.get(is_in);
        For.get(is_out);
        Log.log("Error (copy)");
      }

      public void save(InstructionStream is) {
        For.get(is);
        Log.log("Error (save)");
      }
    };

    iea[GLYPH_NEXT] = iea[ERROR];
    iea[GLYPH_NUMBER] = iea[ERROR];
    iea[STRAIGHT_LINE] = new Instruction(STRAIGHT_LINE, "StraightLine") {
      public void execute(InstructionStream is) {
        if (InstructionArray.needsLogStatements()) {
          logExecution();
        }

        FEPoint fept1 = is.getFEPointList().add(is);
        int in1x = is.getNextInstruction();

        fept1.setX(in1x);
        int in1y = is.getNextInstruction();

        fept1.setY(in1y);
        is.getFep().add(new CurveStraightLine(getCurrentPoint(), fept1));
        setCurrentPoint(fept1);
      }

      public void copy(InstructionStream is_in, InstructionStream is_out) {
        is_out.add(STRAIGHT_LINE);
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
      }

      public void translate(InstructionStream is, int dx, int dy) {
        is.translateOnePoint(dx, dy);
      }

      public int numberOfCoordinates() {
        return 2;
      }
    };

    iea[QUADRATIC_BEZIER] = new Instruction(QUADRATIC_BEZIER, "QuadraticBezier") {
      public void execute(InstructionStream is) {
        if (InstructionArray.needsLogStatements()) {
          logExecution();
        }

        FEPointList point_list = is.getFEPointList();

        FEPoint fept1 = point_list.add(is);
        int in1x = is.getNextInstruction();

        fept1.setX(in1x);
        int in1y = is.getNextInstruction();

        fept1.setY(in1y);
        FEPoint fept2 = point_list.add(is);
        int in2x = is.getNextInstruction();

        fept2.setX(in2x);
        int in2y = is.getNextInstruction();

        fept2.setY(in2y);
        is.getFep().add(new CurveBezierQuadratic(getCurrentPoint(), fept1, fept2));
        setCurrentPoint(fept2);
      }

      public void copy(InstructionStream is_in, InstructionStream is_out) {
        is_out.add(QUADRATIC_BEZIER);
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
      }

      public void translate(InstructionStream is, int dx, int dy) {
        is.translateOnePoint(dx, dy);
        is.translateOnePoint(dx, dy);
      }

      public int numberOfCoordinates() {
        return 4;
      }
    };

    iea[CUBIC_BEZIER] = new Instruction(CUBIC_BEZIER, "CubicBezier") {
      public void execute(InstructionStream is) {
        if (InstructionArray.needsLogStatements()) {
          logExecution();
        }

        FEPointList point_list = is.getFEPointList();

        FEPoint fept1 = point_list.add(is);
        int in1x = is.getNextInstruction();
        fept1.setX(in1x);

        int in1y = is.getNextInstruction();
        fept1.setY(in1y);
        FEPoint fept2 = point_list.add(is);

        int in2x = is.getNextInstruction();
        fept2.setX(in2x);

        int in2y = is.getNextInstruction();
        fept2.setY(in2y);
        FEPoint fept3 = point_list.add(is);

        int in3x = is.getNextInstruction();
        fept3.setX(in3x);

        int in3y = is.getNextInstruction();
        fept3.setY(in3y);

        is.getFep().add(new CurveBezierCubic(getCurrentPoint(), fept1, fept2, fept3));
        setCurrentPoint(fept3);
      }

      public void copy(InstructionStream is_in, InstructionStream is_out) {
        is_out.add(CUBIC_BEZIER);
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
      }

      public void translate(InstructionStream is, int dx, int dy) {
        is.translateOnePoint(dx, dy);
        is.translateOnePoint(dx, dy);
        is.translateOnePoint(dx, dy);
      }

      public int numberOfCoordinates() {
        return 6;
      }
    };

    iea[OPEN_PATH] = new Instruction(OPEN_PATH, "OpenPath") {
      public void execute(InstructionStream is) {
        if (InstructionArray.needsLogStatements()) {
          logExecution();
        }

        is.setFep(new FEPath(is));
        setCurrentPoint(new FEPoint(0, 0));
      }

      public void copy(InstructionStream is_in, InstructionStream is_out) {
        For.get(is_in);
        is_out.add(OPEN_PATH);
      }
    };

    iea[CLOSE_PATH] = new Instruction(END_GLYPH, "ClosePath") {
      public void execute(InstructionStream is) {
        if (InstructionArray.needsLogStatements()) {
          logExecution();
        }

        // santiy check... 

        FEPath fep = is.getFep();
        int number_of_curves = fep.getNumberOfCurves();
        if (number_of_curves > 0) {

          // fix the first point to be equal to the last point...
          FEPoint end_point = fep.getCurve(number_of_curves - 1).getP4();

          is.getFep().getCurve(0).setP1(end_point);
          is.getFEPathList().add(is.getFep());
        }
      }

      public void copy(InstructionStream is_in, InstructionStream is_out) {
        is_out.add(CLOSE_PATH);
        is_in.setQuit(true);
      }
    };

    iea[END_GLYPH] = new Instruction(END_GLYPH, "EndGlyph") {
      public void execute(InstructionStream is) {
        if (InstructionArray.needsLogStatements()) {
          logExecution();
        }
        is.setQuit(true);
      }

      public void copy(InstructionStream is_in, InstructionStream is_out) {
        is_out.add(END_GLYPH);
        is_in.setQuit(true);
      }
    };

    iea[END_FONT] = new Instruction(END_FONT, "EndFont") {
      public void execute(InstructionStream is) {
        if (InstructionArray.needsLogStatements()) {
          logExecution();
        }
        is.setQuit(true);
      }

      public void copy(InstructionStream is_in, InstructionStream is_out) {
        is_out.add(END_GLYPH);
        is_in.setQuit(true);
      }
    };
  }

  static void resetPoints() {
    last_point_x = 0;
    setLastPointY(0);
  }

  public static Instruction getIns(int instruction_number) {
    return iea[instruction_number];
  }

  static void setTrace(boolean trace) {
    InstructionArray.trace = trace;
  }

  static boolean isTracing() {
    return trace;
  }

  static void setLastPointX(int last_point_x) {
    InstructionArray.last_point_x = last_point_x;
  }

  static int getLastPointX() {
    return last_point_x;
  }

  static void setLastPointY(int last_point_y) {
    InstructionArray.last_point_y = last_point_y;
  }

  static int getLastPointY() {
    return last_point_y;
  }

  static void setCurrentPoint(FEPoint current_point) {
    InstructionArray.current_point = current_point;
  }

  static FEPoint getCurrentPoint() {
    return current_point;
  }

  static void setLogging(boolean logging) {
    InstructionArray.logging = logging;
  }

  static boolean isLogging() {
    return logging;
  }

  static boolean needsLogStatements() {
    return logging | trace;
  }
}
