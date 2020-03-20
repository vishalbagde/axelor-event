package com.axelor.event.db.repo;

import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.axelor.event.service.EventService;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;

public class EventRegistrationRepo extends EventRegistrationRepository {

  @Inject EventRepository eventRepo;

  @Override
  public void remove(EventRegistration entity) {
    super.remove(entity);
    Event event = entity.getEvent();

    BigDecimal discount = event.getEventFees().subtract(entity.getAmount());
    event.setTotalEntry(event.getTotalEntry() - 1);
    event.setAmountCollected(event.getAmountCollected().subtract(entity.getAmount()));
    event.setTotalDiscount(event.getTotalDiscount().subtract(discount));

    eventRepo.save(event);
  }

  @Override
  public EventRegistration save(EventRegistration entity) {

    Event event = entity.getEvent();
    event = Beans.get(EventService.class).verifyEvent(event);
    eventRepo.save(event);
    return entity;
  }
}
