package com.axelor.event.db.repo;

import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.google.inject.Inject;

public class EventRegistrationRepo extends EventRegistrationRepository {

  @Inject EventRepository eventRepo;

  @Override
  public void remove(EventRegistration entity) {
    super.remove(entity);
    Event event = entity.getEvent();
    event.setTotalEntry(event.getTotalEntry() - 1);
    event.setAmountCollected(event.getAmountCollected().subtract(entity.getAmount()));
    event.setTotalDiscount(event.getTotalDiscount().subtract(event.getEventFees()));
    eventRepo.save(event);
  }
}
