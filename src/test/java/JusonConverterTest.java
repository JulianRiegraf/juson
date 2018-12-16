import static org.junit.Assert.assertEquals;

import exception.JusonException;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import utils.table.Record;
import utils.table.Table;

public class JusonConverterTest {

  @Test
  public void jsonArrayWithValueNodesTest() throws JusonException {

    final String json = "{\n"
        + "   \"pets\":[\n"
        + "      \"dogs\",\n"
        + "      \"cats\"\n"
        + "   ]\n"
        + "}";

    List<Table> tables = new LinkedList<>();
    List<Record> records = new LinkedList<>();

    JusonConverter a = new JusonConverter(tables, records);
    a.handleJsonObject("root", new JSONObject(json));

    Main.printTablesAndRecords(tables, records);

    assertEquals(3, tables.size());
    assertEquals(5, records.size());


  }


  @Test
  public void jsonArrayWithObjectNodesTest() throws JusonException {

    final String json = "{\n"
        + "   \"pets\":[\n"
        + "      {\n"
        + "         \"name\":\"dog\"\n"
        + "      },\n"
        + "      {\n"
        + "         \"name\":\"cat\"\n"
        + "      }\n"
        + "   ]\n"
        + "}";

    List<Table> tables = new LinkedList<>();
    List<Record> records = new LinkedList<>();

    JusonConverter a = new JusonConverter(tables, records);
    a.handleJsonObject("root", new JSONObject(json));

    Main.printTablesAndRecords(tables, records);

    assertEquals(3, tables.size());
    assertEquals(5, records.size());

  }

  @Test
  public void jsonArrayWithNotStringValueNodesTest() throws JusonException {

    final String json = "{\n"
        + "   \"values\":[1.1, 1.2, 1.3]\n"
        + "}";

    List<Table> tables = new LinkedList<>();
    List<Record> records = new LinkedList<>();

    JusonConverter a = new JusonConverter(tables, records);
    a.handleJsonObject("root", new JSONObject(json));

    Main.printTablesAndRecords(tables, records);

    assertEquals(3, tables.size());
    assertEquals(7, records.size());

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

    final String json = "{\n"
        + "   \"Herausgeber\":\"Xema\",\n"
        + "   \"Nummer\":\"1234-5678-9012-3456\",\n"
        + "   \"Deckung\":2e+6,\n"
        + "   \"Waehrung\":\"EURO\",\n"
        + "   \"Inhaber\":{\n"
        + "      \"Name\":\"Mustermann\",\n"
        + "      \"Vorname\":\"Max\",\n"
        + "      \"maennlich\":true,\n"
        + "      \"Hobbys\":[\n"
        + "         \"Reiten\",\n"
        + "         \"Golfen\",\n"
        + "         \"Lesen\"\n"
        + "      ],\n"
        + "      \"Alter\":42,\n"
        + "      \"Kinder\":[\n"
        + "         {\n"
        + "            \"name\":\"Fabian\",\n"
        + "            \"alter\":20\n"
        + "         },\n"
        + "         {\n"
        + "            \"name\":\"Julian\",\n"
        + "            \"alter\":23\n"
        + "         },\n"
        + "         {\n"
        + "            \"name\":\"David\",\n"
        + "            \"alter\":24\n"
        + "         }\n"
        + "      ],\n"
        + "      \"Partner\":null\n"
        + "   }\n"
        + "}";

    List<Table> tables = new LinkedList<>();
    List<Record> records = new LinkedList<>();

    JusonConverter a = new JusonConverter(tables, records);
    a.handleJsonObject("root", new JSONObject(json));

    Main.printTablesAndRecords(tables, records);

    assertEquals(6, tables.size());

  }
}









