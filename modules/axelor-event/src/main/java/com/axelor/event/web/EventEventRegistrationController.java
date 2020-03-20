package com.axelor.event.web;

import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.axelor.event.exception.IExceptionEvent;
import com.axelor.event.service.EventRegistrationSevice;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.math.BigDecimal;

public class EventEventRegistrationController {

  public void checkRegistrationDate(ActionRequest request, ActionResponse response) {
    EventRegistration eventRegistration = request.getContext().asType(EventRegistration.class);
    Event event = null;
    if (request.getContext().getParent() != null) {

      event = request.getContext().getParent().asType(Event.class);

    } else {
      event = eventRegistration.getEvent();
    }
    if (event != null) {
      if (event.getRegOpenDate() != null && event.getRegCloseDate() != null) {

        if (eventRegistration.getRegDate().compareTo(event.getRegOpenDate()) < 0
            || eventRegistration.getRegDate().compareTo(event.getRegCloseDate()) > 0) {

          response.setValue("amount", BigDecimal.ZERO);
          response.setValue("regDate", null);

          response.setFlash(
              "Reg Date are must between "
                  + event.getRegOpenDate()
                  + " And "
                  + event.getRegCloseDate());
        }
      } else {
        response.setValue("regDate", null);
        response.setFlash("Invalid Registration Date or select Registration Date");
      }
    }
  }

  public void calculateEventRegistrationAmount(ActionRequest request, ActionResponse response) {
    EventRegistration eventRegistration = request.getContext().asType(EventRegistration.class);
    Event event;
    if (request.getContext().getParent() != null) {
      event = request.getContext().getParent().asType(Event.class);
    } else {
      event = eventRegistration.getEvent();
    }

    if (event != null) {
      if (eventRegistration.getRegDate() != null
          && (eventRegistration.getRegDate().compareTo(event.getRegOpenDate()) >= 0)
          && (eventRegistration.getRegDate().compareTo(event.getRegCloseDate()) <= 0)) {

        eventRegistration.setAmount(
            event
                .getEventFees()
                .subtract(
                    Beans.get(EventRegistrationSevice.class)
                        .getEventRegisrationDiscountAmount(eventRegistration, event)));
        response.setValues(eventRegistration);
      } else {
        response.setError(I18n.get(IExceptionEvent.ERROR_INVALID_DATE));
      }
    }
  }

  public void validationForRegistrationCapacity(ActionRequest request, ActionResponse response) {

    EventRegistration eventRegistration = request.getContext().asType(EventRegistration.class);

    if (eventRegistration.getId() == null) {

      Event event = null;
      if (request.getContext().getParent() != null) {
        event = request.getContext().getParent().asType(Event.class);
      } else {
        event = eventRegistration.getEvent();
      }
      if (event != null
          && !Beans.get(EventRegistrationSevice.class).isRegistrationCapacityIsNotFull(event)) {

        response.setValue("regDate", null);

        response.setFlash("Exceed Event Capacity");
        response.setFlash(I18n.get(IExceptionEvent.ERROR_EVENT_CAPACITY));
      }
    }
  }
}
