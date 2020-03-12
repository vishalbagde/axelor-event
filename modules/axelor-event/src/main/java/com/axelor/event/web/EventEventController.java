package com.axelor.event.web;

import com.axelor.event.db.Event;
import com.axelor.event.service.EventService;
import com.axelor.meta.db.MetaFile;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class EventEventController {

  @Inject EventService eventService;

  public void computeTotal(ActionRequest request, ActionResponse response) {
    Event event = request.getContext().asType(Event.class);
    event = eventService.computeTotal(event);
    response.setValues(event);
  }

  public void verifyEvent(ActionRequest request, ActionResponse response) {
    Event event = request.getContext().asType(Event.class);
    event = eventService.verifyEvent(event);

    response.setValue("discountList", event.getDiscountList());
    response.setValue("eventRegistrationList", event.getEventRegistrationList());

    response.setValue("totalEntry", event.getTotalEntry());
    response.setValue("amountCollected", event.getAmountCollected());
    response.setValue("totalDiscount", event.getTotalDiscount());

    // response.setValues(event);
  }

  public void importCsvEventRegistration(ActionRequest request, ActionResponse response) {
    // LinkedHashMap<String, Object> map =(LinkedHashMap<String, Object>)
    // request.getContext().get("metaFile");
    MetaFile metaFile = request.getContext().asType(MetaFile.class);
  }
}
