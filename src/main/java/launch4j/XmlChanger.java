package launch4j;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlChanger {

	private final String outputDirectory;
	private final String finalName;
	private final List<String> classpath;
	private final Log log;

	public XmlChanger(String outputDirectory, String finalName,
			List<String> classpath, Log log) {
		this.outputDirectory = outputDirectory;
		this.finalName = finalName;
		this.log = log;
		this.classpath = getJars(classpath);

	}

	public String changeXml(File config) throws IOException {
		String outputConfigFilename = extractFilename(config.getCanonicalPath());
		outputConfigFilename = outputDirectory + "\\" + outputConfigFilename;
		log.debug("outputConfigFilename=" + outputConfigFilename);
		XPathModifier xpm = new XPathModifier(config);
		xpathJar(xpm);
		xpathExe(xpm);
		xpathClasspath(xpm);
		xpm.save(new File(outputConfigFilename));
		return outputConfigFilename;
	}

	private String extractFilename(String filename) {
		filename = filename.replace('\\', '/');
		String out = StringUtils.substringAfterLast(filename, "/");
		if (out.length() > 0) {
			return out;
		}
		return filename;
	}

	private void xpathJar(XPathModifier xpm) throws IOException {
		Node item = xpm.xpathOne("//launch4jConfig/jar");
		String jarName = outputDirectory + "\\" + finalName + ".jar";
		log.debug("jarName=" + jarName);
		item.setTextContent(jarName);
	}

	private void xpathClasspath(XPathModifier xpm) throws IOException {
		Node item = xpm.xpathOne("//classPath");
		List<String> jars = classpath;
		for (String jar : jars) {
			Element child = xpm.addChild(item);
			child.setTextContent(jar);
		}
	}

	private static List<String> getJars(List<String> classpathList) {
		for (Iterator<String> iterator = classpathList.iterator(); iterator
				.hasNext();) {
			String next = iterator.next();
			if (!next.endsWith("jar")) {
				iterator.remove();
			}
		}
		return classpathList;
	}

	private void xpathExe(XPathModifier xpm) throws IOException {
		Node item = xpm.xpathOne("//launch4jConfig/outfile");
		String exe = item.getTextContent();
		exe = extractFilename(exe);
		item.setTextContent(outputDirectory + "\\" + exe);
	}
}
