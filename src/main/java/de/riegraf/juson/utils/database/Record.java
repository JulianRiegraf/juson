package de.riegraf.juson.utils.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Represents a record (row) of a de.riegraf.juson.database database.
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
    Optional<Column> opetionalColumn = this.table.getColumn(columnName);

    Column column = opetionalColumn.orElseThrow(() -> new NoSuchElementException(
        "There is no column '" + columnName + "' in database '" + table.getName() + "'"));

    int index = table.getColumnsAsList().indexOf(column);
    this.data.put(index, data);
    return this;
  }

  public Optional<String> getData(String columnName) {
    Optional<Column> opetionalColumn = this.table.getColumnsAsList().stream()
        .filter(x -> x.getName().equalsIgnoreCase(columnName)).findFirst();

    Column column = opetionalColumn.orElseThrow(() -> new NoSuchElementException(
        "There is no column '" + columnName + "' in database '" + table.getName() + "'"));

    int index = table.getColumnsAsList().indexOf(column);
    return Optional.ofNullable(data.get(index));
  }

  public Optional<String> getData(int index) {
    return Optional.ofNullable(data.get(index));
  }

  public List<String> getData() {
    return new ArrayList<>(data.values());
  }

}
