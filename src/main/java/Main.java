import database.PostgreSQL;
import exception.JusonException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import jira.JiraCaller;
import org.json.JSONException;
import org.json.JSONObject;
import utils.table.Record;
import utils.table.Table;

public class Main {

  public static void main(final String[] args)
      throws JSONException, JusonException, SQLException, ClassNotFoundException, IOException {

    //final String json = new JiraCaller().call().get();

    String filename = "C:/Users/julia/Desktop/json.json";

    System.out.println(new File(filename).length());

    String json = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);

    List<Table> tables = new LinkedList<>();
    List<Record> records = new LinkedList<>();

    JusonConverter a = new JusonConverter(tables, records);
    a.handleJsonObject("root", new JSONObject(json));
    printTablesAndRecords(tables, records);

    PostgreSQL postgreSQL = new PostgreSQL("localhost:5432", "postgres", "postgres");
    postgreSQL.executeSQL("DROP SCHEMA IF EXISTS json CASCADE");
    postgreSQL.executeSQL("CREATE SCHEMA json");

    tables.forEach(x -> {
      try {
        postgreSQL.executeSQL(x.getCreateTableQuery());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });

    tables.forEach(table -> {
      try {
        PreparedStatement p = postgreSQL
            .createPreparedStatement(table.getInsertSqlForPrepStatement());

        List<Record> recordOfTable = records.stream()
            .filter(r -> r.getTable().equals(table))
            .collect(Collectors.toList());

        insertRecords(recordOfTable, table, p);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
  }

  private static void insertRecords(List<Record> records, Table table, PreparedStatement p) {
    try {
      for (Record r : records) {
        for (int i = 0; i < r.getData().size(); i++) {
          p.setString(i + 1, r.getData().get(i));
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

    System.out.println(
        "--- Records ---\n" + records.stream()
            .map(x -> x.getTable().getName() + " VALUES (" + x.getData().stream().
                map(String::toString)
                .collect(Collectors.joining(", ")) + ")")
            .map(Objects::toString)
            .collect(Collectors.joining("\n"))
    );
  }
}
