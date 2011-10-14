package com.jgraph.gaeawt;

import java.io.IOException;
import javax.servlet.http.*;

import com.jgraph.gaeawt.java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class Gae_awtServlet extends HttpServlet
{
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException
	{
		int type = BufferedImage.TYPE_INT_ARGB;
		BufferedImage result = new BufferedImage(500, 500, type);
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
}
