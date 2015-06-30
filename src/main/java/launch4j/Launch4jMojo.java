package launch4j;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Goal which touches a timestamp file.
 *
 * @goal launch4j
 * @phase package
 */
public class Launch4jMojo extends AbstractMojo {
	/**
	 * Location of the file.
	 *
	 * @parameter property="project.build.finalName"
	 * @required
	 */
	private String finalName;

	/**
	 * Location of the file.
	 *
	 * @parameter property="project.build.directory"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * @parameter property="launch4j.configs"
	 * @required
	 */
	private File[] configs;

	/**
	 * @parameter property="launch4j.launch4jHome"
	 * @required
	 */
	private File launch4jHome;

	/**
	 * @parameter property="project.runtimeClasspathElements"
	 */
	private List<String> classpath;

	public void execute() throws MojoExecutionException, MojoFailureException {
		checkExistance("<configuration><configs><param>", configs);
		checkExistance("<configuration><launch4jHome>", launch4jHome);

		Log log = getLog();
		if (log.isDebugEnabled()) {
			log.debug("this = "
					+ ToStringBuilder.reflectionToString(this,
					ToStringStyle.MULTI_LINE_STYLE));
		}

		try {
			XmlChanger changer = new XmlChanger(outputDirectory
					.getCanonicalPath(), finalName, classpath, getLog());
			for (File config : configs) {
				String outputConfigFilename = changer.changeXml(config);
				run(outputConfigFilename);
			}
		} catch (IOException e) {
			throw new MojoExecutionException("", e);
		}
	}

	private void run(String outputConfigName) throws IOException {
		Process start = new ProcessBuilder(launch4jHome.getCanonicalPath(),
				outputConfigName).start();
		InputStream errorStream = start.getInputStream();
		Scanner sc = new Scanner(errorStream);
		try {
			while (sc.hasNextLine()) {
				getLog().info(sc.nextLine());
			}
		} finally {
			sc.close();
		}
	}

	private void checkExistance(String name, File... files)
			throws MojoFailureException {
		if (files.length == 0) {
			throw new MojoFailureException(name + " not found");
		}
		for (File file : files) {
			if (file == null) {
				throw new MojoFailureException(name + " not found");
			}
			boolean exists = file.exists();
			if (!exists) {
				throw new MojoFailureException(file
						+ " doesnt exists for param = " + name);
			}
		}
	}
}
