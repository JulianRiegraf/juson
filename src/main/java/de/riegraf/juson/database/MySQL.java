package de.riegraf.juson.database;

import de.riegraf.juson.utils.table.Table;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * A wrapper for the MySQL connection.
 */
public class MySQL implements DatabaseConnection {

  private Connection connection;

  public MySQL(String url, String user, String password, List<String> parameters)
      throws SQLException {
    List<String> params = parameters != null ? parameters : Collections.emptyList();
    connection = DriverManager.getConnection(
        "jdbc:mysql://" + url + "?" + params.stream().collect(Collectors.joining("&")), user,
        password);
  }

  public MySQL() {
  }

  /**
   * Executes an SQL statement on the de.riegraf.juson.database. Use this if you are not interested
   * in a ResultSet.
   *
   * @param sql the sql statement
   * @return true if the result is a ResultSet object
   */
  @Override
  public boolean executeSQL(String sql) throws SQLException {
    return connection.createStatement().execute(sql);
  }

  /**
   * Executes a sql statement.
   *
   * @param sql the sql statement
   * @return the response from the de.riegraf.juson.database in a ResultSet
   */
  @Override
  public ResultSet querySQL(String sql) throws SQLException {
    return connection.createStatement().executeQuery(sql);
  }

  /**
   * Create a prepared statement.
   *
   * @param sql the sql statement that contain one or more '?' placeholder
   * @return the precompiled PreparedStatement
   */
  @Override
  public PreparedStatement createPreparedStatement(String sql) throws SQLException {
    return connection.prepareStatement(sql);
  }

  /**
   * Closes the de.riegraf.juson.database connection.
   */
  @Override
  public void close() throws SQLException {
    connection.close();
  }

  /**
   * Builds a SQL statement to insert the table data into the de.riegraf.juson.database.
   *
   * @return List of statements
   */
  @Override
  public String getInsertSqlForPrepStatement(Table table) {
    return "INSERT INTO " + table.getSchema() + "." + table.getName() + " (" + table.getColumns().entrySet().stream()
        .map(Entry::getValue)
        .filter(x -> x.defaultValue == null)
        .map(e -> e.name)
        .collect(Collectors.joining(", "))
        + ") VALUES (" + table.getColumns().entrySet().stream()
        .map(Entry::getValue)
        .filter(x -> x.defaultValue == null)
        .map(e -> "?")
        .collect(Collectors.joining(", ")) + ")";
  }


  /**
   * Builds a SQL statement to create the table.
   *
   * @return The statement
   */
  @Override
  public String getCreateTableQuery(Table table) {
    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(table.getSchema())
        .append(".").append(table.getName()).append(" (");

    if (table.getColumnsAsList().isEmpty()) {
      return sb.append(")").toString();
    }

    return table.getColumns().entrySet().stream()
        .map(Entry::getValue)
        .map(e -> e.toString())
        .collect(Collectors.joining(", ", sb.toString(), ")"));
  }
}
