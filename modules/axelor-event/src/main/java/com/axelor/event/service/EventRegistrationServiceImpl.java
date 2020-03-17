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
  public BigDecimal getEventRegisrationDiscountAmount(
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
    return discountAmount;
  }

  public boolean isRegistrationCapacityIsNotFull(Event event) {
    Integer registrationSize = 0;
    if (event.getEventRegistrationList() != null) {
      registrationSize = event.getEventRegistrationList().size();
    }
    if (event.getCapacity() > registrationSize) {
      return true;
    } else {
      return false;
    }
  }
}
