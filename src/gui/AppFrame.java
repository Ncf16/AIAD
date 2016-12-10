package gui;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Toolkit;

public class AppFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private AppPanel panel;
	private JPanel menuButtonsPanel;
	private JPanel simulationButtonsPanel;

	private JButton start, exit;
	private JButton createAgent, createCompany;

	public AppFrame() throws IOException {
		setTitle("TradeHero");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		panel = new AppPanel();
		menuButtonsPanel = new JPanel();
		simulationButtonsPanel = new JPanel();

		setupButtons();
		getContentPane().setLayout(new BorderLayout(0, 0));
		addMenuButtons();
		getContentPane().add(panel);
	}

	public void start() {
		setSize(534, 401);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);

		setVisible(true);
	}

	public void setupButtons() {
		// Main Menu Buttons
		start = new JButton("Start Simulation");
		start.addActionListener(new StartActionListener());

		exit = new JButton("Exit");
		exit.addActionListener(new ExitActionListener());

		// Gameplay Buttons
		createAgent = new JButton("Create Agent");
		createAgent.addActionListener(new CreateAgentActionListener());

		createCompany = new JButton("Create Company");
		createCompany.addActionListener(new CreateCompanyActionListener());
	}

	public void addMenuButtons() {
		menuButtonsPanel.setLayout(new GridLayout(2, 1));
		menuButtonsPanel.add(start);
		menuButtonsPanel.add(exit);
		getContentPane().add(menuButtonsPanel, BorderLayout.SOUTH);
	}

	public void addGameButtons() {
		simulationButtonsPanel.setLayout(new GridLayout(2, 1));
		simulationButtonsPanel.add(createAgent);
		simulationButtonsPanel.add(createCompany);
		getContentPane().add(simulationButtonsPanel, BorderLayout.SOUTH);
	}

	public void hideMenuButtons() {
		menuButtonsPanel.setVisible(false);
	}

	public void showMenuButtons() {
		menuButtonsPanel.setVisible(true);
	}

	public void showGameButtons() {
		simulationButtonsPanel.setVisible(true);
	}

	public void hideGameButtons() {
		simulationButtonsPanel.setVisible(false);
	}

	public class StartActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String msg = "Do you want to start a new game?";
			int res = JOptionPane.showConfirmDialog(rootPane, msg);

			if (res == JOptionPane.YES_OPTION) {
				setSize(642, 598);

				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);

				// starting new game with new options
				hideMenuButtons();
				addGameButtons();
				panel.startSimulation();
			}

		}

	}

	public class ExitActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String msg = "Do you want to quit?";
			int res = JOptionPane.showConfirmDialog(rootPane, msg);

			if (res == JOptionPane.YES_OPTION)
				System.exit(0);
		}
	}

	public class CreateCompanyActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JTextField agentName = new JTextField(5);
			JPanel panel = new JPanel();
			panel.setLayout((LayoutManager) new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(new JLabel("Name"));
			panel.add(agentName);
			panel.add(Box.createVerticalStrut(15)); // a spacer

			JTextField stockValue = new JTextField(5);
			panel.add(new JLabel("Stock Value: "));
			panel.add(stockValue);

			panel.add(new JLabel("Stock Type"));
			final JRadioButton normalStock = new JRadioButton("Normal");
			final JRadioButton volatileStock = new JRadioButton("Volatile");
			final JRadioButton veryVolatileStock = new JRadioButton("Very Volatile");
			panel.add(normalStock);
			panel.add(volatileStock);
			panel.add(veryVolatileStock);

			int result = JOptionPane.showConfirmDialog(null, panel, "Create New Company",
					JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				// Create an agent
			}

		}
	}

	public class CreateAgentActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JTextField agentName = new JTextField(5);
			JPanel panel = new JPanel();
			panel.setLayout((LayoutManager) new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(new JLabel("Name"));
			panel.add(agentName);
			panel.add(Box.createVerticalStrut(15)); // a spacer

			panel.add(new JLabel("Agent Type"));
			final JRadioButton cautious = new JRadioButton("Cautious");
			final JRadioButton normal = new JRadioButton("regular");
			final JRadioButton greedy = new JRadioButton("Greedy");
			panel.add(cautious);
			panel.add(normal);
			panel.add(greedy);

			JTextField startingMoney = new JTextField(5);
			panel.add(new JLabel("Starting Money: "));
			panel.add(startingMoney);

			JTextField goalMoney = new JTextField(5);
			panel.add(new JLabel("Goal Money: "));
			panel.add(goalMoney);

			int result = JOptionPane.showConfirmDialog(null, panel, "Create New Buyer Agent",
					JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				// Create an agent
			}

		}
	}
	


}
