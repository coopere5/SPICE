package edu.udayton.coopere5.spice;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Polygon;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * CircuitComponent is the abstract base class for all components of a circuit.
 * A CircuitComponent object encapsulates the information needed to render and
 * interact with Resistors, Wires, and other components.
 *
 * @author Evan Cooper
 *
 */
public abstract class CircuitComponent {

	public static final int RESISTOR = 0;
	public static final int WIRE = 1;
	public static final int VOLTAGE = 2;
	public static final int CURRENT = 3;
	
	public static final int GRID_SIZE = 10;
	
	/**
	 * Parses a value string of the form xk for the suffixes 'M', 'k', 'm' and
	 * 'u'.
	 *
	 * @param strVal
	 *            the value to be parsed in the form xk
	 * @return the parsed value of the string
	 * @throws NumberFormatException
	 */
	public static double parseValue(String strVal) {
		double val;
		String num = strVal.substring(0, strVal.length() - 1);
		switch (strVal.substring(strVal.length() - 1)) {
		case "M":
			val = Double.parseDouble(num) * 1000000;
			break;
		case "k":
			val = Double.parseDouble(num) * 1000;
			break;
		case "m":
			val = Double.parseDouble(num) * 0.001;
			break;
		case "u":
			val = Double.parseDouble(num) * 0.000001;
			break;
		default:
			val = Double.parseDouble(strVal);
			break;
		}
		return val;
	}

	protected int[] net;

	protected int type;
	protected String name;
	protected double value;
	protected int xpos, ypos;
	public Polygon area;
	protected int angle;

	protected Color drawColor = Color.BLACK;

	/**
	 * @see Wire#addPoint(int, int)
	 */
	public void addPoint(int xpos2, int ypos2) {
	}

	/**
	 * Draws the component
	 *
	 * @param g
	 *            - the graphics context.
	 * @see ResistancePanel#paintComponents(Graphics)
	 */
	public abstract void draw(Graphics g);

	public int getAngle() {
		return angle;
	}

	public String getName() {
		return name;
	}

	public int[] getNet() {
		return net;
	}

	/**
	 * Generates a String in our standard netlist format. Used to save a
	 * component list to a file.
	 *
	 * @return generated netlist string:<br>
	 *         <code>TYPE NAME NET1 NET2 VALUE XPOS YPOS ANGLE</code>
	 */
	public String getNetLine() {
		String rv;
		switch (type) {
		case RESISTOR:
			rv = "R ";
			break;
		case CURRENT:
			rv = "I ";
			break;
		case VOLTAGE:
			rv = "V ";
			break;
		case WIRE:
		default:
			throw new UnsupportedOperationException("getNetLine() is NYI for type " + type);
		}
		rv += name + " ";
		rv += net[0] + " " + net[1] + " ";
		rv += this.valueString() + " ";
		rv += Integer.toString(xpos) + " " + Integer.toString(ypos) + " ";
		rv += Integer.toString(angle);
		return rv;
	}

	public int getType() {
		return type;
	}

	public double getValue() {
		return value;
	}

	/**
	 * Rotates this CircuitComponent 45 degrees.
	 *
	 * @param dir
	 *            - rotation direction:
	 *            <ul>
	 *            <li>1 = clockwise</li>
	 *            <li>-1 = counter-clockwise</li>
	 *            </ul>
	 */
	public void rotate(int dir) {
		if (dir != 1 && dir != -1) {
			throw new IllegalArgumentException("Rotation too far");
		}
		angle = angle + dir;
		if (angle == -1) {
			angle = 7;
		}
		angle = angle % 8;
		this.relocateArea();
	}

	public void setAngle(int ori) {
		this.angle = ori % 8;
		this.relocateArea();
	}

	/**
	 * @see Wire#setLastPoint(int, int)
	 */
	public void setLastPoint(int x, int y) {
	}

	public void setName(String n) {
		this.name = n;
	}

	public void setPosition(int x, int y) {
		this.xpos = roundPos(x);
		this.ypos = roundPos(y);
		this.relocateArea();
	}

	public void setSelected(boolean selected) {
		if (selected) {
			drawColor = Color.BLUE;
		} else {
			drawColor = Color.BLACK;
		}
	}

	/**
	 * Sets the value of this CircuitComponent, disallowing negative values.
	 *
	 * @param val
	 *            - desired value
	 * @throws IllegalArgumentException
	 *             if val<0
	 */
	public void setValue(double val) throws IllegalArgumentException {
		if (val >= 0) {
			this.value = val;
		} else {
			throw new IllegalArgumentException("Value must be nonnegative.");
		}
	}

	/**
	 * Displays a component modification dialog
	 *
	 * @param p
	 *            - parent component
	 * @param isNew
	 *            - <code>true</code> for a new instance of this object,
	 *            <code>false</code> otherwise
	 * @return an integer indicating the option chosen by the user, or
	 *         <code>JOptionPane.CLOSED_OPTION</code> if the dialog is closed.
	 */
	public int showDialog(Component p, boolean isNew) {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		JPanel labels = new JPanel(new GridLayout(0, 1, 2, 2));

		labels.add(new JLabel("Label", SwingConstants.RIGHT));
		labels.add(new JLabel("Value", SwingConstants.RIGHT));
		labels.add(new JLabel("Orientation", SwingConstants.RIGHT));
		labels.add(new JLabel("Net 1", SwingConstants.RIGHT));
		labels.add(new JLabel("Net 2", SwingConstants.RIGHT));
		panel.add(labels, BorderLayout.WEST);

		JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));

		JTextField txtL = new JTextField();
		txtL.setText(name);
		controls.add(txtL);

		JTextField txtR = new JTextField();
		txtR.setText(Double.toString(value));
		controls.add(txtR);

		String[] angles = { "Horizontal", "Down 45", "Vertical", "Up 45" };
		JComboBox<String> cmbO = new JComboBox<String>(angles);
		controls.add(cmbO);

		JTextField txtN1 = new JTextField();
		txtN1.setText(Integer.toString(net[0]));
		controls.add(txtN1);

		JTextField txtN2 = new JTextField();
		txtN2.setText(Integer.toString(net[1]));
		controls.add(txtN2);

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
				this.setValue(parseValue(txtR.getText().replaceAll(" +", "")));
				this.setName(txtL.getText());
				this.setAngle(cmbO.getSelectedIndex());
				net[0] = Integer.parseInt(txtN1.getText());
				net[1] = Integer.parseInt(txtN2.getText());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(p, "Invalid input");
			}
		}

		return status;
	}

	@Override
	public String toString() {
		String retval;
		switch (this.type) {
		case RESISTOR:
			retval = "Resistor ";
			break;
		case CURRENT:
			retval = "Current ";
			break;
		case VOLTAGE:
			retval = "Voltage ";
			break;
		default:
			return "";
		}
		retval += this.name + " " + this.value;
		return retval;
	}

	/**
	 * Translates this CircuitComponent by (x,y)
	 *
	 * @param x
	 *            - translation in x direction
	 * @param y
	 *            - translation in y direction
	 */
	public void translate(int x, int y) {
		xpos += x;
		ypos += y;
		this.relocateArea();
	}

	/**
	 * Generates a formatted string of the value based on the number of figures.
	 * For example, if <code>value</code> is 1000, this method will return
	 * "1.0k".
	 *
	 * @return formatted value string
	 */
	public String valueString() {
		String retval;
		if (this.value < 0) {
			retval = "-";
		} else {
			retval = "";
		}

		if (Math.abs(this.value) >= 1000000) {
			retval += String.format("%.2f", this.value / 1000000.0) + "M";
		} else if (Math.abs(this.value) >= 1000) {
			retval += String.format("%.2f", this.value / 1000.0) + "k";
		} else if (Math.abs(this.value) >= 1) {
			retval += String.format("%.2f", this.value);
		} else if (Math.abs(this.value) >= 0.001) {
			retval += String.format("%.2f", this.value * 1000.0) + "m";
		} else if (Math.abs(this.value) >= 0.000001) {
			retval += String.format("%.2f", this.value * 1000000) + "u";
		} else {
			retval += String.format("%.2f", this.value);
		}
		return retval;
	}
	
	public abstract void drawLabel(Graphics g);

	/**
	 * Moves the mouse bounding box to its new position.
	 */
	protected abstract void relocateArea();
	
	protected int roundPos(int x) {
		int pos = x/GRID_SIZE;
		pos = pos * GRID_SIZE;
		return pos;
	}
}
