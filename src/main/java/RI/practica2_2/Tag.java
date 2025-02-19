package RI.practica2_2;

import com.opencsv.bean.CsvBindByName;

public class Tag {

	@CsvBindByName(column = "Id")
	private String id;

	@CsvBindByName(column = "Tag")
	private String tag;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}