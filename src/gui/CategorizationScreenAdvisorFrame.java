package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import categorize.Nonconformance;
import controller.Controller;

/**
 * Shown an Screen for Categorization info of the program.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 */
public class CategorizationScreenAdvisorFrame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5134427426591954176L;
	private List<Nonconformance> nc;
	private String[] namesNC;
	private JPanel contentPane;
	private JList<String> listNonconformances;
	private JLabel lblMethodNameSetter;
	private JLabel lblClassNameSetter;
	private JLabel lblLikelyCauseSetter;
	private JLabel lblLikelyCauseExplanation;
	private JLabel lblPackageNameSetter;
	private JLabel lblPackageName;
	private JTextArea textAreaTestCases;
	private Highlighter highLit;
	private Highlighter.HighlightPainter painter;
	private JLabel lblNonconfomancesNumberSetter;
	private JFileChooser dirLibs;
	private JFrame actualFrame = this;
	

	/**
	 * Create the frame.
	 */
	 public CategorizationScreenAdvisorFrame(List<Nonconformance> nonconformance) {
		initializingStringForSelectionList(nonconformance);
		
		dirLibs = new JFileChooser();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNonconf = new JLabel("Nonconformances");
		lblNonconf.setBounds(53, 54, 214, 15);
		contentPane.add(lblNonconf);
		
		JLabel lblNewLabel = new JLabel("Location");
		lblNewLabel.setBounds(283, 54, 70, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblTestCases = new JLabel("Test Cases");
		lblTestCases.setBounds(34, 333, 134, 15);
		contentPane.add(lblTestCases);
		
		JButton btnSaveResults = new JButton("Save Results");
		btnSaveResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveResults();
			}
		});
		btnSaveResults.setBounds(599, 358, 143, 25);
		contentPane.add(btnSaveResults);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		btnExit.setBounds(599, 412, 143, 25);
		contentPane.add(btnExit);
		
		JLabel lblClassName = new JLabel("Class Name");
		lblClassName.setBounds(213, 135, 106, 15);
		contentPane.add(lblClassName);
		
		JLabel lblMethodName = new JLabel("Method Name");
		lblMethodName.setBounds(213, 191, 106, 15);
		contentPane.add(lblMethodName);
		
		lblClassNameSetter = new JLabel("");
		lblClassNameSetter.setBounds(223, 165, 349, 15);
		contentPane.add(lblClassNameSetter);
		
		lblMethodNameSetter = new JLabel("");
		lblMethodNameSetter.setBounds(223, 221, 349, 15);
		contentPane.add(lblMethodNameSetter);
		
		JLabel lblCause = new JLabel("Cause");
		lblCause.setBounds(213, 245, 70, 15);
		contentPane.add(lblCause);
		
		lblLikelyCauseSetter = new JLabel("");
		lblLikelyCauseSetter.setBounds(223, 275, 349, 15);
		contentPane.add(lblLikelyCauseSetter);
		
		lblLikelyCauseExplanation = new JLabel("");
		lblLikelyCauseExplanation.setBounds(437, 218, 153, 15);
		contentPane.add(lblLikelyCauseExplanation);
		
		lblPackageName = new JLabel("Package");
		lblPackageName.setBounds(220, 81, 133, 15);
		contentPane.add(lblPackageName);
		
		lblPackageNameSetter = new JLabel("");
		lblPackageNameSetter.setBounds(219, 109, 353, 15);
		contentPane.add(lblPackageNameSetter);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(24, 81, 178, 240);
		contentPane.add(scrollPane);
		
		listNonconformances = new JList<String>(namesNC);
		scrollPane.setViewportView(listNonconformances);
		listNonconformances.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listNonconformances.addListSelectionListener(
			new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					setChangesFromSelectionOnTheList();
				}
			}
		);
		highLit = new DefaultHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(24, 359, 531, 84);
		contentPane.add(scrollPane_1);
		
		textAreaTestCases = new JTextArea();
		scrollPane_1.setViewportView(textAreaTestCases);
		
		JLabel lblWereDetected = new JLabel("Were detected: ");
		lblWereDetected.setBounds(24, 27, 139, 15);
		contentPane.add(lblWereDetected);
		
		lblNonconfomancesNumberSetter = new JLabel(nc.size() + "");
		lblNonconfomancesNumberSetter.setBounds(144, 27, 70, 15);
		contentPane.add(lblNonconfomancesNumberSetter);
		
		JLabel lblNonconfomities = new JLabel("nonconformances.");
		lblNonconfomities.setBounds(175, 27, 184, 15);
		contentPane.add(lblNonconfomities);
		
		JPanel panel = new JPanel();
		panel.setBounds(582, 27, 192, 301);
		contentPane.add(panel);
		
		actualFrame .addWindowStateListener(new WindowAdapter() {
			
			@Override
			public void windowStateChanged(WindowEvent e){
				if(e.getNewState() == JFrame.MAXIMIZED_BOTH){
					actualFrame.setExtendedState(JFrame.NORMAL);
				}
			}
		});

	}

	/**
	 * Operation when button save results are pressed, copy the file results.xml to
	 * another directory specified by user.
	 */
	protected void saveResults() {
		String path = "";
		dirLibs.setApproveButtonText("Select");
		dirLibs.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirLibs.setCurrentDirectory(new File(jarPath()));
		if (dirLibs.showOpenDialog(CategorizationScreenAdvisorFrame.this) == JFileChooser.APPROVE_OPTION) {
			path = dirLibs.getSelectedFile().getAbsolutePath();
		}
		try {
			Controller.saveResultsInXML(path);
			JOptionPane.showMessageDialog(this, "Results saved.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Invalid Directory.");
		}
	}

	private String jarPath() {
		Path path = null;
		try {
			path = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path.getParent().toString();
	}

	
	/**
	 * Execute some changes in labels, when user select some items from the list of nonconformances.
	 */
	private void setChangesFromSelectionOnTheList() {
			lblClassNameSetter.setText(nc.get(listNonconformances.getSelectedIndex()).getClassName());
			lblMethodNameSetter.setText(nc.get(listNonconformances.getSelectedIndex()).getMethodName());
			lblLikelyCauseSetter.setText(nc.get(listNonconformances.getSelectedIndex()).getCause());
			if(nc.get(listNonconformances.getSelectedIndex()).getPackageName() == ""){
				lblPackageNameSetter.setText("<default>");						
			}else{
				lblPackageNameSetter.setText(nc.get(listNonconformances.getSelectedIndex()).getPackageName());						
			}
			textAreaTestCases.setText(nc.get(listNonconformances.getSelectedIndex()).getLinesFromTestFile());
			textAreaTestCases.setEditable(false);
			textAreaTestCases.setHighlighter(highLit);
			int beginIndex = textAreaTestCases.getText().indexOf("\n", textAreaTestCases.getText().indexOf("\n")) + 1;
			beginIndex = textAreaTestCases.getText().indexOf("\n", beginIndex) + 1;
			int endIndex = textAreaTestCases.getText().indexOf(";", beginIndex) + 1;
			try {
				highLit.addHighlight(beginIndex, endIndex, painter);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
	}
	 
	/**
	 * Initialize the list strings. 
	 * @param nonconformance The List of nonconformances categorized by the program.
	 */
	private void initializingStringForSelectionList(List<Nonconformance> nonconformance) {
		nc = nonconformance;
		namesNC = new String[nc.size()];
		for (int i = 0; i < nc.size(); i++) {
			namesNC[i] = nc.get(i).getType() + " " + nc.get(i).getTest(); 
		}
	}

	/**
	 * Close the Screen.
	 */
	protected void closeWindow() {
		this.setVisible(false);
	}
}
