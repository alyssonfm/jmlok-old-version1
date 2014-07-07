package gui;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import utils.Constants;

/**
 * Main class to the GUI. Represents the first screen showed to JMLOK user.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 *
 */
public class Main extends JFrame {

	/**
	 * Creates the frame.
	 */
	private static final long serialVersionUID = 9142967374337903926L;
	private JPanel contentPane;
	private JTextField textFieldSrcFolder;
	private JTextField textFieldExtLibFolder;
	private JButton btnBrowseExtLibFolder;
	private JLabel lblTime;
	private JTextField textFieldTime;
	private JButton btnRun;
	private JFileChooser dirSources;
	private JFileChooser dirLibs;
	private JLabel lblSeconds;
	private Task task;
	public boolean done;
	
	/**
	 * Display the frame. Initialize the program.
	 * @param args from command line(non-used).
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * An class used for thread control, to make GUI changes an the execution of the program
	 * fluid, with response for user.
	 * @author Alysson Milanez and Dennis Sousa.
	 *
	 */
    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            //Initialize progress property.
            setProgress(0);
            runningProgram();
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
        	//Tell progress listener to stop updating progress bar.
            done = true;
            btnRun.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            resetProgram();
        }
    }
	
	/**
	 * Create the frame.
	 */
	public Main() {
		setTitle("JMLOK 2.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 506, 180);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		dirLibs = new JFileChooser();
		dirSources = new JFileChooser();
		
		JLabel lblSrcFolder = new JLabel("src Folder");
		lblSrcFolder.setBounds(55, 6, 90, 36);
		contentPane.add(lblSrcFolder);
		
		JLabel lblExternalLibFolder = new JLabel("external lib Folder");
		lblExternalLibFolder.setBounds(27, 38, 144, 46);
		contentPane.add(lblExternalLibFolder);
		
		textFieldSrcFolder = new JTextField();
		textFieldSrcFolder.setBounds(276, 15, 164, 19);
		contentPane.add(textFieldSrcFolder);
		textFieldSrcFolder.setColumns(10);
		
		textFieldExtLibFolder = new JTextField("");
		textFieldExtLibFolder.setBounds(276, 52, 164, 19);
		contentPane.add(textFieldExtLibFolder);
		textFieldExtLibFolder.setColumns(10);
		
		JButton btnBrowseSrcFolder = new JButton("Browse");
		btnBrowseSrcFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseSrcFolder();
				
			}
		});
		btnBrowseSrcFolder.setBounds(177, 15, 87, 19);
		contentPane.add(btnBrowseSrcFolder);
		
		btnBrowseExtLibFolder = new JButton("Browse");
		btnBrowseExtLibFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseExtLibFolder();
			}
		});
		btnBrowseExtLibFolder.setBounds(177, 52, 87, 19);
		contentPane.add(btnBrowseExtLibFolder);
		
		lblTime = new JLabel("Time");
		lblTime.setBounds(37, 85, 70, 15);
		contentPane.add(lblTime);
		
		textFieldTime = new JTextField("");
		textFieldTime.setBounds(95, 83, 102, 19);
		contentPane.add(textFieldTime);
		textFieldTime.setColumns(10);
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(textFieldSrcFolder.getText().equals(""))
					JOptionPane.showMessageDialog(Main.this, "Choose the source folder before running.");
				else{	
					btnRun.setEnabled(false);
			        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			        //Instances of javax.swing.SwingWorker are not reusuable, so
			        //we create new instances as needed.
			        task = new Task();
			        task.execute();
				}
			}
		});
		btnRun.setBounds(276, 82, 61, 25);
		contentPane.add(btnRun);
		
		lblSeconds = new JLabel("seconds");
		lblSeconds.setBounds(215, 83, 70, 15);
		contentPane.add(lblSeconds);
	}

	/**
	 * Execute the program, after some variables are correctly initialized.
	 */
	protected void runningProgram() {
		// showInterestingMessage();
		String extLibFolder = textFieldExtLibFolder.getText();
		String time = textFieldTime.getText();
		if(extLibFolder.equals("")) {
			if(System.getProperty("os.name").contains("Windows"))
				extLibFolder = Constants.JMLC_LIB;
			else
				extLibFolder = System.getenv("USER_CLASSPATH_LIB");
		}if(time.equals("")){
			time = "10";
		}
		ThreadExecutingProgram t = new ThreadExecutingProgram(this, textFieldSrcFolder.getText(), extLibFolder, time);
		t.run();
	}

	/**
	 * Resets the main screen, leaving all fields blanked.
	 */
	protected void resetProgram() {
		textFieldSrcFolder.setText("");
		textFieldExtLibFolder.setText("");
		textFieldTime.setText("");
	}

	/**
	 * Make user sets the library folder.
	 */
	protected void browseExtLibFolder() {
		dirLibs.setApproveButtonText("Select");
		dirLibs.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (dirLibs.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
			textFieldExtLibFolder.setText(dirLibs.getSelectedFile().getAbsolutePath());
		}
	}

	/**
	 * Make user sets the source folder.
	 */
	protected void browseSrcFolder() {
		dirSources.setApproveButtonText("Select");
		dirSources.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (dirSources.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
			textFieldSrcFolder.setText(dirSources.getSelectedFile().getAbsolutePath());
		}
	}
}
