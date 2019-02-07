package de.riegraf.juson.converter;

import de.riegraf.juson.utils.database.Database;

public interface Converter {

  Database convert(String outerName, Object json);

}
