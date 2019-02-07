package de.riegraf.juson.utils.database;

/**
 * Represents a column in a de.riegraf.juson.database database. Does NOT hold any data.
 */
public class Column {

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDatatype() {
    return datatype;
  }

  public void setDatatype(String datatype) {
    this.datatype = datatype;
  }

  public Long getPrecision() {
    return precision;
  }

  public void setPrecision(Long precision) {
    this.precision = precision;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  private String name;
  private String datatype;
  private Long precision;
  private String defaultValue;

  public Column(String name, String datatype, Long precision, String defaultValue) {
    this.name = name;
    this.datatype = datatype;
    this.precision = precision;
    this.defaultValue = defaultValue;
  }

  public Column(String name, String datatype) {
    this.name = name.toLowerCase();
    this.datatype = datatype.toUpperCase();
    this.precision = null;
    this.defaultValue = null;
  }

  private String getDefaultString() {
    return defaultValue == null ? "" : " DEFAULT '" + defaultValue + "'";
  }

  private String getPreisionString() {
    return precision == null ? "" : " (" + precision + ")";
  }

  @Override
  public String toString() {
    return name + " " + datatype + getPreisionString() + getDefaultString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Column) {
      return ((Column) obj).name.equalsIgnoreCase(this.name);
    }
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return this.name.toLowerCase().hashCode();
  }

}
