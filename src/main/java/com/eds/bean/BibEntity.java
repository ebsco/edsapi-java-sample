package com.eds.bean;

import java.util.List;

public class BibEntity {
	private List<Subject> subjectList;
	private List<Title> titleList;
	private List<Date> datesList;
	public List<Subject> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(List<Subject> subjectList) {
		this.subjectList = subjectList;
	}
	public List<Title> getTitleList() {
		return titleList;
	}
	public void setTitleList(List<Title> titleList) {
		this.titleList = titleList;
	}
	public List<Date> getDatesList() {
		return datesList;
	}
	public void setDatesList(List<Date> datesList) {
		this.datesList = datesList;
	}
}
