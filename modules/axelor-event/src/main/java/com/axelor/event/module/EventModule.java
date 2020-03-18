package com.axelor.event.module;

import com.axelor.app.AxelorModule;
import com.axelor.event.db.repo.EventRegistrationRepo;
import com.axelor.event.db.repo.EventRegistrationRepository;
import com.axelor.event.service.EventRegistrationServiceImpl;
import com.axelor.event.service.EventRegistrationSevice;
import com.axelor.event.service.EventService;
import com.axelor.event.service.EventServiceImpl;

public class EventModule extends AxelorModule {

  @Override
  protected void configure() {

    bind(EventRegistrationRepository.class).to(EventRegistrationRepo.class);
    bind(EventRegistrationSevice.class).to(EventRegistrationServiceImpl.class);
    bind(EventService.class).to(EventServiceImpl.class);
  }
}
