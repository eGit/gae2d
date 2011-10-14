package org.fonteditor.springs;

import org.fonteditor.elements.curves.Curve;
import org.fonteditor.elements.paths.FEPath;
import org.fonteditor.elements.paths.FEPathList;
import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.utilities.callback.CallBackWithReturn;
import org.fonteditor.utilities.general.For;
import org.fonteditor.utilities.random.JUR;

/**
 * Utility methods to construct lists of Springs from outlines...
 */

public class SpringMaker implements SpringConstants {
  private static final int SPRING_THRESHOLD = 0x1800;
  private static final int SPRING_THRESHOLD_SQUARED = SPRING_THRESHOLD * SPRING_THRESHOLD;

  private JUR rnd = new JUR();
  private SpringManager spring_manager;
  private FEPathList fepathlist;
  private FEPointList fepointlist;

  public SpringMaker(FEPathList fepathlist, FEPointList fepointlist) {
    For.get(fepathlist);
    For.get(fepointlist);
    
    if (USE_SPRINGS) {
      this.fepathlist = fepathlist;
      this.fepointlist = fepointlist;
      spring_manager = new SpringManager();
    }
  }

  public SpringManager makeSprings() {
    if (USE_SPRINGS) {
      makeSpringFromPoints();
      makeSpringFromPaths();
    }
    return spring_manager;
  }

  public void makeSpringFromPoints() {
    if (fepointlist.getNumber() > 2) {
      for (int i = fepointlist.getNumber() << 2; --i >= 0;) {
        makeSpring();
      }
    }
  }

  public void makeSpringFromPaths() {
    for (int i = 0; i < fepathlist.getNumber(); i++) {
      makeSpringFromPath(fepathlist.getPath(i));
    }
  }

  public void makeSpringFromPath(FEPath p) {
    int length = p.getNumberOfCurves();

    if (length > 2) {
      for (int i = 1; i < length - 1; i++) {
        makeSpringFromThreeCurves(p.getCurve(i - 1), p.getCurve(i), p.getCurve(i + 1));
      }
      makeSpringFromThreeCurves(p.getCurve(length - 1), p.getCurve(0), p.getCurve(1));
      makeSpringFromThreeCurves(p.getCurve(length - 2), p.getCurve(length - 1), p.getCurve(0));
    }
  }

  private void makeSpringFromThreeCurves(Curve curve1, Curve curve2, Curve curve3) {
    if ((curve1.isStraight()) && (curve2.isStraight()) && (curve3.isStraight())) {
      FEPoint p1 = curve3.returnStartPoint();
      FEPoint p2 = curve2.returnStartPoint();

      // check distance...
      if (p1.quickDistanceFrom(p2) <= SPRING_THRESHOLD) {
        // check h or v...
        if ((p1.getX() == p2.getX()) || (p1.getY() == p2.getY())) {
          add(p1, p2);
        }
      }
    }
  }

  public void makeSpring() {
    if (fepointlist.getNumber() > 2) {
      int idx_p1 = rnd.nextInt(fepointlist.getNumber());
      int idx_p2 = rnd.nextInt(fepointlist.getNumber());

      idx_p2 = findNearestPoint(idx_p1, idx_p2);
      if (idx_p1 != idx_p2) {
        idx_p1 = findNearestPoint(idx_p2, idx_p1);
        if (idx_p1 != idx_p2) {
          idx_p2 = findNearestPoint(idx_p1, idx_p2);
          if (idx_p1 != idx_p2) {
            idx_p1 = findNearestPoint(idx_p2, idx_p1);
            if (idx_p1 != idx_p2) {
              FEPoint fep1 = fepointlist.getPoint(idx_p1);
              FEPoint fep2 = fepointlist.getPoint(idx_p2);

              if (fep1.squaredDistanceFrom(fep2) < SPRING_THRESHOLD_SQUARED) {
                // is it headed outwards from point 1?
                LocalPathAndIndex loc1 = findLocalPathAndIndex(fep1);
                FEPath path1 = loc1.getPath();
                int index1 = loc1.getIndex();
                FEPoint fep1_plus = path1.safelyGetPoint(index1 + 1);
                FEPoint fep1_minus = path1.safelyGetPoint(index1 - 1);
                if (isClockwise(fep1, fep2, fep1_plus, fep1_minus)) {
                  // is it headed outwards from point 2?
                  LocalPathAndIndex loc2 = findLocalPathAndIndex(fep2);
                  FEPath path2 = loc2.getPath();
                  int index2 = loc2.getIndex();
                  FEPoint fep2_plus = path2.safelyGetPoint(index2 + 1);
                  FEPoint fep2_minus = path2.safelyGetPoint(index2 - 1);
                  if (isClockwise(fep2, fep1, fep2_plus, fep2_minus)) {
                    add(fep1, fep2);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  boolean isClockwise(FEPoint fep1, FEPoint fep2, FEPoint fep3, FEPoint fep4) {
    int x = fep1.getX();
    int y = fep1.getY();

    return isClockwise(fep2.getX() - x, fep2.getY() - y, fep3.getX() - x, fep3.getY() - y, fep4.getX() - x, fep4.getY() - y);
  }

  boolean isClockwise(int dx1, int dy1, int dx2, int dy2, int dx3, int dy3) {
    int theta1 = getAngle(dx1, dy1);
    int theta2 = getAngle(dx2, dy2);
    int theta3 = getAngle(dx3, dy3);

    if ((theta1 >= theta2) && (theta2 >= theta3)) {
      return true;
    }

    if ((theta2 >= theta3) && (theta3 >= theta1)) {
      return true;
    }

    if ((theta3 >= theta1) && (theta1 >= theta2)) {
      return true;
    }

    return false;
  }

  int getAngle(int dx, int dy) {
    if (dx == 0) {
      if (dy > 0) {
        return 0x80;
      } else {
        return 0x00;
      }
    }

    if (dy == 0) {
      if (dx > 0) {
        return 0x40;
      } else {
        return 0xC0;
      }
    }

    if (dx > 0) {
      if (dy > 0) {
        if (dx == dy) {
          return 0x60;
        }
        if (dx > dy) {
          return 0x50;
        } else { // dx < dy
          return 0x70;
        }
      } else { // dy < 0
        if (dx == -dy) {
          return 0x20;
        }
        if (dx > -dy) {
          return 0x30;
        } else { // dx > -dy
          return 0x10;
        }
      }
    } else { // dx < 0
      if (dy > 0) {
        if (-dx == dy) {
          return 0xA0;
        }
        if (-dx > dy) {
          return 0xB0;
        } else {
          return 0x90;
        }
      } else {
        if (-dx == -dy) {
          return 0xE0;
        }
        if (-dx > -dy) {
          return 0xD0;
        } else {
          return 0xF0;
        }
      }
    }
  }

  private void add(FEPoint fep1, FEPoint fep2) {
    Spring s = new Spring(fep1, fep2);

    spring_manager.add(s);
  }

  private int findNearestPoint(int idx_p1, int idx_p2) { //, GlyphDisplayOptions gdo) {
    int dir = rnd.nextBoolean() ? -1 : 1;
    boolean flipped = false;
    FEPoint p1 = fepointlist.getPoint(idx_p1);
    FEPoint p2 = fepointlist.getPoint(idx_p2);
    int max = p1.squaredDistanceFrom(p2);
    FEPath path = fepathlist.getPath(p2);
    FEPointList fepl = path.getFEPointList();

    idx_p2 = fepl.getIndexOf(p2);
    for (; true;) {
      int new_idx_p2 = idx_p2 + dir;

      if (new_idx_p2 < 0) {
        new_idx_p2 = fepl.getNumber() - 1;
      }
      if (new_idx_p2 >= fepl.getNumber()) {
        new_idx_p2 = 0;
      }
      p2 = fepl.getPoint(new_idx_p2);
      int dist = p1.squaredDistanceFrom(p2);

      if (dist <= max) {
        max = dist;
        idx_p2 = new_idx_p2;
      } else {
        if (flipped) {
          return fepointlist.getIndexOf(fepl.getPoint(idx_p2));
        } else {
          dir = -dir;
          flipped = true;
        }
      }
    }
  }

  LocalPathAndIndex findLocalPathAndIndex(final FEPoint fepoint) {
    return (LocalPathAndIndex) fepathlist.executeOnEachPath(new CallBackWithReturn() {
      public Object callback(Object o) {
        FEPath fepath = (FEPath) o;
        if (fepath.contains(fepoint)) {
          return new LocalPathAndIndex(fepath, fepath.indexOf(fepoint));
        }

        return null;
      }
    });
  }

  class LocalPathAndIndex {
    private FEPath path;
    private int index;

    public LocalPathAndIndex(FEPath path, int index) {
      this.path = path;
      this.index = index;
    }

    void setPath(FEPath path) {
      this.path = path;
    }

    FEPath getPath() {
      return path;
    }

    void setIndex(int index) {
      this.index = index;
    }

    int getIndex() {
      return index;
    }
  }
}