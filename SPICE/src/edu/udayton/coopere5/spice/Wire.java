package edu.udayton.coopere5.spice;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Evan
 * @see CircuitComponent
 */
public class Wire extends CircuitComponent {
	private List<Point2D> points;

	public Wire() {
		net = new int[1];
		net[0] = 0;
		points = new ArrayList<Point2D>();
		value = -1;
		area = new Polygon();
		angle = -1;
		type = CircuitComponent.WIRE;
	}

	public Wire(String s) {
		this(s.split(" +"));
	}

	public Wire(String... s) {
		net = new int[1];
		net[0] = Integer.parseInt(s[1]);
		points = new ArrayList<Point2D>();
		for (int i = 2; i < s.length; i = i + 2) {
			points.add(new Point(this.roundPos(Integer.parseInt(s[i])), this.roundPos(Integer.parseInt(s[i + 1]))));
		}
		value = -1;
		area = new Polygon();
		angle = -1;

		type = CircuitComponent.WIRE;
	}

	@Override
	public void addPoint(int x, int y) {
		points.add(new Point(this.roundPos(x), this.roundPos(y)));
		// relocateArea();
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(drawColor);
		Graphics2D g2d = (Graphics2D) g;

		GeneralPath polyline = new GeneralPath(Path2D.WIND_EVEN_ODD, points.size());
		polyline.moveTo(points.get(0).getX(), points.get(0).getY());
		for (int i = 1; i < points.size(); i++) {
			polyline.lineTo(points.get(i).getX(), points.get(i).getY());
		}
		g2d.draw(polyline);
		// g2d.drawPolygon(area);
	}

	@Override
	public void drawLabel(Graphics g) {
		// TODO Auto-generated method stub

	}

	/**
	 * Generates a String in our standard netlist format. Used to save a
	 * component list to a file.
	 *
	 * @return generated netlist string:<br>
	 *         <code>WIRE NET X1 Y1 X2 Y2 ...</code>
	 */
	@Override
	public String getNetLine() {
		String rv = "WIRE ";
		rv += net[0] + " ";
		for (int i = 0; i < points.size(); i++) {
			rv += (int) points.get(i).getX() + " " + (int) points.get(i).getY() + " ";
		}
		return rv;
	}

	@Override
	public void relocateComponent(int x, int y) {
	}

	@Override
	public void rotate(int dir) {
	}

	@Override
	public void setAngle(int ori) {
	}

	@Override
	public void setLastPoint(int x, int y) {
		points.set(points.size() - 1, new Point(this.roundPos(x), this.roundPos(y)));
	}

	public void setNet(int n) {
		net[0] = n;
	}

	@Override
	public void setPosition(int x, int y) {
	}

	@Override
	public int showDialog(Component p, boolean isNew) {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		JPanel labels = new JPanel(new GridLayout(0, 1, 2, 2));

		labels.add(new JLabel("Net", SwingConstants.RIGHT));
		panel.add(labels, BorderLayout.WEST);

		JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));

		JTextField txtL = new JTextField();
		txtL.setText(Integer.toString(net[0]));
		controls.add(txtL);

		panel.add(controls, BorderLayout.CENTER);
		Object[] options = { "Ok", "Delete", "Cancel" };
		String message;
		if (isNew) {
			message = "Add Component";
		} else {
			message = "Modify Component";
		}
		int status = JOptionPane.showOptionDialog(p, panel, message, JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, null);

		if (status == JOptionPane.YES_OPTION) {
			try {
				this.setNet(Integer.parseInt(txtL.getText()));
				this.relocateArea();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(p, "Invalid input");
			}
		}

		return status;
	}

	@Override
	public void translate(int x, int y) {
	}

	@Override
	public String valueString() {
		return null;
	}

	@Override
	protected void relocateArea() {
		Area area = new Area();
		for (int i = 1; i < points.size(); i++) {
			Point2D point1 = points.get(i - 1);
			Point2D point2 = points.get(i);

			Line2D.Double ln = new Line2D.Double(point1.getX(), point1.getY(), point2.getX(), point2.getY());
			double indent = 5; // distance from central line
			double length = ln.getP1().distance(ln.getP2());

			double dx_li = (ln.getX2() - ln.getX1()) / length * indent;
			double dy_li = (ln.getY2() - ln.getY1()) / length * indent;

			// moved p1 point
			double p1X = ln.getX1() - dx_li;
			double p1Y = ln.getY1() - dy_li;

			// line moved to the left
			double lX1 = ln.getX1() - dy_li;
			double lY1 = ln.getY1() + dx_li;
			double lX2 = ln.getX2() - dy_li;
			double lY2 = ln.getY2() + dx_li;

			// moved p2 point
			double p2X = ln.getX2() + dx_li;
			double p2Y = ln.getY2() + dy_li;

			// line moved to the right
			double rX1_ = ln.getX1() + dy_li;
			double rY1 = ln.getY1() - dx_li;
			double rX2 = ln.getX2() + dy_li;
			double rY2 = ln.getY2() - dx_li;

			Path2D p = new Path2D.Double();
			p.moveTo(lX1, lY1);
			p.lineTo(lX2, lY2);
			p.lineTo(p2X, p2Y);
			p.lineTo(rX2, rY2);
			p.lineTo(rX1_, rY1);
			p.lineTo(p1X, p1Y);
			p.lineTo(lX1, lY1);

			area.add(new Area(p));
		}
		PathIterator path = area.getPathIterator(null);
		double[] point = new double[2];
		while (!path.isDone()) {
			if (path.currentSegment(point) != PathIterator.SEG_CLOSE) {
				this.area.addPoint((int) point[0], (int) point[1]);
			}
			path.next();
		}
	}

}
