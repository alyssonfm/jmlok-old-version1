
package detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import utils.Constants;
import utils.FileUtil;

/**
 * Class used to detect nonconformances in Java/JML programs.
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 */
public class Detect {

	private boolean isLinux = false;
	private boolean isJMLC = false;
	private boolean isOpenJML = false;
	private String jmlLib;
	private File jmlokDir = new File(Constants.TEMP_DIR);
	private File javaBin = new File(Constants.SOURCE_BIN);
	private File jmlBin = new File(Constants.JML_BIN);
	private File testSource = new File(Constants.TEST_DIR);
	private File testBin = new File(Constants.TEST_BIN);
	
	/**
	 * The constructor of this class, creates a new instance of Detect class, creates the jmlok directory and set the JML compiler used.
	 * @param comp = the integer that indicates which JML compiler will be used.
	 */
	public Detect(int comp) {
		while (!jmlokDir.exists()) {
			jmlokDir.mkdirs();
		}
		switch (comp) {
		case Constants.JMLC_COMPILER:
			isJMLC = true;
			jmlLib = Constants.JMLC_LIB;
			break;
		case Constants.OPENJML_COMPILER:
			isOpenJML = true;
			jmlLib = Constants.OPENJML_SRC;
			break;
		default:
			break;
		}
		isLinux = System.getProperty("os.name").equals("Linux");
	}
	
	/**
	 * Method used to detect the nonconformances.
	 * @param source = the path to classes directory.
	 * @param lib = the path to external libraries directory.
	 * @param timeout = the time to tests generation.
	 * @return - The list of nonconformances detected.
	 * @throws Exception When some XML cannot be read.
	 */
	public Set<TestError> detect(String source, String lib, String timeout) throws Exception {
		execute(source, lib, timeout);
		ResultProducer r = new ResultProducer();
		if(isJMLC) return r.listErrors(Constants.JMLC_COMPILER);
		else return r.listErrors(Constants.OPENJML_COMPILER);
	}
	
	/**
	 * Method that executes the scripts to conformance checking.
	 * @param sourceFolder = the path to source of files to be tested.
	 * @param libFolder = the path to external libraries needed for the current SUT.
	 * @param timeout = the time to tests generation.
	 * @throws Exception When some XML cannot be read.
	 */
	public void execute(String sourceFolder, String libFolder, String timeout) throws Exception {
		getClassListFile(sourceFolder);

		System.out.println("Creating directories...");
		
		createDirectories();
		cleanDirectories();
		System.out.println("Compiling the project...");
		javaCompile(sourceFolder, libFolder);
		System.out.println("Compiling with JML compiler...");
		jmlCompile(sourceFolder);
		System.out.println("Generating tests...");
		generateTests(libFolder, timeout);
		System.out.println("Running JUnit to test the JML code...");
		runTests(libFolder);
	}

	/**
	 * Method used to list all classes present into the directory received as parameter.
	 * @param sourceFolder = the directory source of the files.
	 * @return - the file containing all classes.
	 */
	private File getClassListFile(String sourceFolder) {
		List<String> listClassNames = FileUtil.listNames(sourceFolder, "", ".java");
		StringBuffer lines = new StringBuffer();
		for (String className : listClassNames) {
			className = className + "\n";
			lines.append(className);
		}
		return FileUtil.makeFile(Constants.CLASSES, lines.toString());
	}
	
	/**
	 * Method used to creates all directories to be used by the tool.
	 */
	private void createDirectories(){
		while (!javaBin.exists()) {
			javaBin.mkdirs();
		}
		while (!jmlBin.exists()) {
			jmlBin.mkdirs();
		}
		while (!testSource.exists()) {
			testSource.mkdirs();
		}
		while (!testBin.exists()) {
			testBin.mkdirs();
		}
	}
	
	/**
	 * Method used to clean all directories - for the case of several executions of the tool.
	 */
	private void cleanDirectories(){
		try {
			FileUtils.cleanDirectory(javaBin);
			FileUtils.cleanDirectory(jmlBin);
			FileUtils.cleanDirectory(testSource);
			FileUtils.cleanDirectory(testBin);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to Java compilation of the files (needed for tests generation).
	 * @param sourceFolder = the path to source files.
	 * @param libFolder = the path to external libraries needed to Java compilation.
	 * @throws Exception When the XML cannot be read.
	 */
	public void javaCompile(String sourceFolder, String libFolder) throws Exception{
		jmlLib = jmlLib + libFolder;
		File buildFile = null;
		try {
			buildFile = new File("ant" + Constants.FILE_SEPARATOR + "javaCompile.xml");
		} catch (Exception e) {
			throw new Exception("Erro ao ler o "
					+ "ant" + Constants.FILE_SEPARATOR + "javaCompile.xml");
		}
		Project p = new Project();
		p.setUserProperty("source_folder", sourceFolder);
		p.setUserProperty("source_bin", Constants.SOURCE_BIN);
		p.setUserProperty("lib", libFolder);
		p.setUserProperty("jmlLib", jmlLib);
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget("compile_project");
	}
	
	/**
	 * Method used to generate the tests to conformance checking.
	 * @param libFolder = the path to external libraries needed to tests generation and compilation.
	 * @param timeout = the time to tests generation.
	 * @throws Exception When the XML cannot be read.
	 */
	public void generateTests(String libFolder, String timeout) throws Exception{
		jmlLib = jmlLib + libFolder;
		File buildFile = null;
		Runtime rt = Runtime.getRuntime();
		
		String pathToRandoop;
		pathToRandoop = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();			
		//pathToRandoop = "D:\\Dropbox\\workspace\\JMLOK-Exe\\lib\\randoop.jar";
		
		Process proc = rt.exec(FileUtil.getCommandToUseRandoop(timeout, pathToRandoop, FileUtil.getListPathPrinted(libFolder)));
		final InputStreamReader ou = new InputStreamReader(proc.getInputStream());
		final InputStreamReader er = new InputStreamReader(proc.getErrorStream());
		final BufferedReader bo = new BufferedReader(ou); 
		final BufferedReader be = new BufferedReader(er);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String line = null;
				try {
					while ((line = bo.readLine()) != null) {
						System.out.println(line);
					}
					ou.close();
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String line = null;
				try {
					while ((line = be.readLine()) != null) {
						System.out.println(line);
					}
					er.close();
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
		}).start();


		int exitVal = proc.waitFor();
		if(exitVal != 0) 
			throw new Exception("Error reading: " + pathToRandoop);
		
		
		try {
			buildFile = new File("ant" + Constants.FILE_SEPARATOR + "generateTests.xml");
		} catch (Exception e) {
			throw new Exception("Erro ao ler o "
					+ "ant" + Constants.FILE_SEPARATOR + "generateTests.xml");
		}
		Project p = new Project();
		p.setUserProperty("classes", Constants.CLASSES);
		p.setUserProperty("source_bin", Constants.SOURCE_BIN);
		p.setUserProperty("tests_src", Constants.TEST_DIR);
		p.setUserProperty("tests_bin", Constants.TEST_BIN);
		p.setUserProperty("tests_folder", Constants.TESTS);
		p.setUserProperty("lib", libFolder);
		p.setUserProperty("jmlLib", jmlLib);
		p.setUserProperty("timeout", timeout);
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget("compile_tests");
	}
	
	/**
	 * Method used to do the JML compilation of the files.
	 * @param sourceFolder = the source of files to be compiled.
	 * @throws Exception When the XML cannot be read.
	 */
	public void jmlCompile(String sourceFolder) throws Exception{
		File buildFile = null;
		if(FileUtil.hasDirectories(sourceFolder)){
			if(isJMLC){
				try {
					buildFile = new File("ant" + Constants.FILE_SEPARATOR + "jmlcCompiler.xml");					
				} catch (Exception e) {
					throw new Exception("Erro ao ler o "
							+ "ant" + Constants.FILE_SEPARATOR + "jmlcCompiler.xml");
				}
				Project p = new Project();
				p.setUserProperty("source_folder", sourceFolder);
				p.setUserProperty("jmlBin", Constants.JML_BIN);
				p.setUserProperty("jmlcExec", (isLinux)?(Constants.JMLC_SRC + "jmlc-unix"):(Constants.JMLC_SRC+"jmlc.bat"));
				p.init();
				ProjectHelper helper = ProjectHelper.getProjectHelper();
				p.addReference("ant.projectHelper", helper);
				helper.parse(p, buildFile);
				p.executeTarget("jmlc");
			} else if(isOpenJML){
				try {
					buildFile = new File("ant" + Constants.FILE_SEPARATOR + "openjmlCompiler.xml");
				} catch (Exception e) {
					throw new Exception("Erro ao ler o "
							+ "ant" + Constants.FILE_SEPARATOR + "openjmlCompiler.xml");
				}
				Project p = new Project();
				p.setUserProperty("source_folder", sourceFolder);
				p.setUserProperty("jmlBin", Constants.JML_BIN);
				p.init();
				ProjectHelper helper = ProjectHelper.getProjectHelper();
				p.addReference("ant.projectHelper", helper);
				helper.parse(p, buildFile);
				p.executeTarget("openJML");
			}
		} else {
			if(isJMLC){
				try {
					buildFile = new File("ant" + Constants.FILE_SEPARATOR + "jmlcCompiler2.xml");					
				} catch (Exception e) {
					throw new Exception("Erro ao ler o "
							+ "ant" + Constants.FILE_SEPARATOR + "jmlcCompiler2.xml");
				}
				Project p = new Project();
				p.setUserProperty("source_folder", sourceFolder);
				p.setUserProperty("jmlBin", Constants.JML_BIN);
				p.setUserProperty("jmlcExec", (isLinux)?(Constants.JMLC_SRC + "jmlc-unix"):("jmlc.bat"));
				p.init();
				ProjectHelper helper = ProjectHelper.getProjectHelper();
				p.addReference("ant.projectHelper", helper);
				helper.parse(p, buildFile);
				p.executeTarget("jmlc");
			} else if(isOpenJML){
				try {
					buildFile = new File("ant" + Constants.FILE_SEPARATOR + "openjmlCompiler2.xml");
				} catch (Exception e) {
					throw new Exception("Erro ao ler o "
							+ "ant" + Constants.FILE_SEPARATOR + "openjmlCompiler2.xml");
				}
				Project p = new Project();
				p.setUserProperty("source_folder", sourceFolder);
				p.setUserProperty("jmlBin", Constants.JML_BIN);
				p.init();
				ProjectHelper helper = ProjectHelper.getProjectHelper();
				p.addReference("ant.projectHelper", helper);
				helper.parse(p, buildFile);
				p.executeTarget("openJML");
			}
		}
	}
	
	/**
	 * Method used to run the tests with the JML oracles.
	 * @param libFolder = the path to external libraries needed to tests execution.
	 * @throws Exception When the XML cannot be read.
	 */
	private void runTests(String libFolder) throws Exception{
		File buildFile = null;
		try {
			buildFile = new File("ant" + Constants.FILE_SEPARATOR + "runTests.xml");			
		} catch (Exception e) {
			throw new Exception("Erro ao ler o "
					+ "ant" + Constants.FILE_SEPARATOR + "runTests.xml");
		}
		Project p = new Project();
		p.setUserProperty("lib", libFolder);
		p.setUserProperty("jmlBin", Constants.JML_BIN);
		if(isJMLC) p.setUserProperty("jmlCompiler", Constants.JMLC_SRC);
		else if(isOpenJML) p.setUserProperty("jmlCompiler", Constants.OPENJML_SRC);
		p.setUserProperty("tests_src", Constants.TEST_DIR);
		p.setUserProperty("tests_bin", Constants.TEST_BIN);
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget("run_tests");
	}
}


// D:\Dropbox\Exemplos\CarJustPrecondition