package edu.udayton.coopere5.spice;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

/**
 * Current
 *
 * @author Evan Cooper
 * @see CircuitComponent
 */
public class Current extends CircuitComponent {

	public Current() {
		this.net = new int[2];
		this.net[0] = 0;
		this.net[1] = 0;
		this.xpos = 0;
		this.ypos = 0;

		this.angle = 0;
		this.value = 0;

		this.type = CircuitComponent.CURRENT;

		area = new Polygon();
	}

	public Current(String s) {
		this(s.split(" +"));
	}

	public Current(String n, int n1, int n2, double val, int X, int Y, int angle) {
		this.name = n;

		this.net = new int[2];
		this.net[0] = n1;
		this.net[1] = n2;

		this.xpos = X;
		this.ypos = Y;

		this.angle = angle % 8;

		this.value = val;

		this.type = CircuitComponent.CURRENT;

		this.relocateArea();
	}

	public Current(String[] s) {
		this(s[1], Integer.parseInt(s[2]), Integer.parseInt(s[3]), CircuitComponent.parseValue(s[4]),
				Integer.parseInt(s[5]), Integer.parseInt(s[6]), Integer.parseInt(s[7]));
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(drawColor);
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform old = g2d.getTransform();

		int x = xpos;
		int y = ypos;

		int xPoints[] = { x, x + 20, x + 60, x + 50, x + 60, x + 50, x + 60, x + 80 };
		int yPoints[] = { y, y, y, y - 10, y, y + 10, y, y };
		GeneralPath polyline = new GeneralPath(Path2D.WIND_EVEN_ODD, xPoints.length);
		polyline.moveTo(xPoints[0], yPoints[0]);
		for (int i = 1; i < xPoints.length; i++) {
			polyline.lineTo(xPoints[i], yPoints[i]);
		}
		polyline.append((new Ellipse2D.Double(x + 20, y - 20, 40, 40)).getPathIterator(old), false);
		// g2d.draw(area);
		g2d.rotate(Math.toRadians(45 * angle), x, y);
		g2d.draw(polyline);
		g2d.setTransform(old);
	}

	@Override
	public void setValue(double val) {
		this.value = val;
	}

	@Override
	protected void relocateArea() {
		double ang = Math.PI / 4 * angle;
		int[] xpoints = { (int) (xpos - 5 * Math.cos(ang) + 10 * Math.sin(ang)),
				(int) (xpos + 85 * Math.cos(ang) + 10 * Math.sin(ang)),
				(int) (xpos + 85 * Math.cos(ang) - 10 * Math.sin(ang)),
				(int) (xpos - 5 * Math.cos(ang) - 10 * Math.sin(ang)) };
		int[] ypoints = { (int) (ypos - 10 * Math.cos(ang) - 5 * Math.sin(ang)),
				(int) (ypos - 10 * Math.cos(ang) + 85 * Math.sin(ang)),
				(int) (ypos + 10 * Math.cos(ang) + 85 * Math.sin(ang)),
				(int) (ypos + 10 * Math.cos(ang) - 5 * Math.sin(ang)) };
		area = new Polygon(xpoints, ypoints, xpoints.length);
	}

}
