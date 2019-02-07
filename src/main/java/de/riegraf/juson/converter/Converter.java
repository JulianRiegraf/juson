package de.riegraf.juson.converter;

import de.riegraf.juson.utils.database.Database;
import java.util.Optional;

public interface Converter {

  Database convert(String outerName, Object json);

}
