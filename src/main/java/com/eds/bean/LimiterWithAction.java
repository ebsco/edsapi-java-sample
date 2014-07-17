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

public class LimiterWithAction {

	// Here we should build LimiterValuesWithAction, but Currently we do not
	// need it

	private String Id;
	private String RemoveAction;
	private ArrayList<LimiterValueWithAction> lvalist;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getRemoveAction() {
		return RemoveAction;
	}

	public void setRemoveAction(String removeAction) {
		RemoveAction = removeAction;
	}

	public ArrayList<LimiterValueWithAction> getLvalist() {
		return lvalist;
	}

	public void setLvalist(ArrayList<LimiterValueWithAction> lvalist) {
		this.lvalist = lvalist;
	}

}
