package org.fonteditor.kerning;

import org.fonteditor.cache.CachedGlyph;

/**
 * Kerning data for a *particular* cached glyph...
 */

public interface KerningType {
  int getKerningDistance(CachedGlyph cached_glyph1, CachedGlyph cached_glyph2);
}
