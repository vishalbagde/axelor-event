package com.axelor.event.service;

import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EventServiceImpl implements EventService {

	@Override
	public Event computeTotal(Event event) {

		Integer totalEntry = 0;
		BigDecimal totalCollection = BigDecimal.ZERO;
		BigDecimal totalDiscount = BigDecimal.ZERO;

		if (event.getEventRegistrationList() != null) {
			totalEntry = event.getEventRegistrationList().size();
			totalCollection = event.getEventRegistrationList().stream().map(x -> x.getAmount()).reduce(BigDecimal.ZERO,
					BigDecimal::add);
			totalDiscount = event.getEventFees().multiply(new BigDecimal(totalEntry)).subtract(totalCollection);

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
						event.getEventFees().multiply(discount.getDiscountPercent()).divide(new BigDecimal(100)));
				updatedDiscountList.add(discount);
			}
			event.setDiscountList(updatedDiscountList);
		}

		event = computeTotal(event);
		return event;
	}
}
