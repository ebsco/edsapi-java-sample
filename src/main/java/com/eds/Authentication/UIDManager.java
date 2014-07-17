/*
 * This class manages UID Authentication for the EDS API.
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

package com.eds.Authentication;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Calendar;

import javax.servlet.ServletContext;

import com.eds.Model.AuthService;
import com.eds.bean.AuthToken;

public class UIDManager implements IAuthenticationManager {

	/**
	 * User name associated with Admin account
	 */
	private String username;
	/**
	 * Password associated with Admin account
	 */
	private String password;

	/**
	 * Authentication service to use
	 */
	private AuthService authService = null;

	/**
	 * Request context to obtain settings from
	 */
	private ServletContext servletContext;

	/**
	 * Constructs a UID Authentication manager object
	 * 
	 * @param servletContext
	 *            Request context to obtain settings from
	 */
	public UIDManager(ServletContext servletContext) {
		String authentication_endpoint = servletContext.getAttribute(
				"authentication_end_point").toString();
		String message_format = servletContext.getAttribute("message_format")
				.toString();
		this.username = servletContext.getAttribute("user_name").toString();
		this.password = servletContext.getAttribute("password").toString();
		this.servletContext = servletContext;
		this.authService = new AuthService(authentication_endpoint,
				message_format);
	}

	/**
	 * Obtains the current authentication from the request contexts cache.
	 * 
	 * @return a valid Authentication token object
	 */
	private AuthToken GetAuthTokenFromCache() {
		AuthToken authToken = null;
		try {
			authToken = (AuthToken) servletContext
					.getAttribute("authenticationToken");
			if (null == authToken || null == authToken.getAuthToken()
					|| authToken.getAuthToken().isEmpty())
				authToken = null;
		} catch (Exception e) {
			authToken = null;
		}
		return authToken;
	}

	/**
	 * Interface implementation to add the appropriate authentication
	 * parameters.
	 * 
	 * @param connection
	 *            Request to add authentication information to
	 */
	public void AddAuthenticationToRequest(URLConnection connection)
			throws IOException {
		// Get the authentication token from the servlet cache
		AuthToken authToken = GetValidAuthToken();
		String tokenValue = authToken.getAuthToken();
		if (null != tokenValue && !tokenValue.isEmpty())
			connection.setRequestProperty("x-authenticationToken",
					authToken.getAuthToken());
	}

	/**
	 * Obtains a valid, non-expired authentication token object from the servlet
	 * cache. If none exist or the current one is expired, a new one is
	 * generated with a call to the Authentication service
	 * 
	 * @return A valid authentication token object
	 * @throws IOException
	 */
	private AuthToken GetValidAuthToken() throws IOException {

		AuthToken authToken = GetAuthTokenFromCache();
		// if a token is present, verify that it has not expired.
		if (null != authToken && null != authToken.getAuthToken()
				&& !authToken.getAuthToken().isEmpty()) {
			Calendar currentTime = Calendar.getInstance();
			Calendar expirationTime = authToken.getExpirationTime();
			Long millSecTimeDifference = expirationTime.getTimeInMillis()
					- currentTime.getTimeInMillis();
			if (millSecTimeDifference <= 5 * 60 * 1000)
				authToken = null;
		}
		// Genreate a new authentication if a valid on is not found.
		if (null == authToken)
			authToken = authService.UIDAuth(username, password);
		servletContext.setAttribute("authenticationToken", authToken);
		return authToken;
	}
}
