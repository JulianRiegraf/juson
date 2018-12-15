import exception.JusonException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.table.Column;
import utils.table.Record;
import utils.table.Table;

public class JusonConverter {

  /*
   *
   * TODO: Currently, the table structure is determined only by the first element in the array. This leads to problems when the objects in the array differ.
   *
   * */

  private static final String DATATYPE = "TEXT";
  private static final String ID_EXTENSION = "_id";

  List<Table> tables;
  List<Record> records;

  public JusonConverter(List<Table> tables, List<Record> records) {
    this.tables = tables;
    this.records = records;
  }

  private void handleJsonArray(String name, JSONArray jArray, String parent, String id)
      throws JSONException, JusonException {

    if (jArray.length() < 1) {
      return;
    }
    Object child0 = jArray.get(0);

    // Crate table
    if (getTable(name).isEmpty()) {
      Table table = new Table(name.toLowerCase(), "json");
      Table AtoB = new Table(parent + "_" + name, "json");
      AtoB.addColumn(new Column(parent + ID_EXTENSION, DATATYPE, null, null));
      AtoB.addColumn(new Column(name + ID_EXTENSION, DATATYPE, null, null));

      // Childs in array are value nodes
      if (child0 instanceof String) {
        table.addColumn(new Column(name.toLowerCase(), DATATYPE, null, null));
        table.addColumn(new Column((name + ID_EXTENSION).toLowerCase(), DATATYPE, null, null));

        // Nodes in array are objects
      } else if (child0 instanceof JSONObject) {
        Iterator<String> i = ((JSONObject) child0).keys();
        while (i.hasNext()) {
          table.addColumn(new Column(i.next(), DATATYPE, null, null));
        }
        table.addColumn(new Column((name + ID_EXTENSION).toLowerCase(), DATATYPE, null, null));

        // Array contains another array
      } else if (child0 instanceof JSONArray) {
        throw new JusonException("Nested arrays can not be mapped.");
      }
      tables.add(AtoB);
      tables.add(table);
    }

    for (int i = 0; i < jArray.length(); i++) {

      if (child0 instanceof String) {
        Record r = new Record(getTable(name).get());
        String newId = getId();
        addDataToRecords(r, name, (String) jArray.get(i));
        addDataToRecords(r, name, newId);

        Table aTOb = getTable(parent + "_" + name).get();
        r = new Record(aTOb);
        addDataToRecords(r, aTOb.getName(), id);
        addDataToRecords(r, aTOb.getName(), newId);

        // Nodes in array are objects
      } else if (child0 instanceof JSONObject) {
        String newId = getId();
        JSONObject jsonobj = (JSONObject) jArray.get(i);
        jsonobj.put(name + ID_EXTENSION, newId);
        handleJsonObject(name, jsonobj);

        Table aTOb = getTable(parent + "_" + name).get();
        Record r = new Record(aTOb);
        addDataToRecords(r, aTOb.getName(), id);
        addDataToRecords(r, aTOb.getName(), newId);

      }
    }
  }

  public void handleJsonObject(String name, JSONObject jObject)
      throws JSONException, JusonException {

    // Create table
    if (getTable(name).isEmpty()) {
      JSONArray arr = jObject.names();
      Table table = new Table(name.toLowerCase(), "json");
      for (int j = 0; j < arr.length(); j++) {
        Object o = jObject.get(arr.getString(j));
        if (o instanceof JSONObject | o instanceof JSONArray) {
          table.addColumn(
              new Column((arr.getString(j) + ID_EXTENSION).toLowerCase(), DATATYPE, null, null));
        } else {
          table.addColumn(new Column(arr.getString(j).toLowerCase(), DATATYPE, null, null));
        }
      }
      tables.add(table);
    }

    // Rpocess child nodes
    Iterator<String> iterator = jObject.keys();
    Record r = new Record(getTable(name).get());
    while (iterator.hasNext()) {
      String currentKey = iterator.next();
      Object obj = jObject.get(currentKey);

      if (obj instanceof JSONArray) {
        String id = getId();
        handleJsonArray(currentKey, (JSONArray) obj, name, id);
        addDataToRecords(r, name, id);
      } else if (obj instanceof JSONObject) {
        JSONObject jsonObject = (JSONObject) obj;
        String id = getId();
        jsonObject.put(currentKey + ID_EXTENSION, id);
        handleJsonObject(currentKey, jsonObject);
        addDataToRecords(r, name, id);
      } else {
        // Add Data to record
        addDataToRecords(r, name, obj.toString());
      }
    }
  }

  private void addDataToRecords(Record record, String tableName, String data) {
    Record r = (record == null) ? new Record(getTable(tableName).get()) : record;
    r.addData(data);
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
