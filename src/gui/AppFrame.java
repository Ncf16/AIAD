package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import company.Stock.StockType;
import gui.AppFrame.AgentInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Toolkit;

import main.Main;

public class AppFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private AppPanel panel;
	private JPanel menuButtonsPanel;
	private JPanel simulationButtonsPanel;

	private JButton start, exit;
	private JButton createAgent, createCompany;

	public static ArrayList<AgentInfo> agentList;

	public AppFrame() throws IOException {
		setTitle("TradeHero");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		panel = new AppPanel();
		menuButtonsPanel = new JPanel();
		simulationButtonsPanel = new JPanel();
		agentList = new ArrayList<AgentInfo>();
		setupButtons();
		getContentPane().setLayout(new BorderLayout(0, 0));
		addMenuButtons();
		getContentPane().add(panel);
	}

	public void start() {
		setSize(400, 500);

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
				setSize(600, 450);

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
			JTextField companyName = new JTextField(5);
			JPanel panel = new JPanel();
			panel.setLayout((LayoutManager) new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(new JLabel("Name"));
			panel.add(companyName);
			panel.add(Box.createVerticalStrut(15)); // a spacer

			JTextField stockValue = new JTextField(5);
			panel.add(new JLabel("Stock Value: "));
			panel.add(stockValue);
			panel.add(Box.createVerticalStrut(15));

			panel.add(new JLabel("Stock Type"));
			final JRadioButton normalStock = new JRadioButton("Normal");
			final JRadioButton volatileStock = new JRadioButton("Volatile");
			final JRadioButton veryVolatileStock = new JRadioButton("Very Volatile");

			ButtonGroup group = new ButtonGroup();
			group.add(normalStock);
			group.add(volatileStock);
			group.add(veryVolatileStock);
			panel.add(normalStock);
			panel.add(volatileStock);
			panel.add(veryVolatileStock);
			panel.add(Box.createVerticalStrut(15));

			int result = JOptionPane.showConfirmDialog(null, panel, "Create New Company", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				// Deebuggerz
				System.out.println("name: " + companyName.getText());
				System.out.println("name: " + stockValue.getText());
				StockType stocktype = StockType.NORMAL;
				if (normalStock.isSelected())
					System.out.println("normalStock");
				else if (volatileStock.isSelected()) {
					stocktype = StockType.VOLATILE;
					System.out.println("volatileStock");
				} else if (veryVolatileStock.isSelected()) {
					stocktype = StockType.VERY_VOLATILE;
					System.out.println("veryVolatileStock");
				}

				double stockValue_f = Double.parseDouble(stockValue.getText());

				Main.ci = new CreationInfo(SUtil.createHashMap(new String[] { "companyName", "stockPrice", "stockType" }, new Object[] { companyName.getText(), stockValue_f * 1.0, stocktype }));
				Main.tupleFut = Main.cms.createComponent("myCompanyBDI", "company.CompanyBDI.class", Main.ci);
				Main.cid = Main.tupleFut.getFirstResult();
				AppPanel.companyModel.addElement(companyName.getText());

			}

		}
	}

	public static void addToAgentLog(String name, String log) {
		for (AgentInfo it : agentList) {
			if (it.name.equals(name)) {
				it.agentLog.add(log);
			}
		}
	}

	public class AgentInfo {
		public String name;
		double startMoney = 0.0;
		double goalMoney = 0.0;
		List<String> agentLog;
		public Double currentMoney = 0.0;
		public Double stockMoney = 0.0;

		public AgentInfo(String name, double startMoney, double goalMoney) {
			this.name = name;
			this.startMoney = startMoney;
			this.goalMoney = goalMoney;
			this.agentLog = new ArrayList<String>();
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
			panel.add(Box.createVerticalStrut(15));

			JTextField startingMoney = new JTextField(5);
			panel.add(new JLabel("Starting Money: "));
			panel.add(startingMoney);
			panel.add(Box.createVerticalStrut(15));

			JTextField goalMoney = new JTextField(5);
			panel.add(new JLabel("Goal Money: "));
			panel.add(goalMoney);
			panel.add(Box.createVerticalStrut(15));

			JTextField maxRisk = new JTextField(5);
			panel.add(new JLabel("Maximum Risk [0-1]: "));
			panel.add(maxRisk);
			panel.add(Box.createVerticalStrut(15));

			JTextField lowerBoundOfSalesInterval = new JTextField(5);
			panel.add(new JLabel("Lower Bound of Sales Interval: "));
			panel.add(lowerBoundOfSalesInterval);
			panel.add(Box.createVerticalStrut(15));

			JTextField upperBoundOfSalesInterval = new JTextField(5);
			panel.add(new JLabel("Upper Bound of Sales Interval: "));
			panel.add(upperBoundOfSalesInterval);
			panel.add(Box.createVerticalStrut(15));

			JTextField maxMoneySpentOnPurchase = new JTextField(5);
			panel.add(new JLabel("Maximum Amount Spent on Purchase [0-1]: "));
			panel.add(maxMoneySpentOnPurchase);
			panel.add(Box.createVerticalStrut(15));

			JTextField minAgentPerformance = new JTextField(5);
			panel.add(new JLabel("Minimum Agent Performance[0-1]: "));
			panel.add(minAgentPerformance);
			panel.add(Box.createVerticalStrut(15));

			int result = JOptionPane.showConfirmDialog(null, panel, "Create New Buyer Agent", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {

				// Debug Stuff
				System.out.println("name: " + agentName.getText());
				System.out.println("start: " + startingMoney.getText());
				System.out.println("goal: " + goalMoney.getText());
				System.out.println("maxMoneySpentOnPurchase: " + maxMoneySpentOnPurchase.getText());
				System.out.println("maxRisk: " + maxRisk.getText());
				System.out.println("lowerBoundOfSalesInterval: " + lowerBoundOfSalesInterval.getText());
				System.out.println("upperBoundOfSalesInterval: " + upperBoundOfSalesInterval.getText());
				System.out.println("minAgentPerformance: " + minAgentPerformance.getText());

				// Convert values

				double startingMoney_f = Double.parseDouble(startingMoney.getText());
				double goalMoney_f = Double.parseDouble(goalMoney.getText());
				double maxMoneySpentOnPurchase_f = Double.parseDouble(maxMoneySpentOnPurchase.getText());
				double maxRisk_f = Double.parseDouble(maxRisk.getText());
				double lowerBoundOfSalesInterval_f = Double.parseDouble(lowerBoundOfSalesInterval.getText());
				double upperBoundOfSalesInterval_f = Double.parseDouble(upperBoundOfSalesInterval.getText());
				double minAgentPerformance_f = Double.parseDouble(minAgentPerformance.getText());

				// Create an agent
				Main.ci = new CreationInfo(SUtil.createHashMap(
						new String[] { "platform", "name", "startingMoney", "goalMoney", "maxRisk", "lowerBoundOfSalesInterval", "upperBoundOfSalesInterval", "debug", "maxMoneySpentOnPurchase",
								"minAgentPerformance" },
						new Object[] { Main.platform, agentName.getText(), startingMoney_f * 1.0, goalMoney_f * 1.0, maxRisk_f * 1.0, lowerBoundOfSalesInterval_f * 1.0,
								upperBoundOfSalesInterval_f * 1.0, true, maxMoneySpentOnPurchase_f * 1.0, minAgentPerformance_f * 1.0 }));
				Main.tupleFut = Main.cms.createComponent("myPlayerBDI", "agents.PlayerBDI.class", Main.ci);
				Main.cid = Main.tupleFut.getFirstResult();
				// Add To List
				agentList.add(new AgentInfo(agentName.getText(), startingMoney_f, goalMoney_f));
				AppPanel.agentModel.addElement(agentName.getText());

			}

		}
	}

}
