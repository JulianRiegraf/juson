import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import exception.JusonException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.junit.Test;
import utils.table.Record;
import utils.table.Table;

public class JusonConverterTest {


  @Test
  public void jsonArrayWithValueNodesTest() throws JusonException {

    final String json = Main.jsonFromFile("number.json");

    LinkedList<Table> tables = new LinkedList<>();
    LinkedList<Record> records = new LinkedList<>();

    JusonConverter a = new JusonConverter(tables, records);
    a.handleJsonObject("root", new JSONObject(json));

    Main.printTablesAndRecords(tables, records);

    assertEquals(1, tables.size());
    assertEquals(1, records.size());

    assertEquals("number", tables.getFirst().getName());
    assertEquals("type", tables.getFirst().getColumn(0).name);
    assertEquals("positiv", tables.getFirst().getColumn(1).name);
    assertEquals("value", tables.getFirst().getColumn(2).name);

    assertEquals("double", records.getFirst().getData(0).get());
    assertEquals("true", records.getFirst().getData(1).get());
    assertEquals("1.2", records.getFirst().getData(2).get());
  }

  @Test
  public void jsonArrayWithObjectNodesTest() throws JusonException {

    final String json = Main.jsonFromFile("numbers.json");

    LinkedList<Table> tables = new LinkedList<>();
    LinkedList<Record> records = new LinkedList<>();

    JusonConverter a = new JusonConverter(tables, records);
    a.handleJsonObject("root", new JSONObject(json));

    Main.printTablesAndRecords(tables, records);

    assertEquals(1, tables.size());
    assertEquals(3, records.size());

    assertEquals("numbers", tables.getFirst().getName());

    assertEquals("type", tables.getFirst().getColumn(0).name);
    assertEquals("positiv", tables.getFirst().getColumn(1).name);
    assertEquals("value", tables.getFirst().getColumn(2).name);

    assertEquals("double", records.get(0).getData(0).get());
    assertEquals("true", records.get(0).getData(1).get());
    assertEquals("1.2", records.get(0).getData(2).get());

    assertEquals("int", records.get(1).getData(0).get());
    assertEquals("true", records.get(1).getData(1).get());
    assertEquals("2", records.get(1).getData(2).get());

    assertEquals("double", records.get(2).getData(0).get());
    assertEquals("false", records.get(2).getData(1).get());
    assertEquals("-1.2", records.get(2).getData(2).get());
  }

  @Test
  public void jsonArrayWithNotStringValueNodesTest() throws JusonException {

    final String json = Main.jsonFromFile("api_call.json");

    LinkedList<Table> tables = new LinkedList<>();
    LinkedList<Record> records = new LinkedList<>();

    JusonConverter a = new JusonConverter(tables, records);
    a.handleJsonObject("root", new JSONObject(json));

    Main.printTablesAndRecords(tables, records);

    assertEquals(3, tables.size());
    assertEquals(3, records.size());

    int index_api_call = tables.indexOf(new Table("api_call", null));
    int index_api_call_result = tables.indexOf(new Table("api_call_result", null));
    int index_result = tables.indexOf(new Table("result", null));

    assertNotEquals(-1, index_api_call);
    assertNotEquals(-1, index_api_call_result);
    assertNotEquals(-1, index_result);

    assertEquals("api_call_id", tables.get(index_api_call).getColumn(0).name);
    assertEquals("status", tables.get(index_api_call).getColumn(1).name);

    assertEquals("result_id", tables.get(index_api_call_result).getColumn(0).name);
    assertEquals("api_call_id", tables.get(index_api_call_result).getColumn(1).name);

    assertEquals("result_id", tables.get(index_result).getColumn(0).name);
    assertEquals("type", tables.get(index_result).getColumn(1).name);
    assertEquals("positiv", tables.get(index_result).getColumn(2).name);
    assertEquals("value", tables.get(index_result).getColumn(3).name);

    Record r_api_call = records.stream()
        .filter(r -> r.getTable().getName().equals("api_call"))
        .findFirst()
        .get();

    Record r_api_call_result = records.stream()
        .filter(r -> r.getTable().getName().equals("api_call_result"))
        .findFirst()
        .get();

    Record r_result = records.stream()
        .filter(r -> r.getTable().getName().equals("result"))
        .findFirst()
        .get();

    assertEquals("OK", r_api_call.getData(1).get());
    assertEquals("double", r_result.getData(1).get());
    assertEquals("true", r_result.getData(2).get());
    assertEquals("1.2", r_result.getData(3).get());

    String api_call_id = r_api_call.getData(0).get();
    String result_id = r_result.getData(0).get();

    assertEquals(api_call_id, r_api_call_result.getData(0).get());
    assertEquals(result_id, r_api_call_result.getData(1).get());
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

    List<Table> tables = new LinkedList<>();
    List<Record> records = new LinkedList<>();

    JusonConverter a = new JusonConverter(tables, records);
    a.handleJsonObject("root", new JSONObject(json));
  }

  @Test
  public void extendedTest() throws JusonException {

    final String json = Main.jsonFromFile("articles_call.json");

    LinkedList<Table> tables = new LinkedList<>();
    LinkedList<Record> records = new LinkedList<>();

    JusonConverter a = new JusonConverter(tables, records);
    a.handleJsonObject("root", new JSONObject(json));

    Main.printTablesAndRecords(tables, records);

    assertEquals(5, tables.size());
    assertEquals(13, records.size());

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

    assertEquals("articles_call_id", tables.get(index_articles_call).getColumn(0).name);
    assertEquals("status", tables.get(index_articles_call).getColumn(1).name);
    assertEquals("last_updated", tables.get(index_articles_call).getColumn(2).name);
    assertEquals("num_results", tables.get(index_articles_call).getColumn(3).name);

    assertEquals("results_id", tables.get(index_results).getColumn(0).name);
    assertEquals("section", tables.get(index_results).getColumn(1).name);
    assertEquals("title", tables.get(index_results).getColumn(2).name);
    assertEquals("item_type", tables.get(index_results).getColumn(3).name);
    assertEquals("published_date", tables.get(index_results).getColumn(4).name);

    assertEquals("multimedia_id", tables.get(index_multimedia).getColumn(0).name);
    assertEquals("format", tables.get(index_multimedia).getColumn(1).name);
    assertEquals("height", tables.get(index_multimedia).getColumn(2).name);
    assertEquals("width", tables.get(index_multimedia).getColumn(3).name);
    assertEquals("type", tables.get(index_multimedia).getColumn(4).name);
    assertEquals("file", tables.get(index_multimedia).getColumn(5).name);

    assertEquals("articles_call_id", tables.get(index_articles_call_results).getColumn(0).name);
    assertEquals("results_id", tables.get(index_articles_call_results).getColumn(1).name);

    assertEquals("results_id", tables.get(index_results_multimedia).getColumn(0).name);
    assertEquals("multimedia_id", tables.get(index_results_multimedia).getColumn(1).name);

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
        .filter(r -> r.getData().contains("null"))
        .count());

    assertEquals(1, record_map.get(tables.get(index_multimedia)).stream()
        .filter(r -> r.getData().contains("thumbLarge"))
        .filter(r -> r.getData().contains("150"))
        .filter(r -> r.getData().contains("image"))
        .filter(r -> r.getData().contains("null"))
        .count());

    assertEquals(1, record_map.get(tables.get(index_multimedia)).stream()
        .filter(r -> r.getData().contains("Normal"))
        .filter(r -> r.getData().contains("132"))
        .filter(r -> r.getData().contains("190"))
        .filter(r -> r.getData().contains("image"))
        .filter(r -> r.getData().contains("png"))
        .count());

    assertEquals(1, record_map.get(tables.get(index_multimedia)).stream()
        .filter(r -> r.getData().contains("mediumThreeByTwo210"))
        .filter(r -> r.getData().contains("140"))
        .filter(r -> r.getData().contains("210"))
        .filter(r -> r.getData().contains("photo"))
        .filter(r -> r.getData().contains("null"))
        .count());

    String articles_call_id = record_map.get(tables.get(index_articles_call)).get(0)
        .getData("articles_call_id").get();
    String results_id_0 = record_map.get(tables.get(index_articles_call)).get(0)
        .getData("results_id").get();
    String results_id_1 = record_map.get(tables.get(index_articles_call)).get(1)
        .getData("results_id").get();
    String multimedia_id_0 = record_map.get(tables.get(index_articles_call)).get(0)
        .getData("multimedia_id").get();
    String multimedia_id_1 = record_map.get(tables.get(index_articles_call)).get(1)
        .getData("multimedia_id").get();
    String multimedia_id_2 = record_map.get(tables.get(index_articles_call)).get(2)
        .getData("multimedia_id").get();
    String multimedia_id_3 = record_map.get(tables.get(index_articles_call)).get(3)
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
}









