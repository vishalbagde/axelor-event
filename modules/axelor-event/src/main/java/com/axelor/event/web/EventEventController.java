package com.axelor.event.web;

import com.axelor.event.db.Event;
import com.axelor.event.service.EventService;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import java.util.LinkedHashMap;

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

    Integer event_id = (Integer) request.getContext().get("_id");

    request.getContext().asType(Event.class);
    LinkedHashMap<String, Object> map =
        (LinkedHashMap<String, Object>) request.getContext().get("metaFile");

    if (map.get("fileType").toString().equals("text/csv")) {
      MetaFile metaFile =
          Beans.get(MetaFileRepository.class).find(Long.parseLong(map.get("id").toString()));
      Beans.get(EventService.class).importCsvInEventRegistration(metaFile, event_id);
    } else {
      response.setError("Invalid File Type");
    }
    response.setFlash("CSV DATA Import Successful");   
    response.setCanClose(true);
    
  }

  public void emailSend(ActionRequest request, ActionResponse response) {
    Event event = request.getContext().asType(Event.class);
    event = Beans.get(EventService.class).sendEmail(event);
    response.setValue("eventRegistrationList", event.getEventRegistrationList());
    response.setFlash("Email Send Successful");
  }
}
