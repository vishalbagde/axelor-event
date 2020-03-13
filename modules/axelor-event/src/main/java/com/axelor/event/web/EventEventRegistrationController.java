package com.axelor.event.web;

import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.axelor.event.service.EventRegistrationSevice;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class EventEventRegistrationController {

	@Inject
	EventRegistrationSevice eventRegistrationService;

	public void checkRegistrationDate(ActionRequest request, ActionResponse response) {
		EventRegistration eventRegistration = request.getContext().asType(EventRegistration.class);
		Event event = getEvent(request, response);
		/*
		 * if (event.getRegOpenDate() == null || event.getRegCloseDate() == null ||
		 * eventRegistration.getRegDate().isBefore(event.getRegOpenDate()) ||
		 * eventRegistration.getRegDate().isAfter(event.getRegCloseDate()))
		 */
		if (event.getRegOpenDate() == null || event.getRegCloseDate() == null
				|| eventRegistration.getRegDate().compareTo(event.getRegOpenDate()) < 0
				|| eventRegistration.getRegDate().compareTo(event.getRegCloseDate()) > 0) {

			response.setValue("regDate", null);

			response.setFlash(
					"Reg Date are must between " + event.getRegOpenDate() + " And " + event.getRegCloseDate());
		}
	}

	public void calculateEventRegistrationAmount(ActionRequest request, ActionResponse response) {
		EventRegistration eventRegistration = request.getContext().asType(EventRegistration.class);
		Event event = this.getEvent(request, response);

		if (eventRegistration.getRegDate() != null
				&& (eventRegistration.getRegDate().compareTo(event.getRegOpenDate()) >= 0)
				&& (eventRegistration.getRegDate().compareTo(event.getRegCloseDate()) <= 0)) {

			eventRegistration = eventRegistrationService.calculateEventRegisrationAmount(eventRegistration, event);

			response.setValues(eventRegistration);
			// response.setFlash(eventRegistration.getEvent().getReference());
		} else {
			response.setError("Invalida Date Date Must Be Start and End Date");
		}
	}

	public void validationForRegistrationCapacity(ActionRequest request, ActionResponse response) {
		Event event = this.getEvent(request, response);
		if (!eventRegistrationService.isRegistrationCapacityIsNotFull(event)) {
			response.setValue("regDate", null);
			response.setFlash("Exceed Event Capacity");
		}
	}

	public Event getEvent(ActionRequest request, ActionResponse response) {
		EventRegistration eventRegistration = request.getContext().asType(EventRegistration.class);
		Event event = null;
		if (request.getContext().getParent() != null) {
			event = request.getContext().getParent().asType(Event.class);
		} else {
			event = eventRegistration.getEvent();
		}
		return event;
	}
}
