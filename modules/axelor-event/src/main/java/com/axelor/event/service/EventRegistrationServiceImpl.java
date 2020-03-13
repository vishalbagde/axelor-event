package com.axelor.event.service;

import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import java.math.BigDecimal;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EventRegistrationServiceImpl implements EventRegistrationSevice {

  @Override
  public EventRegistration calculateEventRegisrationAmount(
      EventRegistration eventRegistration, Event event) {

    Period intervalPeriod = Period.between(eventRegistration.getRegDate(), event.getRegCloseDate());
    System.err.println(intervalPeriod.getDays());

    BigDecimal discountAmount = BigDecimal.ZERO;

    if (event.getDiscountList() != null || !event.getDiscountList().isEmpty()) {

      List<Discount> discountList =
          event
              .getDiscountList()
              .stream()
              .sorted(Comparator.comparing(Discount::getBeforeDays).reversed())
              .collect(Collectors.toList());

      for (Discount discount : discountList) {
        if (intervalPeriod.getDays() > discount.getBeforeDays()) {
          System.err.println("selected Discount : " + discount.getBeforeDays());
          discountAmount = discount.getDiscountAmount();
          break;
        }
      }
    }

    eventRegistration.setAmount(event.getEventFees().subtract(discountAmount));
    return eventRegistration;
  }

  public boolean isRegistrationCapacityIsNotFull(Event event) {
    if (event.getCapacity() > event.getEventRegistrationList().size()) {
      return true;
    } else {
      return false;
    }
  }
  
  
  
  
}
