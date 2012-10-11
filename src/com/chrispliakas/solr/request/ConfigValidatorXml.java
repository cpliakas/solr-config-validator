package com.chrispliakas.solr.request;

import java.io.StringReader;
import java.util.HashMap;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

/**
 * Validates that the upload file is valid XML.
 */
public class ConfigValidatorXml implements ConfigValidator {

  @Override
  public boolean validate(String document, HashMap context) {
    try {

      // @see http://stackoverflow.com/a/562207/870667
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      InputSource is = new InputSource(new StringReader(document));
      Document xmlDocument = builder.parse(is);

      // Stores the XML Document in context for use by other validators.
      context.put("xmlDocument", xmlDocument);

    } catch (Exception e) {
      // @todo Log message somewhere?
      return false;
    }

    return true;
  }

}
