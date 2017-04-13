package org.tehnexus.home;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.tehnexus.home.warranty.TabWarranty;

/**
 * @author neXus
 */
public class MainFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	/**
	 * @param string
	 */
	public MainFrame(String string) {
		super(string);
		getContentPane().setBackground(new Color(176, 196, 222));

		createGUI();

		addMenuBar();

		// addToolBar();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	private void addMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu mFile = new JMenu("File");
		menuBar.add(mFile);

		JMenuItem mnuFileRead = new JMenuItem("Read", new ImageIcon("src/images/DATA_READ.png"));
		mnuFileRead.setActionCommand("");
		mnuFileRead.addActionListener(this);
		mFile.add(mnuFileRead);

		mFile.add(new JSeparator());

		JMenuItem mnuFileQuit = new JMenuItem("Quit", new ImageIcon("src/images/SYSTEM_EXIT.png"));
		mnuFileQuit.addActionListener((ActionEvent e) -> {
			System.exit(0);
		});

		mFile.add(mnuFileQuit);

		JMenu mHelp = new JMenu("Help");
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(mHelp);

		JMenuItem mnuHelpAbout = new JMenuItem("About");
		mHelp.add(mnuHelpAbout);

		setJMenuBar(menuBar);
	}

	private void createGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		getContentPane().add(tabbedPane);

		TabWarranty tabWarranty = new TabWarranty();
		tabbedPane.addTab("Warranty", null, tabWarranty, null);

	}

}
