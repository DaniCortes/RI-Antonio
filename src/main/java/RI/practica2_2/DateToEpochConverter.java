package RI.practica2_2;

import java.time.Instant;

import com.opencsv.bean.AbstractBeanField;

public class DateToEpochConverter extends AbstractBeanField<Long, String> {

	@Override
	public Long convert(String date_str) {
		if (date_str == null || date_str.trim().isEmpty()) {
			return null;
		}
		return Instant.parse(date_str).toEpochMilli();
	}

}
