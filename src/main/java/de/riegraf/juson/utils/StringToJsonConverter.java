package de.riegraf.juson.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class StringToJsonConverter {

  private StringToJsonConverter() {
    throw new IllegalStateException("Utility class");
  }

  public static Object toJsonObject(String json) {
    if (json.startsWith("{")) {
      return new JSONObject(json);
    } else if (json.startsWith("[")) {
      return new JSONArray(json);
    } else {
      throw new IllegalArgumentException("Argument json contains not a valid json.");
    }
  }
}
