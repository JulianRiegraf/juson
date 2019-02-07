package de.riegraf.juson.utils.database;

import java.util.List;

public class Database {

  protected List<Table> tables;
  protected List<Record> records;

  public Database(List<Table> tables,
      List<Record> records) {
    this.tables = tables;
    this.records = records;
  }

  public List<Table> getTables() {
    return tables;
  }

  public List<Record> getRecords() {
    return records;
  }
}
