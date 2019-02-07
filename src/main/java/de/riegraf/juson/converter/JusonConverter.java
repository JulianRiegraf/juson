package de.riegraf.juson.converter;

import de.riegraf.juson.utils.database.Column;
import de.riegraf.juson.utils.database.Database;
import de.riegraf.juson.utils.database.Record;
import de.riegraf.juson.utils.database.Table;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;


public class JusonConverter implements Converter {

  /*
   *
   * TODO: Currently, the database structure is determined only by the first element in the array. This leads to problems when the objects in the array differ.
   *
   * */

  private static final String DATATYPE = "TEXT";
  private static final String ID_EXTENSION = "_id";
  private String schema;

  List<Table> tables = new LinkedList<>();
  List<Record> records = new LinkedList<>();

  public JusonConverter(String schema) {
    this.schema = schema;
  }

  public Database convert(String outerName, Object json) {

    if (json instanceof JSONObject) {
      handleJsonObject(outerName, (JSONObject) json);
    } else if (json instanceof JSONArray) {
      handleJsonArray(outerName, (JSONArray) json, null, null);
    } else {
      throw new IllegalArgumentException("Argument has to be a JSONObject or an JSONArray.");
    }
    return new Database(tables, records);

  }

  private void handleJsonArray(String name, JSONArray jArray, String parentName, String parentId) {

    if (jArray.length() < 1) {
      return;
    }

    Object child0 = jArray.get(0);

    // Crate database
    if (getTable(name).isEmpty()) {
      Table table = new Table(name.toLowerCase(), schema);
      if (parentName != null) {
        createAssignmentTable(name, parentName);
      }

      // Nodes in array are objects
      // Create columns
      if (child0 instanceof JSONObject) {
        JSONObject jObject = (JSONObject) child0;
        JSONArray arr = jObject.names();
        for (int j = 0; j < arr.length(); j++) {
          Object o = jObject.get(arr.getString(j));
          if (o instanceof JSONObject) {
            table.addColumn(new Column((arr.getString(j) + ID_EXTENSION), DATATYPE));
          } else if (o instanceof JSONArray) {
            // No need to create column for id, because the id is stored in an additional assignment database
          } else {
            table.addColumn(new Column(arr.getString(j), DATATYPE));
          }
        }

        if (parentName != null) {
          table.addColumn(new Column((name + ID_EXTENSION), DATATYPE));
        }

        // Array contains another array
      } else if (child0 instanceof JSONArray) {
        throw new RuntimeException("Nested arrays can not be mapped.");

        // Childs in array are value nodes
      } else {
        table.addColumn(new Column(name, DATATYPE));
        table.addColumn(new Column((name + ID_EXTENSION), DATATYPE));
      }
      tables.add(table);
    }

    // Process array nodes
    for (int i = 0; i < jArray.length(); i++) {

      // Nodes in array are objects
      if (child0 instanceof JSONObject) {
        JSONObject jsonobj = (JSONObject) jArray.get(i);
        if (parentName != null) {
          // Generate an ID for the array elemnt and add the id to the element
          String newId = getId();
          jsonobj.put(name + ID_EXTENSION, newId);

          Table aTOb = getTable(parentName + "_" + name).get();
          Record r = new Record(aTOb);
          addDataToRecords(r, aTOb.getName(), name + ID_EXTENSION, newId);
          addDataToRecords(r, aTOb.getName(), parentName + ID_EXTENSION, parentId);
        }

        handleJsonObject(name, jsonobj);

        // Nodes are values
      } else {
        Record r = new Record(getTable(name).get());
        String newId = getId();
        addDataToRecords(r, name, name, jArray.get(i).toString());

        if (parentName != null) {
          addDataToRecords(r, name, name + ID_EXTENSION, newId);

          Table aTOb = getTable(parentName + "_" + name).get();
          r = new Record(aTOb);
          addDataToRecords(r, aTOb.getName(), name + ID_EXTENSION, newId);
          addDataToRecords(r, aTOb.getName(), parentName + ID_EXTENSION, parentId);
        }

      }
    }
  }

  private void createAssignmentTable(String name, String parent) {
    Table AtoB = new Table(parent + "_" + name, schema);
    AtoB.addColumn(new Column(parent + ID_EXTENSION, DATATYPE));
    AtoB.addColumn(new Column(name + ID_EXTENSION, DATATYPE));
    tables.add(AtoB);
  }

  private void handleJsonObject(String jsonName, JSONObject jObject) {

    // Create database
    if (getTable(jsonName).isEmpty()) {
      JSONArray arr = jObject.names();
      Table table = new Table(jsonName.toLowerCase(), schema);
      for (int j = 0; j < arr.length(); j++) {
        Object o = jObject.get(arr.getString(j));
        if (o instanceof JSONObject) {
          table.addColumn(new Column((arr.getString(j) + ID_EXTENSION), DATATYPE));
        } else if (o instanceof JSONArray) {
          table.addColumn(new Column((jsonName + ID_EXTENSION), DATATYPE));
        } else {
          table.addColumn(new Column(arr.getString(j), DATATYPE));
        }
      }
      tables.add(table);
    }

    // Rpocess child nodes
    Iterator<String> iterator = jObject.keys();
    Record r = new Record(getTable(jsonName).get());
    while (iterator.hasNext()) {
      String currentKey = iterator.next();
      Object obj = jObject.get(currentKey);

      if (obj instanceof JSONArray) {
        // Child node is Array
        // Generating an ID for the parent node and adding it to the record
        // Don't generate new ID when there is a ID already

        String id = jObject.has(jsonName + ID_EXTENSION) ?
            jObject.getString(jsonName + ID_EXTENSION) : getId();
        addDataToRecords(r, jsonName, jsonName + ID_EXTENSION, id);
        handleJsonArray(currentKey, (JSONArray) obj, jsonName, id);

      } else if (obj instanceof JSONObject) {
        JSONObject jsonObject = (JSONObject) obj;
        String id = r.getData(currentKey + ID_EXTENSION).orElseGet(this::getId);
        jsonObject.put(currentKey + ID_EXTENSION, id);
        handleJsonObject(currentKey, jsonObject);
        addDataToRecords(r, jsonName, currentKey + ID_EXTENSION, id);

      } else {
        // Add Data to record
        addDataToRecords(r, jsonName, currentKey, obj.toString());
      }
    }
  }

  private void addDataToRecords(Record record, String tableName, String columnName, String data) {
    Record r = (record == null) ? new Record(getTable(tableName).get()) : record;

    if (r.getTable().getColumn(columnName).isEmpty()) {
      r.getTable().addColumn(columnName, DATATYPE, null, null);
    }

    r.addData(columnName, data);
    if (!records.contains(r)) {
      records.add(r);
    }
  }

  private String getId() {
    return UUID.randomUUID().toString();
  }

  private Optional<Table> getTable(String name) {
    return tables.stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst();
  }


}
