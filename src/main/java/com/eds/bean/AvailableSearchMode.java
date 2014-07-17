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

public class AvailableSearchMode {
	private String mode;
	private String label;
	private String defaultOn;
	private String addAction;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAddAction() {
		return addAction;
	}

	public void setAddAction(String addAction) {
		this.addAction = addAction;
	}

	public String getDefaultOn() {
		return defaultOn;
	}

	public void setDefaultOn(String defaultOn) {
		this.defaultOn = defaultOn;
	}
}
