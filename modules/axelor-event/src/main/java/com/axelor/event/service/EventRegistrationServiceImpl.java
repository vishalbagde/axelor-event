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

    BigDecimal discountAmount = BigDecimal.ZERO;

    List<Discount> discountList = event.getDiscountList();

    if (discountList != null && !discountList.isEmpty()) {

      discountList =
          discountList
              .stream()
              .sorted(
                  Comparator.comparing(Discount::getBeforeDays)
                      .reversed()
                      .thenComparing(Comparator.comparing(Discount::getDiscountPercent).reversed()))
              .collect(Collectors.toList());

      for (Discount discount : discountList) {
        if (intervalPeriod.getDays() >= discount.getBeforeDays()) {
          System.err.println(
              "selected Discount : "
                  + discount.getBeforeDays()
                  + " Discount Amount : "
                  + discount.getDiscountAmount());
          discountAmount = discount.getDiscountAmount();
          break;
        }
      }
    }
    return discountAmount;
  }

  public boolean isRegistrationCapacityIsNotFull(Event event) {
    Integer registrationSize = 0;
    List<EventRegistration> eventRegistrationList = event.getEventRegistrationList();
    if (eventRegistrationList != null) {
      registrationSize = eventRegistrationList.size();
    }
    if (event.getCapacity() > registrationSize) {
      return true;
    } else {
      return false;
    }
  }
}
