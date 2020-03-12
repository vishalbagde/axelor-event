package com.axelor.event.service;

import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;

public interface EventRegistrationSevice {

  public EventRegistration calculateEventRegisrationAmount(
      EventRegistration eventRegistration, Event event);

  public boolean isRegistrationCapacityIsNotFull(Event event);
}
