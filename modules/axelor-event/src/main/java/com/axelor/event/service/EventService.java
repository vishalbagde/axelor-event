package com.axelor.event.service;

import com.axelor.event.db.Event;
import com.axelor.meta.db.MetaFile;

public interface EventService {
  public Event computeTotal(Event event);

  public Event verifyEvent(Event event);

  public void sendEmail(Event event);

  public void importCsvInEventRegistration(MetaFile metaFile, Integer event_id);
}
