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

public class AvailableLimiter {

	private String Id;
	private String Label;
	private String Type;
	private String AddAction;
	private String order;
	private String defaultOn;

	private ArrayList<LimiterValue> limiterValues = new ArrayList<LimiterValue>();

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getLabel() {
		return Label;
	}

	public void setLabel(String label) {
		Label = label;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getAddAction() {
		if (0 == this.getType().compareTo("select"))
			return AddAction.replace("value", "y");
		return AddAction;
	}

	public void setAddAction(String addAction) {
		AddAction = addAction;
	}

	public ArrayList<LimiterValue> getLimitervalues() {
		return limiterValues;
	}

	public void setLimitervalues(ArrayList<LimiterValue> limitervalues) {
		this.limiterValues = limitervalues;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getDefaultOn() {
		return defaultOn;
	}

	public void setDefaultOn(String defaultOn) {
		this.defaultOn = defaultOn;
	}

}
