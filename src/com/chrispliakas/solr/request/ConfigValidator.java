package com.chrispliakas.solr.request;

import java.util.HashMap;

/**
 * Implemented by classes that validate files being written.
 */
public interface ConfigValidator {

  /**
   * Validates the passed document.
   *
   * @param document The contents of the file being written.
   * @param context A hash map of data that is to persists across validators.
   */
  boolean validate(String document, HashMap context);

}
