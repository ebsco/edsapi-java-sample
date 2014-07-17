/*
 * This class processes XML formatted messages to send and received from the EDS
 * API and AuthService.
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jsoup.Jsoup;
import org.xml.sax.InputSource;

import com.eds.Helpers.TransDataToHTML;
import com.eds.bean.ApiErrorMessage;
import com.eds.bean.AuthToken;
import com.eds.bean.AvailableExpander;
import com.eds.bean.AvailableLimiter;
import com.eds.bean.AvailableSearchField;
import com.eds.bean.AvailableSearchMode;
import com.eds.bean.AvailableSort;
import com.eds.bean.BookJacket;
import com.eds.bean.CustomLink;
import com.eds.bean.EachFacetValue;
import com.eds.bean.ExpandersWithAction;
import com.eds.bean.Facet;
import com.eds.bean.FacetFilterWithAction;
import com.eds.bean.FacetValue;
import com.eds.bean.FacetValueWithAction;
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

public class XMLProcessor implements IMessageProcessor {
	private static final String _contentType = "application/xml";

	public String GetContentType() {
		return _contentType;
	}

	public void SetContentHeaders(URLConnection connection) {
		connection.setRequestProperty("Accept", _contentType);
		connection.setRequestProperty("Content-Type", _contentType);
	}

	public static final String HTTP_BAD_REQUEST = "400";

	public AuthToken ProcessUIDAuthResponse(ServiceResponse response) {

		BufferedReader reader = response.getReader();
		String authTokenXML = "";
		AuthToken authToken = new AuthToken();
		if (null != response.getErrorStream()
				&& !response.getErrorStream().isEmpty()) {
			authToken.setErrorMessage(ProcessAuthError(
					response.getErrorNumber(), response.getErrorStream()));
			return authToken;
		}
		if (null != reader) {
			try {
				String line = "";
				while ((line = reader.readLine()) != null) {
					authTokenXML += line;
				}
				StringReader stringReader = new StringReader(authTokenXML);
				InputSource inputSource = new InputSource(stringReader);
				Document doc = (new SAXBuilder()).build(inputSource);
				Element root = doc.getRootElement();
				Content content = root.getContent().get(0);
				Content timeout = root.getContent().get(1);

				if (content.getValue() != null) {
					authToken.setAuthToken(content.getValue());
					authToken.setAuthTimeout(timeout.getValue());
				}
			} catch (Exception e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing UID auth response");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				authToken.setErrorMessage(errorMessage);
			}
		}
		return authToken;
	}

	public SessionToken ProcessCreateSessionResponse(ServiceResponse response) {
		BufferedReader reader = response.getReader();
		String line = "";
		String sessionTokenXML = "";
		// Check for errors
		SessionToken sessionToken = new SessionToken();
		if (null != response.getErrorStream()
				&& !response.getErrorStream().isEmpty()) {
			sessionToken.setApiErrorMessage(ProcessError(
					response.getErrorNumber(), response.getErrorStream()));
		} else {
			if (null != reader) {
				try {
					while ((line = reader.readLine()) != null) {
						sessionTokenXML += line;
					}
				} catch (IOException e) {
					ApiErrorMessage errorMessage = new ApiErrorMessage();
					errorMessage
							.setErrorDescription("Error processing create session response");
					errorMessage.setDetailedErrorDescription(e.getMessage());
					sessionToken.setApiErrorMessage(errorMessage);
				}
			}
			/*
			 * Parse String to XML and the get the value
			 */
			try {
				StringReader stringReader = new StringReader(sessionTokenXML);
				InputSource inputSource = new InputSource(stringReader);
				Document doc = (new SAXBuilder()).build(inputSource);
				Element root = doc.getRootElement();
				Content content = root.getContent().get(0);

				if (content.getValue() != null) {
					sessionToken.setSessionToken(content.getValue());
				}
			} catch (Exception e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing search response");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				sessionToken.setApiErrorMessage(errorMessage);
			}
		}
		return sessionToken;

	}

	public SearchResponse ProcessSearchResponse(ServiceResponse serviceResponse) {

		BufferedReader reader = serviceResponse.getReader();
		SearchResponse searchResponse = new SearchResponse();
		if (null != serviceResponse.getErrorStream()
				&& !serviceResponse.getErrorStream().isEmpty()) {
			searchResponse.setApierrormessage(ProcessError(
					serviceResponse.getErrorNumber(),
					serviceResponse.getErrorStream()));
		} else {
			String resultsListXML = "";
			try {
				String line = "";
				while ((line = reader.readLine()) != null) {
					resultsListXML += line;
				}
			} catch (IOException e1) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing search response");
				errorMessage.setDetailedErrorDescription(e1.getMessage());
				searchResponse.setApierrormessage(errorMessage);
			}

			try {
				StringReader stringReader = new StringReader(resultsListXML);
				InputSource inputSource = new InputSource(stringReader);
				Document doc = (new SAXBuilder()).build(inputSource);
				// Process root element
				Element searchResponseMessageGet = doc.getRootElement();

				Element searchResult = searchResponseMessageGet
						.getChild("SearchResult",
								searchResponseMessageGet.getNamespace());

				// Process search request message returned in the response.
				Element searchRequestGet = searchResponseMessageGet.getChild(
						"SearchRequestGet",
						searchResponseMessageGet.getNamespace());
				if (null != searchRequestGet) {

					// process the querystring
					String queryString = searchRequestGet.getChildText(
							"QueryString", searchRequestGet.getNamespace());
					searchResponse.setQueryString(queryString);

					// Process search criteria
					Element searchCriteriaWithActions = searchRequestGet
							.getChild("SearchCriteriaWithActions",
									searchRequestGet.getNamespace());
					if (null != searchCriteriaWithActions) {

						// Process Queries with actions
						ArrayList<QueryWithAction> queryList = new ArrayList<QueryWithAction>();
						Element queriesWithAction = searchCriteriaWithActions
								.getChild("QueriesWithAction",
										searchCriteriaWithActions
												.getNamespace());
						if (null != queriesWithAction) {
							List<Element> queriesWithActions = queriesWithAction
									.getChildren();
							for (Element queryWithAction : queriesWithActions) {
								QueryWithAction queryObject = new QueryWithAction();
								String removeAction = queryWithAction
										.getChildText("RemoveAction",
												queryWithAction.getNamespace());
								queryObject.setRemoveAction(removeAction);

								Element query = queryWithAction
										.getChild("Query",
												queryWithAction.getNamespace());
								if (null != query) {
									String term = query.getChildText("Term",
											queryWithAction.getNamespace());
									String fieldCode = queryWithAction
											.getChildText("FieldCode",
													queryWithAction
															.getNamespace());
									String booleanOperator = queryWithAction
											.getChildText("BooleanOperator",
													queryWithAction
															.getNamespace());
									queryObject.setTerm(term);
									queryObject.setFieldCode(fieldCode);
									queryObject.setOperator(booleanOperator);
								}
								queryList.add(queryObject);
							}
						}
						searchResponse.setQueryList(queryList);
						if (null != searchResponse.getQueryList()
								&& !searchResponse.getQueryList().isEmpty())
							searchResponse.setQuery(searchResponse
									.getQueryList().get(0));

						// process limiters with action
						ArrayList<LimiterWithAction> limiterList = new ArrayList<LimiterWithAction>();
						Element limitersWithAction = searchCriteriaWithActions
								.getChild("LimitersWithAction",
										searchCriteriaWithActions
												.getNamespace());
						if (limitersWithAction != null) {
							List<Element> eLimitersWithAction = limitersWithAction
									.getChildren();
							for (int i = 0; i < eLimitersWithAction.size(); i++) {
								Element eLimiterWithAction = (Element) eLimitersWithAction
										.get(i);
								LimiterWithAction lwa = new LimiterWithAction();
								String Id = eLimiterWithAction
										.getChildText("Id", eLimiterWithAction
												.getNamespace());
								String removeAction = eLimiterWithAction
										.getChildText("RemoveAction",
												eLimiterWithAction
														.getNamespace());
								lwa.setId(Id);
								lwa.setRemoveAction(removeAction);

								Element eLimiterValuesWithAction = eLimiterWithAction
										.getChild("LimiterValuesWithAction",
												eLimiterWithAction
														.getNamespace());
								List<Element> limiterValuesWithActionList = eLimiterValuesWithAction
										.getChildren();
								ArrayList<LimiterValueWithAction> lvalist = new ArrayList<LimiterValueWithAction>();
								for (int j = 0; j < limiterValuesWithActionList
										.size(); j++) {
									LimiterValueWithAction lvwa = new LimiterValueWithAction();
									Element eLimiterValueWithAction = (Element) limiterValuesWithActionList
											.get(j);
									String value = eLimiterValueWithAction
											.getChildText("Value",
													eLimiterValueWithAction
															.getNamespace());

									String vRemoveAction = eLimiterValueWithAction
											.getChildText("RemoveAction",
													eLimiterValueWithAction
															.getNamespace());
									lvwa.setValue(value);
									lvwa.setRemoveAction(vRemoveAction);
									lvalist.add(lvwa);
								}
								lwa.setLvalist(lvalist);
								limiterList.add(lwa);
							}
						}
						searchResponse.setSelectedLimiterList(limiterList);

						// Process applied expanders
						ArrayList<ExpandersWithAction> expanderList = new ArrayList<ExpandersWithAction>();

						Element ExpandersWithAction = searchCriteriaWithActions
								.getChild("ExpandersWithAction",
										searchCriteriaWithActions
												.getNamespace());
						if (ExpandersWithAction != null) {
							List<Element> expandersWithActionList = ExpandersWithAction
									.getChildren();
							for (int i = 0; i < expandersWithActionList.size(); i++) {
								Element expanderWithAction = (Element) expandersWithActionList
										.get(i);
								ExpandersWithAction ewa = new ExpandersWithAction();
								String id = expanderWithAction
										.getChildText("Id", expanderWithAction
												.getNamespace());
								String removeAction = expanderWithAction
										.getChildText("RemoveAction",
												expanderWithAction
														.getNamespace());
								ewa.setId(id);
								ewa.setRemoveAction(removeAction);
								expanderList.add(ewa);
							}
						}
						searchResponse.setExpanderwithActionList(expanderList);

						// process applied facets
						ArrayList<FacetFilterWithAction> facetFiltersList = new ArrayList<FacetFilterWithAction>();
						Element facetFiltersWithAction = searchCriteriaWithActions
								.getChild("FacetFiltersWithAction",
										searchCriteriaWithActions
												.getNamespace());
						if (facetFiltersWithAction != null) {
							for (Element facetFilterWithActionXML : facetFiltersWithAction
									.getChildren()) {
								FacetFilterWithAction facetWithAction = new FacetFilterWithAction();

								String filterId = facetFilterWithActionXML
										.getChildText("FilterId",
												facetFilterWithActionXML
														.getNamespace());
								String removeAction = facetFilterWithActionXML
										.getChildText("RemoveAction",
												facetFilterWithActionXML
														.getNamespace());
								facetWithAction.setFilterId(filterId);
								facetWithAction.setRemoveAction(removeAction);

								ArrayList<FacetValueWithAction> facetValuesWithActionList = new ArrayList<FacetValueWithAction>();
								Element facetValuesWithAction = facetFilterWithActionXML
										.getChild("FacetValuesWithAction",
												facetFilterWithActionXML
														.getNamespace());

								for (Element facetValueWithAction : facetValuesWithAction
										.getChildren()) {

									FacetValueWithAction fvwa = new FacetValueWithAction();
									String eRemoveAction = facetValueWithAction
											.getChildText("RemoveAction",
													facetValueWithAction
															.getNamespace());
									fvwa.setRemoveAction(eRemoveAction);

									Element eFacetValue = facetValueWithAction
											.getChild("FacetValue",
													facetValueWithAction
															.getNamespace());

									if (null != eFacetValue) {
										EachFacetValue efv = new EachFacetValue();
										String id = eFacetValue.getChildText(
												"Id",
												eFacetValue.getNamespace());
										String value = eFacetValue
												.getChildText("Value",
														eFacetValue
																.getNamespace());
										efv.setValue(value);
										efv.setId(id);
										fvwa.setEachfacetvalue(efv);
									}
									facetValuesWithActionList.add(fvwa);
								}
								facetWithAction
										.setFacetvaluewithaction(facetValuesWithActionList);
								facetFiltersList.add(facetWithAction);
							}
						}
						searchResponse.setFacetfiltersList(facetFiltersList);
					}
				}
				// Process the search result returned in the response

				// Get Total Hits and Total Search Time
				Element statistics = searchResult.getChild("Statistics",
						searchResult.getNamespace());
				long hits = 0;
				if (null != statistics) {
					String totalHits = statistics.getChildText("TotalHits",
							statistics.getNamespace());
					try {
						hits = Long.parseLong(totalHits);
					} catch (Exception e) {
						hits = 0;
					}
					String totalSearchTime = statistics.getChildText(
							"TotalSearchTime", statistics.getNamespace());
					searchResponse.setSearchTime(totalSearchTime);
				}
				searchResponse.setHits(String.valueOf(hits));
				if (hits > 0) {
					// process results Results
					Element data = searchResult.getChild("Data",
							searchResult.getNamespace());
					if (null != data) {
						Element records = data.getChild("Records",
								data.getNamespace());
						if (null != records) {
							List<Element> recordsList = records.getChildren();
							for (int i = 0; i < recordsList.size(); i++) {
								Element record = (Element) recordsList.get(i);
								Result result = constructRecord(record);
								searchResponse.getResultsList().add(result);
							}
						}
					}

					// Get Facets. if there are no hits, don't bother checking
					// facets
					Element availableFacets = searchResult.getChild(
							"AvailableFacets", searchResult.getNamespace());
					if (null != availableFacets) {
						List<Element> facetsList = availableFacets
								.getChildren();
						for (int e = 0; e < facetsList.size(); e++) {
							Facet facet = new Facet();
							Element availableFacet = (Element) facetsList
									.get(e);
							String id = availableFacet.getChildText("Id",
									availableFacet.getNamespace());
							String label = availableFacet.getChildText("Label",
									availableFacet.getNamespace());
							facet.setId(id);
							facet.setLabel(label);

							Element availableFacetValues = availableFacet
									.getChild("AvailableFacetValues",
											availableFacet.getNamespace());
							if (null != availableFacetValues) {
								List<Element> availableFacetValuesList = availableFacetValues
										.getChildren();
								for (int f = 0; f < availableFacetValuesList
										.size(); f++) {
									FacetValue facetValue = new FacetValue();
									Element availableFacetValue = (Element) availableFacetValuesList
											.get(f);
									String value = availableFacetValue
											.getChildText("Value",
													availableFacetValue
															.getNamespace());
									String count = availableFacetValue
											.getChildText("Count",
													availableFacetValue
															.getNamespace());
									String addAction = availableFacetValue
											.getChildText("AddAction",
													availableFacetValue
															.getNamespace());
									facetValue.setValue(value);
									facetValue.setCount(count);
									facetValue.setAddAction(addAction);
									facet.getFacetsValueList().add(facetValue);
								}
							}
							searchResponse.getFacetsList().add(facet);

							// --------end to handle resultsList
						}
					}
				}
			} catch (Exception e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing search response");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				searchResponse.setApierrormessage(errorMessage);
			}
		}
		return searchResponse;

	}

	public String BuildUIDAuthRequestMessage(String userName, String password) {
		return "<UIDAuthRequestMessage xmlns=\"http://www.ebscohost.com/services/public/AuthService/Response/2012/06/01\">"
				+ "<UserId>"
				+ userName.trim()
				+ "</UserId>"
				+ "<Password>"
				+ password.trim() + "</Password>" + "</UIDAuthRequestMessage>";
	}

	public RetrieveResponse ProcessRetrieveResponse(
			ServiceResponse serviceResponse) {

		RetrieveResponse retrieveResponse = new RetrieveResponse();
		BufferedReader reader = serviceResponse.getReader();

		if (!serviceResponse.getErrorStream().equals("")) {
			ApiErrorMessage errorMessage = ProcessError(
					serviceResponse.getErrorNumber(),
					serviceResponse.getErrorStream());
			retrieveResponse.setApiErrorMessage(errorMessage);
		} else {
			String RecordXML = "";
			try {
				String line = "";
				while ((line = reader.readLine()) != null) {
					RecordXML += line;
				}
			} catch (IOException e1) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing retrieve response");
				errorMessage.setDetailedErrorDescription(e1.getMessage());
				retrieveResponse.setApiErrorMessage(errorMessage);
				return retrieveResponse;
			}
			try {

				StringReader stringReader = new StringReader(RecordXML);
				InputSource inputSource = new InputSource(stringReader);
				Document doc = (new SAXBuilder()).build(inputSource);

				// root element (level 1)
				Element retrieveResponseMessage = doc.getRootElement();
				// level 2 elements
				Element xmlRecord = retrieveResponseMessage.getChild("Record",
						retrieveResponseMessage.getNamespace());

				Result result = this.constructRecord(xmlRecord, true);
				retrieveResponse.setRecord(result);

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

	public Info ProcessInfoResponse(ServiceResponse serviceResponse) {
		BufferedReader reader = serviceResponse.getReader();
		Info info = new Info();
		if (!serviceResponse.getErrorStream().equals("")) {
			ApiErrorMessage errorMessage = ProcessError(
					serviceResponse.getErrorNumber(),
					serviceResponse.getErrorStream());
			info.setErrorMessage(errorMessage);
		} else {
			String InfoXML = "";
			try {
				String line = "";
				while ((line = reader.readLine()) != null) {
					InfoXML += line;
				}
				StringReader stringReader = new StringReader(InfoXML);
				InputSource inputSource = new InputSource(stringReader);
				Document doc = (new SAXBuilder()).build(inputSource);
				Element infoResponseMessage = doc.getRootElement();

				Element availableSearchCriteria = infoResponseMessage.getChild(
						"AvailableSearchCriteria",
						infoResponseMessage.getNamespace());
				if (null != availableSearchCriteria) {
					// Process Sorts
					ArrayList<AvailableSort> sortsList = new ArrayList<AvailableSort>();
					Element availableSorts = availableSearchCriteria.getChild(
							"AvailableSorts",
							availableSearchCriteria.getNamespace());
					if (null != availableSorts) {
						List<Element> availableSortsList = availableSorts
								.getChildren();
						for (int i = 0; i < availableSortsList.size(); i++) {
							Element eAvailableSort = (Element) availableSortsList
									.get(i);
							if (null != eAvailableSort) {
								AvailableSort as = new AvailableSort();
								String Id = eAvailableSort.getChildText("Id",
										eAvailableSort.getNamespace());
								String Label = eAvailableSort.getChildText(
										"Label", eAvailableSort.getNamespace());
								String AddAction = eAvailableSort.getChildText(
										"AddAction",
										eAvailableSort.getNamespace());
								as.setId(Id);
								as.setLabel(Label);
								as.setAddAction(AddAction);
								sortsList.add(as);
							}

						}
					}
					info.setAvailableSortsList(sortsList);

					// Process available Search Field list
					ArrayList<AvailableSearchField> searchFieldsList = new ArrayList<AvailableSearchField>();
					Element availableSearchFields = availableSearchCriteria
							.getChild("AvailableSearchFields",
									availableSearchCriteria.getNamespace());
					if (null != availableSearchFields) {
						List<Element> availableSearchFieldsList = availableSearchFields
								.getChildren();
						for (int i = 0; i < availableSearchFieldsList.size(); i++) {
							Element eAvailableSearchField = (Element) availableSearchFieldsList
									.get(i);
							AvailableSearchField asf = new AvailableSearchField();
							String fieldCode = eAvailableSearchField
									.getContent(0).getValue();
							String label = eAvailableSearchField.getContent(1)
									.getValue();
							asf.setFieldCode(fieldCode);
							asf.setLabel(label);
							searchFieldsList.add(asf);

						}
					}
					info.setAvailableSearchFieldsList(searchFieldsList);

					// process available expanders
					ArrayList<AvailableExpander> expandersList = new ArrayList<AvailableExpander>();
					Element availableExpanders = availableSearchCriteria
							.getChild("AvailableExpanders",
									availableSearchCriteria.getNamespace());
					if (null != availableExpanders) {
						List<Element> availableExpandersList = availableExpanders
								.getChildren();
						for (int i = 0; i < availableExpandersList.size(); i++) {

							Element eAvailableExpander = (Element) availableExpandersList
									.get(i);
							AvailableExpander ae = new AvailableExpander();
							String id = eAvailableExpander.getChildText("Id",
									eAvailableExpander.getNamespace());
							String label = eAvailableExpander.getChildText(
									"Label", eAvailableExpander.getNamespace());
							String addAction = eAvailableExpander.getChildText(
									"AddAction",
									eAvailableExpander.getNamespace());
							ae.setId(id);
							ae.setLabel(label);
							ae.setAddAction(addAction);
							expandersList.add(ae);
						}
					}
					info.setAvailableExpandersList(expandersList);

					// process available limiters
					ArrayList<AvailableLimiter> limitersList = new ArrayList<AvailableLimiter>();
					Element availableLimiters = availableSearchCriteria
							.getChild("AvailableLimiters",
									availableSearchCriteria.getNamespace());
					if (null != availableLimiters) {
						List<Element> availableLimitersList = availableLimiters
								.getChildren();
						for (int i = 0; i < availableLimitersList.size(); i++) {
							Element eAvailableLimiter = (Element) availableLimitersList
									.get(i);
							AvailableLimiter al = new AvailableLimiter();
							String id = eAvailableLimiter.getChildText("Id",
									eAvailableLimiter.getNamespace());
							String label = eAvailableLimiter.getChildText(
									"Label", eAvailableLimiter.getNamespace());
							String type = eAvailableLimiter.getChildText(
									"Type", eAvailableLimiter.getNamespace());
							String addAction = eAvailableLimiter.getChildText(
									"AddAction",
									eAvailableLimiter.getNamespace());
							String defaultOn = eAvailableLimiter.getChildText(
									"DefaultOn",
									eAvailableLimiter.getNamespace());
							String order = eAvailableLimiter.getChildText(
									"Order", eAvailableLimiter.getNamespace());
							al.setId(id);
							al.setLabel(label);
							al.setType(type);
							al.setAddAction(addAction);
							al.setDefaultOn(defaultOn);
							al.setOrder(order);

							if (type.equals("multiselectvalue")) {
								Element eLimiterValues = eAvailableLimiter
										.getChild("LimiterValues",
												eAvailableLimiter
														.getNamespace());
								if (null != eLimiterValues) {
									List<Element> limiterValues = eLimiterValues
											.getChildren();
									ArrayList<LimiterValue> limiterValueList = new ArrayList<LimiterValue>();
									for (int j = 0; j < limiterValues.size(); j++) {
										Element eLimiterValue = (Element) limiterValues
												.get(j);
										LimiterValue lv = new LimiterValue();

										String valueValue = eLimiterValue
												.getChildText("Id",
														eLimiterValue
																.getNamespace());
										String valueAddAction = eLimiterValue
												.getChildText("AddAction",
														eLimiterValue
																.getNamespace());
										// This sample application is only going
										// one
										// level deep
										lv.setValue(valueValue);
										lv.setAddAction(valueAddAction);
										limiterValueList.add(lv);
									}
									al.setLimitervalues(limiterValueList);
								}
							}
							limitersList.add(al);
						}
						info.setAvailableLimitersList(limitersList);
					}

					// set available Search Modes
					ArrayList<AvailableSearchMode> searchModeList = new ArrayList<AvailableSearchMode>();
					Element availableSearchModes = availableSearchCriteria
							.getChild("AvailableSearchModes",
									availableSearchCriteria.getNamespace());
					if (null != availableSearchModes) {
						List<Element> availableSearchModeList = availableSearchModes
								.getChildren();
						for (int i = 0; i < availableSearchModeList.size(); i++) {
							Element eAvailableSearchMode = (Element) availableSearchModeList
									.get(i);
							AvailableSearchMode asm = new AvailableSearchMode();
							String mode = eAvailableSearchMode
									.getChildText("Mode",
											eAvailableSearchMode.getNamespace());
							String label = eAvailableSearchMode.getChildText(
									"Label",
									eAvailableSearchMode.getNamespace());
							String addAction = eAvailableSearchMode
									.getChildText("AddAction",
											eAvailableSearchMode.getNamespace());
							String defaultOn = eAvailableSearchMode
									.getChildText("DefaultOn",
											eAvailableSearchMode.getNamespace());
							asm.setMode(mode);
							asm.setLabel(label);
							asm.setAddAction(addAction);
							asm.setDefaultOn(defaultOn);
							searchModeList.add(asm);
						}
					}
					info.setAvailableSearchModeList(searchModeList);
				}

				// Set ViewResult settings
				Element viewResultSettings = infoResponseMessage.getChild(
						"ViewResultSettings",
						infoResponseMessage.getNamespace());
				if (null != viewResultSettings) {
					ViewResultSettings vrs = new ViewResultSettings();
					String resultsPerPage = viewResultSettings
							.getChildText("ResultsPerPage",
									viewResultSettings.getNamespace());
					int rpp = 20;
					if (null != resultsPerPage) {
						try {
							rpp = Integer.parseInt(resultsPerPage);
						} catch (NumberFormatException e) {
						}
					}
					vrs.setResultsPerPage(rpp);
					vrs.setResultListView(viewResultSettings.getChildText(
							"ResultListView", viewResultSettings.getNamespace()));
					info.setViewResultSettings(vrs);
				}
			} catch (Exception e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing info response");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				info.setErrorMessage(errorMessage);
			}
		}
		return info;
	}

	public String ProcessEndSessionResponse(ServiceResponse serviceResponse) {
		BufferedReader reader = serviceResponse.getReader();
		String EndSessionXML = "";
		String IsSuccessful = "0";

		try {
			String line = "";
			while ((line = reader.readLine()) != null) {
				EndSessionXML += line;
			}
			StringReader stringReader = new StringReader(EndSessionXML);
			InputSource inputSource = new InputSource(stringReader);
			Document doc = (new SAXBuilder()).build(inputSource);
			Element EndSessionResponse = doc.getRootElement();
			IsSuccessful = EndSessionResponse.getContent(0).getValue();
		} catch (Exception e) {
		}

		return IsSuccessful;
	}

	public ApiErrorMessage ProcessError(String errorNumber, String errorStream) {
		ByteArrayInputStream errorInputStream = new ByteArrayInputStream(
				errorStream.getBytes());
		InputStreamReader in = new InputStreamReader(errorInputStream);
		BufferedReader errorreader = new BufferedReader(in);
		ApiErrorMessage apiErrorMessage = new ApiErrorMessage();
		try {
			if (errorNumber.equals(HTTP_BAD_REQUEST)) {
				String line = "";
				String resultListErrorStream = "";

				try {
					while ((line = errorreader.readLine()) != null) {
						resultListErrorStream += line;
					}
				} catch (IOException e) {
					apiErrorMessage.setErrorDescription("Error occurred");
					apiErrorMessage.setDetailedErrorDescription(e.getMessage());
					return apiErrorMessage;
				}
				StringReader stringReader = new StringReader(
						resultListErrorStream);
				InputSource inputSource = new InputSource(stringReader);
				Document doc = (new SAXBuilder()).build(inputSource);

				if (doc.getRootElement().getName() == "APIErrorMessage") {
					Element root = doc.getRootElement();
					String detailedErrorDescription = root.getChildText(
							"DetailedErrorDescription", root.getNamespace());
					String errorDescription = root.getChildText(
							"ErrorDescription", root.getNamespace());
					String errorNum = root.getChildText("ErrorNumber",
							root.getNamespace());

					apiErrorMessage
							.setDetailedErrorDescription(detailedErrorDescription);
					apiErrorMessage.setErrorDescription(errorDescription);
					apiErrorMessage.setErrorNumber(errorNum);

				} else {
					apiErrorMessage.setDetailedErrorDescription(errorStream);
					apiErrorMessage.setErrorDescription(errorStream);
					apiErrorMessage.setErrorNumber(errorNumber);
				}
			}
		} catch (Exception e) {
			apiErrorMessage.setErrorDescription("Error occurred");
			apiErrorMessage.setDetailedErrorDescription(e.getMessage());
		}
		return apiErrorMessage;
	}

	public ApiErrorMessage ProcessAuthError(String errorNumber,
			String errorStream) {
		ByteArrayInputStream errorInputStream = new ByteArrayInputStream(
				errorStream.getBytes());
		InputStreamReader in = new InputStreamReader(errorInputStream);
		BufferedReader errorreader = new BufferedReader(in);
		ApiErrorMessage apiErrorMessage = new ApiErrorMessage();
		try {
			if (errorNumber.equals(HTTP_BAD_REQUEST)) {
				String line = "";
				String resultListErrorStream = "";

				try {
					while ((line = errorreader.readLine()) != null) {
						resultListErrorStream += line;
					}
				} catch (IOException e) {
					apiErrorMessage.setErrorDescription("Error occurred");
					apiErrorMessage.setDetailedErrorDescription(e.getMessage());
					return apiErrorMessage;
				}

				StringReader stringReader = new StringReader(
						resultListErrorStream);
				InputSource inputSource = new InputSource(stringReader);
				Document doc = (new SAXBuilder()).build(inputSource);

				if (doc.getRootElement().getName() == "ErrorResponseMessage") {
					Element root = doc.getRootElement();
					String detailedErrorDescription = root.getChildText(
							"AdditionalDetail", root.getNamespace());
					String errorDescription = root.getChildText("Reason",
							root.getNamespace());
					String errorNum = root.getChildText("ErrorCode",
							root.getNamespace());

					apiErrorMessage
							.setDetailedErrorDescription(detailedErrorDescription);
					apiErrorMessage.setErrorDescription(errorDescription);
					apiErrorMessage.setErrorNumber(errorNum);

				} else {
					apiErrorMessage.setDetailedErrorDescription(errorStream);
					apiErrorMessage.setErrorDescription(errorStream);
					apiErrorMessage.setErrorNumber(errorNumber);
				}
			}

		} catch (Exception e) {
			apiErrorMessage.setErrorDescription("Error occurred");
			apiErrorMessage.setDetailedErrorDescription(e.getMessage());
		}
		return apiErrorMessage;
	}

	private Result constructRecord(Element xmlRecord) {
		return constructRecord(xmlRecord, false);
	}

	private Result constructRecord(Element xmlRecord, Boolean parse) {
		Result result = new Result();

		// Get Record Id
		String resultId = xmlRecord.getChildText("ResultId",
				xmlRecord.getNamespace());
		result.setResultId(resultId);

		// Get Header Info
		Element header = xmlRecord.getChild("Header", xmlRecord.getNamespace());
		if (null != header) {
			String dbId = header.getChildText("DbId", header.getNamespace());
			String dbLabel = header.getChildText("DbLabel",
					header.getNamespace());
			String an = header.getChildText("An", header.getNamespace());
			String pubType = header.getChildText("PubType",
					header.getNamespace());
			String pubTypeId = header.getChildText("PubTypeId",
					header.getNamespace());

			result.setDbId(dbId);
			result.setDbLabel(dbLabel);
			result.setPubTypeID(pubTypeId);
			result.setAn(an);
			result.setPubType(pubType);
		}
		// Get PLink
		String pLink = xmlRecord
				.getChildText("PLink", xmlRecord.getNamespace());
		result.setpLink(pLink);

		// Get ImageInfo
		Element imageInfo = xmlRecord.getChild("ImageInfo",
				xmlRecord.getNamespace());

		if (imageInfo != null) {
			List<Element> coverArts = imageInfo.getChildren();
			for (int b = 0; b < coverArts.size(); b++) {
				Element coverArt = (Element) coverArts.get(b);
				if (null != coverArt) {
					BookJacket bookJacket = new BookJacket();
					String size = coverArt.getChildText("Size",
							coverArt.getNamespace());
					String target = coverArt.getChildText("Target",
							coverArt.getNamespace());
					bookJacket.setSize(size);
					bookJacket.setTarget(target);
					result.getBookJacketList().add(bookJacket);
				}
			}
		}

		// Get Custom Links
		Element customLinks = xmlRecord.getChild("CustomLinks",
				xmlRecord.getNamespace());
		if (customLinks != null) {
			List<Element> customLinksList = customLinks.getChildren();
			for (int c = 0; c < customLinksList.size(); c++) {
				Element cl = (Element) customLinksList.get(c);
				if (null != cl) {
					String clurl = cl.getChildText("Url", cl.getNamespace());
					String name = cl.getChildText("Name", cl.getNamespace());
					String category = cl.getChildText("Category",
							cl.getNamespace());
					String text = cl.getChildText("Text", cl.getNamespace());
					String icon = cl.getChildText("Icon", cl.getNamespace());
					String mouseOverText = cl.getChildText("MouseOverText",
							cl.getNamespace());

					CustomLink customLink = new CustomLink();
					customLink.setUrl(clurl);
					customLink.setName(name);
					customLink.setCategory(category);
					customLink.setText(text);
					customLink.setIcon(icon);
					customLink.setMouseOverText(mouseOverText);
					result.getCustomLinkList().add(customLink);
				}
			}
		}

		// Get Full Text Info
		Element fullText = xmlRecord.getChild("FullText",
				xmlRecord.getNamespace());
		result.setHtmlAvailable("0");
		result.setPdfAvailable("0");
		if (null != fullText) {
			Element text = fullText.getChild("Text", fullText.getNamespace());
			if (null != text) {
				// 0 - embedded full text is not available
				// 1 - full text is available
				// -1 - database not configured to provide full text to
				// guests
				String htmlAvailable = text.getChildText("Availability",
						fullText.getNamespace());
				if (null != htmlAvailable && !htmlAvailable.isEmpty()
						&& htmlAvailable.equals("1")) {
					result.setHtmlAvailable("1");
					String htmlFullTextValue = text.getChildText("Value",
							fullText.getNamespace());
					if (null != htmlFullTextValue
							&& !htmlFullTextValue.isEmpty()) {
						if (parse)
							htmlFullTextValue = Jsoup.parse(htmlFullTextValue)
									.text().toString();
						// translate data to valid HTML tags
						htmlFullTextValue = TransDataToHTML
								.transDataToHTML(htmlFullTextValue);
					}
					result.setHtmlFullText(htmlFullTextValue);
				}
				result.setHtmlAvailable(htmlAvailable);
			}
			// determine whether or not there are full text links
			Element linkElement = fullText.getChild("Links",
					fullText.getNamespace());
			if (null != linkElement) {
				List<Element> links = linkElement.getChildren();
				if (null != links && 0 < links.size()) {
					ArrayList<Link> otherLinks = new ArrayList<Link>();
					for (int j = 0; j < links.size(); j++) {
						Element link = (Element) links.get(j);
						String type = link.getChildText("Type",
								link.getNamespace());
						String url = link.getChildText("Url",
								link.getNamespace());
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
		}
		// Process Items
		Element items = xmlRecord.getChild("Items", xmlRecord.getNamespace());
		if (null != items) {
			List<Element> itemList = items.getChildren();
			for (int j = 0; j < itemList.size(); j++) {
				Element itemElement = (Element) itemList.get(j);
				Item item = new Item();
				String label = itemElement.getChildText("Label",
						itemElement.getNamespace());
				String group = itemElement.getChildText("Group",
						itemElement.getNamespace());
				String itemData = itemElement.getChildText("Data",
						itemElement.getNamespace());

				if (parse)
					itemData = Jsoup.parse(itemData).text().toString();
				// translate data to valid HTML tags
				itemData = TransDataToHTML.transDataToHTML(itemData);
				item.setLabel(label);
				item.setGroup(group);
				item.setData(itemData);
				result.getItemList().add(item);
			}
		}
		return result;
	}

}
