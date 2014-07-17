/*
 * This class is used set the initial application configuration.
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
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eds.Authentication.AuthenticationFactory;
import com.eds.Authentication.IAuthenticationManager;
import com.eds.Model.EDSAPI;
import com.eds.bean.ApiErrorMessage;
import com.eds.bean.Info;
import com.eds.bean.SessionToken;

public class initApp extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Endpoint of the EBSCO EDS API
	 */
	private String edsapi_end_point;

	/**
	 * Format to send and receive messages in
	 */
	private String message_format;

	/**
	 * Endpoint of the EBSCO Authentication Service
	 */
	private String authentication_end_point;

	/**
	 * EBSCO Admin username
	 */
	private String user_name;

	/**
	 * EBSCO Admin password
	 */
	private String password;

	/**
	 * Type of Authentication to use in conjunction with the EDS API
	 */
	private String authentication_type;

	/**
	 * EBSCO Admin profile
	 */
	private String profile;

	/**
	 * Reads and set configuration settings from the servlet configuration and
	 * stores them in the properties
	 * 
	 * @param config
	 *            Servlet configuration to obtain settings from
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		edsapi_end_point = config.getInitParameter("edsapi_end_point")
				.toString();
		authentication_end_point = config.getInitParameter(
				"authentication_end_point").toString();
		authentication_type = config.getInitParameter("authentication_type")
				.toString();
		user_name = config.getInitParameter("user_name").toString();
		password = config.getInitParameter("password").toString();
		message_format = config.getInitParameter("message_format").toString();
		profile = config.getInitParameter("profile").toString();

	}

	/**
	 * Sets the servlet configuration properties as attributes in the the
	 * request servlets' context. This method includes the appropriate sequence
	 * of calls to obtain data from the info method. 1. call the authentication
	 * service to obtain an authentication token if necessary (see documentation
	 * regarding different authentication types). 2. Call the EDS API CREATE
	 * SESSION method to generate a new session for the call to INFO, 3. Call
	 * the EDS API INFO method, 4. Call the EDS API END SESSION method. This
	 * application assumes that all users will be using the same profile, and
	 * this will be sharing the data returned from the INFO method. If you
	 * application allows the user of more than one profile, then INFO needs to
	 * be stored at the session level.
	 * 
	 * @param request
	 *            context in which to set the application attributes on
	 * @param response
	 * 
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ApiErrorMessage errorMessage = null;
		ServletContext application = request.getServletContext();
		try {
			Object aep = application.getAttribute("config_set");
			if (null == aep || aep.toString().isEmpty()) {
				application.setAttribute("authentication_end_point",
						authentication_end_point);
				application.setAttribute("password", password);
				application.setAttribute("user_name", user_name);
				application.setAttribute("authentication_type",
						authentication_type);
				application.setAttribute("message_format", message_format);
				application.setAttribute("edsapi_end_point", edsapi_end_point);
				application.setAttribute("profile", profile);

				IAuthenticationManager authManager = AuthenticationFactory
						.getAuthenticationManager(request.getServletContext());
				EDSAPI edsapi = new EDSAPI(edsapi_end_point, message_format,
						authManager, "");
				SessionToken sessionToken = edsapi.createSession(profile);
				// display error page.
				if (null == sessionToken) {
					errorMessage = new ApiErrorMessage();
					errorMessage
							.setErrorDescription("Unknown error occurred during session creation.");
				} else if (null != sessionToken.getApiErrorMessage())
					errorMessage = sessionToken.getApiErrorMessage().clone();
				else {
					edsapi.setSessionToken(sessionToken.getSessionToken());
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("profile", new String[] { profile });
					Info info = edsapi.requestInfo(parameters);
					if (null == info) {
						errorMessage = new ApiErrorMessage();
						errorMessage
								.setErrorDescription("Unknown error occurred during Info request.");
					} else if (null != info.getErrorMessage())
						errorMessage = info.getErrorMessage().clone();
					else {
						application.setAttribute("info", info);
						application.setAttribute("config_set", "1");
					}
				}
				edsapi.endSession();
			}
		} catch (IOException e) {
			errorMessage = new ApiErrorMessage();
			errorMessage.setErrorDescription("An unknown error occurred");
		}
		String page = "index.jsp";
		if (null != errorMessage) {
			page = "error.jsp";
			request.getSession().setAttribute("errorMessage", errorMessage);
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		dispatcher.forward(request, response);
	}

	/**
	 * Sets the servlet configuration properties as attributes in the the
	 * request servlets' context. This method includes the appropriate sequence
	 * of calls to obtain data from the info method. 1. call the authentication
	 * service to obtain an authentication token if necessary (see documentation
	 * regarding different authentication types). 2. Call the EDS API CREATE
	 * SESSION method to generate a new session for the call to INFO, 3. Call
	 * the EDS API INFO method, 4. Call the EDS API END SESSION method. This
	 * application assumes that all users will be using the same profile, and
	 * this will be sharing the data returned from the INFO method.
	 * 
	 * @param request
	 *            context in which to set the application attributes on
	 * @param response
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
