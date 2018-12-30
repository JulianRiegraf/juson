package de.riegraf.juson.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseConnection {

  boolean executeSQL(String sql) throws SQLException;

  ResultSet querySQL(String sql) throws SQLException;

  void close() throws SQLException;

  PreparedStatement createPreparedStatement(String sqlForPrepStatement) throws SQLException;
}
