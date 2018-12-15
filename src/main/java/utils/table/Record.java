package utils.table;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a record (row) of a database table.
 */
public class Record {

  private List<String> data;
  private Table table;

  public Record(Table table) {
    this.table = table;
    data = new ArrayList<>(table.getColumns().size());
  }

  public Table getTable() {
    return table;
  }

  public int size() {
    return data.size();
  }

  public void addData(String data) {
    this.data.add(data);
  }

  public void addData(List<String> data) {
    data.forEach(x -> this.data.add(x));
  }

  public List<String> getData() {
    return data;
  }

  public void setData(List<String> data) {
    this.data = data;
  }
}
