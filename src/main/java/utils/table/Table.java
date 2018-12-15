package utils.table;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a database table. Does NOT hold any data.
 */
public class Table {

  private String name;
  private String schema;
  private List<Column> columns = new ArrayList<>();

  public Table(String name, String schema) {
    this.name = name;
    this.schema = schema;
  }

  public boolean equals(Table that) {
    return this.name.equals(that.name);
  }

  public boolean equals(String that) {
    return this.name.equals(that);
  }

  public List<Column> getColumns() {
    return columns;
  }

  public String getName() {
    return name;
  }

  public String getSchema() {
    return schema;
  }

  public Column getColumn(String name) {
    for (Column c : columns) {
      if (c.name.equals(name)) {
        return c;
      }
    }
    return null;
  }

  public void addColumn(String name, String datatype, Number precision, String defaultValue) {
    addColumn(
        new Column(name, datatype, (precision == null) ? null : precision.longValue(),
            defaultValue));
  }

  public void addColumn(Column newColumn) {
    for (Column c : columns) {
      if (c.name.equals(newColumn.name)) {
        throw new IllegalArgumentException(
            "Column '" + newColumn.name + "' exists already in table '" + this.name + "'");
      }
    }
    columns.add(newColumn);
  }

  public void removeColumn(String name) {
    columns.removeIf(x -> x.name.equals(name));
  }


  /**
   * Builds a SQL statement to insert the table data into the database.
   *
   * @return List of statements
   */
  public String getInsertSqlForPrepStatement() {
    return "INSERT INTO " + schema + "." + name + " (" + columns.stream()
        .filter(x -> x.defaultValue == null).map(e -> e.name)
        .collect(Collectors.joining(", "))
        + ") VALUES (" + columns.stream().filter(x -> x.defaultValue == null).map(e -> "?")
        .collect(Collectors.joining(", ")) + ")";
  }


  /**
   * Builds a SQL statement to create the table.
   *
   * @return The statement
   */
  public String getCreateTableQuery() {
    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(schema)
        .append(".").append(name).append(" (");

    if (columns.isEmpty()) {
      return sb.append(")").toString();
    }

    return columns.stream().map(e -> e.toString())
        .collect(Collectors.joining(", ", sb.toString(), ")"));
  }


  @Override
  public String toString() {
    return name + " (" + columns.stream().map(c -> c.name).collect(Collectors.joining(", ")) + ")";
  }
}
