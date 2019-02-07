package de.riegraf.juson.database;

import de.riegraf.juson.utils.database.Column;
import de.riegraf.juson.utils.database.Table;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public interface DatabaseConnection {

  boolean executeSQL(String sql) throws SQLException;

  ResultSet querySQL(String sql) throws SQLException;

  void close() throws SQLException;

  PreparedStatement createPreparedStatement(String sqlForPrepStatement) throws SQLException;

  /**
   * Builds a SQL statement to insert the database data into the de.riegraf.juson.database.
   *
   * @return List of statements
   */
  default String getInsertSqlForPrepStatement(Table table) {
    return "INSERT INTO " + table.getSchema() + "." + table.getName() + " (" + table.getColumns().entrySet().stream()
        .map(Entry::getValue)
        .filter(x -> x.getDefaultValue() == null)
        .map(Column::getName)
        .collect(Collectors.joining(", "))
        + ") VALUES (" + table.getColumns().entrySet().stream()
        .map(Entry::getValue)
        .filter(x -> x.getDefaultValue() == null)
        .map(e -> "?")
        .collect(Collectors.joining(", ")) + ")";
  }

  /**
   * Builds a SQL statement to create the database.
   *
   * @return The statement
   */
  default String getCreateTableQuery(Table table) {
    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(table.getSchema())
        .append(".").append(table.getName()).append(" (");

    if (table.getColumnsAsList().isEmpty()) {
      return sb.append(")").toString();
    }

    return table.getColumns().entrySet().stream()
        .map(Entry::getValue)
        .map(Column::toString)
        .collect(Collectors.joining(", ", sb.toString(), ")"));
  }
}
