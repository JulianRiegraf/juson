package de.riegraf.juson.converter;

import de.riegraf.juson.database.PostgreSQL;
import de.riegraf.juson.exception.JusonException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Main {

  public static void main(final String[] args) {
    final String filename = "persons.json";
    final String path = "/home/julian/IdeaProjects/juson/src/test/resources/" + filename;
    final String schema = "fromJson";

    try {
      final String json = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
      PostgreSQL postgreSQL = new PostgreSQL("localhost:5432", "postgres", "docker");
      postgreSQL.executeSQL("DROP SCHEMA IF EXISTS " + schema + " CASCADE");
      postgreSQL.executeSQL("CREATE SCHEMA " + schema);
      new JusonDatabaseWriter(filename, json, postgreSQL, schema);

    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JusonException e) {
      e.printStackTrace();
    }

  }
}