package com.axelor.event.service;

import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.Template;
import com.axelor.apps.message.db.repo.TemplateRepository;
import com.axelor.apps.message.service.TemplateMessageService;
import com.axelor.common.FileUtils;
import com.axelor.data.ImportTask;
import com.axelor.data.csv.CSVImporter;
import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.axelor.event.db.repo.EventRegistrationRepository;
import com.axelor.event.db.repo.EventRepository;
import com.axelor.inject.Beans;
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

	@Inject
	TemplateMessageService templateService;

	@Inject
	MetaFileRepository metaFileRepository;

	@Inject
	TemplateRepository templateRepository;

	@Inject
	EventRegistrationRepository eventRegistrationRepository;

	@Override
	public Event computeTotal(Event event) {

		Integer totalEntry = 0;
		BigDecimal totalCollection = BigDecimal.ZERO;
		BigDecimal totalDiscount = BigDecimal.ZERO;

		List<EventRegistration> eventRegistrationList = event.getEventRegistrationList();

		if (eventRegistrationList != null) {
			totalEntry = eventRegistrationList.size();
			totalCollection = eventRegistrationList.stream().map(x -> x.getAmount()).reduce(BigDecimal.ZERO,
					BigDecimal::add);
			totalDiscount = event.getEventFees().multiply(new BigDecimal(totalEntry)).subtract(totalCollection);

			event.setAmountCollected(totalCollection);
			event.setTotalDiscount(totalDiscount);
			event.setTotalEntry(eventRegistrationList.size());
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
						event.getEventFees().multiply(discount.getDiscountPercent()).divide(new BigDecimal(100)));
				updatedDiscountList.add(discount);
			}
			event.setDiscountList(updatedDiscountList);
		}

		event = computeTotal(event);
		return event;
	}

	public void importCsvInEventRegistration(MetaFile metaFile, Integer event_id) {
		
		File configFile = this.getConfigFile();
		File csvFile = this.getDataCsvFile(metaFile);

		CSVImporter importer = new CSVImporter(configFile.getAbsolutePath());
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("event_id", event_id.longValue());
		Event event = Beans.get(EventRepository.class).find(Long.parseLong(event_id.toString()));
		context.put("reg_list_size", event.getEventRegistrationList().size());

		importer.setContext(context);

		importer.run(new ImportTask() {
			@Override
			public void configure() throws IOException {
				input("[event_registration]", csvFile);
			}
		});

		deleteTempFile(configFile);
		deleteTempFile(csvFile);
		
		removeMetaFile(metaFile);
		// metaFileRepository.remove(metaFile);
	}

	@Transactional
	public void removeMetaFile(MetaFile metaFile) {
		metaFileRepository.remove(metaFile);
	}

	private void deleteTempFile(File file) {
		try {
			if (file.isDirectory()) {
				FileUtils.deleteDirectory(file);
			} else {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	@Transactional
	@Override
	public void sendEmail(Event event) {

		Template template = templateRepository.findByName("event");

		if (template != null) {
			List<EventRegistration> eventRegistrationList = event.getEventRegistrationList();
			if (eventRegistrationList != null) {

				for (EventRegistration eventRegistration : eventRegistrationList) {
					try {
						if (eventRegistration.getEmail() != null) {
							Message message = templateService.generateAndSendMessage(eventRegistration, template);
							if (message != null) {
								eventRegistration.setIsEmailSend(true);
								eventRegistrationRepository.save(eventRegistration);
							}
						} else {
							System.err.println("Email Id not found");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			System.err.println("Template Not Found Please set Template");
		}
	}
}
