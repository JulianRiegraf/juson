package de.riegraf.juson.utils.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Represents a record (row) of a de.riegraf.juson.database table.
 */
public class Record {

  private Map<Integer, String> data;
  private Table table;

  public Record(Table table) {
    this.table = table;
    data = new HashMap<>(table.getColumnsAsList().size());
  }

  public Table getTable() {
    return table;
  }

  public Record addData(int index, String data) {
    this.data.put(index, data);
    return this;
  }

  public Record addData(String columnName, String data) {
    Optional<Column> column = this.table.getColumn(columnName);

    if (column.isEmpty()) {
      throw new NoSuchElementException(
          "There is no column '" + columnName + "' in table '" + table.getName() + "'");
    }
    int index = table.getColumnsAsList().indexOf(column.get());
    this.data.put(index, data);
    return this;
  }

  public Optional<String> getData(String columnName) {
    Optional<Column> column = this.table.getColumnsAsList().stream()
        .filter(x -> x.name.equalsIgnoreCase(columnName)).findFirst();
    if (column.isEmpty()) {
      throw new NoSuchElementException(
          "There is no column '" + columnName + "' in table  '" + table.getName() + "'");
    }
    int index = table.getColumnsAsList().indexOf(column.get());
    return Optional.ofNullable(data.get(index));
  }

  public Optional<String> getData(int index) {
    return Optional.ofNullable(data.get(index));
  }

  public List<String> getData() {
    return new ArrayList<>(data.values());
  }

}
