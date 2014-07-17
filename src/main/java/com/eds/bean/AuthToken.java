/* 
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
package com.eds.bean;

import java.util.Calendar;

public class AuthToken {

	private String authToken;
	private String authTimeout;
	private Calendar CreationTime;
	private ApiErrorMessage errorMessage;

	public ApiErrorMessage getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(ApiErrorMessage errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Calendar getCreationTime() {
		return this.CreationTime;
	}

	public void setCreationTime(Calendar creationTime) {
		this.CreationTime = creationTime;
	}

	public AuthToken() {
		this.setCreationTime(Calendar.getInstance());
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthTimeout(String timeout) {
		authTimeout = timeout;
	}

	public String getAuthTimeout() {
		return authTimeout;
	}

	public Calendar getExpirationTime() {

		if (null == this.getCreationTime())
			return Calendar.getInstance();
		Calendar expirationTime = (Calendar) this.CreationTime.clone();
		Integer timeout = Integer.valueOf(this.getAuthTimeout());
		if (null != timeout)
			expirationTime.add(Calendar.SECOND, timeout);
		return expirationTime;

	}
}
