package utils.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * The class extends the java.util.Properties class to handle lists in properties.
 */
public class DdhProperties extends Properties {

  public static final int PACKAGE_SIZE = 100_000;
  private String delimiter = ",";

  public DdhProperties() {
    super();
  }

  /**
   * Reads a property list (key and element pairs) from the input file and loads it into the
   * Properties object via java.util.Properties.load().<br> The file has to be in the resources
   * folder.
   *
   * @param fileName the filename of the properties file
   * @return the filled DdhProperties object; null if the file is not found
   */
  public static DdhProperties fromFile(String fileName) {
    DdhProperties properties = null;
    String appConfigPath = Thread.currentThread().getContextClassLoader().getResource(fileName)
        .getPath();
    try {
      properties = new DdhProperties();
      properties.load(new FileInputStream(appConfigPath));
    } catch (IOException e) {
      System.err.println("File '" + fileName + "' not found in " + appConfigPath);
      return null;
    }
    return properties;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  /**
   * Searches for the property with the given key. The value is split at the separator. Every
   * substring is trimmed.<br>
   * <i>"property=value1, value2, value3"</i> will return <i>List["value1", "value2", "value3"]</i>
   *
   * @param key the property key
   * @return a list of the values or null if the key is not found
   */
  public List<String> getList(String key) {
    String s = this.getProperty(key);
    if (s == null) {
      return null;
    }
    if (s.trim().isEmpty()) {
      return Collections.emptyList();
    }
    List<String> values = new ArrayList<>();
    for (String str : s.split(delimiter)) {
      values.add(str.trim());
    }
    return values;
  }
}
