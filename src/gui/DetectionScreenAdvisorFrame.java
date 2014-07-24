package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.output.ByteArrayOutputStream;

import controller.Controller;

/**
 * Screen shown after Detection phase, executed by the program. An advisor screen
 * were it just shows that detection occurred with no problems.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 */
public class DetectionScreenAdvisorFrame extends JFrame {

	private static final long serialVersionUID = 7840357361061019283L;
	private JPanel contentPane;
	private JFrame actualFrame = this;
	private boolean detectionSuceeded = false;

	/**
	 * Create the frame.
	 */
	public DetectionScreenAdvisorFrame(ByteArrayOutputStream baos, boolean detectionSuceeded) {
		setDetectionSuceeded(detectionSuceeded);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 520);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblDetectionPhaseIs = new JLabel("Detection Phase finished.");
		lblDetectionPhaseIs.setBounds(30, 17, 219, 15);
		contentPane.add(lblDetectionPhaseIs);
		
		if(isDetectionSuceeded()){
			JButton btnNext = new JButton("Nonconformances");
			btnNext.setBounds(426, 12, 185, 25);
			btnNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					callsCategorization();
				}
			});
			contentPane.add(btnNext);
		}else{
			JButton btnNext = new JButton("Exit");
			btnNext.setBounds(426, 12, 185, 25);
			btnNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					exit();
				}
			});
			contentPane.add(btnNext);
		}
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 51, 614, 388);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setText(baos.toString());
		textArea.setEditable(false);
		
		actualFrame.addWindowStateListener(new WindowAdapter() {
			
			@Override
			public void windowStateChanged(WindowEvent e){
				if(e.getNewState() == JFrame.MAXIMIZED_BOTH){
					actualFrame.setExtendedState(JFrame.NORMAL);
				}
			}
		});

	}

	protected void exit() {
		setVisible(false);
	}

	/**
	 * Calls categorization Screen.
	 */
	protected void callsCategorization() {
		Controller.showCategorizationScreen();
		setVisible(false);
	}

	public boolean isDetectionSuceeded() {
		return detectionSuceeded;
	}

	public void setDetectionSuceeded(boolean detectionSuceeded) {
		this.detectionSuceeded = detectionSuceeded;
	}
}
