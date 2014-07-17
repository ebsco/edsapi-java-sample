/*
 * This factory class generates an authentication manager object to properly
 * handle authentication for the EDS API
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

import javax.servlet.ServletContext;

public class AuthenticationFactory {

	/**
	 * Construct an authentication manager object to manager authentication with
	 * the EDS API.
	 * 
	 * @param servletConfig
	 *            Configuration parameters used to determine the type of
	 *            authentication and settings required
	 * @return An object that will handle the authentication mechanism necessary
	 *         to communicate with the EDS API. If no type is set, IP will be
	 *         used.
	 */
	public static IAuthenticationManager getAuthenticationManager(
			ServletContext servletContext) {
		AuthType authType = GetAuthType(servletContext.getAttribute(
				"authentication_type").toString());

		IAuthenticationManager authManager = null;
		switch (authType) {
		case UID:
			authManager = new UIDManager(servletContext);
			break;
		case IP:
		default:
			authManager = new IPAuthManager();
			break;
		}
		return authManager;
	}

	/**
	 * Convert the authType string to an AuthType enum value. For null, empty,
	 * or unknown auth types, AUTO_IP authentication is used
	 * 
	 * @param authType
	 *            String representing the type of authentication requested
	 * @return Enum value representing the authentication type
	 * 
	 */
	private static AuthType GetAuthType(String authType) {
		AuthType defaultType = AuthType.IP;

		if (null == authType || authType.isEmpty())
			return defaultType;

		if (authType.equals("UID"))
			return AuthType.UID;
		if (authType.equals("IP"))
			return AuthType.IP;
		// for all unknown authTypes, return the default type
		return defaultType;
	}

}
