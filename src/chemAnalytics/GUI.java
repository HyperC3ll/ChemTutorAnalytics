package chemAnalytics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

public class GUI extends JFrame implements HidObserver {
	
	public final class StudentData {
		public static final int FIRST_NAME = 0;
		public static final int LAST_NAME = 1;
		public static final int AFFILIATION = 2;
		public static final int MAJOR = 3;
		public static final int GRADELEVEL = 4;
		public static final int CLASS = 5;
		
		private StudentData() {}
	}
	
	private ExcelAPI jxlApi;
	
	private static final long serialVersionUID = 1L;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnAnalytics;
	private JMenu mnOptions;
	private JMenuItem mntmExit;
	private JTextField lblReaderStatus;
	private JPanel pnlStudentData;
	private JLabel lblNewLabel;
	private JTextField txtStudentName;
	private JLabel lblAffiliation;
	private JTextField txtAffiliation;
	private JLabel lblCourse;
	private JTextField txtStudentID;
	private JLabel lblStudentId;
	private Component verticalStrut;
	private JTextField txtCourseName;
	private Component verticalStrut_1;
	private JLabel lblDataConfirm;
	private JPanel panel;
	private JButton btnConfirm;
	private JButton btnDeny;
	private JSeparator separator;

	public GUI(ExcelAPI jxlApi) {
		this.jxlApi = jxlApi;
		initialize();
		setVisible(true);
	}

	private void initialize() {
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e) { }
		setBounds(100, 100, 628, 444);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Chemistry Tutoring Center Login [Creator: Matthew Baresch]");
		setAlwaysOnTop(true);

		//setIconImage(ImageIO.read(ClassLoader.getSystemResource("uml_logo.jpg")));
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		mnAnalytics = new JMenu("Analytics");
		menuBar.add(mnAnalytics);
		
		mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		lblReaderStatus = new JTextField("Connecting to Reader...");
		lblReaderStatus.setBackground(Color.ORANGE);
		lblReaderStatus.setEditable(false);
		lblReaderStatus.setFont(HidTest.DEFAULT_FONT);
		lblReaderStatus.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblReaderStatus, BorderLayout.NORTH);
		
		pnlStudentData = new JPanel();
		getContentPane().add(pnlStudentData, BorderLayout.CENTER);
		pnlStudentData.setLayout(new MigLayout("", "[grow][49px][258px,grow][grow]", "[grow][][20px][][20px][20px][][][grow][][][grow]"));
		
		lblStudentId = new JLabel("Student ID:");
		pnlStudentData.add(lblStudentId, "cell 1 1,alignx trailing");
		
		txtStudentID = new JTextField();
		txtStudentID.setHorizontalAlignment(SwingConstants.LEFT);
		txtStudentID.setEditable(false);
		pnlStudentData.add(txtStudentID, "cell 2 1,growx");
		txtStudentID.setColumns(10);
		
		lblNewLabel = new JLabel("Name:");
		pnlStudentData.add(lblNewLabel, "cell 1 2,alignx right,aligny center");
		
		txtStudentName = new JTextField();
		txtStudentName.setHorizontalAlignment(SwingConstants.LEFT);
		txtStudentName.setEditable(false);
		pnlStudentData.add(txtStudentName, "cell 2 2,growx,aligny top");
		txtStudentName.setColumns(10);
		
		verticalStrut = Box.createVerticalStrut(20);
		pnlStudentData.add(verticalStrut, "cell 2 3");
		
		lblAffiliation = new JLabel("Affiliation:");
		pnlStudentData.add(lblAffiliation, "cell 1 4,alignx left,aligny center");
		
		txtAffiliation = new JTextField();
		txtAffiliation.setEditable(false);
		pnlStudentData.add(txtAffiliation, "cell 2 4,growx,aligny top");
		txtAffiliation.setColumns(10);
		
		lblCourse = new JLabel("Course:");
		pnlStudentData.add(lblCourse, "cell 1 5,alignx trailing,aligny center");
		
		txtCourseName = new JTextField();
		txtCourseName.setEditable(false);
		pnlStudentData.add(txtCourseName, "cell 2 5,growx");
		txtCourseName.setColumns(10);
		
		verticalStrut_1 = Box.createVerticalStrut(20);
		pnlStudentData.add(verticalStrut_1, "cell 2 6");
		
		separator = new JSeparator();
		pnlStudentData.add(separator, "cell 0 7 4 1,growx,aligny center");
		
		lblDataConfirm = new JLabel("IS ALL OF THE ABOVE DATA CORRECT?");
		pnlStudentData.add(lblDataConfirm, "cell 1 9 2 1,alignx center");
		
		panel = new JPanel();
		pnlStudentData.add(panel, "cell 1 10 2 1,grow");
		
		btnConfirm = new JButton("Yes (Sign In)");
		panel.add(btnConfirm);
		
		btnDeny = new JButton("No (Edit Data)");
		panel.add(btnDeny);
	}

	@Override
	public void updateConnection(ObservableHid hid, Boolean readerConnected) {
		lblReaderStatus.setText((Boolean)readerConnected ? "Reader Connected" : "Reader Disconnected (Please Connect MagTek Reader)");
		lblReaderStatus.setBackground((Boolean)readerConnected ? Color.GREEN : Color.RED);
	}
	
	@Override
	public void updatePan(ObservableHid hid, String pan) {
		txtStudentID.setText(pan);
		
		String[] studentData = jxlApi.getProcessedStudentData(pan);
		if(studentData.length == 1) {
			txtStudentName.setText(studentData[0]);
		} else {
			txtStudentName.setText(studentData[StudentData.FIRST_NAME] + " " + studentData[StudentData.LAST_NAME]);
			txtAffiliation.setText(studentData[StudentData.AFFILIATION]);
			txtCourseName.setText(studentData[StudentData.CLASS]);
		}
	}
}
