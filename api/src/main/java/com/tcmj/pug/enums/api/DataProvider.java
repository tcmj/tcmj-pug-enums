package com.tcmj.pug.enums.api;

import java.util.Objects;
import com.tcmj.pug.enums.model.EnumData;

/** API to implement a data loader which can provide enum data from any source. */
public interface DataProvider {
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
