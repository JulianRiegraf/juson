package de.riegraf.juson;

import de.riegraf.juson.converter.JusonDatabaseWriter;
import de.riegraf.juson.database.DatabaseConnection;
import de.riegraf.juson.database.MySQL;
import de.riegraf.juson.database.PostgreSQL;
import de.riegraf.juson.exception.JusonException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;

public class Main {

  public static void main(final String[] args) {
    final String filename = "persons.json";
    final String path = "/home/julian/IdeaProjects/juson/src/test/resources/" + filename;
    final String schema = "fromJson";

    try {
      final String json = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
      //DatabaseConnection conn = new PostgreSQL("localhost:5432", "postgres", "docker");
      DatabaseConnection conn = new MySQL("localhost:3306", "root", "docker", Arrays.asList("useSSL=false", "allowPublicKeyRetrieval=true"));
      conn.executeSQL("DROP SCHEMA IF EXISTS " + schema + "");
      conn.executeSQL("CREATE SCHEMA " + schema);
      new JusonDatabaseWriter(filename, json, conn, schema);

    } catch (SQLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JusonException e) {
      e.printStackTrace();
    }

  }
}