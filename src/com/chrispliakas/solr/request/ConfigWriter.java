
package com.chrispliakas.solr.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.document.Field;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

/**
 * Allows users to upload select configuration files to the Solr core.
 *
 * <p>This class allows for the uploading of non-critical configs such as
 * the *.txt files via a request handler. This goal is to be able to modify
 * certain files from the user's application to eliminate the need for a Solr
 * savvy system administrator from having to deploy the file.
 * </p>
 */
public class ConfigWriter extends RequestHandlerBase implements WritableDirectory
{
  protected DocumentWriter documentWriter = null;

  @Override
  public void init(NamedList args) {
    initArgs = args;
    documentWriter = new DocumentWriter(this);
  }

  @Override
  public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {
    // It doesn't make sense for this request handler to cache anything.
    rsp.setHttpCaching(false);

    // Throw server error if we are not in a multicore configuration.
    SolrCore core = req.getCore();
    if (core.getName().isEmpty()) {
      throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, "multicore configuration required");
    }

    // Get the params required for the operation.
    SolrParams params = req.getParams();
    String filename = params.get("filename", "");
    String document = params.get("document", "");

    // Ensure all required params are passed.
    if (filename.isEmpty()) {
      throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "filename required");
    }
    if (document.isEmpty()) {
      throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "document required");
    }

    // Get matching configuration, validate document.
    String match = this.matchFilename(filename);
    if (!this.validateDocument(core, match, document)) {
      throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "document not valid");
    }

    // Write document and reload core for changed to be reflected.
    //documentWriter.write(core, filename, document).reloadCore(core);

    // All good if we got here.
    rsp.add("status", "OK");
  }

  /**
   * Matches the filename against the configuration.
   *
   * If there is no match then the file is not allowed to be modified. If there
   * is a match, return the name attribute of the arr element.
   *
   * @param filename The name of the config file.
   */
  public String matchFilename(String filename) throws SolrException {
    Iterator iterator = this.initArgs.iterator();
    while (iterator.hasNext()) {
      Map.Entry map = (Map.Entry) iterator.next();
      String key = (String) map.getKey();
      if (key.equals(filename)) {
        return key;
      }
    }
    throw new SolrException(SolrException.ErrorCode.FORBIDDEN, "configuration file cannot be modified");
  }

  /**
   * Validates the passed document.
   *
   * @param core The core being acted on.
   * @param match The filename's matching name attribute of the arr element.
   * @param document The contents of the config file being written.
   */
  public boolean validateDocument(SolrCore core, String match, String document) throws SolrException {

    // Initialize the context object so that data can persist across validators.
    HashMap context = new HashMap();

    // Iterate over ConfigValidator classes, validate document.
    SolrResourceLoader loader = core.getResourceLoader();
    ArrayList list = (ArrayList) this.initArgs.get(match);
    for (int i = 0; i < list.size(); i++) {
      String cname = (String) list.get(i);
      ConfigValidator validator = (ConfigValidator) loader.newInstance(cname);
      if (!validator.validate(document, context)) {
        // @todo Log validation error or build response object.
        return false;
      }
    }

    // Returns validation status of document.
    return true;
  }

  //
  // WritableDirectory methods
  //

  @Override
  public String getDirectory(SolrCore core) {
    return core.getResourceLoader().getConfigDir();
  }

  //
  // SolrInfoMBeans methods
  //

  @Override
  public String getVersion() {
    return "0.1-dev";
  }

  @Override
  public String getDescription() {
    return "Request handler that allows users to upload changes to various configuration files.";
  }

  @Override
  public String getSourceId() {
    return "cpliakas";
  }

  @Override
  public String getSource() {
    return "Acquia Engineering SVN repo";
  }
}
