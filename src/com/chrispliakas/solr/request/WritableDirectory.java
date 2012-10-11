package com.chrispliakas.solr.request;

import org.apache.solr.core.SolrCore;

/**
 * Implemented by request handlers that write files to a directory.
 */
public interface WritableDirectory {

  /**
   * The directory being written to with a trailing "/".
   *
   * @param core The core being acted on.
   */
  public String getDirectory(SolrCore core);
}
