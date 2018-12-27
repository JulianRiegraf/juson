package de.riegraf.juson.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A wrapper for the MySQL connection.
 */
public class MySQL {

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
   * Executes an SQL statement on the de.riegraf.juson.database. Use this if you are not interested in a ResultSet.
   *
   * @param sql the sql statement
   * @return true if the result is a ResultSet object
   */
  public boolean executeSQL(String sql) throws SQLException {
    return connection.createStatement().execute(sql);
  }

  /**
   * Executes a sql statement.
   *
   * @param sql the sql statement
   * @return the response from the de.riegraf.juson.database in a ResultSet
   */
  public ResultSet querySQL(String sql) throws SQLException {
    return connection.createStatement().executeQuery(sql);
  }

  /**
   * Create a prepared statement.
   *
   * @param sql the sql statement that contain one or more '?' placeholder
   * @return the precompiled PreparedStatement
   */
  public PreparedStatement createPreparedStatement(String sql) throws SQLException {
    return connection.prepareStatement(sql);
  }

  /**
   * Closes the de.riegraf.juson.database connection.
   */
  public void close() throws SQLException {
    connection.close();
  }
}
