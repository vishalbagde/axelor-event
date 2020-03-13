package com.axelor.event.service;

import java.util.Map;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.axelor.event.db.repo.EventRepository;
import com.axelor.inject.Beans;
import com.google.inject.Inject;

public class EventCSVImportServiceImpl {

	@Inject
	EventRegistrationSevice eventRegistrationService;

	public Object importRegistrationData(Object bean, Map<String, Object> values) {

		assert bean instanceof EventRegistration;
		EventRegistration eventRegistration = (EventRegistration) bean;
		Event event = (Beans.get(EventRepository.class).find(Long.parseLong(values.get("event").toString())));
		if (eventRegistrationService.isRegistrationCapacityIsNotFull(event)) {

			if (event.getRegOpenDate() != null && event.getRegCloseDate() != null
					&& (!eventRegistration.getRegDate().isBefore(event.getRegOpenDate())
							&& !eventRegistration.getRegDate().isAfter(event.getRegCloseDate()))) {

				eventRegistration = eventRegistrationService.calculateEventRegisrationAmount(eventRegistration, event);
				eventRegistration.setEvent(event);
			} else {
				System.err.println("Invalid Registration Date");
			}
		} else {
			System.err.println("Capacity is exceed");
		}

		return eventRegistration;
	}

}
