package com.axelor.event.service;

import com.axelor.event.db.Event;

public interface EventService {

  public Event computeTotal(Event event);

  public Event verifyEvent(Event event);
}
