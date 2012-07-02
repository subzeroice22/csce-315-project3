package team2.reversi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


import android.renderscript.Element;

public class Write {

	public void writeFile() {
		
		try {
		
			File highscoreFile = new File("/sdcard/highscores.xml");
		
			if(highscoreFile.createNewFile()) {


				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = (Element) doc.createElement("HIGHSCORES");
				doc.appendChild((Node) rootElement);
				
				// entry elements
				Element entry = (Element) doc.createElement("Entry");
				((Node) rootElement).appendChild((Node) entry);
				
				// set attribute to entry element
				Attr attr = doc.createAttribute("difficulty");
				attr.setValue(GameFacadeImpl.difficultyLevel);
				((org.w3c.dom.Element) entry).setAttributeNode(attr);
				
				//short method
				//entry.setAttribute("difficulty",GameFacadeImpl.difficultyLevel); 
				
				// winning difference
				Element differ = (Element) doc.createElement("differential");
				String winDif = Integer.toString(GameFacadeImpl.winningDifferential);				
				((Node) differ).appendChild(doc.createTextNode(winDif));
				
				// game time
				Element runTime = (Element) doc.createElement("gameTime");
				String time = Long.toString(GameFacadeImpl.gameTime);
				((Node) runTime).appendChild(doc.createTextNode(time));
				
				// date
				Element date = (Element) doc.createElement("date");
				Date currentDate = new Date();				
				String sDate = currentDate.toString();
				((Node) runTime).appendChild(doc.createTextNode(sDate));
			
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File("/sdcard/highscores.xml"));
				
				transformer.transform(source, result);
				
				System.out.println("File saved!");
				
			}
			
			
			
			
		} catch (Exception e) {
			//...
		}
		
	}
	
}
