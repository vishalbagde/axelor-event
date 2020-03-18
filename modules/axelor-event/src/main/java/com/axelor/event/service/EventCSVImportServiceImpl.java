package com.axelor.event.service;

import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.axelor.event.db.repo.EventRepository;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.util.Map;

public class EventCSVImportServiceImpl {

  @Inject EventRegistrationSevice eventRegistrationService;

  static int capacityCounter = 0;

  public Object importRegistrationData(Object bean, Map<String, Object> values) {

    assert bean instanceof EventRegistration;
    EventRegistration eventRegistration = (EventRegistration) bean;
    Event event =
        (Beans.get(EventRepository.class).find(Long.parseLong(values.get("event_id").toString())));

    if (event.getCapacity()
        > (Integer.parseInt(values.get("reg_list_size").toString()) + capacityCounter)) {
      capacityCounter++;
      if (event.getRegOpenDate() != null
          && event.getRegCloseDate() != null
          && (!eventRegistration.getRegDate().isBefore(event.getRegOpenDate())
              && !eventRegistration.getRegDate().isAfter(event.getRegCloseDate()))) {

        eventRegistration.setAmount(
            event
                .getEventFees()
                .subtract(
                    eventRegistrationService.getEventRegisrationDiscountAmount(
                        eventRegistration, event)));

        eventRegistration.setEvent(event);
        return eventRegistration;
      } else {
        System.err.println("Invalid Registration Date");
      }
    } else {
      capacityCounter = 0;
      System.err.println("Capacity is exceed");
    }
    return null;
  }
}
