package com.chrispliakas.solr.request;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.solr.common.SolrException;
import org.apache.solr.core.SolrCore;

/**
 * Writes a file to a directory flagged by a request handler as writable.
 */
public class DocumentWriter {

  protected WritableDirectory handler = null;

  /**
   * Sets request handler that writes files to a directory.
   *
   * @param reqHandler Handler that writes files to a directory.
   */
  public DocumentWriter(WritableDirectory reqHandler) {
    handler = reqHandler;
  }

  /**
   * Writes a file to the directory flagged by the request handler as writable.
   *
   * @param core The core being acted on.
   * @param filename The name of the config file.
   * @param document The contents of the file being written.
   */
  public DocumentWriter write(SolrCore core, String filename, String document) throws Exception {
    // Get the path to the file being written.
    String filepath = handler.getDirectory(core) + filename;

    // Write the contents of the document to the file.
    FileWriter fstream = new FileWriter(filepath);
    BufferedWriter out = new BufferedWriter(fstream);
    out.write(document);
    out.close();

    // Return this class for chaining.
    return this;
  }

  /**
   * Reload the core, usually so that file changes are reflected immediately.
   *
   * @param core The Solr core being reloaded.
   *
   * @see http://lucene.472066.n3.nabble.com/Get-access-to-CoreContainer-td501891.html
   */
  public void reloadCore(SolrCore core) throws Exception {
    core.getCoreDescriptor().getCoreContainer().reload(core.getName());
  }
}
