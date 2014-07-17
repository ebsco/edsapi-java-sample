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

public class Info {

	private ViewResultSettings viewResultSettings;
	private ArrayList<AvailableSort> availableSortsList;
	private ArrayList<AvailableSearchField> availableSearchFieldsList;
	private ArrayList<AvailableExpander> availableExpandersList;
	private ArrayList<AvailableLimiter> availableLimitersList;
	private ArrayList<AvailableSearchMode> availableSearchModeList;

	private ApiErrorMessage errorMessage;

	public ArrayList<AvailableSort> getAvailableSortsList() {
		return availableSortsList;
	}

	public void setAvailableSortsList(
			ArrayList<AvailableSort> availableSortsList) {
		this.availableSortsList = availableSortsList;
	}

	public ArrayList<AvailableSearchField> getAvailableSearchFieldsList() {
		return availableSearchFieldsList;
	}

	public void setAvailableSearchFieldsList(
			ArrayList<AvailableSearchField> availableSearchFieldsList) {
		this.availableSearchFieldsList = availableSearchFieldsList;
	}

	public ArrayList<AvailableExpander> getAvailableExpandersList() {
		return availableExpandersList;
	}

	public void setAvailableExpandersList(
			ArrayList<AvailableExpander> availableExpandersList) {
		this.availableExpandersList = availableExpandersList;
	}

	public ArrayList<AvailableLimiter> getAvailableLimitersList() {
		return availableLimitersList;
	}

	public void setAvailableLimitersList(
			ArrayList<AvailableLimiter> availableLimitersList) {
		this.availableLimitersList = availableLimitersList;
	}

	public ApiErrorMessage getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(ApiErrorMessage errorMessage) {
		this.errorMessage = errorMessage;
	}

	public ViewResultSettings getViewResultSettings() {
		return this.viewResultSettings;
	}

	public void setViewResultSettings(ViewResultSettings viewResultSettings) {
		this.viewResultSettings = viewResultSettings;
	}

	public ArrayList<AvailableSearchMode> getAvailableSearchModeList() {
		return availableSearchModeList;
	}

	public void setAvailableSearchModeList(
			ArrayList<AvailableSearchMode> availableSearchModeList) {
		this.availableSearchModeList = availableSearchModeList;
	}

}
