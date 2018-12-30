package de.riegraf.juson.converter;

import de.riegraf.juson.database.DatabaseConnection;
import de.riegraf.juson.exception.JusonException;
import de.riegraf.juson.utils.table.Record;
import de.riegraf.juson.utils.table.Table;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class JusonDatabaseWriter {

  public JusonDatabaseWriter(String rootName, String json, DatabaseConnection dbConnection, String schema) throws JusonException {

    JusonConverter jusonConverter = new JusonConverter(schema);
    JusonConverter.Database db = jusonConverter.convert(rootName, json);

    List<Table> tables = db.getTables();
    List<Record> records = db.getRecords();

    tables.forEach(x -> {
      try {
        dbConnection.executeSQL(x.getCreateTableQuery());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });

    tables.forEach(table -> {
      try {
        PreparedStatement p = dbConnection
            .createPreparedStatement(table.getInsertSqlForPrepStatement());

        List<Record> recordOfTable = records.stream()
            .filter(r -> r.getTable().equals(table))
            .collect(Collectors.toList());

        insertRecords(recordOfTable, p);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
  }

  private static void insertRecords(List<Record> records, PreparedStatement p) {
    try {
      for (Record r : records) {
        for (int i = 0; i < r.getTable().getColumns().size(); i++) {
          p.setString(i + 1, r.getData(i).orElse(null));
        }
        p.addBatch();
      }
      p.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void printTablesAndRecords(List<Table> tables, List<Record> records) {
    System.out.println(
        "--- Tables ---\n" + tables.stream()
            .map(Table::getCreateTableQuery)
            .collect(Collectors.joining("\n"))
    );

    Map<Table, List<Record>> collect = records.stream()
        .collect(Collectors.groupingBy(Record::getTable));

    collect.entrySet().stream().map(JusonDatabaseWriter::recordsToString).forEach(System.out::println);
  }


  public static String recordsToString(Map.Entry<Table, List<Record>> entry) {
    int columnCount = entry.getKey().getColumns().size();
    return entry.getValue().stream().map(r ->
        "INSERT INTO " + r.getTable().getName() + " VALUES ("
            + IntStream.range(0, columnCount)
            .mapToObj(index -> r.getData(index).orElse("null"))
            .collect(Collectors.joining(", "))
            + ")").collect(Collectors.joining("\n"));
  }

}