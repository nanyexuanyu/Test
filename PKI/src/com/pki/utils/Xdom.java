package com.pki.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Xdom {
	public static Map<String, String> getDomNodeAttribute(String filePath) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Map<String, String> map = new HashMap<String, String>();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(filePath);
			NodeList keyList = doc.getElementsByTagName("key");
			for (int i = 0; i < keyList.getLength(); i++) {
				Node key = keyList.item(i);
				Element elem = (Element) key;
				if (elem.getAttribute("value") == null || elem.getAttribute("value").equals("")) {
					continue;
				}
				map.put(elem.getAttribute("id"), elem.getAttribute("value"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("com.pki.utils.Xdom getDomNodeByTagName异常" + e);
		}
		return map;
	}

	public static void setDomNodeAttribute(String filePath, Map<String, String> map) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(filePath);
			NodeList keyList = doc.getElementsByTagName("key");
			for (int i = 0; i < keyList.getLength(); i++) {
				Node key = keyList.item(i);
				Element elem = (Element) key;
				elem.setAttribute("value", map.get(elem.getAttribute("id")));
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.transform(new DOMSource(doc), new StreamResult(new File(filePath)));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("com.pki.utils.Xdom getDomNodeByTagName异常" + e);
		}
	}
}
