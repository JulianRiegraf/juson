package de.riegraf.juson.utils.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a de.riegraf.juson.database database. Does NOT hold any data.
 */
public class Table {

  private String name;
  private String schema;
  private Map<Integer, Column> columns = new HashMap<>();

  public Table(String name, String schema) {
    this.name = name;
    this.schema = schema;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Table) {
      return ((Table) obj).getName().equalsIgnoreCase(this.name) ? true : false;
    }
    return super.equals(obj);
  }

  public Map<Integer, Column> getColumns() {
    return columns;
  }

  public List<Column> getColumnsAsList() {
    return new ArrayList<>(columns.values());
  }

  public String getName() {
    return name;
  }

  public String getSchema() {
    return schema;
  }

  public Optional<Column> getColumn(String name) {
    return columns.entrySet().stream()
        .map(Entry::getValue)
        .filter(c -> c.name.equalsIgnoreCase(name))
        .findFirst();
  }

  public void addColumn(String name, String datatype, Number precision, String defaultValue) {
    addColumn(
        new Column(name, datatype, (precision == null) ? null : precision.longValue(),
            defaultValue));
  }

  public void addColumn(Column newColumn) {
    if (getColumn(newColumn.name).isPresent()) {
      throw new IllegalArgumentException(
          "Column '" + newColumn.name + "' exists already in database '" + this.name + "'");
    }
    columns.put(columns.size(), newColumn);
  }

  public void removeColumn(String name) {
    columns.remove(getColumn(name));
  }

  @Override
  public String toString() {
    return name + " (" + columns.entrySet().stream()
        .map(Entry::getValue)
        .map(c -> c.name)
        .collect(Collectors.joining(", ")) + ")";
  }

  public Column getColumn(int i) {
    return columns.get(i);
  }
}
