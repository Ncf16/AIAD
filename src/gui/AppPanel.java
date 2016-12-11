package gui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AppPanel extends JPanel implements ListSelectionListener {
	private static final long serialVersionUID = 1L;
	private Image logo;
	private boolean showLogo = true;

	public static DefaultListModel agentModel;
	public static DefaultListModel companyModel;
	public static DefaultListModel logModel;
	
	private JList companyList;
	private JList agentList;
	private JList actionLog;

	public AppPanel() {
		loadLogo();
	}

	public void loadLogo() {
		ImageIcon icon;
		icon = new ImageIcon(this.getClass().getResource("images/TradeHeroLogo.png"));
		logo = icon.getImage();
	}

	public void paintComponent(Graphics g) {
		Graphics2D graphics2d = (Graphics2D) g;
		if (showLogo)
			graphics2d.drawImage(logo, 0, 0, this.getWidth(), this.getHeight(), 0, 0, logo.getWidth(null),
					logo.getHeight(null), null);
		else
			drawSimulation(graphics2d);
	}

	public void startSimulation() {
		showLogo = false;
		setFocusable(true);
		setDoubleBuffered(true);
		createLists();
		requestFocus();

	}

	private void createLists() {
		createAgentList();
		createCompanyList();
		createLog();
	}

	public void createAgentList() {
		agentModel = new DefaultListModel();
		agentList = new JList(agentModel);
		
		agentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		agentList.setSelectedIndex(0);
		agentList.addListSelectionListener(this);
		agentList.setVisibleRowCount(5);

		agentList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2) {
					openAgentInfo(list.getSelectedIndex());
				}
			}
		});

		JScrollPane listScrollPane = new JScrollPane(agentList);
		add(listScrollPane, BorderLayout.WEST);
	}

	private void openAgentInfo(int agentIndex) {
		JPanel panel = new JPanel();
		panel.setLayout((LayoutManager) new BoxLayout(panel, BoxLayout.Y_AXIS));

		String title = "Agent " + (agentIndex + 1);

		panel.add(new JLabel("Name: " + "Agent Name"));
		panel.add(Box.createVerticalStrut(15)); // a spacer

		panel.add(new JLabel("Agent Type: " + "TYPE"));
		panel.add(Box.createVerticalStrut(15)); // a spacer

		panel.add(new JLabel("Starting Money: "));
		panel.add(Box.createVerticalStrut(15)); // a spacer

		panel.add(new JLabel("Goal Money: "));
		panel.add(Box.createVerticalStrut(15)); // a spacer
		
		panel.add(new JLabel("Current Money [cash]: "));
		panel.add(Box.createVerticalStrut(15)); // a spacer
		
		panel.add(new JLabel("Current Money [stocks]: "));
		panel.add(Box.createVerticalStrut(15)); // a spacer
		
		DefaultListModel listModel = new DefaultListModel();
		listModel.addElement("Company A : X in stock");
		listModel.addElement("Company A : X in stock");
		listModel.addElement("Company A : X in stock");
		listModel.addElement("Company A : X in stock");
		listModel.addElement("Company A : X in stock");


		JList stockholdings = new JList(listModel);
		stockholdings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stockholdings.setSelectedIndex(0);
		stockholdings.addListSelectionListener(this);
		stockholdings.setVisibleRowCount(5);
		
		panel.add(stockholdings);


		int result = JOptionPane.showConfirmDialog(null, panel, title, JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			// Create an agent
		}
	}

	public void createCompanyList() {
		companyModel = new DefaultListModel();


		companyList = new JList(companyModel);
		companyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		companyList.setSelectedIndex(0);
		companyList.addListSelectionListener(this);
		companyList.setVisibleRowCount(5);

		JScrollPane listScrollPane = new JScrollPane(companyList);
		add(listScrollPane, BorderLayout.CENTER);
	}

	public void createLog() {
		logModel = new DefaultListModel();


		actionLog = new JList(logModel);
		actionLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		actionLog.setSelectedIndex(0);
		actionLog.addListSelectionListener(this);
		actionLog.setVisibleRowCount(5);

		JScrollPane listScrollPane = new JScrollPane(actionLog);
		add(listScrollPane, BorderLayout.EAST);
	}

	public void drawSimulation(Graphics2D graphics2d) {

	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

}