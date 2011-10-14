package org.fonteditor.hinter;

// This class is not used... FIZ

//import org.fonteditor.elements.paths.FEPath;
//import org.fonteditor.elements.points.FEPointList;
//import org.fonteditor.font.FEGlyph;
//
////import java.awt.Rectangle;
//
//class HintingGroup {
//  private FEGlyph glyph;
//  private FEPath path;
//  private FEPointList fepl;
//
//  HintingGroup(FEGlyph g, FEPath p) {
//    this.glyph = g;
//    this.path = p;
//    // Make pointlist from paths *completely* enclosed by this one...
//    fepl = new FEPointList();
//    this.path.addPointsToFEPointList(fepl);
//    //Log.log("before:" + fepl.number);
//    // this is rather slow...
//    //    glyph.instruction_stream.fepathlist.executeOnEachPath(new ExecutorOnFEPath() {
//    //      void execute(FEPath p, Object o) {
//    //        FEPath p2 = (FEPath) o;
//    //
//    //        if (p.getMinX() <= p2.getMinX()) {
//    //          return;
//    //        }
//    //
//    //        if (p.getMaxX() >= p2.getMaxX()) {
//    //          return;
//    //        }
//    //
//    //        if (p.getMinY() <= p2.getMinY()) {
//    //          return;
//    //        }
//    //
//    //        if (p.getMaxY() >= p2.getMaxY()) {
//    //          return;
//    //        }
//    //
//    //        p.addPointsToFEPointList(fepl);
//    //      }
//    //    }, path);
//    //Log.log("after:" + fepl.number);
//  }
//
///*
//  void translate(int dx, int dy) {
//    fepl.translate(dx, dy);
//    path.getSliders().translate(dx, dy);
//  }
//  
//  void rescaleWithFixedLeft(SliderManagerBase s_m, int fixed, int o, int n) {
//    fepl.rescaleWithFixedLeft(fixed, o, n);
//    s_m.rescaleWithFixedLeftOrTop(fixed, o, n);
//  }
//
//  void rescaleWithFixedTop(SliderManagerBase s_m, int fixed, int o, int n) {
//    fepl.rescaleWithFixedTop(fixed, o, n);
//    s_m.rescaleWithFixedLeftOrTop(fixed, o, n);
//  }
//  */
//
//  void setFepl(FEPointList fepl) {
//    this.fepl = fepl;
//  }
//
//  FEPointList getFepl() {
//    return fepl;
//  }
//  
//  //  void scaleRangeX(int fixed_index, int moving_index, int new_pos) {
//  //    fepl.scaleRangeX(fixed_index, moving_index, new_pos);
//  //  }
//  //
//  //  void scaleRangeY(int fixed_index, int moving_index, int new_pos) {
//  //    fepl.scaleRangeY(fixed_index, moving_index, new_pos);
//  //  }
//
//  //  private void rescaleWithFixedRight(SliderManagerBase s_m, int fixed, int o, int n) {
//  //    fepl.rescaleWithFixedRight(fixed, o, n);
//  //    s_m.rescaleWithFixedRightOrBottom(fixed, o, n);
//  //  }
//  //  private void rescaleWithFixedBottom(SliderManagerBase s_m, int fixed, int o, int n) {
//  //    fepl.rescaleWithFixedBottom(fixed, o, n);
//  //    s_m.rescaleWithFixedRightOrBottom(fixed, o, n);
//  //  }
//}