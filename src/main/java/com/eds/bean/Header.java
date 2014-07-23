package com.eds.bean;

public class Header {
	private String dbId;
	private String dbLabel;
	private int aN;
	private int relevancyScore;
	private String pubType;
	private String pubTypeId;
	public String getDbId() {
		return dbId;
	}
	public void setDbId(String dbId) {
		this.dbId = dbId;
	}
	public String getDbLabel() {
		return dbLabel;
	}
	public void setDbLabel(String dbLabel) {
		this.dbLabel = dbLabel;
	}
	public int getRelevancyScore() {
		return relevancyScore;
	}
	public void setRelevancyScore(int relevancyScore) {
		this.relevancyScore = relevancyScore;
	}
	public int getaN() {
		return aN;
	}
	public void setaN(int aN) {
		this.aN = aN;
	}
	public String getPubType() {
		return pubType;
	}
	public void setPubType(String pubType) {
		this.pubType = pubType;
	}
	public String getPubTypeId() {
		return pubTypeId;
	}
	public void setPubTypeId(String pubTypeId) {
		this.pubTypeId = pubTypeId;
	}
}
