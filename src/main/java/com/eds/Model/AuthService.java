/*
 * This class is used to send requests and process responses from the Auth
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

import com.eds.bean.AuthToken;
import com.eds.bean.ServiceResponse;

public class AuthService {

	/**
	 * URL of the EBSCO Auth Service
	 */
	private String end_point;
	/**
	 * Message process to use to construct requests and process responses.
	 */
	private IMessageProcessor messageProcessor = null;

	public AuthService(String end_point, String messagingFormat) {
		this.end_point = end_point;
		this.messageProcessor = MessageProcessorFactory
				.getFormatter(messagingFormat);
	}

	/**
	 * Constructs an Auth Service UIDAUth Request, executes the request,
	 * processes the response message, and returns the generated authentication
	 * token and related data
	 * 
	 * @param username
	 *            the username for a valid EDS API account in EBSCO admin
	 * @param password
	 *            password for a valid EDS API account in EBSCO admin
	 */
	public AuthToken UIDAuth(String username, String password)
			throws IOException {
		String url = end_point + "/uidauth";
		String requestMessageBody = messageProcessor
				.BuildUIDAuthRequestMessage(username, password);
		ServiceResponse reader = ServiceRequestProcessor.Post(url, null,
				requestMessageBody, messageProcessor);
		AuthToken authToken = messageProcessor.ProcessUIDAuthResponse(reader);
		return authToken;
	}

}
