package edu.udayton.coopere5.spice;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import Jama.Matrix;

/**
 * @author Evan Cooper
 *
 */
public class ResistancePanel extends JPanel {

	private class MouseHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent event) {
			xpos = event.getX();
			ypos = event.getY();
			int clicks = event.getClickCount();
			if (!wireDraw) {
				prevComponent = currentComponent;
				if (prevComponent != null) {
					prevComponent.setSelected(false);
				}
				currentComponent = ResistancePanel.this.find(event.getPoint());
				if (currentComponent != null) {
					currentComponent.setSelected(true);
					if (currentComponent.getType() == CircuitComponent.WIRE) {
						popupRotate.setEnabled(false);
						popupFlip.setEnabled(false);
					} else {
						popupRotate.setEnabled(true);
						popupFlip.setEnabled(true);
					}
					popupDelete.setEnabled(true);
				} else {
					popupRotate.setEnabled(false);
					popupDelete.setEnabled(false);
					popupFlip.setEnabled(false);
				}
				ResistancePanel.this.repaint();
				if (clicks > 1) {
					ResistancePanel.this.makeDirty();
					if (currentComponent == null) {
						popup.show(ResistancePanel.this, event.getX(), event.getY());
					} else {
						int status = currentComponent.showDialog(ResistancePanel.this.getParent(), false);
						if (status == JOptionPane.NO_OPTION) {
							ResistancePanel.this.removeComponent();
						}
					}
				} else if (SwingUtilities.isRightMouseButton(event)) {
					popup.show(ResistancePanel.this, event.getX(), event.getY());
				}
			} else if (clicks == 1 && SwingUtilities.isLeftMouseButton(event)) {
				currentComponent.addPoint(event.getX(), event.getY());
				ResistancePanel.this.repaint();
			} else if (clicks > 1 || SwingUtilities.isRightMouseButton(event)) {
				wireDraw = false;
				int status = currentComponent.showDialog(ResistancePanel.this.getParent(), true);
				if (status != JOptionPane.YES_OPTION) {
					ResistancePanel.this.removeComponent();
				}
			}
			ResistancePanel.this.repaint();
		}

		@Override
		public void mouseDragged(MouseEvent event) {
			if (!wireDraw && SwingUtilities.isLeftMouseButton(event)) {
				xpos = event.getX();
				ypos = event.getY();
				// current = find(event.getPoint());
				if (currentComponent != null) {
					currentComponent.relocateComponent(xpos, ypos);
					ResistancePanel.this.makeDirty();
				}
				ResistancePanel.this.repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent event) {
			if (wireDraw) {
				currentComponent.setLastPoint(event.getX(), event.getY());
				ResistancePanel.this.repaint();
			}
		}

		@Override
		public void mousePressed(MouseEvent event) {
			if (!wireDraw) {
				prevComponent = currentComponent;
				if (prevComponent != null) {
					prevComponent.setSelected(false);
				}
				currentComponent = ResistancePanel.this.find(event.getPoint());
				if (currentComponent != null) {
					currentComponent.setSelected(true);
					if (currentComponent.getType() == CircuitComponent.WIRE) {
						popupRotate.setEnabled(false);
						popupFlip.setEnabled(false);
					} else {
						popupRotate.setEnabled(true);
						popupFlip.setEnabled(true);
					}
					popupDelete.setEnabled(true);
				} else {
					popupRotate.setEnabled(false);
					popupDelete.setEnabled(false);
					popupFlip.setEnabled(false);
				}
				ResistancePanel.this.repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent event) {

		}
	}

	private static final long serialVersionUID = 1L;

	private List<CircuitComponent> components;
	private CircuitComponent currentComponent = null;
	private CircuitComponent prevComponent = null;
	private boolean dirty;
	private int xpos, ypos;

	private File currentFile;
	private boolean wireDraw;

	private JPopupMenu popup;
	private JMenuItem popupRotate;
	private JMenuItem popupDelete;
	private JMenuItem popupFlip;

	private JFileChooser fileChooser;

	private ActionListener menuListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getActionCommand() == "Resistor") {
				currentComponent = new Resistor();
				currentComponent.setPosition(xpos, ypos);
				int status = currentComponent.showDialog(ResistancePanel.this.getParent(), true);
				if (status == JOptionPane.YES_OPTION) {
					ResistancePanel.this.addComponent(currentComponent);
				} else {
					currentComponent = null;
				}
			} else if (event.getActionCommand() == "Wire") {
				currentComponent = new Wire();
				components.add(currentComponent);
				wireDraw = true;
				currentComponent.addPoint(xpos, ypos);
			} else if (event.getActionCommand() == "Voltage") {
				currentComponent = new Voltage();
				currentComponent.setPosition(xpos, ypos);
				int status = currentComponent.showDialog(ResistancePanel.this.getParent(), true);
				if (status == JOptionPane.YES_OPTION) {
					ResistancePanel.this.addComponent(currentComponent);
				} else {
					currentComponent = null;
				}
			} else if (event.getActionCommand() == "Current") {
				currentComponent = new Current();
				currentComponent.setPosition(xpos, ypos);
				int status = currentComponent.showDialog(ResistancePanel.this.getParent(), true);
				if (status == JOptionPane.YES_OPTION) {
					ResistancePanel.this.addComponent(currentComponent);
				} else {
					currentComponent = null;
				}
			} else if (event.getActionCommand() == "Rotate") {
				ResistancePanel.this.rotate(1);
			} else if (event.getActionCommand() == "Delete") {
				ResistancePanel.this.removeComponent();
			} else if (event.getActionCommand() == "Flip") {
				ResistancePanel.this.flip();
			}
		}
	};

	public ResistancePanel() {
		super();
		this.components = new ArrayList<CircuitComponent>();

		MouseHandler mouse = new MouseHandler();
		this.setPreferredSize(new Dimension(500, 500));
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);

		this.popup = new JPopupMenu();

		this.fileChooser = new JFileChooser(System.getProperty("user.dir"));

		JMenuItem item;
		popup.add(item = new JMenuItem("Resistor"));
		item.addActionListener(menuListener);
		popup.add(item = new JMenuItem("Wire"));
		item.addActionListener(menuListener);
		popup.add(item = new JMenuItem("Voltage"));
		item.addActionListener(menuListener);
		popup.add(item = new JMenuItem("Current"));
		item.addActionListener(menuListener);
		popup.add(popupRotate = new JMenuItem("Rotate"));
		popupRotate.setEnabled(false);
		popupRotate.addActionListener(menuListener);
		popup.add(popupDelete = new JMenuItem("Delete"));
		popupDelete.setEnabled(false);
		popupDelete.addActionListener(menuListener);
		popup.add(popupFlip = new JMenuItem("Flip"));
		popupFlip.setEnabled(false);
		popupFlip.addActionListener(menuListener);

		this.dirty = false;
		this.wireDraw = false;
	}

	public ResistancePanel(List<CircuitComponent> cList) {
		this();
		this.components = cList;
	}

	public void addComponent(CircuitComponent c) {
		this.makeDirty();
		components.add(c);
		this.repaint();
	}

	public void flip() {
		this.makeDirty();
		currentComponent.flip();
		this.repaint();
	}

	public List<CircuitComponent> getCircuitComponents() {
		return components;
	}

	public CircuitComponent getCurrentComponent() {
		return currentComponent;
	}

	/**
	 * @return - name of {@link #currentFile}
	 */
	public String getFileName() {
		if (currentFile != null) {
			return this.currentFile.getName();
		} else {
			return "untitled";
		}
	}

	/**
	 * @return {@link #saveIfDirty()}
	 */
	public int newFile() {
		int rv = this.saveIfDirty();
		if (rv == 0) {
			this.clear();
		}
		currentFile = null;

		return rv;
	}

	/**
	 * @return
	 * 		<ul>
	 *         <li>0 upon success</li>
	 *         <li>-1 upon save cancel or open cancel</li>
	 *         </ul>
	 */
	public int open() {
		int rv = this.saveIfDirty();
		if (rv != -1) {
			rv = fileChooser.showOpenDialog(this);
			if (rv != JFileChooser.APPROVE_OPTION) {
				return -1;
			}
			currentFile = fileChooser.getSelectedFile();
			this.rebuildComponents();
			this.makeClean();
			return 0;
		}
		return -1;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (CircuitComponent c : this.components) {
			c.draw(g);
		}
	}

	public void removeComponent() {
		if (currentComponent != null) {
			this.makeDirty();
			components.remove(currentComponent);
			this.repaint();
		}
	}

	public void rotate(int dir) {
		if (currentComponent != null) {
			this.makeDirty();
			currentComponent.rotate(dir);
			this.repaint();
		}
	}

	/**
	 * @return
	 * 		<ul>
	 *         <li>0 upon success</li>
	 *         <li>-1 upon failure</li>
	 *         </ul>
	 * @see #save(File)
	 */
	public int save() {
		return this.save(currentFile);
	}

	/**
	 * @param f
	 *            - The File to save to. Shows a file chooser if null.
	 * @return
	 * 		<ul>
	 *         <li>0 upon success</li>
	 *         <li>-1 upon failure</li>
	 *         </ul>
	 */
	public int save(File f) {
		if (f == null) {
			int rv = fileChooser.showSaveDialog(this);
			if (rv != JFileChooser.APPROVE_OPTION) {
				return -1;
			}
			f = fileChooser.getSelectedFile();
		}
		try {
			f.createNewFile();
			PrintWriter writer = new PrintWriter(f);
			for (CircuitComponent c : this.getCircuitComponents()) {
				writer.println(c.getNetLine());
			}
			writer.close();
			this.makeClean();
			currentFile = f;
			return 0;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "IOException");
			return -1;
		}
	}

	public void saveAndExit() {
		if (this.saveIfDirty() != -1) {
			System.exit(0);
		}
	}

	public void solve() {
		Matrix solutionMatrix = NodeAnalysis.solver(components);
		String[] matrixLabels = NodeAnalysis.solutionNames(components);
		String readout = "";
		for (int i = 0; i < solutionMatrix.getRowDimension(); i++) {
			readout += matrixLabels[i] + ": " + String.format("%.2f", solutionMatrix.get(i, 0)) + "\n";
		}
		JOptionPane.showMessageDialog(this, readout, "Solution", JOptionPane.INFORMATION_MESSAGE);
	}

	private void clear() {
		this.currentComponent = null;
		popupRotate.setEnabled(false);
		popupDelete.setEnabled(false);
		popupFlip.setEnabled(false);

		this.prevComponent = null;
		this.currentFile = null;

		this.getCircuitComponents().clear();

		this.repaint();
	}

	private CircuitComponent find(Point p) {
		for (CircuitComponent c : components) {
			if (c.area.contains(p)) {
				c.setMousePoint((int) p.getX(), (int) p.getY());
				return c;
			}
		}
		return null;
	}

	private boolean isDirty() {
		return dirty;
	}

	private void makeClean() {
		this.dirty = false;
	}

	private void makeDirty() {
		this.dirty = true;
	}

	private void rebuildComponents() {
		if (currentFile != null) {
			components.clear();
			int i = 1;
			try {
				Scanner sc = new Scanner(currentFile);
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					String[] split = line.split(" +");
					String type = split[0];
					try {
						if (type.equals("R")) {
							this.addComponent(new Resistor(split));
						} else if (type.equals("WIRE")) {
							this.addComponent(new Wire(split));
						} else if (type.equals("I")) {
							this.addComponent(new Current(split));
						} else if (type.equals("V")) {
							this.addComponent(new Voltage(split));
						} else {
							System.err.println("Invalid component indicator '" + type + "' on line " + i
									+ ". Excluding component " + split[1]);
						}
					} catch (NumberFormatException e) {
						System.err.println(
								"Invalid value '" + split[4] + "' on line " + i + ". Excluding component " + split[1]);
					}
					i++;
				}
				sc.close();
				sc = null;
				this.repaint();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, e);
			}
		}
	}

	/**
	 * @return
	 * 		<ul>
	 *         <li>0 if clean or saved</li>
	 *         <li>1 if not saved</li>
	 *         <li>-1 if cancelled or closed</li>
	 *         </ul>
	 */
	private int saveIfDirty() {
		if (this.isDirty()) {
			int saveDialog = JOptionPane.showConfirmDialog(this, "Save?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (saveDialog == JOptionPane.YES_OPTION) {
				return this.save(currentFile);
			} else if (saveDialog == JOptionPane.NO_OPTION) {
				return 1;
			} else {
				return -1;
			}
		}
		return 0;
	}
}
