/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eds.bean;

import java.util.ArrayList;
import java.util.Map;

public class Result {

	private String resultId;
	private String pubType;
	private String dbId;
	private String dbLabel;
	private String an;
	private String pLink;
	private String pdfAvailable;
	private String htmlAvailable;
	private String pubTypeID;
	private String pdfLink;
	private String htmlFullText;
	private String RelevancyScore;
	private ImageInfo imageInfo;
	private ArrayList<BookJacket> bookJacketList = new ArrayList<BookJacket>();
	private ArrayList<CustomLink> CustomLinkList = new ArrayList<CustomLink>();
	private ArrayList<Link> otherFullTextLinks = new ArrayList<Link>();
	private ArrayList<Item> itemList = new ArrayList<Item>();
	

	public String getPubTypeID() {
		return pubTypeID;
	}

	public void setPubTypeID(String pubTypeID) {
		this.pubTypeID = pubTypeID;
	}

	public String getResultId() {
		return resultId;
	}

	public void setResultId(String resultId) {
		this.resultId = resultId;
	}

	public String getPubType() {
		return pubType;
	}

	public void setPubType(String pubType) {
		this.pubType = pubType;
	}

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

	public String getAn() {
		return an;
	}

	public void setAn(String an) {
		this.an = an;
	}

	public String getpLink() {
		return pLink;
	}

	public void setpLink(String pLink) {
		this.pLink = pLink;
	}

	public String getPdfAvailable() {
		return pdfAvailable;
	}

	public void setPdfAvailable(String pDF) {
		pdfAvailable = pDF;
	}

	public String getHtmlAvailable() {
		return htmlAvailable;
	}

	public void setHtmlAvailable(String hTML) {
		htmlAvailable = hTML;
	}

	public ArrayList<BookJacket> getBookJacketList() {
		return bookJacketList;
	}

	public void setBookJacketList(ArrayList<BookJacket> bookJacketList) {
		this.bookJacketList = bookJacketList;
	}

	public ArrayList<CustomLink> getCustomLinkList() {
		return CustomLinkList;
	}

	public void setCustomLinkList(ArrayList<CustomLink> customLinkList) {
		CustomLinkList = customLinkList;
	}

	public ArrayList<Item> getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList<Item> itemList) {
		this.itemList = itemList;
	}

	public String getHtmlFullText() {
		return htmlFullText;
	}

	public void setHtmlFullText(String htmlFullText) {
		this.htmlFullText = htmlFullText;
	}


	public String getPdfLink() {
		return pdfLink;
	}

	public void setPdfLink(String pdfLink) {
		this.pdfLink = pdfLink;
	}

	public ArrayList<Link> getOtherFullTextLinks() {
		return otherFullTextLinks;
	}

	public void setOtherFullTextLinks(ArrayList<Link> otherFullTextLinks) {
		this.otherFullTextLinks = otherFullTextLinks;
	}


	public ImageInfo getImageInfo() {
		return imageInfo;
	}

	public void setImageInfo(ImageInfo imageInfo) {
		this.imageInfo = imageInfo;
	}

	public String getRelevancyScore() {
		return RelevancyScore;
	}

	public void setRelevancyScore(String relevancyScore) {
		RelevancyScore = relevancyScore;
	}



}
