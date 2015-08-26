# gae2d
Automatically exported from code.google.com/p/gae2d

This project intends to implement the Java awt.Graphics2D in pure Java so that it runs correctly on Google App Engine. It is not a complete awt implementation, the scope is restricted to building up the raster of a buffered image and exporting that to common image formats. By restricting the scope this gives the possibility to achieve performance improvements.

The libraries currently imported are Apache Harmony, Apache Sanselan and http://www.fonteditor.org/.

Apache Harmony is the Apache JDK implementation that was largely abandoned in 2010 when IBM left. The code is not production quality in parts, but the basic functionality to provide most of the awt package in pure Java is there. The code is imported from the current SVN HEAD of harmony. It has been significantly trimmed to remove the levels of indirection required to support varying graphics devices and hardware. Specifically, the ColorModel, SampleModel and DataBuffer can be considered unavailable. The internal storage is only linear intARGB. The namespace of the package has been changed to com.jgraph.gaeawt.java.awt to avoid problems with the Eclipse GAE plugin.

Apache Sanselan is a pure Java image codec library, it provides encoding to various image formats. It has been trimmed slightly and references to Java's awt have been changed to reference the modified Harmony code.

FontEditor is a public domain pure Java font rendering library/application. Although, we haven't made calls to the library yet, it is altered to use the adapted awt API.

There is another similar project on Google Code, http://code.google.com/p/appengine-awt/. This uses Harmony and Sanselan to provide the whole awt package. That is not the intention of this project, it's scope is limited to just providing the Graphics2D API, transforms, fonts, etc, necessary to create an image. The raster, color and sample models, and databuffer are considered intermediate, internal functionality and so performance is the priority over maintaining their APIs.

Note appengine-awt states 4 problems. 3 and 4 are reasonably solved in this codebase. Remaining are:

Text rendering is not supported.
Antialising is not available.
The text issue is aimed to be solved as far as possible using FontEditor, though it is likely the performance of font rendering will be the bottleneck. Antialising algorithms are fairly well known, the issue will be integrating them into the rendering pipeline and maintaining reasonable performance.

Note that the software is alpha quality, large parts of functionality are completely untested.
