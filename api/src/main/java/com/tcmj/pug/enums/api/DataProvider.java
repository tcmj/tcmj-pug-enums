package com.tcmj.pug.enums.api;

import java.util.Objects;
import com.tcmj.pug.enums.model.EnumData;

/** API to implement a data loader which can provide enum data from any source. 
 * A DataProvider should used to load tabular data. You should define the main column used for the
 * constant names and either no subfields or some subfield columns.
 * A mandatory field can be a address where to load the data from but this is implementation specific.
 */
@FunctionalInterface
public interface DataProvider {
  /** Main method used to provide a EnumData object loaded with data. */
  EnumData load();

  default DataProvider and(DataProvider other) {
    Objects.requireNonNull(other);
    return () -> {
      EnumData loaded1 = load();
      EnumData loaded2 = other.load();
      loaded1.getData().addAll(loaded2.getData());
      return loaded1;
    };
  }
}
