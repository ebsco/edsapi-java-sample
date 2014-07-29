/*
 * This class is responsible for responding to search requests
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
package com.eds.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eds.Authentication.AuthenticationFactory;
import com.eds.Authentication.IAuthenticationManager;
import com.eds.Model.EDSAPI;
import com.eds.bean.ApiErrorMessage;
import com.eds.bean.AvailableRelatedContent;
import com.eds.bean.Info;
import com.eds.bean.Query;
import com.eds.bean.QueryStringParameterNames;
import com.eds.bean.SearchRequest;
import com.eds.bean.SearchResponse;
import com.eds.bean.SessionToken;

public class AdvancedSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Processes incoming GET search requests and process the results to create
	 * the result listing.
	 * 
	 * @param request
	 *            the servlet request
	 * @param response
	 *            the servlet response
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ApiErrorMessage errorMessage = null;
		HttpSession session = request.getSession();
		SearchResponse searchResponse = null;
		try {
			ServletContext application = getServletContext();
			// Build a search request object from values set int he request
			// context
			SearchRequest searchRequest = BuildSearchRequest(request);

			// obtain the end_point and messageFormat from java
			// servlet application scope
			String edsapi_end_point = (String) application
					.getAttribute("edsapi_end_point");
			String message_format = (String) application
					.getAttribute("message_format");

			// Generate the appropriate EDS API parameters
			Map<String, String[]> parameters = searchRequest.getApiParameters();

			// generate an authentication manager to handle authentication
			// method the application is configured to use
			IAuthenticationManager authManager = AuthenticationFactory
					.getAuthenticationManager(request.getServletContext());

			// Obtain (if set) the session token associated with this user
			// session
			String sessionToken = (String) session
					.getAttribute("session_token");
			// Generate an eds api object from the above settings
			EDSAPI api = new EDSAPI(edsapi_end_point, message_format,
					authManager, sessionToken);
			// if there is no session token present, generate a new one
			if (null == sessionToken || sessionToken.isEmpty()) {
				String profile = (String) request.getServletContext()
						.getAttribute("profile");
				String isGuest ="n";
				if(null == session.getAttribute("userId")|| session.getAttribute("userId")!= application.getAttribute("user_name"))
					isGuest = "y";
				SessionToken token = api.createSession(profile, isGuest);
				request.getSession().setAttribute("session_token",
						token.getSessionToken());
				if (null != token && null != token.getSessionToken()
						&& !token.getSessionToken().isEmpty()) {
					request.getSession().setAttribute("session_token",
							token.getSessionToken());
					api.setSessionToken(token.getSessionToken());
				}
			}
			// send the search request to the API
			searchResponse = api.search(parameters);
			// re-submit request if the session token is no longer valid
			if (null != searchResponse.getApierrormessage()
					&& null != searchResponse.getApierrormessage()
							.getErrorNumber()
					&& !searchResponse.getApierrormessage().getErrorNumber()
							.isEmpty()) {
				// Add additional logic here to handle errors appropriately for
				// your usage. Correct the problem
				// and retry if appropriate
				switch (Integer.parseInt(searchResponse.getApierrormessage()
						.getErrorNumber())) {
				case 108: // session token missing
				case 109: // session token invalid
					String profile = (String) request.getServletContext()
							.getAttribute("profile");
					String isGuest ="n";
					if(null == session.getAttribute("userId")|| !session.getAttribute("userId").equals(application.getAttribute("user_name")))
						isGuest = "y";
					SessionToken token = api.createSession(profile, isGuest);
					request.getSession().setAttribute("session_token",
							token.getSessionToken());
					if (null != token && null != token.getSessionToken()
							&& !token.getSessionToken().isEmpty()) {
						request.getSession().setAttribute("session_token",
								token.getSessionToken());
						api.setSessionToken(token.getSessionToken());
						// re-send the search request
						searchResponse = api.search(parameters);
					}
					break;
				}
			}
			if (null == searchResponse) {
				errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error encountered during search.");
			} else if (null != searchResponse.getApierrormessage())
				errorMessage = searchResponse.getApierrormessage().clone();

			// set the result list on the session
			session.setAttribute("resultsList", searchResponse);
		} catch (Exception e) {
			errorMessage = new ApiErrorMessage();
			errorMessage.setErrorDescription("An unknown error occurred");
		}
		String page = "resultsList.jsp";
		if (null != errorMessage) {
			page = "error.jsp";
			request.getSession().setAttribute("errorMessage", errorMessage);
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		//Save URL to return after login
		String thisUrl = request.getRequestURL() + "?" + request.getQueryString();
		request.setAttribute("url", thisUrl);
		//This helps if there is a failed login attempt
		session.setAttribute("lastPageVisited", thisUrl);
		//This is for our back to result list button on the record page
		session.setAttribute("resultListUrl", thisUrl);

		dispatcher.forward(request, response);
	}

	/**
	 * Process incoming POST search requests and create a result listing
	 * 
	 * @param request
	 *            the servlet request
	 * @param response
	 *            the servlet respones
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * This method builds a search request object to send to the EDS API from
	 * properties set on the servlet request
	 * 
	 * @param request
	 *            the servlet request
	 * @return SearchRequest a populated search request object
	 */
	private SearchRequest BuildSearchRequest(HttpServletRequest request) {
		SearchRequest searchRequest = new SearchRequest();
		String indexedQuery = request
				.getParameter(QueryStringParameterNames.queryIndexed);
		if (null != indexedQuery && !indexedQuery.isEmpty())
			searchRequest.query = Query.PopulateFromQueryString(request
					.getParameter(QueryStringParameterNames.queryIndexed));
		else {
			String fieldCode = request.getParameter("fieldcode");
			String term = request.getParameter(QueryStringParameterNames.query);
			searchRequest.query = new Query();
			searchRequest.query.setFieldCode(fieldCode);
			searchRequest.query.setTerm(term);
		}
		ServletContext application = getServletContext();
		Info info = (Info) application.getAttribute("info");
		ArrayList<String> rcList = new ArrayList<String>();
		if (info.getAvailableRelatedContent() != null) {
			for (int i = 0; i < info.getAvailableRelatedContent().size(); i++) {
				AvailableRelatedContent currentRelatedContent = info
						.getAvailableRelatedContent().get(i);
				if (currentRelatedContent.getDefaultOn().equals("y")) {
					rcList.add(currentRelatedContent.getType());
				}
			}
			searchRequest.relatedContent = new String[rcList.size()];
			searchRequest.relatedContent = rcList
					.toArray(searchRequest.relatedContent);
		}
		// Get get values of parameters that there may be more than one of
		searchRequest.actions = request
				.getParameterValues(QueryStringParameterNames.action);
		searchRequest.expander = request
				.getParameter(QueryStringParameterNames.expander);
		searchRequest.facetFilters = request
				.getParameterValues(QueryStringParameterNames.facetFilter);
		searchRequest.highlight = request
				.getParameter(QueryStringParameterNames.highlight);
		searchRequest.includeFacets = request
				.getParameter(QueryStringParameterNames.includeFacets);
		searchRequest.limiters = request
				.getParameterValues(QueryStringParameterNames.limiter);
		searchRequest.pageNumber = request
				.getParameter(QueryStringParameterNames.pageNumber);
		if (null == searchRequest.pageNumber
				|| searchRequest.pageNumber.isEmpty())
			searchRequest.pageNumber = "1";
		searchRequest.resultsPerPage = request
				.getParameter(QueryStringParameterNames.resultsPerPage);
		searchRequest.searchmode = request
				.getParameter(QueryStringParameterNames.searchmode);
		searchRequest.sort = request
				.getParameter(QueryStringParameterNames.sort);
		searchRequest.view = request.getParameter("pageoption");
		return searchRequest;
	}

}
