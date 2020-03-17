package com.axelor.event.service;

import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import java.math.BigDecimal;

public interface EventRegistrationSevice {

  public BigDecimal getEventRegisrationDiscountAmount(
      EventRegistration eventRegistration, Event event);

  public boolean isRegistrationCapacityIsNotFull(Event event);
}
