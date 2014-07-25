/*
 * This class is used to send requests and process responses from the EDS API
 * Service
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.eds.Authentication.IAuthenticationManager;
import com.eds.bean.Info;
import com.eds.bean.RetrieveResponse;
import com.eds.bean.SearchResponse;
import com.eds.bean.ServiceResponse;
import com.eds.bean.SessionToken;

public class EDSAPI {

	/**
	 * URL of the EBSCO EDS API
	 */
	private String end_point;

	/**
	 * Message process to use to construct requests and process responses.
	 */
	private IMessageProcessor messageProcessor = null;

	/**
	 * Authentication manager to handle authentication processing for API
	 * request
	 */
	private IAuthenticationManager authenticationManager = null;

	/**
	 * EBSCO Admin profile name to be used for EDS API requests
	 */
	private String profile = null;

	/**
	 * Session token to use with EDS API requests
	 */
	private String sessionToken = null;

	public String getSessionToken() {
		return this.sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public EDSAPI(String end_point, String messagingFormat,
			IAuthenticationManager authenticationManager, String sessionToken) {
		this.end_point = end_point;
		this.messageProcessor = MessageProcessorFactory
				.getFormatter(messagingFormat);
		this.authenticationManager = authenticationManager;
		this.sessionToken = sessionToken;
		this.profile = "";// profile;
	}

	/**
	 * Constructs an EDS API CreateSession request, executes the request,
	 * processes the response message, and returns the generated session token
	 * and related data
	 * 
	 * @param profile
	 *            EBSCO Admin profile name to be used for EDS API requests
	 */
	public SessionToken createSession(String profile) throws IOException {
		String url = end_point + "/CreateSession";
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("profile", new String[] { profile });
		ServiceResponse response = ServiceRequestProcessor.Get(url, null,
				parameters, messageProcessor, authenticationManager);
		return messageProcessor.ProcessCreateSessionResponse(response);
	}
	
	public SessionToken createSession(String profile, String guest) throws IOException {
		String url = end_point + "/CreateSession";
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("profile", new String[] { profile });
		parameters.put("guest", new String[] { guest });
		ServiceResponse response = ServiceRequestProcessor.Get(url, null,
				parameters, messageProcessor, authenticationManager);
		return messageProcessor.ProcessCreateSessionResponse(response);
	}

	/**
	 * Constructs an EDS API EndSession request, executes the request, processes
	 * the response message, and returns a 'y' if the session was ended
	 * successfully, or a 'n' if it was not
	 */
	public String endSession() throws IOException {

		String url = end_point + "/EndSession";
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("sessiontoken", new String[] { sessionToken });
		ServiceResponse response = ServiceRequestProcessor.Get(url, null,
				parameters, messageProcessor, authenticationManager);
		return messageProcessor.ProcessEndSessionResponse(response);

	}

	/**
	 * Constructs an EDS API search request, executes the request, processes the
	 * response message, and returns the search response data
	 * 
	 * @param parameters
	 *            data request parameters to send the the EDS API search method
	 */
	public SearchResponse search(Map<String, String[]> parameters) {
		String url = end_point + "/Search";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("x-sessionToken", sessionToken);
		ServiceResponse response = ServiceRequestProcessor.Get(url, headers,
				parameters, messageProcessor, authenticationManager);
		return messageProcessor.ProcessSearchResponse(response);
	}

	/**
	 * Constructs an EDS API retrieve request, executes the request, processes
	 * the response message, and returns the retrieval response data
	 * 
	 * @param parameters
	 *            data request parameters to send the the EDS API retrieve
	 *            method
	 */
	public RetrieveResponse requestRetrieve(Map<String, String[]> parameters)
			throws IOException {
		String url = end_point + "/Retrieve";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("x-sessionToken", sessionToken);
		ServiceResponse response = ServiceRequestProcessor.Get(url, headers,
				parameters, messageProcessor, authenticationManager);
		return messageProcessor.ProcessRetrieveResponse(response);
	}

	/**
	 * Constructs an EDS API Info request, executes the request, processes the
	 * response message, and returns the info response data
	 * 
	 * @param parameters
	 *            data request parameters to send the the EDS API info method
	 */
	public Info requestInfo(Map<String, String[]> parameters)
			throws IOException {
		String url = end_point + "/Info";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("x-sessionToken", sessionToken);
		ServiceResponse response = ServiceRequestProcessor.Get(url, headers,
				parameters, messageProcessor, authenticationManager);
		return messageProcessor.ProcessInfoResponse(response);
	}

	/**
	 * 
	 * @return the the EBSCO Admin profile associated with EDS API requests
	 */
	public String getProfile() {
		return profile;
	}

	/**
	 * 
	 * @param profile
	 *            profile the related EDS API requests are to be assocated with
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

}
