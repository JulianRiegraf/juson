package de.riegraf.juson.converter;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import de.riegraf.juson.converter.JusonConverter.Database;
import de.riegraf.juson.exception.JusonException;
import de.riegraf.juson.utils.table.Column;
import de.riegraf.juson.utils.table.Record;
import de.riegraf.juson.utils.table.Table;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class JusonConverterTest {

  public String jsonFromFile(String resourceName) throws IOException {
    InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
    if (is == null) {
      throw new NullPointerException("Cannot find resource file " + resourceName);
    }
    return IOUtils.toString(is, "UTF-8");
  }


  @Test
  public void simpleJsonObjectMappingTest() throws JusonException, IOException {

    final String filename = "number.json";
    final String json = jsonFromFile(filename);

    JusonConverter a = new JusonConverter("json");
    Database db = a.convert(filename, json);
    List<Table> tables = db.getTables();
    List<Record> records = db.getRecords();

    Juson.printTablesAndRecords(tables, records);

    assertEquals(1, tables.size());
    assertEquals(1, records.size());

    assertEquals("number", tables.get(0).getName());

    assertEquals("positiv", tables.get(0).getColumn(0).name);
    assertEquals("type", tables.get(0).getColumn(1).name);
    assertEquals("value", tables.get(0).getColumn(2).name);

    assertEquals("double", records.get(0).getData("type").get());
    assertEquals("true", records.get(0).getData("positiv").get());
    assertEquals("1.2", records.get(0).getData("value").get());
  }

  @Test
  public void simpleJsonArrayMappingTest() throws JusonException, IOException {

    final String filename = "numbers.json";
    final String json = jsonFromFile(filename);

    JusonConverter a = new JusonConverter("json");
    Database db = a.convert(filename, json);
    List<Table> tables = db.getTables();
    List<Record> records = db.getRecords();

    Juson.printTablesAndRecords(tables, records);

    assertEquals(1, tables.size());
    assertEquals(3, records.size());

    assertEquals("numbers", tables.get(0).getName());

    assertTrue(tables.get(0).getColumns().containsAll(Arrays.asList(
        new Column("type", ""),
        new Column("positiv", ""),
        new Column("value", "")))
    );

    assertEquals("double", records.get(0).getData("type").get());
    assertEquals("true", records.get(0).getData("positiv").get());
    assertEquals("1.2", records.get(0).getData(2).get());

    assertEquals("int", records.get(1).getData("type").get());
    assertEquals("true", records.get(1).getData("positiv").get());
    assertEquals("2", records.get(1).getData("value").get());

    assertEquals("double", records.get(2).getData("type").get());
    assertEquals("false", records.get(2).getData("positiv").get());
    assertEquals("-1.2", records.get(2).getData("value").get());
  }

  @Test
  public void nestedObjectMappingTest() throws JusonException, IOException {

    final String filename = "api_call.json";
    final String json = jsonFromFile(filename);

    JusonConverter a = new JusonConverter("json");
    Database db = a.convert(filename, json);
    List<Table> tables = db.getTables();
    List<Record> records = db.getRecords();

    Juson.printTablesAndRecords(tables, records);

    assertEquals(2, tables.size());
    assertEquals(2, records.size());

    int index_api_call = tables.indexOf(new Table("api_call", null));
    int index_result = tables.indexOf(new Table("result", null));

    assertNotEquals(-1, index_api_call);
    assertNotEquals(-1, index_result);

    assertTrue(tables.get(index_api_call).getColumns().containsAll(Arrays.asList(
        new Column("result_id", ""),
        new Column("status", "")))
    );

    assertTrue(tables.get(index_result).getColumns().containsAll(Arrays.asList(
        new Column("result_id", ""),
        new Column("type", ""),
        new Column("positiv", ""),
        new Column("value", "")))
    );

    Record r_api_call = records.stream()
        .filter(r -> r.getTable().getName().equals("api_call"))
        .findFirst()
        .get();

    Record r_result = records.stream()
        .filter(r -> r.getTable().getName().equals("result"))
        .findFirst()
        .get();

    assertEquals("OK", r_api_call.getData("status").get());
    assertEquals("double", r_result.getData("type").get());
    assertEquals("true", r_result.getData("positiv").get());
    assertEquals("1.2", r_result.getData("value").get());

    String api_call_id = r_api_call.getData(0).get();
    String result_id = r_result.getData(0).get();
  }

  @Test
  public void nestedObjectsAndArraysDataTest() throws JusonException, IOException {

    final String filename = "articles_call.json";
    final String json = jsonFromFile(filename);

    JusonConverter a = new JusonConverter("json");
    Database db = a.convert(filename, json);
    List<Table> tables = db.getTables();
    List<Record> records = db.getRecords();

    Juson.printTablesAndRecords(tables, records);

    assertEquals(13, records.size());

    int index_articles_call = tables.indexOf(new Table("articles_call", null));
    int index_results = tables.indexOf(new Table("results", null));
    int index_multimedia = tables.indexOf(new Table("multimedia", null));
    int index_articles_call_results = tables.indexOf(new Table("articles_call_results", null));
    int index_results_multimedia = tables.indexOf(new Table("results_multimedia", null));

    Map<Table, List<Record>> record_map = records.stream()
        .collect(Collectors.groupingBy(Record::getTable));

    assertEquals(1, record_map.get(tables.get(index_articles_call)).stream()
        .filter(r -> r.getData().contains("OK"))
        .filter(r -> r.getData().contains("2018-12-15T17:27:12-05:00"))
        .filter(r -> r.getData().contains("2"))
        .count());

    assertEquals(1, record_map.get(tables.get(index_results)).stream()
        .filter(r -> r.getData().contains("World"))
        .filter(r -> r.getData().contains("Fancy title here"))
        .filter(r -> r.getData().contains("Article"))
        .filter(r -> r.getData().contains("2018-12-15T12:05:59-05:00"))
        .count());

    assertEquals(1, record_map.get(tables.get(index_results)).stream()
        .filter(r -> r.getData().contains("Europe"))
        .filter(r -> r.getData().contains("Another fancy title"))
        .filter(r -> r.getData().contains("Article"))
        .filter(r -> r.getData().contains("2018-12-15T16:41:18-05:00"))
        .count());

    assertEquals(1, record_map.get(tables.get(index_multimedia)).stream()
        .filter(r -> r.getData().contains("Standard Thumbnail"))
        .filter(r -> r.getData().contains("75"))
        .filter(r -> r.getData().contains("image"))
        //.filter(r -> r.getData().contains("null"))
        .count());

    assertEquals(1, record_map.get(tables.get(index_multimedia)).stream()
        .filter(r -> r.getData().contains("thumbLarge"))
        .filter(r -> r.getData().contains("150"))
        .filter(r -> r.getData().contains("image"))
        //.filter(r -> r.getData().contains("null"))
        .count());

    assertEquals(1, record_map.get(tables.get(index_multimedia)).stream()
        .filter(r -> r.getData().contains("Normal"))
        .filter(r -> r.getData().contains("132"))
        .filter(r -> r.getData().contains("190"))
        .filter(r -> r.getData().contains("image"))
        //.filter(r -> r.getData().contains("png"))
        .count());

    assertEquals(1, record_map.get(tables.get(index_multimedia)).stream()
        .filter(r -> r.getData().contains("mediumThreeByTwo210"))
        .filter(r -> r.getData().contains("140"))
        .filter(r -> r.getData().contains("210"))
        .filter(r -> r.getData().contains("photo"))
        //.filter(r -> r.getData().contains("null"))
        .count());

    String articles_call_id = record_map.get(tables.get(index_articles_call)).get(0)
        .getData("articles_call_id").get();
    String results_id_0 = record_map.get(tables.get(index_results)).get(0)
        .getData("results_id").get();
    String results_id_1 = record_map.get(tables.get(index_results)).get(1)
        .getData("results_id").get();
    String multimedia_id_0 = record_map.get(tables.get(index_multimedia)).get(0)
        .getData("multimedia_id").get();
    String multimedia_id_1 = record_map.get(tables.get(index_multimedia)).get(1)
        .getData("multimedia_id").get();
    String multimedia_id_2 = record_map.get(tables.get(index_multimedia)).get(2)
        .getData("multimedia_id").get();
    String multimedia_id_3 = record_map.get(tables.get(index_multimedia)).get(3)
        .getData("multimedia_id").get();

    assertEquals(1, record_map.get(tables.get(index_articles_call_results)).stream()
        .filter(r -> r.getData().contains(results_id_0))
        .filter(r -> r.getData().contains(articles_call_id))
        .count());

    assertEquals(1, record_map.get(tables.get(index_articles_call_results)).stream()
        .filter(r -> r.getData().contains(results_id_1))
        .filter(r -> r.getData().contains(articles_call_id))
        .count());

    assertEquals(1, record_map.get(tables.get(index_results_multimedia)).stream()
        .filter(r -> r.getData().contains(results_id_0))
        .filter(r -> r.getData().contains(multimedia_id_0))
        .count());

    assertEquals(1, record_map.get(tables.get(index_results_multimedia)).stream()
        .filter(r -> r.getData().contains(results_id_0))
        .filter(r -> r.getData().contains(multimedia_id_1))
        .count());

    assertEquals(1, record_map.get(tables.get(index_results_multimedia)).stream()
        .filter(r -> r.getData().contains(results_id_1))
        .filter(r -> r.getData().contains(multimedia_id_2))
        .count());

    assertEquals(1, record_map.get(tables.get(index_results_multimedia)).stream()
        .filter(r -> r.getData().contains(results_id_1))
        .filter(r -> r.getData().contains(multimedia_id_3))
        .count());
  }

  @Test
  public void nestedObjectsAndArraysStructureTest() throws JusonException, IOException {

    final String filename = "articles_call.json";
    final String json = jsonFromFile(filename);

    JusonConverter a = new JusonConverter("json");
    Database db = a.convert(filename, json);
    List<Table> tables = db.getTables();
    List<Record> records = db.getRecords();

    Juson.printTablesAndRecords(tables, records);

    assertEquals(5, tables.size());

    int index_articles_call = tables.indexOf(new Table("articles_call", null));
    int index_results = tables.indexOf(new Table("results", null));
    int index_multimedia = tables.indexOf(new Table("multimedia", null));
    int index_articles_call_results = tables.indexOf(new Table("articles_call_results", null));
    int index_results_multimedia = tables.indexOf(new Table("results_multimedia", null));

    assertNotEquals(-1, index_articles_call);
    assertNotEquals(-1, index_results);
    assertNotEquals(-1, index_multimedia);
    assertNotEquals(-1, index_articles_call_results);
    assertNotEquals(-1, index_results_multimedia);

    assertEquals(4, tables.get(index_articles_call).getColumns().size());
    assertEquals(5, tables.get(index_results).getColumns().size());
    assertEquals(5, tables.get(index_multimedia).getColumns().size());
    assertEquals(2, tables.get(index_articles_call_results).getColumns().size());
    assertEquals(2, tables.get(index_results_multimedia).getColumns().size());

    assertTrue(tables.get(index_articles_call).getColumns().containsAll(Arrays.asList(
        new Column("articles_call_id", ""),
        new Column("status", ""),
        new Column("last_updated", ""),
        new Column("num_results", "")))
    );

    assertTrue(tables.get(index_results).getColumns().containsAll(Arrays.asList(
        new Column("results_id", ""),
        new Column("section", ""),
        new Column("title", ""),
        new Column("item_type", ""),
        new Column("published_date", "")))
    );

    assertTrue(tables.get(index_multimedia).getColumns().containsAll(Arrays.asList(
        new Column("multimedia_id", ""),
        new Column("format", ""),
        new Column("height", ""),
        new Column("width", ""),
        new Column("type", "")))//,
        //new Column("file", "")))
    );

    assertTrue(tables.get(index_articles_call_results).getColumns().containsAll(Arrays.asList(
        new Column("articles_call_id", ""),
        new Column("results_id", "")))
    );

    assertTrue(tables.get(index_results_multimedia).getColumns().containsAll(Arrays.asList(
        new Column("results_id", ""),
        new Column("multimedia_id", "")))
    );


  }

  @Test(expected = JusonException.class)
  public void nestedArrayShouldThrowExceptinTest() throws JusonException {

    final String json = "{\n"
        + "   \"values\":[\n"
        + "      [\n"
        + "         1,\n"
        + "         2,\n"
        + "         3\n"
        + "      ],\n"
        + "      [\n"
        + "         4,\n"
        + "         5,\n"
        + "         6\n"
        + "      ]\n"
        + "   ]\n"
        + "}";

    JusonConverter a = new JusonConverter("json");
    Database db = a.convert("root", json);
    List<Table> tables = db.getTables();
    List<Record> records = db.getRecords();
  }
}









