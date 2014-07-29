/* This class processes JSON formatted messages to send and received from the
 * EDS API and AuthService
 *
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
package com.eds.Model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import com.eds.Helpers.TransDataToHTML;
import com.eds.bean.ApiErrorMessage;
import com.eds.bean.AuthToken;
import com.eds.bean.AvailableExpander;
import com.eds.bean.AvailableLimiter;
import com.eds.bean.AvailableRelatedContent;
import com.eds.bean.AvailableSearchField;
import com.eds.bean.AvailableSort;
import com.eds.bean.BookJacket;
import com.eds.bean.CoverArt;
import com.eds.bean.CustomLink;
import com.eds.bean.EachFacetValue;
import com.eds.bean.ExpandersWithAction;
import com.eds.bean.Facet;
import com.eds.bean.FacetFilterWithAction;
import com.eds.bean.FacetValue;
import com.eds.bean.FacetValueWithAction;
import com.eds.bean.ImageInfo;
import com.eds.bean.Info;
import com.eds.bean.Item;
import com.eds.bean.LimiterValue;
import com.eds.bean.LimiterValueWithAction;
import com.eds.bean.LimiterWithAction;
import com.eds.bean.Link;
import com.eds.bean.QueryWithAction;
import com.eds.bean.Result;
import com.eds.bean.RetrieveResponse;
import com.eds.bean.SearchResponse;
import com.eds.bean.ServiceResponse;
import com.eds.bean.SessionToken;
import com.eds.bean.ViewResultSettings;

public class JSONProcessor implements IMessageProcessor {

	private static final String _contentType = "application/json";
	public static final String HTTP_BAD_REQUEST = "400";

	public String GetContentType() {
		return _contentType;
	}

	public void SetContentHeaders(URLConnection connection) {
		connection.setRequestProperty("Accept", _contentType);
		connection.setRequestProperty("Content-Type", _contentType);
	}

	public AuthToken ProcessUIDAuthResponse(ServiceResponse response) {
		BufferedReader reader = response.getReader();
		AuthToken authtoken = new AuthToken();
		// if there was an error, process the error and then return
		String errorStream = response.getErrorStream();
		if (null != errorStream && !errorStream.isEmpty()) {
			ApiErrorMessage errorMessage = ProcessAuthErrors(
					response.getErrorNumber(), errorStream);
			authtoken.setErrorMessage(errorMessage);
			return authtoken;
		}
		try {
			JSONObject object = (JSONObject) new JSONTokener(reader)
					.nextValue();
			authtoken.setAuthToken(object.getString("AuthToken"));
			authtoken.setAuthTimeout(object.getString("AuthTimeout"));
			authtoken.setCreationTime(Calendar.getInstance());
		} catch (JSONException e) {
			ApiErrorMessage errorMessage = new ApiErrorMessage();
			errorMessage
					.setErrorDescription("Error parsing auth response message");
			errorMessage.setDetailedErrorDescription(e.getMessage());
			authtoken.setErrorMessage(errorMessage);
		} catch (Exception e) {
			ApiErrorMessage errorMessage = new ApiErrorMessage();
			errorMessage.setErrorDescription("Error processing auth response");
			errorMessage.setDetailedErrorDescription(e.getMessage());
			authtoken.setErrorMessage(errorMessage);
		}
		return authtoken;
	}

	public SessionToken ProcessCreateSessionResponse(ServiceResponse response) {
		SessionToken sessionToken = new SessionToken();
		BufferedReader reader = response.getReader();
		if (null != response.getErrorStream()
				&& !response.getErrorStream().isEmpty()) {
			sessionToken.setApiErrorMessage(ProcessErrors(
					response.getErrorNumber(), response.getErrorStream()));
			return sessionToken;
		}
		JSONObject object = null;
		try {
			JSONTokener jsonTokener = new JSONTokener(reader);
			if (null != jsonTokener)
				object = (JSONObject) jsonTokener.nextValue();
			if (null != object)
				sessionToken.setSessionToken(object.getString("SessionToken"));
		} catch (JSONException e) {
			ApiErrorMessage errorMessage = new ApiErrorMessage();
			errorMessage
					.setErrorDescription("Error parsing JSON formatted create session response");
			sessionToken.setApiErrorMessage(errorMessage);
		}
		return sessionToken;
	}

	public SearchResponse ProcessSearchResponse(ServiceResponse serviceResponse) {

		JSONObject object = null;
		SearchResponse searchResponse = new SearchResponse();
		BufferedReader reader = null;
		try {
			// if there was an error, process the error and then return
			if (null != serviceResponse.getErrorStream()
					&& !serviceResponse.getErrorStream().isEmpty()) {

				ApiErrorMessage errorMessage = ProcessErrors(
						serviceResponse.getErrorNumber(),
						serviceResponse.getErrorStream());
				searchResponse.setApierrormessage(errorMessage);
				return searchResponse;
			}
			reader = serviceResponse.getReader();
			object = (JSONObject) new JSONTokener(reader).nextValue();
			// Here we set result list's attribute QueryString

			JSONObject searchRequestGet = object
					.getJSONObject("SearchRequestGet");

			// process the querystring
			String queryString = searchRequestGet.getString("QueryString");
			searchResponse.setQueryString(queryString);

			// here parse resultsList with action
			JSONObject searchCriteriaWithActions = searchRequestGet
					.getJSONObject("SearchCriteriaWithActions");

			// process the search criteria
			if (null != searchCriteriaWithActions) {
				JSONArray jsonQueriesWithActions = searchCriteriaWithActions
						.getJSONArray("QueriesWithAction");
				ArrayList<QueryWithAction> queriesWithActions = new ArrayList<QueryWithAction>();
				if (null != jsonQueriesWithActions) {
					for (int i = 0; i < jsonQueriesWithActions.length(); i++) {
						JSONObject querywaJson = jsonQueriesWithActions
								.getJSONObject(i);
						if (null != querywaJson) {
							JSONObject queryJson = querywaJson
									.getJSONObject("Query");
							if (null != queryJson) {
								QueryWithAction qwa = new QueryWithAction();
								qwa.setTerm(queryJson.getString("Term"));
								qwa.setOperator(queryJson
										.optString("BooleanOperator"));
								qwa.setFieldCode(queryJson
										.optString("FieldCode"));
								qwa.setRemoveAction(querywaJson
										.optString("RemoveAction"));
								queriesWithActions.add(qwa);
							}
						}
					}
				}
				if (!queriesWithActions.isEmpty()) {
					searchResponse.setQueryList(queriesWithActions);
					searchResponse.setQuery(searchResponse.getQueryList()
							.get(0));
				}

			}

			JSONArray limitersWithAction = searchCriteriaWithActions
					.optJSONArray("LimitersWithAction");
			// process applied limiters, expanders, and facets
			if (null != limitersWithAction && 0 < limitersWithAction.length()) {
				ArrayList<LimiterWithAction> limiterList = new ArrayList<LimiterWithAction>();
				for (int i = 0; i < limitersWithAction.length(); i++) {
					JSONObject limiterJson = limitersWithAction
							.getJSONObject(i);
					LimiterWithAction lwa = new LimiterWithAction();
					String id = limiterJson.getString("Id");
					String RemoveAction = limiterJson.optString("RemoveAction");
					lwa.setId(id);
					lwa.setRemoveAction(RemoveAction);

					JSONArray limiterValuesWithAction = limiterJson
							.optJSONArray("LimiterValuesWithAction");
					if (null != limiterValuesWithAction) {
						ArrayList<LimiterValueWithAction> lvalist = new ArrayList<LimiterValueWithAction>();
						for (int j = 0; j < limiterValuesWithAction.length(); j++) {
							LimiterValueWithAction lvwa = new LimiterValueWithAction();
							JSONObject LimiterValueWithAction = (JSONObject) limiterValuesWithAction
									.get(j);
							String Eachvalue = LimiterValueWithAction
									.getString("Value");
							String EachRemoveAction = LimiterValueWithAction
									.optString("RemoveAction");

							lvwa.setValue(Eachvalue);
							lvwa.setRemoveAction(EachRemoveAction);
							lvalist.add(lvwa);
						}
						lwa.setLvalist(lvalist);
					}
					limiterList.add(lwa);
				}
				searchResponse.setSelectedLimiterList(limiterList);

			}

			// Here we address ExpandersWithAction
			JSONArray expandersWithAction = searchCriteriaWithActions
					.optJSONArray("ExpandersWithAction");
			if (null != expandersWithAction) {
				ArrayList<ExpandersWithAction> expanderList = new ArrayList<ExpandersWithAction>();
				for (int i = 0; i < expandersWithAction.length(); i++) {
					JSONObject expanderJson = expandersWithAction
							.getJSONObject(i);
					ExpandersWithAction ewa = new ExpandersWithAction();

					String id = expanderJson.getString("Id");
					String RemoveAction = expanderJson
							.optString("RemoveAction");
					ewa.setId(id);
					ewa.setRemoveAction(RemoveAction);
					expanderList.add(ewa);
				}
				searchResponse.setExpanderwithActionList(expanderList);
			}

			// Here we address FacetFilterWithAction

			JSONArray facetFiltersWithActionJson = searchCriteriaWithActions
					.optJSONArray("FacetFiltersWithAction");
			if (null != facetFiltersWithActionJson) {
				ArrayList<FacetFilterWithAction> facetFilterList = new ArrayList<FacetFilterWithAction>();
				for (int i = 0; i < facetFiltersWithActionJson.length(); i++) {
					JSONObject facetFilterWithActionJson = facetFiltersWithActionJson
							.getJSONObject(i);
					FacetFilterWithAction facetWithAction = new FacetFilterWithAction();

					String FilterId = facetFilterWithActionJson
							.getString("FilterId");

					String RemoveAction = facetFilterWithActionJson
							.optString("RemoveAction");

					facetWithAction.setFilterId(FilterId);
					facetWithAction.setRemoveAction(RemoveAction);

					ArrayList<FacetValueWithAction> facetvaluewithactionlist = new ArrayList<FacetValueWithAction>();

					JSONArray facetValuesWithActionArray = facetFilterWithActionJson
							.getJSONArray("FacetValuesWithAction");
					for (int j = 0; j < facetValuesWithActionArray.length(); j++) {
						JSONObject facetValuesWithActionJson = (JSONObject) facetValuesWithActionArray
								.get(j);
						FacetValueWithAction fvwa = new FacetValueWithAction();
						String removeaction = facetValuesWithActionJson
								.getString("RemoveAction");

						JSONObject eachFacetValueJson = facetValuesWithActionJson
								.optJSONObject("FacetValue");
						if (null != eachFacetValueJson) {
							String id = eachFacetValueJson.getString("Id");
							String Value = eachFacetValueJson
									.getString("Value");

							EachFacetValue efv = new EachFacetValue();
							efv.setId(id);
							efv.setValue(Value);
							fvwa.setEachfacetvalue(efv);
						}
						fvwa.setRemoveAction(removeaction);
						facetvaluewithactionlist.add(fvwa);
					}
					facetWithAction
							.setFacetvaluewithaction(facetvaluewithactionlist);

					facetFilterList.add(facetWithAction);

				}
				searchResponse.setFacetfiltersList(facetFilterList);
			}
			// Research Starter Placard Data

			JSONObject searchResult = object.getJSONObject("SearchResult");

			if (searchResult.has("RelatedContent")
					&& !searchResult.isNull("RelatedContent")) {
				JSONObject relatedContent = searchResult
						.getJSONObject("RelatedContent");
				JSONArray relatedRecords = relatedContent
						.getJSONArray("RelatedRecords");
				ArrayList<Result> researchStarterRecords = new ArrayList<Result>();
				for (int i = 0; i < relatedRecords.length(); i++) {
					JSONObject currentRelatedRecord = relatedRecords
							.getJSONObject(i);

					JSONArray researchStarters = currentRelatedRecord
							.getJSONArray("Records");

					for (int e = 0; e < researchStarters.length(); e++) {
						JSONObject currentResearchStarter = researchStarters
								.getJSONObject(e);
						Result aRecord = new Result();
						aRecord.setResultId(currentResearchStarter
								.getString("ResultId"));
						JSONObject JSONheader = currentResearchStarter
								.getJSONObject("Header");
						aRecord.setDbId(JSONheader.getString("DbId"));
						aRecord.setDbLabel(JSONheader.getString("DbLabel"));
						aRecord.setAn(JSONheader.getString("An"));
						aRecord.setRelevancyScore(JSONheader
								.optString("RelevancyScore"));
						aRecord.setPubTypeID(JSONheader.getString("PubTypeId"));
						aRecord.setpLink(currentResearchStarter
								.getString("PLink"));
						if (!currentResearchStarter.isNull("ImageInfo")
								&& currentResearchStarter.has("ImageInfo")) {
							JSONArray JSONImageInfo = currentResearchStarter
									.getJSONArray("ImageInfo");
							ImageInfo imageInfo = new ImageInfo();
							for (int q = 0; q < JSONImageInfo.length(); q++) {
								JSONObject currentImageInfo = JSONImageInfo
										.getJSONObject(q);
								CoverArt coverArt = new CoverArt();
								coverArt.setSize(currentImageInfo
										.getString("Size"));
								coverArt.setTarget(currentImageInfo
										.getString("Target"));
								imageInfo.setCoverArt(coverArt);
							}
							aRecord.setImageInfo(imageInfo);
						}
						JSONObject JSONFullText = currentResearchStarter
								.getJSONObject("FullText");

						JSONObject JSONText = JSONFullText
								.getJSONObject("Text");

						aRecord.setHtmlAvailable(JSONText
								.getString("Availability"));

						JSONArray JSONItems = currentResearchStarter
								.getJSONArray("Items");
						ArrayList<Item> itemsList = new ArrayList< Item>();
						for (int z = 0; z < JSONItems.length(); z++) {
							JSONObject currentJSONItem = JSONItems
									.getJSONObject(z);
							Item currentItem = new Item();
							currentItem.setLabel(currentJSONItem
									.getString("Label"));
							currentItem.setGroup(currentJSONItem
									.getString("Group"));
							currentItem.setData(currentJSONItem
									.getString("Data"));

							itemsList.add(
									currentItem);
						}
						aRecord.setItemList(itemsList);
						researchStarterRecords.add(aRecord);
					}
					searchResponse.setRecordsList(researchStarterRecords);

				}

			}

			// the end of results with end action

			// Here we set result list's attribute TotalHits and
			// TotalSearchTime

			JSONObject statistics = searchResult.getJSONObject("Statistics");

			String totalHits = statistics.getString("TotalHits");
			String totalSearchTime = statistics.getString("TotalSearchTime");
			searchResponse.setHits(totalHits);
			searchResponse.setSearchTime(totalSearchTime);

			// Here we set result list's attribute result list;

			ArrayList<Result> resultlist = new ArrayList<Result>();
			JSONObject data = searchResult.optJSONObject("Data");
			if (null != data) {

				JSONArray Records = data.optJSONArray("Records");
				if (null != Records) {
					for (int i = 0; i < Records.length(); i++) {

						Result result = this.constructRecord(Records
								.getJSONObject(i));
						resultlist.add(result);
					}
				}
			}
			searchResponse.setResultsList(resultlist);

			// Here we set result list's attribute facetsList

			ArrayList<Facet> facetsList = new ArrayList<Facet>();
			if (searchResult.has("AvailableFacets")
					&& !searchResult.isNull("AvailableFacets")) {
				JSONArray availableFacets = searchResult
						.getJSONArray("AvailableFacets");
				if (null != availableFacets) {
					for (int i = 0; i < availableFacets.length(); i++) {
						JSONObject availableFacet = availableFacets
								.getJSONObject(i);
						Facet facet = new Facet();
						String Id = availableFacet.getString("Id");
						String Label = availableFacet.getString("Label");

						facet.setId(Id);
						facet.setLabel(Label);
						JSONArray availableFacetValues = availableFacet
								.getJSONArray("AvailableFacetValues");
						ArrayList<FacetValue> facetsValueList = new ArrayList<FacetValue>();
						for (int j = 0; j < availableFacetValues.length(); j++) {

							JSONObject availableFacetValue = availableFacetValues
									.getJSONObject(j);
							FacetValue facetvalue = new FacetValue();

							String Value = availableFacetValue
									.getString("Value");
							String Count = availableFacetValue
									.getString("Count");
							String AddAction = availableFacetValue
									.optString("AddAction");

							facetvalue.setAddAction(AddAction);
							facetvalue.setCount(Count);
							facetvalue.setValue(Value);
							facetsValueList.add(facetvalue);
						}
						facet.setFacetsValueList(facetsValueList);
						facetsList.add(facet);
					}
					searchResponse.setFacetsList(facetsList);

				}
			}
		} catch (JSONException e) {
			ApiErrorMessage errorMessage = new ApiErrorMessage();
			errorMessage.setErrorDescription("Error parsing reponse message");
			errorMessage.setDetailedErrorDescription(e.getMessage());
			searchResponse.setApierrormessage(errorMessage);
		} catch (Exception e) {
			ApiErrorMessage errorMessage = new ApiErrorMessage();
			errorMessage
					.setErrorDescription("Error processing search response");
			errorMessage.setDetailedErrorDescription(e.getMessage());
			searchResponse.setApierrormessage(errorMessage);
		}
		return searchResponse;
	}

	public String BuildUIDAuthRequestMessage(String username, String password) {
		return "{\"UserId\":\"" + username + "\",\"Password\":\"" + password
				+ "\"}";
	}

	public RetrieveResponse ProcessRetrieveResponse(
			ServiceResponse serviceResponse) {
		RetrieveResponse retrieveResponse = new RetrieveResponse();
		// if there was an error, process the error and then return
		if (null != serviceResponse.getErrorStream()
				&& !serviceResponse.getErrorStream().isEmpty()) {

			ApiErrorMessage errorMessage = ProcessErrors(
					serviceResponse.getErrorNumber(),
					serviceResponse.getErrorStream());
			retrieveResponse.setApiErrorMessage(errorMessage);
			return retrieveResponse;
		} else {
			try {
				BufferedReader reader = serviceResponse.getReader();
				JSONObject object = (JSONObject) new JSONTokener(reader)
						.nextValue();
				// Here we address JSON object from the top layer this is JSON
				JSONObject jsonRecord = object.optJSONObject("Record");
				if (null != jsonRecord) {
					Result result = this.constructRecord(jsonRecord);
					retrieveResponse.setRecord(result);
				}
			} catch (JSONException e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error parsing reponse message");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				retrieveResponse.setApiErrorMessage(errorMessage);
			} catch (Exception e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing retrieve response");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				retrieveResponse.setApiErrorMessage(errorMessage);
			}
		}
		return retrieveResponse;
	}

	public Info ProcessInfoResponse(ServiceResponse response) {
		BufferedReader reader = response.getReader();
		Info info = new Info();
		if (null == reader)
			return info;
		JSONObject obj = null;
		try {
			obj = (JSONObject) new JSONTokener(reader).nextValue();

			// Process Available Search Criteria
			JSONObject AvailableSearchCriteria = obj
					.getJSONObject("AvailableSearchCriteria");

			// Parse AvailableSorts

			if (AvailableSearchCriteria.has("AvailableSorts")) {

				JSONArray AvailableSorts = AvailableSearchCriteria
						.getJSONArray("AvailableSorts");
				ArrayList<AvailableSort> availableSortList = new ArrayList<AvailableSort>();

				for (int i = 0; i < AvailableSorts.length(); i++) {

					JSONObject AvailableSort = (JSONObject) AvailableSorts
							.get(i);
					AvailableSort availableSort = new AvailableSort();
					String Id = AvailableSort.getString("Id");
					String Label = AvailableSort.getString("Label");
					String AddAction = AvailableSort.getString("AddAction");
					availableSort.setAddAction(AddAction);
					availableSort.setId(Id);
					availableSort.setLabel(Label);
					availableSortList.add(availableSort);

				}
				info.setAvailableSortsList(availableSortList);
			}

			// Parse AvailableSearchFields

			if (AvailableSearchCriteria.has("AvailableSearchFields")) {
				JSONArray AvailableSearchFieldList = AvailableSearchCriteria
						.getJSONArray("AvailableSearchFields");
				ArrayList<AvailableSearchField> availableSortList = new ArrayList<AvailableSearchField>();

				for (int i = 0; i < AvailableSearchFieldList.length(); i++) {

					JSONObject availableSearch = (JSONObject) AvailableSearchFieldList
							.get(i);
					AvailableSearchField availableSearchField = new AvailableSearchField();
					String FieldCode = availableSearch.getString("FieldCode");
					String Label = availableSearch.getString("Label");
					availableSearchField.setFieldCode(FieldCode);
					availableSearchField.setLabel(Label);
					availableSortList.add(availableSearchField);

				}

				info.setAvailableSearchFieldsList(availableSortList);
			}

			if (AvailableSearchCriteria.has("AvailableExpanders")) {

				JSONArray AvailableExpandersList = AvailableSearchCriteria
						.getJSONArray("AvailableExpanders");
				ArrayList<AvailableExpander> availableExpanderList = new ArrayList<AvailableExpander>();

				for (int i = 0; i < AvailableExpandersList.length(); i++) {

					JSONObject availableExpander = (JSONObject) AvailableExpandersList
							.get(i);
					AvailableExpander availableexpander = new AvailableExpander();
					String Id = availableExpander.getString("Id");
					String Label = availableExpander.getString("Label");
					String AddAction = availableExpander.getString("AddAction");

					availableexpander.setAddAction(AddAction);
					availableexpander.setId(Id);
					availableexpander.setLabel(Label);

					availableExpanderList.add(availableexpander);

				}

				info.setAvailableExpandersList(availableExpanderList);
			}

			if (AvailableSearchCriteria.has("AvailableRelatedContent")) {
				JSONArray AvailableRelatedContent = AvailableSearchCriteria
						.getJSONArray("AvailableRelatedContent");
				ArrayList<AvailableRelatedContent> availableRelatedContentList = new ArrayList<AvailableRelatedContent>();
				for (int p = 0; p < AvailableRelatedContent.length(); p++) {
					JSONObject availableRelatedContent = (JSONObject) AvailableRelatedContent
							.get(p);
					AvailableRelatedContent currentContent = new AvailableRelatedContent();
					currentContent.setType(availableRelatedContent
							.getString("Type"));
					currentContent.setLabel(availableRelatedContent
							.getString("Label"));
					currentContent.setDefaultOn(availableRelatedContent
							.getString("DefaultOn"));
					currentContent.setAddAction(availableRelatedContent
							.getString("AddAction"));
					availableRelatedContentList.add(currentContent);
				}
				info.setAvailableRelatedContent(availableRelatedContentList);

			}

			if (AvailableSearchCriteria.has("AvailableLimiters")) {

				JSONArray AvailableLimiters = AvailableSearchCriteria
						.getJSONArray("AvailableLimiters");
				ArrayList<AvailableLimiter> availableLimiterList = new ArrayList<AvailableLimiter>();

				for (int i = 0; i < AvailableLimiters.length(); i++) {

					JSONObject availablLimiter = (JSONObject) AvailableLimiters
							.get(i);
					ArrayList<LimiterValue> list = new ArrayList<LimiterValue>();

					AvailableLimiter availablelimiter = new AvailableLimiter();

					String Id = availablLimiter.getString("Id");
					String Label = availablLimiter.getString("Label");
					String Type = availablLimiter.getString("Type");

					availablelimiter.setId(Id);
					availablelimiter.setLabel(Label);
					availablelimiter.setType(Type);

					if (Type.equals("multiselectvalue"))

					{
						JSONArray limiterValueArray = availablLimiter
								.getJSONArray("LimiterValues");

						availablelimiter.setAddAction("");

						for (int index = 0; index < limiterValueArray.length(); index++) {

							JSONObject lvjson = limiterValueArray
									.getJSONObject(index);

							String Value = lvjson.getString("Value");
							String AddAction = lvjson.getString("AddAction");

							LimiterValue lv = new LimiterValue();
							lv.setAddAction(AddAction);
							lv.setValue(Value);

							list.add(lv);
						}

						availablelimiter.setLimitervalues(list);

						availableLimiterList.add(availablelimiter);

					}

					else {

						String AddAction = availablLimiter
								.getString("AddAction");

						availablelimiter.setAddAction(AddAction);
						availablelimiter.setLimitervalues(null);
						availableLimiterList.add(availablelimiter);

					}

				}

				info.setAvailableLimitersList(availableLimiterList);
			}
			// Parse View ResultsSettings
			if (obj.has("ViewResultSettings")) {
				ViewResultSettings vrs = new ViewResultSettings();
				JSONObject viewResultSettings = obj
						.getJSONObject("ViewResultSettings");
				if (viewResultSettings.has("ResultsPerPage"))
					vrs.setResultsPerPage(viewResultSettings
							.getInt("ResultsPerPage"));
				if (viewResultSettings.has("ResultListView"))
					vrs.setResultListView(viewResultSettings
							.getString("ResultListView"));
				info.setViewResultSettings(vrs);
			}
		} catch (JSONException e) {
			ApiErrorMessage errorMessage = new ApiErrorMessage();
			errorMessage.setErrorDescription("Error parsing info message");
			errorMessage.setDetailedErrorDescription(e.getMessage());
			info.setErrorMessage(errorMessage);
		} catch (Exception e) {
			ApiErrorMessage errorMessage = new ApiErrorMessage();
			errorMessage.setErrorDescription("Error processing info response");
			errorMessage.setDetailedErrorDescription(e.getMessage());
			info.setErrorMessage(errorMessage);
		}
		return info;
	}

	public String ProcessEndSessionResponse(ServiceResponse response) {
		BufferedReader reader = response.getReader();
		String IsSuccessful = "0";
		try {
			JSONObject object = (JSONObject) new JSONTokener(reader)
					.nextValue();
			IsSuccessful = object.getString("IsSuccessful");
		} catch (Exception e) {
		}
		return IsSuccessful;

	}

	public ApiErrorMessage ProcessError(ServiceResponse serviceResponse) {
		ApiErrorMessage apiErrorMessage = new ApiErrorMessage();
		try {
			String errorString = serviceResponse.getErrorStream();
			if (!serviceResponse.getErrorStream().equals("")) {
				ByteArrayInputStream errorInputStream = new ByteArrayInputStream(
						errorString.getBytes());
				InputStreamReader in = new InputStreamReader(errorInputStream);
				BufferedReader errorreader = new BufferedReader(in);
				String errorNumber = serviceResponse.getErrorNumber();
				// TODO: Put a try catch around here and if it can't be case to
				// a JSON Object, throw an unknown error.
				if (errorNumber.equals(HTTP_BAD_REQUEST)) {
					Object tmpObject = new JSONTokener(errorreader).nextValue();
					JSONObject object = (JSONObject) tmpObject;
					String DetailedErrorDescription = object
							.getString("DetailedErrorDescription");
					String ErrorDescription = object
							.getString("ErrorDescription");
					String ErrorNumber = object.getString("ErrorNumber");
					apiErrorMessage
							.setDetailedErrorDescription(DetailedErrorDescription);
					apiErrorMessage.setErrorDescription(ErrorDescription);
					apiErrorMessage.setErrorNumber(ErrorNumber);

				} else {
					String DetailedErrorDescription = serviceResponse
							.getErrorStream();
					String ErrorNumber = errorNumber;
					String ErrorDescription = serviceResponse.getErrorStream();
					apiErrorMessage
							.setDetailedErrorDescription(DetailedErrorDescription);
					apiErrorMessage.setErrorDescription(ErrorDescription);
					apiErrorMessage.setErrorNumber(ErrorNumber);
				}
			}
		} catch (JSONException e) {
			apiErrorMessage
					.setErrorDescription("Error parsing response message");
			apiErrorMessage.setDetailedErrorDescription(e.getMessage());
		} catch (Exception e) {
			apiErrorMessage
					.setErrorDescription("Error processing error response");
			apiErrorMessage.setDetailedErrorDescription(e.getMessage());
		}
		return apiErrorMessage;
	}

	/* Build an error message object */
	private ApiErrorMessage ProcessErrors(String errorNumber, String errorStream) {
		InputStream errorInputStream = new ByteArrayInputStream(
				errorStream.getBytes());
		InputStreamReader in = new InputStreamReader(errorInputStream);
		BufferedReader errorReader = new BufferedReader(in);
		ApiErrorMessage apiErrorMessage = new ApiErrorMessage();
		try {
			if (errorNumber.equals(HTTP_BAD_REQUEST)) {
				JSONObject object = (JSONObject) new JSONTokener(errorReader)
						.nextValue();
				String DetailedErrorDescription = object
						.getString("DetailedErrorDescription");
				String ErrorDescription = object.getString("ErrorDescription");
				String ErrorNumber = object.getString("ErrorNumber");
				apiErrorMessage
						.setDetailedErrorDescription(DetailedErrorDescription);
				apiErrorMessage.setErrorDescription(ErrorDescription);
				apiErrorMessage.setErrorNumber(ErrorNumber);
			} else {
				String DetailedErrorDescription = errorStream;
				String ErrorNumber = errorNumber;
				String ErrorDescription = errorStream;
				apiErrorMessage
						.setDetailedErrorDescription(DetailedErrorDescription);
				apiErrorMessage.setErrorDescription(ErrorDescription);
				apiErrorMessage.setErrorNumber(ErrorNumber);
			}
		} catch (JSONException e) {
			apiErrorMessage
					.setErrorDescription("Error parsing response message");
			apiErrorMessage.setDetailedErrorDescription(e.getMessage());
		} catch (Exception e) {
			apiErrorMessage
					.setErrorDescription("Error processing error response");
			apiErrorMessage.setDetailedErrorDescription(e.getMessage());
		}
		return apiErrorMessage;
	}

	/* Build an error message object for Auth service errors */
	private ApiErrorMessage ProcessAuthErrors(String errorNumber,
			String errorStream) {
		InputStream errorInputStream = new ByteArrayInputStream(
				errorStream.getBytes());
		InputStreamReader in = new InputStreamReader(errorInputStream);
		BufferedReader errorReader = new BufferedReader(in);
		ApiErrorMessage apiErrorMessage = new ApiErrorMessage();
		try {
			if (errorNumber.equals(HTTP_BAD_REQUEST)) {
				JSONObject object = (JSONObject) new JSONTokener(errorReader)
						.nextValue();
				String DetailedErrorDescription = object
						.optString("AdditionalDetail");
				String ErrorDescription = object.optString("Reason");
				String ErrorNumber = object.getString("ErrorCode");
				apiErrorMessage
						.setDetailedErrorDescription(DetailedErrorDescription);
				apiErrorMessage.setErrorDescription(ErrorDescription);
				apiErrorMessage.setErrorNumber(ErrorNumber);

			} else {

				String DetailedErrorDescription = errorStream;
				String ErrorNumber = errorNumber;
				String ErrorDescription = errorStream;
				apiErrorMessage
						.setDetailedErrorDescription(DetailedErrorDescription);
				apiErrorMessage.setErrorDescription(ErrorDescription);
				apiErrorMessage.setErrorNumber(ErrorNumber);

			}
		} catch (JSONException e) {
			apiErrorMessage
					.setErrorDescription("Error parsing response message");
			apiErrorMessage.setDetailedErrorDescription(e.getMessage());
		} catch (Exception e) {
			apiErrorMessage
					.setErrorDescription("Error processing error response");
			apiErrorMessage.setDetailedErrorDescription(e.getMessage());
		}
		return apiErrorMessage;
	}

	/**
	 * Constructs a record object from an EDS API JSON response
	 * 
	 * @throws JSONException
	 */
	private Result constructRecord(JSONObject jsonRecord) throws JSONException {
		Result result = new Result();
		String resultId = jsonRecord.getString("ResultId");
		String pLink = jsonRecord.optString("PLink");
		JSONObject header = jsonRecord.getJSONObject("Header");
		String dbId = header.getString("DbId");
		String dbLabel = header.getString("DbLabel");
		String an = header.getString("An");
		String pubType = header.optString("PubType");
		String pubTypeId = header.optString("PubTypeId");
		String relScore = header.optString("RelevancyScore");

		result.setAn(an);
		result.setpLink(pLink);
		result.setDbLabel(dbLabel);
		result.setPubType(pubType);
		result.setDbId(dbId);
		result.setResultId(resultId);
		result.setPubTypeID(pubTypeId);
		result.setRelevancyScore(relScore);

		// set BookJacket list in the result
		ArrayList<BookJacket> bookJacketList = new ArrayList<BookJacket>();
		JSONArray imageInfoArray = jsonRecord.optJSONArray("ImageInfo");
		if (null != imageInfoArray) {
			for (int j = 0; j < imageInfoArray.length(); j++) {
				BookJacket bookJacket = new BookJacket();
				JSONObject Imageinfo = imageInfoArray.getJSONObject(j);
				String Size = Imageinfo.optString("Size");
				String Target = Imageinfo.optString("Target");
				bookJacket.setSize(Size);
				bookJacket.setTarget(Target);
				bookJacketList.add(bookJacket);
			}
			result.setBookJacketList(bookJacketList);
		}

		// set Custom link List in the result
		ArrayList<CustomLink> customLinkList = new ArrayList<CustomLink>();
		JSONArray customLinks = jsonRecord.optJSONArray("CustomLinks");
		if (null != customLinks) {
			for (int j = 0; j < customLinks.length(); j++) {
				CustomLink customlink = new CustomLink();
				JSONObject customLinkJSON = customLinks.getJSONObject(j);
				String customLinkURl = customLinkJSON.optString("Url");
				String customLinkName = customLinkJSON.optString("Name");
				String customLinkCategory = customLinkJSON
						.optString("Category");
				String customLinkText = customLinkJSON.optString("Text");
				String customLinkIcon = customLinkJSON.optString("Icon");
				String mouseOverText = customLinkJSON
						.optString("MouseOverText");
				customlink.setCategory(customLinkCategory);
				customlink.setIcon(customLinkIcon);
				customlink.setMouseOverText(mouseOverText);
				customlink.setName(customLinkName);
				customlink.setText(customLinkText);
				customlink.setUrl(customLinkURl);
				customLinkList.add(customlink);
			}
			result.setCustomLinkList(customLinkList);
		}

		// set Item in the result
		ArrayList<Item> itemList = new ArrayList<Item>();
		JSONArray items = jsonRecord.optJSONArray("Items");
		if (null != items) {
			for (int j = 0; j < items.length(); j++) {
				Item item = new Item();
				JSONObject itemJSON = items.getJSONObject(j);
				String itemData = itemJSON.optString("Data");
				String label = itemJSON.optString("Label");
				String group = itemJSON.optString("Group");
				itemData = Jsoup.parse(itemData).text().toString();
				itemData = TransDataToHTML.transDataToHTML(itemData);
				item.setData(itemData);
				item.setGroup(group);
				item.setLabel(label);
				itemList.add(item);
			}
			result.setItemList(itemList);
		}
		// Set Full Text Info
		JSONObject fullText = jsonRecord.optJSONObject("FullText");
		result.setHtmlAvailable("0");
		result.setPdfAvailable("0");
		if (null != fullText) {
			// Check for HTML full text and availability
			JSONObject text = fullText.optJSONObject("Text");
			if (null != text) {
				String htmlAvailable = text.optString("Availability");
				// 0 - embedded full text is not available
				// 1 - full text is available
				// -1 - database not configured to provide full text to
				// guests
				if (null != htmlAvailable && !htmlAvailable.isEmpty()
						&& htmlAvailable.equals("1")) {
					result.setHtmlAvailable("1");
					String htmlFullTextValue = text.optString("Value");
					if (!htmlFullTextValue.isEmpty()) {
						htmlFullTextValue = Jsoup.parse(htmlFullTextValue)
								.text().toString();
						htmlFullTextValue = TransDataToHTML
								.transDataToHTML(htmlFullTextValue);
					}
					result.setHtmlFullText(htmlFullTextValue);
				}
			}
			// determine whether or not there are full text links
			JSONArray links = fullText.optJSONArray("Links");
			if (null != links && 0 < links.length()) {
				ArrayList<Link> otherLinks = new ArrayList<Link>();
				for (int j = 0; j < links.length(); j++) {
					JSONObject link = links.getJSONObject(j);
					String type = link.optString("Type");
					String url = link.optString("Url");
					if (null != type && type.equals("pdflink")) {
						result.setPdfAvailable("1");
						result.setPdfLink(url);
					} else if (null != type && !type.isEmpty()) {
						Link otherFTLink = new Link();
						otherFTLink.setType(type);
						otherFTLink.setUrl(url);
						otherLinks.add(otherFTLink);
					}
				}
				if (!otherLinks.isEmpty())
					result.setOtherFullTextLinks(otherLinks);
			}
		}
		return result;
	}
}
