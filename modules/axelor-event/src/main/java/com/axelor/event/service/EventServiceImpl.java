package com.axelor.event.service;

import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.Template;
import com.axelor.apps.message.db.repo.TemplateRepository;
import com.axelor.apps.message.service.TemplateMessageService;
import com.axelor.data.ImportTask;
import com.axelor.data.Listener;
import com.axelor.data.csv.CSVImporter;
import com.axelor.db.Model;
import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;

public class EventServiceImpl implements EventService {

  @Inject TemplateMessageService templateService;

  @Inject MetaFileRepository metaFileRepository;
  
  @Inject TemplateRepository templateRepository;

  @Override
  public Event computeTotal(Event event) {

    Integer totalEntry = 0;
    BigDecimal totalCollection = BigDecimal.ZERO;
    BigDecimal totalDiscount = BigDecimal.ZERO;

    if (event.getEventRegistrationList() != null) {
      totalEntry = event.getEventRegistrationList().size();
      totalCollection =
          event
              .getEventRegistrationList()
              .stream()
              .map(x -> x.getAmount())
              .reduce(BigDecimal.ZERO, BigDecimal::add);
      totalDiscount =
          event.getEventFees().multiply(new BigDecimal(totalEntry)).subtract(totalCollection);

      event.setAmountCollected(totalCollection);
      event.setTotalDiscount(totalDiscount);
      event.setTotalEntry(event.getEventRegistrationList().size());
    }
    return event;
  }

  @Override
  public Event verifyEvent(Event event) {

    if (event.getDiscountList() != null) {
      List<Discount> discountList = event.getDiscountList();
      List<Discount> updatedDiscountList = new ArrayList<>();

      for (Discount discount : discountList) {
        discount.setDiscountAmount(
            event
                .getEventFees()
                .multiply(discount.getDiscountPercent())
                .divide(new BigDecimal(100)));
        updatedDiscountList.add(discount);
      }
      event.setDiscountList(updatedDiscountList);
    }

    event = computeTotal(event);
    return event;
  }

  public void importCsvInEventRegistration(MetaFile metaFile, Integer event_id) {

    CSVImporter importer =
        new CSVImporter(
            this.getConfigFile().getAbsolutePath());
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("event_id", event_id.longValue());
    
    importer.setContext(context);

    importer.addListener(
        new Listener() {

          @Override
          public void imported(Integer total, Integer success) {
            System.err.println("Total data import" + total);
          }

          @Override
          public void imported(Model bean) {}

          @Override
          public void handle(Model bean, Exception e) {}
        });

    importer.run(
        new ImportTask() {
          @Override
          public void configure() throws IOException {
            input("[event_registration]", getDataCsvFile(metaFile));
          }
        });

    removeMetaFile(metaFile);
  }

  @Transactional
  public void removeMetaFile(MetaFile metaFile) {
    metaFileRepository.remove(metaFile);
  }

  public File getConfigFile() {

    File configFile = null;
    try {
      configFile = File.createTempFile("input-config", ".xml");
      InputStream is = this.getClass().getResourceAsStream("/import-configs/input-config.xml");
      FileOutputStream os = new FileOutputStream(configFile);
      IOUtils.copy(is, os);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return configFile;
  }

  public File getDataCsvFile(MetaFile dataFile) {

    File csvFile = null;
    try {
      File tempDir = Files.createTempDir();
      csvFile = new File(tempDir, "eventRegistration.csv");
      Files.copy(MetaFiles.getPath(dataFile).toFile(), csvFile);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return csvFile;
  }

  @Override
  public Event sendEmail(Event event) {

    Template template = templateRepository.findByName("event");

    if (event.getEventRegistrationList() != null) {

      List<EventRegistration> eventRegistrationList = event.getEventRegistrationList();
      for (EventRegistration eventRegistration : eventRegistrationList) {
        try {
          Message message = templateService.generateAndSendMessage(eventRegistration, template);
          if (message != null) {
            eventRegistration.setIsEmailSend(true);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        event.setEventRegistrationList(eventRegistrationList);
      }
    }
    return event;
  }
}
