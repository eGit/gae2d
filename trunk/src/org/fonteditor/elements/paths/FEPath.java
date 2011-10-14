package org.fonteditor.elements.paths;

import org.fonteditor.elements.curves.Curve;
import org.fonteditor.elements.curves.CurveBezierQuadratic;
import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.graphics.WideLine;
import org.fonteditor.instructions.InstructionStream;
import org.fonteditor.options.coords.Coords;
import org.fonteditor.options.display.DisplayOptions;
import org.fonteditor.options.pen.Pen;
import org.fonteditor.sliders.Sliders;
import org.fonteditor.utilities.log.Log;

import com.jgraph.gaeawt.java.awt.Color;
import com.jgraph.gaeawt.java.awt.Graphics;
import com.jgraph.gaeawt.java.awt.Point;
import com.jgraph.gaeawt.java.awt.Polygon;
import com.jgraph.gaeawt.java.awt.Rectangle;

/**
 * A single closed loop.
 * Part of a glyph.
 */

public class FEPath implements PathConstants {
  private static final int INCREMENT = 16;
  private static final int LINE_WIDTH = 0x100;
  private static final int LINE_WIDTH_THIN = 0xC0;

  private static final int CIRCULAR = 0x1234; // MAGIC :-(
  private static final int SQUARE = 0x5678; // MAGIC :-(

  private static final Color DARK_BLUE = new Color(0x0000A0);
  private static final Color DARK_GREEN = new Color(0x00A000);

  private static int cfg_shape = CIRCULAR;

  private int number_of_curves;
  private Curve[] curve;
  private Polygon internal_polygon; // needs to go in the cache...
  private Polygon graphics_polygon; // needs to go in the cache...
  private boolean direction;
  private boolean got_direction = false;
  private int instruction_pointer; // for use keeping track of things in the editor...
  private InstructionStream instruction_stream; // for use keeping track of things in the editor...
  private FEPointList fepl;
  private Sliders sliders;

  public FEPath(InstructionStream is) {
    reset();
    curve = new Curve[number_of_curves];
    instruction_stream = is;
    instruction_pointer = is.getInstructionPointer() - 1;
  }

  private void reset() {
    number_of_curves = 0;
  }

  public void add(Curve e) {
    if (number_of_curves >= curve.length) {
      makeMore();
    }
    curve[number_of_curves++] = e;
  }

  private void makeMore() {
    Curve[] new_array = new Curve[number_of_curves + INCREMENT];

    System.arraycopy(curve, 0, new_array, 0, curve.length);
    curve = new_array;
  }

  private void constructPolygons(Coords c) {
    internal_polygon = null; //???
    getInternalPolygon();
    constructGraphicsPolygon(c);
  }

  private Polygon getInternalPolygon() {
    if (internal_polygon == null) {
      internal_polygon = constructInternalPolygon();
    }
    return internal_polygon;
  }

  private Polygon constructInternalPolygon() {
    internal_polygon = new Polygon();
    for (int i = 0; i < number_of_curves - 1; i++) {
      curve[i].addFinalPointsToPolygon(internal_polygon);
    }

    curve[number_of_curves - 1].addFinalPointsToPolygon(internal_polygon);
    return internal_polygon;
  }

  private Polygon getGraphicsPolygon(Coords c) {
    if (graphics_polygon == null) {
      graphics_polygon = constructGraphicsPolygon(c);
    }
    return graphics_polygon;
  }

  private Polygon constructGraphicsPolygon(Coords c) {
    graphics_polygon = new Polygon();
    getInternalPolygon();
    for (int i = 0; i < internal_polygon.npoints; i++) {
      graphics_polygon.addPoint(c.scaleX(internal_polygon.xpoints[i]), c.scaleY(internal_polygon.ypoints[i]));
    }
    return graphics_polygon;
  }

  public void draw(Graphics g, DisplayOptions gdo, FEPointList fepl, int number_of_path) {
    makePolygonIfNecessary(gdo.getCoords());
    getGraphicsPolygon(gdo.getCoords());
    if (!got_direction) {
      direction = isClockwise(); // base this on as few points as possible...
      got_direction = true;
    }

    Color fill_colour = direction ? Color.white : Color.black;
    Color outline_colour = Color.black;

    if (gdo.isFill()) {
      g.setColor(fill_colour);
      fillPolygon(g);
    }

    if (gdo.getPen().getWidth() != 0) {
      g.setColor(outline_colour);
      drawPolygonWithWideLines(g, gdo.getPen(), gdo.getCoords());
    }

    if (gdo.isShowPoints()) {
      drawConstructionLines(g, gdo.getCoords());
      drawConstructionPoints(g, fepl, gdo.getCoords());
    }
  }

  private void makePolygonIfNecessary(Coords c) {
    if (internal_polygon == null) {
      constructPolygons(c);
    }
  }

  private void fillPolygon(Graphics g) {
    g.fillPolygon(graphics_polygon);
  }

  private void drawPolygonWithWideLines(Graphics g, Pen pen, Coords c) {
    WideLine.drawPolygon(g, internal_polygon, pen, c);
  }

  boolean isInsidePolygon(Point pt, Coords c) {
    return getGraphicsPolygon(c).contains(pt);
  }

  boolean isInsideBox(int min_x, int min_y, int max_x, int max_y, Coords c) {
    makePolygonIfNecessary(c);
    for (int i = 0; i < internal_polygon.npoints; i++) {
      if (internal_polygon.xpoints[i] > max_x) {
        return false;
      }

      if (internal_polygon.xpoints[i] < min_x) {
        return false;
      }

      if (internal_polygon.ypoints[i] > max_y) {
        return false;
      }

      if (internal_polygon.ypoints[i] < min_y) {
        return false;
      }
    }

    return true;
  }

  // min and max could be cached here...
  public void invalidatePolygons() {
    internal_polygon = null;
    invalidateGraphics();
  }

  public void invalidateGraphics() {
    graphics_polygon = null;
  }

  private void drawConstructionPoints(Graphics g, FEPointList fepl, Coords c) {
    for (int i = 0; i < number_of_curves; i++) {
      FEPoint p1;
      FEPoint p2;
      FEPoint p3;
      FEPoint p4;
      Curve pe;

      pe = curve[i];
      p1 = pe.returnStartPoint();
      p2 = pe.returnStartControlPoint();
      p3 = pe.returnEndControlPoint();
      p4 = pe.returnEndPoint();

      if (p3 != null) {
        plotControlPoint(g, p3, DARK_BLUE, Color.cyan, c);
      }

      if (p2 != null) {
        plotControlPoint(g, p2, DARK_BLUE, Color.cyan, c);
      }

      plotControlPoint(g, p1, DARK_GREEN, Color.green, c);
      plotControlPoint(g, p4, DARK_GREEN, Color.green, c);
    }
  }

  private void drawConstructionLines(Graphics g, Coords c) {
    for (int i = 0; i < number_of_curves; i++) {
      FEPoint p1;
      FEPoint p2;
      FEPoint p3;
      FEPoint p4;
      Curve pe;

      pe = curve[i];
      p1 = pe.returnStartPoint();
      p2 = pe.returnStartControlPoint();
      p3 = pe.returnEndControlPoint();
      p4 = pe.returnEndPoint();
      if (p3 != null) {
        g.setColor(Color.gray);
        drawWideLine(g, p3, p4, LINE_WIDTH_THIN, c);
      }
      if (p2 != null) {
        g.setColor(Color.gray);
        drawWideLine(g, p1, p2, LINE_WIDTH_THIN, c);

        // FIZ instanceof
        if (pe instanceof CurveBezierQuadratic) {
          g.setColor(Color.gray);
          drawWideLine(g, p2, p4, LINE_WIDTH_THIN, c);
        }
      }
    }
  }

  private void plotControlPoint(Graphics g, FEPoint p, Color c_in, Color c_out, Coords c) {
    g.setColor(c_out);
    // drawRect(g, p.x - cp_offset, p.y - cp_offset, cp_size, cp_size, line_width, line_width);
    fillCircle(g, p, CP_SIZE, c);
    g.setColor(c_in);
    // fillRect(g, p.x - cp_offset, p.y - cp_offset, cp_size, cp_size);
    fillCircle(g, p, CP_SIZE >> 1, c);
  }

  private void ring(Graphics g, FEPoint p, Coords c) {
    g.setColor(Color.red);
    if (cfg_shape == SQUARE) {
      Rectangle r = new Rectangle(p.getX() - CP_SIZE, p.getY() - CP_SIZE, CP_SIZE << 1, CP_SIZE << 1);
      drawRect(g, r, LINE_WIDTH, LINE_WIDTH, c);
    } else {
      drawCircle(g, p, (CP_SIZE * 6) >> 3, CP_SIZE, c);
    }
  }

  private void drawWideLine(Graphics g, FEPoint p1, FEPoint p2, int width, Coords c) {
    WideLine.renderRound(g, p1, p2, width, c);
  }

  private void fillCircle(Graphics g, FEPoint p, int width, Coords c) {
    int w = width >> 1;

    g.fillOval(c.scaleX(p.getX() - w), c.scaleY(p.getY() - w), c.scaleX(width), c.scaleY(width));
  }

  private void drawCircle(Graphics g, FEPoint p, int inner, int outer, Coords c) {
    int step = Math.min(c.rescaleX(1), c.rescaleY(1));

    step = step >> 2;
    for (int i = inner; i < outer; i += step) {
      g.drawOval(c.scaleX(p.getX() - i), c.scaleY(p.getY() - i), c.scaleX(i << 1), c.scaleY(i << 1));
    }
  }

  private void drawRect(Graphics g, Rectangle r, int width, int height, Coords c) {
    int x1 = c.scaleX(r.x);
    int x2 = c.scaleX(r.x + width);
    int x3 = c.scaleX(r.x + r.width - width);
    int x4 = c.scaleX(r.x + r.width);
    int y1 = c.scaleY(r.y);
    int y2 = c.scaleY(r.y + height);
    int y3 = c.scaleY(r.y + r.height - height);
    int y4 = c.scaleY(r.y + r.height);

    g.fillRect(x1, y1, x4 - x1, y2 - y1); // top...
    g.fillRect(x1, y3, x4 - x1, y4 - y3); // bottom...
    g.fillRect(x1, y2, x2 - x1, y3 - y2); // left...
    g.fillRect(x3, y2, x4 - x3, y3 - y2); // right...
  }

  private void drawCircle(Graphics g, int x, int y, int r, Coords c) {
    int rx = c.scaleX(r);
    int ry = c.scaleY(r);

    g.drawOval(c.scaleX(x) - rx, c.scaleY(y) - ry, rx << 1, ry << 1);
  }

  public boolean isClockwise() {
    return isClockwise(getInternalPolygon());
  }

  // To optimise: use cross product on top point
  private boolean isClockwise(Polygon p) {
    int area = 0;

    for (int i = p.npoints; --i >= 0;) {
      int j = (i == 0) ? p.npoints - 1 : i - 1;

      // the following shifts avoid some bugs...
      area += ((p.xpoints[i] - p.xpoints[j]) >> 1) * ((p.ypoints[i] + p.ypoints[j]) >> 1);
    }
    return area > 0;
  }

  //  private BaseCurve safelyGetPathElement(int i) {
  //    if (i < 0) {
  //      return path_element[number_of_elements + i];
  //    }
  //
  //    if (i >= number_of_elements) {
  //      return path_element[i - number_of_elements];
  //    }
  //
  //    return path_element[i];
  //  }
  // could try caching this...?
  public FEPointList getFEPointList() {
    if (fepl == null) {
      fepl = new FEPointList();
      for (int i = 0; i < number_of_curves; i++) {
        curve[i].simplyAddPoints(fepl);
      }
    }
    return fepl;
  }

  public void addPointsToFEPointList(FEPointList fepl) {
    for (int i = 0; i < number_of_curves; i++) {
      curve[i].simplyAddPoints(fepl);
    }
  }

  public Sliders getSliders() {
    if (sliders == null) {
      sliders = new Sliders(this);
    }

    return sliders;
  }

  void dump() {
    Log.log("  Number of curves:" + number_of_curves);
    for (int i = number_of_curves; --i >= 0;) {
      // Log.log("Curves:" + i);
      Curve cv = curve[i];

      cv.dump();
    }
  }

  public boolean contains(FEPoint p) {
    return indexOf(p) >= 0;
  }

  public int indexOf(FEPoint p) {
    FEPointList fepl = getFEPointList();

    return fepl.indexOf(p);
  }

  public FEPoint safelyGetPoint(int i) {
    FEPointList fepl = getFEPointList();

    return fepl.safelyGetPoint(i);
  }

  public void setNumberOfElements(int number_of_elements) {
    this.number_of_curves = number_of_elements;
  }

  public int getNumberOfCurves() {
    return number_of_curves;
  }

  public void setPathElement(Curve[] path_element) {
    this.curve = path_element;
  }

  public Curve getCurve(int i) {
    return curve[i];
  }

  public void setInstructionPointer(int instruction_pointer) {
    this.instruction_pointer = instruction_pointer;
  }

  public int getInstructionPointer() {
    return instruction_pointer;
  }
}
