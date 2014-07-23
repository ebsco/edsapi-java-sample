package com.eds.bean;

import com.eds.Helpers.TransDataToHTML;

public class CoverArt {
	private String size;
	private String target;
	public CoverArt()
	{
		
	}
	public CoverArt(String size, String target)
	{
		this.size = size;
		this.target = target;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		target = TransDataToHTML.transDataToHTML(target);
		this.target = target;
	}
}
