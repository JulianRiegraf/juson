package de.riegraf.juson.database;

import de.riegraf.juson.utils.table.Table;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseConnection {

  boolean executeSQL(String sql) throws SQLException;

  ResultSet querySQL(String sql) throws SQLException;

  void close() throws SQLException;

  String getInsertSqlForPrepStatement(Table table);

  String getCreateTableQuery(Table table);

  PreparedStatement createPreparedStatement(String sqlForPrepStatement) throws SQLException;
}
