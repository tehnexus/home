package com.github.tehnexus.home.warranty;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.github.tehnexus.awt.Dimension;

/* 1.4 example used by DialogDemo.java. */
public class ConfirmDialog extends JDialog implements ActionListener, PropertyChangeListener {

	private String		typedText	= null;
	private JTextField	textField;
	private JFrame		parent;

	private String		magicWord;
	private JOptionPane	optionPane;

	private String		btnString1	= "Enter";
	private String		btnString2	= "Cancel";

	/** Creates the reusable dialog. */
	public ConfirmDialog(JFrame parent, String title, String message, String pass) {
		super(parent, title, true);
		this.parent = parent;

		magicWord = pass;
		textField = new JTextField(magicWord.length());

		// Create an array of the text and components to be displayed.
		String msgString = message + magicWord;
		Object[] array = { msgString, textField };

		// Create an array specifying the number of dialog buttons
		// and their text.
		Object[] options = { btnString1, btnString2 };

		// Create the JOptionPane.
		optionPane = new JOptionPane(array, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options,
				options[0]);

		// Make this dialog display it.
		setContentPane(optionPane);

		// Handle window closing correctly.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent we) {
				/*
				 * Instead of directly closing the window, we're going to change
				 * the JOptionPane's value property.
				 */
				optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
			}
		});

		// Ensure the text field always gets the first focus.
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent ce) {
				textField.requestFocusInWindow();
			}
		});

		// Register an event handler that puts the text into the option pane.
		textField.addActionListener(this);

		// Register an event handler that reacts to option pane state changes.
		optionPane.addPropertyChangeListener(this);

		// set up the dialog
		setSize(new Dimension(400, 150));
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/** This method handles events for the text field. */
	@Override
	public void actionPerformed(ActionEvent e) {
		optionPane.setValue(btnString1);
	}

	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		// textField.setText(null);
		setVisible(false);
	}

	/**
	 * Returns null if the typed string was invalid; otherwise, returns the
	 * string as the user entered it.
	 */
	public String getValidatedText() {
		return typedText;
	}

	public boolean isConfirmed() {
		return magicWord.equals(typedText);
	}

	/** This method reacts to state changes in the option pane. */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();

		if (isVisible() && (e.getSource() == optionPane)
				&& (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
			Object value = optionPane.getValue();

			if (value == JOptionPane.UNINITIALIZED_VALUE)
				return;

			// Reset the JOptionPane's value.
			// If you don't do this, then if the user
			// presses the same button next time, no
			// property change event will be fired.
			optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

			if (btnString1.equals(value)) {
				typedText = textField.getText();
				if (magicWord.equals(typedText)) // we're done; clear and
													// dismiss the dialog
					clearAndHide();

				else {
					// text was invalid
					textField.selectAll();
					JOptionPane.showMessageDialog(parent, "Sorry, \"" + typedText + "\" "
							+ "isn't a valid response.\nPlease enter " + magicWord + ".", "Try again",
							JOptionPane.ERROR_MESSAGE);
					typedText = null;
					textField.requestFocusInWindow();
				}
			}
			else { // user closed dialog or clicked cancel
				typedText = null;
				clearAndHide();
			}
		}
	}
}
