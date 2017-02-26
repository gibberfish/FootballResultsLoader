package mindbadger.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@Component
public class XMLFileWriter {
	Logger logger = Logger.getLogger(XMLFileWriter.class);
	
	public void writeXMLFile (String fullyQualifiedFileName, Document doc) throws TransformerException, FileNotFoundException {
      //set up a transformer
      TransformerFactory transfac = TransformerFactory.newInstance();
      Transformer trans = transfac.newTransformer();
      trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      trans.setOutputProperty(OutputKeys.INDENT, "yes");

      //create string from xml tree
      StringWriter sw = new StringWriter();
      StreamResult result = new StreamResult(sw);
      DOMSource source = new DOMSource(doc);
      trans.transform(source, result);
      String xmlString = sw.toString();

      //print xml
      // logger.debug("Here's the xml:\n\n" + xmlString);
     
      PrintStream out = null;
      try {
          out = new PrintStream(new FileOutputStream(fullyQualifiedFileName));
          out.print(xmlString);
      }
      finally {
          if (out != null) out.close();
      }
	}
}
