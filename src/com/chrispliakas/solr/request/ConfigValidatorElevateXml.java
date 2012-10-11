package com.chrispliakas.solr.request;

import java.util.HashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.solr.common.SolrException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathExpression;

/**
 * Validates that the markup is suitable for an elevate.xml file.
 */
public class ConfigValidatorElevateXml implements ConfigValidator {

  protected XPathFactory xpathFactory = XPathFactory.newInstance();

  /**
   * Return a new XPath object from the class XPath factory.
   */
  public XPath getXPath() {
    return xpathFactory.newXPath();
  }

  @Override
  public boolean validate(String document, HashMap context) {
    try {

      // Get XML document from context.
      Document xml = (Document) context.get("xmlDocument");
      if (null == xml) {
        throw new Exception("xmlDocument context required.");
      }

      // Instantiates XPath factory.
      String query = "/elevate/query";
      Node node = (Node) this.getXPath().evaluate(query, xml, XPathConstants.NODE);

      boolean balls = true;
    } catch (Exception e) {
      // @todo Log message somewhere?
      return false;
    }

    return true;
  }
}
