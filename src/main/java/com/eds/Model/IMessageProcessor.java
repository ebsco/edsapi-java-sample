/*
 * This interface specifies the methods required for generating request messages
 * and processing response messages to send to and returned from the EDSAPI.
 * Messages can be send and received in different formats (ex. XML and JSON)
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

import java.net.URLConnection;

import com.eds.bean.AuthToken;
import com.eds.bean.Info;
import com.eds.bean.RetrieveResponse;
import com.eds.bean.SearchResponse;
import com.eds.bean.ServiceResponse;
import com.eds.bean.SessionToken;

public interface IMessageProcessor {

	/**
	 * Constructs an AuthToken from an EBSCO Auth service response message
	 * 
	 * @param reader
	 *            - reponse data from the UID Auth request
	 * @return
	 */
	public AuthToken ProcessUIDAuthResponse(ServiceResponse reader);

	/**
	 * Constructs an uid Auth Request message to POST to the Authentication
	 * service
	 * 
	 * @param username
	 *            - user name associated with an EDS API EBSCO Admin account
	 * @param password
	 *            - password associated with an EDS API EBSCO Admin account
	 * @return
	 */
	public String BuildUIDAuthRequestMessage(String username, String password);

	/**
	 * Constructs a session token object from an EDS API Create Session response
	 * message
	 * 
	 * @param reader
	 *            - Response data from the Search request
	 * @return
	 */
	public SessionToken ProcessCreateSessionResponse(ServiceResponse reader);

	/**
	 * Constructs a Search response object from an EDS API Search response
	 * message
	 * 
	 * @param reader
	 *            - Response data from the create session request
	 * @return
	 */
	public SearchResponse ProcessSearchResponse(ServiceResponse serviceResponse);

	/**
	 * Constructs a Retrieve respones object from an EDS API Retrieve response
	 * message
	 * 
	 * @param reader
	 *            - Response data from the retrieve request
	 * @return
	 */
	public RetrieveResponse ProcessRetrieveResponse(ServiceResponse response);

	/**
	 * Constructs a info object from an EDS API Info response message
	 * 
	 * @param reader
	 *            - Response data from the Info request
	 * @return
	 */
	public Info ProcessInfoResponse(ServiceResponse reader);

	/**
	 * Processes and end session response message
	 * 
	 * @param reader
	 *            - Response data from the end session request
	 * @return - value indicating whether or not the operation was successful
	 */
	public String ProcessEndSessionResponse(ServiceResponse reader);

	/**
	 * The format of message that this Message processor parses.
	 * 
	 * @return
	 */
	public String GetContentType();

	/**
	 * Sets Header values associated with this message format
	 * 
	 * @param connection
	 *            - object to set headers on
	 */
	public void SetContentHeaders(URLConnection connection);

}
