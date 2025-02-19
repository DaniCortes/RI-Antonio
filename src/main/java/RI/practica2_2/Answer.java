package RI.practica2_2;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

public class Answer {

	@CsvBindByName(column = "Id")
	private String id;

	@CsvBindByName(column = "OwnerUserId")
	private String ownerUserId;

	@CsvCustomBindByName(column = "CreationDate", converter = DateToEpochConverter.class)
	private Long creationDate;

	@CsvBindByName(column = "ParentId")
	private String parentId;

	@CsvBindByName(column = "Score")
	private Integer score;

	@CsvBindByName(column = "IsAcceptedAnswer")
	private String isAcceptedAnswer;

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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getIsAcceptedAnswer() {
		return isAcceptedAnswer;
	}

	public void setIsAcceptedAnswer(String isAcceptedAnswer) {
		this.isAcceptedAnswer = isAcceptedAnswer;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String toStringAnswer() {
		return "Answer {" + "id='" + id + '\'' + ", ownerUserId='" + ownerUserId + '\'' + ", creationDate="
				+ creationDate + ", parentId='" + parentId + '\'' + ", score=" + score + ", isAcceptedAnswer='"
				+ isAcceptedAnswer + '\'' + ", body='" + body + '\'' + '}';
	}

}