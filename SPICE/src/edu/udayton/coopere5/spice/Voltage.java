package edu.udayton.coopere5.spice;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Evan
 * @see CircuitComponent
 */
public class Voltage extends CircuitComponent {

	private int[] net;

	// negative voltage unsupported currently
	public Voltage() {
		this.net = new int[2];
		this.net[0] = 0;
		this.net[1] = 0;

		this.xpos = 0;
		this.ypos = 0;

		this.angle = 0;
		this.value = 0;

		this.type = CircuitComponent.VOLTAGE;

		area = new Polygon();
		// throw new UnsupportedOperationException("Voltage is NYI");
	}

	public Voltage(String s) {
		this(s.split(" +"));
	}

	public Voltage(String n, int n1, int n2, double val, int X, int Y, int angle) {
		this.name = n;

		this.net = new int[2];
		this.net[0] = n1;
		this.net[1] = n2;

		this.xpos = this.roundPos(X);
		this.ypos = this.roundPos(Y);

		this.angle = angle % 8;
		this.value = val;

		this.type = CircuitComponent.VOLTAGE;

		this.relocateArea();
		// throw new UnsupportedOperationException("Voltage is NYI");
	}

	public Voltage(String[] s) {
		this(s[1], Integer.parseInt(s[2]), Integer.parseInt(s[3]), CircuitComponent.parseValue(s[4]),
				Integer.parseInt(s[5]), Integer.parseInt(s[6]), Integer.parseInt(s[7]));
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub

		g.setColor(drawColor);
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform old = g2d.getTransform();

		int x = xpos;
		int y = ypos;
		/*
		 * int xPoints[] = { x, x + 20, x + 60, x + 50, x + 60, x + 50, x + 60,
		 * x + 80 }; int yPoints[] = { y, y, y, y - 10, y, y + 10, y, y };
		 * GeneralPath polyline = new GeneralPath(Path2D.WIND_EVEN_ODD,
		 * xPoints.length); polyline.moveTo(xPoints[0], yPoints[0]); for (int i
		 * = 1; i < xPoints.length; i++) { polyline.lineTo(xPoints[i],
		 * yPoints[i]); }
		 */

		GeneralPath polyline = new GeneralPath(Path2D.WIND_EVEN_ODD, 2);
		polyline.moveTo(x, y);
		polyline.lineTo(x + 20, y);

		polyline.append((new Ellipse2D.Double(x + 20, y - 20, 40, 40)).getPathIterator(old), false);
		polyline.moveTo(x + 22, y);
		polyline.lineTo(x + 26, y);
		polyline.moveTo(x + 58, y);
		polyline.lineTo(x + 54, y);
		polyline.moveTo(x + 56, y - 2);
		polyline.lineTo(x + 56, y + 2);
		polyline.moveTo(x + 60, y);
		polyline.lineTo(x + 80, y);
		// g2d.draw(area);
		g2d.rotate(Math.toRadians(45 * angle), x, y);
		g2d.draw(polyline);
		g2d.setTransform(old);
		drawLabel(g);

		System.err.println("Voltage drawing is NYI");
	}

	@Override
	public void drawLabel(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		FontMetrics fm = g2d.getFontMetrics();
		Rectangle2D rn = fm.getStringBounds(name, g2d);
		Rectangle2D rv = fm.getStringBounds(this.valueString(), g2d);

		int xn, yn, xv, yv;
		int x = xpos;
		int y = ypos;

		switch (angle) {
		case 4:
			x = x - 80;
		case 0:
			xn = x + ((80 - (int) rn.getWidth()) / 2);
			yn = y - 25;
			xv = x + ((80 - (int) rv.getWidth()) / 2);
			yv = y + 25 + (int) rv.getHeight();
			break;
		case 5:
			x = x - 56;
			y = y - 56;
		case 1:
			xn = x + 45;
			yn = y;
			xv = x + 45;
			yv = yn + (int) rv.getHeight();
			break;
		case 6:
			y = y - 80;
		case 2:
			xn = x + 25;
			yn = y + 40;
			xv = x + 25;
			yv = yn + (int) rv.getHeight();
			break;
		case 7:
			x = x + 56;
			y = y - 56;
		case 3:
			xn = x - 45 - (int) rn.getWidth();
			yn = y;
			xv = x - 45 - (int) rv.getWidth();
			yv = yn + (int) rv.getHeight();
			break;
		default:
			xn = x;
			yn = y;
			xv = x;
			yv = y;
			break;
		}
		g.drawString(name, xn, yn);
		g.drawString(this.valueString(), xv, yv);
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
