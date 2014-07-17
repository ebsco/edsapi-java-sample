/*
 * This class handles managing authentication tokens to be used with the EDS API.
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
package com.eds.tokenManager;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.eds.Model.EDSAPI;
import com.eds.bean.SessionToken;

public class SessionTokenManager {

	/**
	 * Obtains a valid session token to be used with requests to the EDS API
	 * 
	 * @param request
	 *            incoming user request to obtain/add session tokens to.
	 * @param edsapi
	 *            service to process requests if a new session token needs to be
	 *            generated
	 * @return A valid session token object to use with EDS API requests
	 * @throws IOException
	 */
	public static SessionToken ManageSessionToken(HttpServletRequest request,
			EDSAPI edsapi) throws IOException {
		HttpSession session = (null == request) ? null : request.getSession();
		SessionToken sessionToken = null;
		if (null != session && null != session.getAttribute("sessiontoken"))
			sessionToken = (SessionToken) request.getSession().getAttribute(
					"sessiontoken");
		if (null == sessionToken) {
			sessionToken = edsapi.createSession("");
			request.getSession().setAttribute("sessiontoken", sessionToken);
		}
		return sessionToken;

	}

}
