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

import com.eds.Helpers.QueryStringParameterHelper;

public class SearchResponse {

	private ArrayList<QueryWithAction> queryList = new ArrayList<QueryWithAction>();
	private QueryWithAction query;
	private String queryString;
	private String hits;
	private String searchTime;
	private ApiErrorMessage apierrormessage;
	private ArrayList<Result> resultsList = new ArrayList<Result>();
	private ArrayList<Facet> facetsList = new ArrayList<Facet>();
	private ArrayList<Result> researchStartersList = new ArrayList<Result>();
	private ArrayList<LimiterWithAction> selectedLimiterList = new ArrayList<LimiterWithAction>();
	private ArrayList<ExpandersWithAction> expanderwithActionList = new ArrayList<ExpandersWithAction>();
	private ArrayList<FacetFilterWithAction> facetfiltersList = new ArrayList<FacetFilterWithAction>();

	public ApiErrorMessage getApierrormessage() {
		return apierrormessage;
	}

	public void setApierrormessage(ApiErrorMessage apierrormessage) {
		this.apierrormessage = apierrormessage;
	}

	public ArrayList<LimiterWithAction> getSelectedLimiterList() {
		return selectedLimiterList;
	}

	public void setSelectedLimiterList(
			ArrayList<LimiterWithAction> selectedLimiterList) {
		this.selectedLimiterList = selectedLimiterList;
	}

	public ArrayList<ExpandersWithAction> getExpanderwithActionList() {
		return expanderwithActionList;
	}

	public void setExpanderwithActionList(
			ArrayList<ExpandersWithAction> expanderwithActionList) {
		this.expanderwithActionList = expanderwithActionList;
	}

	public ArrayList<FacetFilterWithAction> getFacetfiltersList() {
		return facetfiltersList;
	}

	public void setFacetfiltersList(
			ArrayList<FacetFilterWithAction> facetfiltersList) {
		this.facetfiltersList = facetfiltersList;
	}

	public QueryWithAction getQuery() {
		return query;
	}

	public void setQuery(QueryWithAction query) {
		this.query = query;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getHits() {
		return hits;
	}

	public String getSearchTime() {
		return searchTime;
	}

	public void setSearchTime(String searchTime) {
		this.searchTime = searchTime;
	}

	public void setHits(String hits) {
		this.hits = hits;
	}

	public ArrayList<Result> getResultsList() {
		return resultsList;
	}

	public void setResultsList(ArrayList<Result> resultsList) {
		this.resultsList = resultsList;
	}

	public ArrayList<Facet> getFacetsList() {
		return facetsList;
	}

	public void setFacetsList(ArrayList<Facet> facetsList) {
		this.facetsList = facetsList;
	}

	public ArrayList<QueryWithAction> getQueryList() {
		return this.queryList;
	}

	public void setQueryList(ArrayList<QueryWithAction> queryList) {
		this.queryList = queryList;
	}

	public String getSelectedPageNumber() {
		return QueryStringParameterHelper.GetPageNumber(this.queryString);
	}

	public String getSelectedResultsPerPage() {
		return QueryStringParameterHelper.GetResultPerPage(this.queryString);
	}

	public String getSelectedView() {
		return QueryStringParameterHelper.GetView(this.queryString);
	}

	public String getSelectedSort() {
		return QueryStringParameterHelper.GetSort(this.queryString);
	}

	public ArrayList<Result> getRecordsList() {
		return researchStartersList;
	}

	public void setRecordsList(ArrayList<Result> recordsList) {
		this.researchStartersList = recordsList;
	}
}
