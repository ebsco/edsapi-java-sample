/*
 * This interface represents the required methods for handling authentication in
 * conjunction with the EDS API
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

public interface IAuthenticationManager {

	/**
	 * Add the appropriate authentication parameters to the request
	 * 
	 * @param connection
	 *            Request to add authentication information to
	 * @throws IOException
	 */
	void AddAuthenticationToRequest(URLConnection connection)
			throws IOException;

}