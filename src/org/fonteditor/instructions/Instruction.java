package org.fonteditor.instructions;

import org.fonteditor.utilities.general.For;
import org.fonteditor.utilities.log.Log;

/**
 * Represents a single instruction
 */

public class Instruction {
  private int number;
  private String name;

  Instruction(int n, String d) {
    number = n;
    name = d;
  }

  void copy(InstructionStream is_in, InstructionStream is_out) {
    For.get(is_in);
    For.get(is_out);
    Log.log("Raw instruction error (copy)");
  }

  void execute(InstructionStream is) {
    For.get(is);
    Log.log("Raw instruction error (execute)");
  }

  void translate(InstructionStream is_in, int dx, int dy) {
    For.get(is_in);
    For.get(dx);
    For.get(dy);
  }

  public int numberOfCoordinates() {
    return 0;
  }

  public String toString() {
    return name;
  }

  public void logExecution() {
    if (InstructionArray.needsLogStatements()) {
      Log.log("Execute instruction: " + toString());
    }
  }
}
