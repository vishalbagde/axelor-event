package com.axelor.event.db.repo;

import com.axelor.event.db.Address;
import com.google.common.base.Strings;

public class AddressRepo extends AddressRepository {

  @Override
  public Address save(Address entity) {

    entity.setFullName(
        Strings.nullToEmpty(entity.getFlatNo())
            + ","
            + Strings.nullToEmpty(entity.getStreet())
            + ","
            + Strings.nullToEmpty(entity.getLandmark())
            + ","
            + Strings.nullToEmpty(entity.getCity())
            + ","
            + Strings.nullToEmpty(entity.getCountry()));
    return entity;
  }
}
