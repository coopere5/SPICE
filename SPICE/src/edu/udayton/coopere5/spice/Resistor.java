package edu.udayton.coopere5.spice;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Evan
 * @see CircuitComponent
 */
public class Resistor extends CircuitComponent {

	public Resistor() {
		this.net = new int[2];
		this.net[0] = 0;
		this.net[1] = 0;
		this.xpos = 0;
		this.ypos = 0;

		this.angle = 0;
		this.value = 0;

		area = new Polygon();
		type = CircuitComponent.RESISTOR;
	}

	public Resistor(String s) {
		this(s.split(" +"));
	}

	public Resistor(String n, int n1, int n2, double val, int X, int Y, int angle) {
		this.name = n;

		this.net = new int[2];
		this.net[0] = Math.min(n1, n2);
		this.net[1] = Math.max(n1, n2);

		this.xpos = X;
		this.ypos = Y;

		this.angle = angle % 8;

		if (val >= 0) {
			this.value = val;
		} else {
			throw new IllegalArgumentException("Resistance must be nonnegative.");
		}
		// area = new Rectangle(X-5, Y - 10, 90, 20);
		this.relocateArea();
	}

	public Resistor(String[] s) {
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

		int xPoints[] = { x, x + 10, x + 15, x + 25, x + 35, x + 40, x + 45, x + 55, x + 65, x + 70, x + 80 };
		int yPoints[] = { y, y, y - 10, y + 10, y - 10, y, y + 10, y - 10, y + 10, y, y };
		GeneralPath polyline = new GeneralPath(Path2D.WIND_EVEN_ODD, xPoints.length);
		polyline.moveTo(xPoints[0], yPoints[0]);
		for (int i = 1; i < xPoints.length; i++) {
			polyline.lineTo(xPoints[i], yPoints[i]);
		}
		// g2d.draw(area);
		g2d.rotate(Math.toRadians(45 * angle), x, y);
		g2d.draw(polyline);
		g2d.setTransform(old);

		this.drawLabel(g);
	}

	public void parallel(Resistor... resistors) {
		double val = 0;
		for (Resistor resistor : resistors) {
			double resistance = resistor.getValue();
			if (resistance != 0) {
				val = val + (1 / resistance);
			} else {
				this.setValue(0);
				return;
			}
		}
		if (val != 0) {
			this.setValue(1 / val);
		} else {
			this.setValue(0);
		}
	}

	public void series(Resistor... resistors) {
		double val = 0;
		for (Resistor resistor : resistors) {
			double resistance = resistor.getValue();
			val = val + resistance;
		}
		this.setValue(val);
	}

	private void drawLabel(Graphics g) {
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
			yn = y - 15;
			xv = x + ((80 - (int) rv.getWidth()) / 2);
			yv = y + 15 + (int) rv.getHeight();
			break;
		case 5:
			x = x - 113;
			y = y - 113;
		case 1:
			xn = x + 30;
			yn = y + 28;
			xv = x + 30;
			yv = yn + (int) rv.getHeight();
			break;
		case 6:
			y = y - 80;
		case 2:
			xn = x + 20;
			yn = y + 40;
			xv = x + 20;
			yv = yn + (int) rv.getHeight();
			break;
		case 7:
			x = x + 113;
			y = y - 113;
		case 3:
			xn = x - 30;
			yn = y + 28;
			xv = x - 30;
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
