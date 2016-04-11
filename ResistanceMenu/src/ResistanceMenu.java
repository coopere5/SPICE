import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.udayton.coopere5.spice.ResistancePanel;

/**
 * @author Evan Cooper
 *
 */
public class ResistanceMenu extends JFrame implements ActionListener, Runnable {

	private class CloseHandler implements WindowListener {
		@Override
		public void windowActivated(WindowEvent arg0) {
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			RPanel.saveAndExit();
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
		}
	}

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new ResistanceMenu());
	}

	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenuItem menuFileNew;
	private JMenuItem menuFileOpen;
	private JMenuItem menuFileSave;
	private JMenuItem menuFileSaveAs;
	private JMenuItem menuFileExit;
	private JMenu menuEdit;
	private JMenuItem menuEditRotateCW;
	private JMenuItem menuEditRotateCCW;
	private JMenuItem menuEditSimulate;
	private JMenu menuHelp;

	private JMenuItem menuHelpAbout;

	private ResistancePanel RPanel;

	public ResistanceMenu() throws HeadlessException {
		this.setTitle("untitled");
		this.setSize(500, 500);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new CloseHandler());

		RPanel = new ResistancePanel();
		RPanel.setLayout(new BorderLayout());
		this.getContentPane().add(RPanel);

		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		menuFile = new JMenu("File");
		menuFile.setMnemonic('F');
		menuBar.add(menuFile);

		menuFileNew = this.CreateMenuItem(menuFile, "New", null, 'N', null);
		menuFileOpen = this.CreateMenuItem(menuFile, "Open...", null, 'O', null);
		menuFileSave = this.CreateMenuItem(menuFile, "Save", null, 'S', null);
		menuFileSaveAs = this.CreateMenuItem(menuFile, "Save As...", null, 'A', null);
		menuFile.addSeparator();
		menuFileExit = this.CreateMenuItem(menuFile, "Exit", null, 'x', null);

		menuEdit = new JMenu("Edit");
		menuEdit.setMnemonic('E');
		menuBar.add(menuEdit);

		menuEditRotateCW = this.CreateMenuItem(menuEdit, "Rotate Clockwise", null, 0, null);
		menuEditRotateCCW = this.CreateMenuItem(menuEdit, "Rotate Counter-Clockwise", null, 0, null);
		menuEditSimulate = this.CreateMenuItem(menuEdit, "Simulate", null, 'S', null);

		menuHelp = new JMenu("Help");
		menuHelp.setMnemonic('H');
		menuBar.add(menuHelp);

		menuHelpAbout = this.CreateMenuItem(menuHelp, "About", null, 'A', null);

		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		switch (event.getActionCommand()) {
		case "New":
			RPanel.newFile();
			this.setTitle("untitled");
			break;
		case "Open...":
			RPanel.open();
			this.setTitle(RPanel.getFileName());
			break;
		case "Save":
			RPanel.save();
			this.setTitle(RPanel.getFileName());
			break;
		case "Save As...":
			RPanel.save(null);
			this.setTitle(RPanel.getFileName());
			break;
		case "Exit":
			RPanel.saveAndExit();
			break;
		case "Rotate Clockwise":
			RPanel.rotate(1);
			break;
		case "Rotate Counter-Clockwise":
			RPanel.rotate(-1);
			break;
		case "Simulate":
			RPanel.solve();
			break;
		case "About":
			this.about();
			break;
		default:
			System.err.println(event.getActionCommand() + " is NYI in the actionlistener");
			break;
		}
	}

	public JMenuItem CreateMenuItem(JMenu menu, String text, ImageIcon image, int mnemonic, String tooltip) {
		JMenuItem menuItem;
		menuItem = new JMenuItem();
		menuItem.setText(text);
		if (image != null) {
			menuItem.setIcon(image);
		}
		if (mnemonic > 0) {
			menuItem.setMnemonic(mnemonic);
		}
		if (tooltip != null) {
			menuItem.setToolTipText(tooltip);
		}
		menuItem.addActionListener(this);
		menu.add(menuItem);
		return menuItem;
	}

	@Override
	public void run() {
	}

	private void about() {
		String message = "ResistanceMenu.java\n";
		message += "ECE 449 Assignment 8\n";
		message += "Created by Evan Cooper";
		JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
	}
}
