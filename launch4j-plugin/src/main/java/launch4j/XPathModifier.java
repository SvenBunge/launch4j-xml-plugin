package launch4j;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathModifier {

	private Document doc;

	public XPathModifier(File file) throws IOException {
		try {
			DocumentBuilder newDocumentBuilder = DocumentBuilderFactory
					.newInstance().newDocumentBuilder();
			doc = newDocumentBuilder.parse(file);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public NodeList xpath(String expression) throws IOException {
		try {
			NodeList list = (NodeList) XPathFactory.newInstance().newXPath()
					.evaluate(expression, doc, XPathConstants.NODESET);
			if (list.getLength() == 0) {
				throw new IOException(expression + " not found");
			}
			return list;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public Node xpathOne(String expression) throws IOException {
		NodeList list = xpath(expression);
		if (list.getLength() == 0) {
			throw new IOException(expression + " not found");
		}
		return list.item(0);
	}

	public void save(File file) throws IOException {
		try {
			Transformer trans = TransformerFactory.newInstance()
					.newTransformer();
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			Source src = new DOMSource(doc);
			Result dest = new StreamResult(file);
			trans.transform(src, dest);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public Element addChild(Node item) {
		Element child = doc.createElement("cp");
		item.appendChild(child);
		return child;
	}
}
