/*
 * This class is responsible for responding to retrieval requests
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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eds.Authentication.AuthenticationFactory;
import com.eds.Authentication.IAuthenticationManager;
import com.eds.Helpers.QueryStringHelper;
import com.eds.Model.EDSAPI;
import com.eds.bean.ApiErrorMessage;
import com.eds.bean.Link;
import com.eds.bean.RetrieveResponse;
import com.eds.bean.SessionToken;

public class Retrieve extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Process incoming GET retrieve requests and create a record listing
	 * 
	 * @param request
	 *            the servlet request
	 * @param response
	 *            the servlet respones
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ApiErrorMessage errorMessage = null;
		// obtain request parameters
		String an = request.getParameter("an");
		String dbid = request.getParameter("dbid");
		String highlight = request.getParameter("highlight");
		String directRetrievalType = request.getParameter("type");
		RetrieveResponse record = null;

		// build the querystring to send to the EDS API
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("an", new String[] { QueryStringHelper.Escape(an) });
		parameters.put("dbid", new String[] { QueryStringHelper.Escape(dbid) });
		parameters.put("highlightterms",
				new String[] { QueryStringHelper.Escape(highlight) });

		// obtain the end_point, authentication_end_point and messageFormat from
		// java
		// servlet application scope
		String edsapi_end_point = (String) getServletContext().getAttribute(
				"edsapi_end_point");
		String message_format = (String) getServletContext().getAttribute(
				"message_format");
		// Obtain the session token associated with the users session
		String sessionToken = (String) request.getSession().getAttribute(
				"session_token");
		// generate an authentication manager to handle authentication
		// method the application is configured to use
		IAuthenticationManager authManager = AuthenticationFactory
				.getAuthenticationManager(request.getServletContext());

		// Generate an eds api object from the above settings
		EDSAPI api = new EDSAPI(edsapi_end_point, message_format, authManager,
				sessionToken);
		request.setAttribute("url", request.getRequestURL() + "?" + request.getQueryString());
		try {
			// call the EDS API Retrieve method
			record = api.requestRetrieve(parameters);

			if (null != record.getApiErrorMessage()
					&& null != record.getApiErrorMessage().getErrorNumber()
					&& !record.getApiErrorMessage().getErrorNumber().isEmpty()) {
				// Add additional logic here to handle errors appropriately for
				// your usage. Correct the problem
				// and retry if appropriate
				switch (Integer.parseInt(record.getApiErrorMessage()
						.getErrorNumber())) {
				case 108: // session token missing
				case 109: // session token invalid
					// regenerate the session token and re-execute the
					// search
					String profile = (String) request.getServletContext()
							.getAttribute("profile");
					SessionToken token = api.createSession(profile, "y");
					if (null != token && !token.getSessionToken().isEmpty()) {
						request.getSession().setAttribute("session_token",
								token.getSessionToken());
						api.setSessionToken(token.getSessionToken());
						// Retry the retrieve request
						record = api.requestRetrieve(parameters);
					}
					break;
				}
			}
			if (null == record) {
				errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error encountered during retrieve.");
			} else if (null != record.getApiErrorMessage())
				errorMessage = record.getApiErrorMessage().clone();
			request.getSession().setAttribute("record", record);

		} catch (Exception e) {
			errorMessage = new ApiErrorMessage();
			errorMessage.setErrorDescription("An unknown error occurred");
		}
		String page = "record.jsp";
		if (null != errorMessage) {
			page = "error.jsp";
			request.getSession().setAttribute("errorMessage", errorMessage);
		}

		// if the 'type' parameter was set, then display the appropriate full
		// text without going to the record page.
		// if there is no full text link, the display the record page.
		String url = "";
		if (null == errorMessage && null != directRetrievalType
				&& !directRetrievalType.isEmpty() && null != record
				&& null != record.getResult()) {
			if (directRetrievalType.equalsIgnoreCase("pdf")) {
				url = record.getResult().getPdfLink();
			} else if (null != record.getResult().getOtherFullTextLinks()) {
				for (Link link : record.getResult().getOtherFullTextLinks()) {
					if (link.getType().equalsIgnoreCase(directRetrievalType)) {
						url = link.getUrl();
						break;
					}
				}
			}
		}
		if (null != url && !url.isEmpty()) {
			response.sendRedirect(url);
		} else {
			RequestDispatcher dispatcher = request.getRequestDispatcher(page);
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Process incoming retrieve POST requests and create a record listing
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

}
