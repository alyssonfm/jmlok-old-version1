package categorize;

import java.io.IOException;

import utils.Constants;
import utils.FileUtil;

/**
 * Class that represents a nonconformance.
 * @author Alysson Milanez and Dennis Sousa.
 *
 */
public class Nonconformance {

	private Category type;
	private String cause = "";
	private String test = "";
	private String testFile = "";
	private String className = "";
	private String methodName = "";
	private String packageName = "";
	private String linesFromTestFile = "";
	private String methodCalling = "";
	private String message = "";
	
	/**
	 * Constructor of the class. Since it will be used widely with the sets, we decided to leave
	 * the constructor empty and the sets will do the main work to initialize the fields. 
	 */
	public Nonconformance() {
	}
	
	/**
	 * Get the type of the nonconformance.
	 * @return the type of the nonconformance.
	 */
	public String getType() {
		return type.getType();
	}

	/**
	 * Set the type of the nonconformance.
	 * @param type The type of the nonconformance.
	 */
	public void setType(Category type) {
		this.type = type;
	}

	/**
	 * Get the cause of the nonconformance.
	 * @return the cause of the nonconformance.
	 */
	public String getCause() {
		return cause;
	}

	/**
	 * Set the cause of the nonconformance.
	 * @param cause The cause of the nonconformance.
	 */
	public void setCause(String cause) {
		this.cause = cause;
	}

	/**
	 * Get the test name that generate the nonconformance.
	 * @return the test name that generate the nonconformance.
	 */
	public String getTest() {
		return test;
	}

	/**
	 * Set the test name that generate the nonconformance.
	 * @param test The test name that generate the nonconformance.
	 */
	public void setTest(String test) {
		this.test = test;
	}

	/**
	 * Get the method in which the nonconformance occurred.
	 * @return the method in which the nonconformance occurred.
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Set the method in which the nonconformance occurred.
	 * @param methodName The method in which the nonconformance occurred.
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * Get the class in which the nonconformance occurred.
	 * @return the class in which the nonconformance occurred.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Set the class in which the nonconformance occurred.
	 * @param className The class in which the nonconformance occurred.
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Get the name of the java file in which the nonconformance occurred.
	 * @return the name of the java file in which the nonconformance occurred.
	 */
	public String getTestFile() {
		return testFile;
	}

	/**
	 * Set the name of the java file in which the nonconformance occurred.
	 * @param testFile The name of the java file in which the nonconformance occurred.
	 */
	public void setTestFile(String testFile) {
		this.testFile = testFile;
	}

	/**
	 * Get the package name in which the nonconformance occurred.
	 * @return the package name in which the nonconformance occurred.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Set the package name in which the nonconformance occurred.
	 * @param packageName the package name in which the nonconformance occurred.
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	/**
	 * Get some lines from the test file which generate the nonconformance.
	 * @return some lines from the test file which generate the nonconformance.
	 */
	public String getLinesFromTestFile() {
		return linesFromTestFile;
	}
	
	/**
	 * Set some lines from the test file which generate the nonconformance.
	 * @param specifiedLine The exact line where error was thrown.
	 */
	public void setLinesFromTestFile(int specifiedLine) {
		try {
			this.linesFromTestFile = FileUtil.testCaseContent(this.testFile, specifiedLine);
		} catch (IOException e) {
			this.linesFromTestFile = "Error when reading the test files.";
		}
	}
	
	/**
	 * Get the message displayed on the error log of the nonconformance. 
	 * @return the message displayed on the error log of the nonconformance. 
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Set the message displayed on the error log of the nonconformance. 
	 * @param message The message displayed on the error log of the nonconformance.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Get the method definition which nonconformance was generated, for method overridden issues,
	 * this is necessary. This differentiate invariant, constraint and evaluation errors, where
	 * the line was explicit in error log.
	 * @return the method definition which nonconformance was generated.
	 */
	public String getMethodCalling() {
		return this.methodCalling;
	}
	
	/**
	 * Set the method definition which nonconformance was generated, for method overridden issues,
	 * this is necessary. This differentiate invariant, constraint and evaluation errors, where
	 * the line was explicit in error log.
	 * @param lineOfErrorInJava The line in java file of the class where the method was generated.
	 * @param sourceFolder The folder where the .java of the project are.
	 */
	public void setMethodCalling(int lineOfErrorInJava, String sourceFolder) {
		if(lineOfErrorInJava == -1)
			this.methodCalling = "";
		else if(lineOfErrorInJava == 0)
			this.methodCalling = "defaultConstructor is not explicit";
		else{
			String name = (this.packageName + "." + this.className).replace('.', '/');
			name += ".java";
		    this.methodCalling = FileUtil.readSingleLineOfFile(sourceFolder + Constants.FILE_SEPARATOR + name, lineOfErrorInJava);
		    int temp = this.methodCalling.lastIndexOf("{");
		    if(temp != -1)
		    	this.methodCalling = this.methodCalling.substring(0, this.methodCalling.lastIndexOf("{")).trim();
		}
		if(this.methodCalling.contains(this.getClassName())){
			this.methodCalling = this.methodCalling.replaceFirst(this.className, "<init>");
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if((obj instanceof Nonconformance) 
				&& ((Nonconformance) obj).getType().equals(this.getType()) 
				&& ((Nonconformance) obj).getCause().equalsIgnoreCase(this.getCause())
				&& ((Nonconformance) obj).getTest().equalsIgnoreCase(this.getTest())
				&& ((Nonconformance) obj).getTestFile().equalsIgnoreCase(this.getTestFile())
				&& ((Nonconformance) obj).getClassName().equalsIgnoreCase(this.getClassName())
				&& ((Nonconformance) obj).getMethodName().equalsIgnoreCase(this.getMethodName())
				&& ((Nonconformance) obj).getPackageName().equalsIgnoreCase(this.getPackageName()))
		{ 
			return true;
		} else {
			return false;
	
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cause == null) ? 0 : cause.hashCode());
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + ((test == null) ? 0 : test.hashCode());
		result = prime * result
				+ ((testFile == null) ? 0 : testFile.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
}
