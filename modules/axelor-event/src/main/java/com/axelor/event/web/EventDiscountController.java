package com.axelor.event.web;

import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.exception.IExceptionEvent;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.math.BigDecimal;

public class EventDiscountController {

  public void calculateDiscountAmount(ActionRequest request, ActionResponse response) {
    Discount discount = request.getContext().asType(Discount.class);
    Event event = request.getContext().getParent().asType(Event.class);
    if (event.getEventFees() != null && discount.getDiscountPercent() != null) {
      response.setValue(
          "discountAmount",
          event.getEventFees().multiply(discount.getDiscountPercent()).divide(new BigDecimal(100)));
    } else {
      response.setError(I18n.get(IExceptionEvent.ERROR_INVALID_DAYS_OR_PERCENTAGE));
    }
  }
}
