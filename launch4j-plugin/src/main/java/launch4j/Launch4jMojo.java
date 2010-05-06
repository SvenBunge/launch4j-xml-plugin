package launch4j;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal exe
 * @phase package
 */
public class Launch4jMojo extends AbstractMojo {
	/**
	 * Location of the file.
	 * 
	 * @parameter expression="${project.build.finalName}"
	 * @required
	 * 
	 */
	private String finalName;

	/**
	 * Location of the file.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 * 
	 */
	private File outputDirectory;

	/**
	 * @parameter expression="${exe.configs}"
	 * @required
	 * 
	 */
	private File[] configs;

	/**
	 * @parameter expression="${exe.launch4jHome}"
	 * @required
	 * 
	 */
	private File launch4jHome;

	/**
	 * @parameter expression="${project.runtimeClasspathElements}"
	 * 
	 * 
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
			if (sc != null) {
				sc.close();
			}
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
