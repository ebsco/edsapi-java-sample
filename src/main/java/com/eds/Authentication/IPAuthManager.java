/*
 * This class manages IP Authentication for the EDS API.
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

public class IPAuthManager implements IAuthenticationManager {

	public IPAuthManager() {
	}

	/**
	 * Interface implementation to add the appropriate authentication
	 * parameters. This type of authentication doesn't require the addition of
	 * anything the the request to the request.
	 * 
	 * @param connection
	 *            Request to add authentication information to
	 */

	public void AddAuthenticationToRequest(URLConnection connection)
			throws IOException {
		// Auto IP Authentication does not add anything to the request

	}

}
