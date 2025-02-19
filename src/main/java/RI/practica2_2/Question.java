package RI.practica2_2;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

public class Question {

	@CsvBindByName(column = "Id")
	private String id;

	@CsvBindByName(column = "OwnerUserId")
	private String ownerUserId;

	@CsvCustomBindByName(column = "CreationDate", converter = DateToEpochConverter.class)
	private Long creationDate;

	@CsvBindByName(column = "Score")
	private Integer score;

	@CsvBindByName(column = "Title")
	private String title;

	@CsvBindByName(column = "Body")
	private String body;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwnerUserId() {
		return ownerUserId;
	}

	public void setOwnerUserId(String ownerUserId) {
		this.ownerUserId = ownerUserId;
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String toString() {
		return "Question {" + "id='" + id + '\'' + ", ownerUserId='" + ownerUserId + '\'' + ", creationDate="
				+ creationDate + ", score=" + score + ", title='" + title + '\'' + ", body='" + body + '\'' + '}';
	}

}