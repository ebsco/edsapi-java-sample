/*
 * This class manages the handling of service requests
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import com.eds.Authentication.IAuthenticationManager;
import com.eds.bean.ServiceResponse;

public class ServiceRequestProcessor {

	/**
	 * Process a GET Request
	 * 
	 * @param end_point
	 *            URL to send the request to
	 * @param headers
	 *            Header to append to the request
	 * @param parameters
	 *            Key/values pairs to construct querystring parameters from
	 * @param messageProcessor
	 *            Object to handle the parsing/constructing or
	 *            responses/requests
	 * @return Object representing the service response
	 */
	public static ServiceResponse Get(String end_point,
			Map<String, String> headers, Map<String, String[]> parameters,
			IMessageProcessor messageProcessor) {
		return Get(end_point, headers, parameters, messageProcessor, null);
	}

	/**
	 * Process a POST request
	 * 
	 * @param end_point
	 *            URL to send the request to
	 * @param headers
	 *            Header to append to the request
	 * @param messageBody
	 *            Data to POST
	 * 
	 * @param messageProcessor
	 *            Object to handle the parsing/constructing or
	 *            responses/requests
	 * @return Object representing the service response
	 */
	public static ServiceResponse Post(String end_point,
			Map<String, String> headers, String messageBody,
			IMessageProcessor messageProcessor) {
		return Post(end_point, headers, messageBody, messageProcessor, null);
	}

	/**
	 * Process a GET Request
	 * 
	 * @param end_point
	 *            URL to send the request to
	 * @param headers
	 *            Header to append to the request
	 * @param parameters
	 *            Key/values pairs to construct querystring parameters from
	 * @param messageProcessor
	 *            Object to handle the parsing/constructing or
	 *            responses/requests
	 * @param authProcessor
	 *            Object to handle authentication for this request
	 * @return Object representing the service response
	 */
	@SuppressWarnings("deprecation")
	public static ServiceResponse Get(String end_point,
			Map<String, String> headers, Map<String, String[]> parameters,
			IMessageProcessor messageProcessor,
			IAuthenticationManager authProcessor) {
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		String errorStream = "";
		ServiceResponse serviceResponse = new ServiceResponse();
		String errorNumber = "";

		try {
			String url = end_point;
			// Add query string parameters
			StringBuilder queryStringParameters = new StringBuilder();
			if (null != parameters && !parameters.isEmpty()) {
				for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
					String name = entry.getKey();
					for (String value : entry.getValue()) {
						value = URLEncoder.encode(value);
						if (!name.isEmpty() && !value.isEmpty())
							queryStringParameters.append(String.format(
									"%s=%s&", name, value));
					}
				}
				if (queryStringParameters.length() > 0) {
					String paramString = queryStringParameters.toString();
					if (paramString.endsWith("&"))
						paramString = paramString.substring(0,
								paramString.length() - 1);
					url = url + "?" + paramString;
				}
			}
			URL geturl = new URL(url);
			connection = (HttpURLConnection) geturl.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			if (null != headers && !headers.isEmpty()) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					if (!key.isEmpty() && !value.isEmpty())
						connection.setRequestProperty(key, value);
				}
			}
			// Set the content type and Accept headers
			messageProcessor.SetContentHeaders(connection);

			// Process any required authenticaiton
			if (null != authProcessor)
				authProcessor.AddAuthenticationToRequest(connection);

			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

		} catch (IOException ioe) {

			InputStream error = ((HttpURLConnection) connection)
					.getErrorStream();

			try {
				errorNumber = ((HttpURLConnection) connection)
						.getResponseCode() + "";
			} catch (IOException e) {
			}
			errorStream = GetErrorStream(error);
		} catch (Exception e) {
		}
		serviceResponse.setErrorStream(errorStream);
		serviceResponse.setReader(reader);
		serviceResponse.setErrorNumber(errorNumber);
		return serviceResponse;

	}

	/**
	 * Process a POST request
	 * 
	 * @param end_point
	 *            URL to send the request to
	 * @param headers
	 *            Header to append to the request
	 * @param messageBody
	 *            Data to POST
	 * @param messageProcessor
	 *            Object to handle the parsing/constructing or
	 *            responses/requests
	 * @param authProcessor
	 *            Object to handle authentication for this request
	 * 
	 * @return Object representing the service response
	 */
	public static ServiceResponse Post(String end_point,
			Map<String, String> headers, String messageBody,
			IMessageProcessor messageProcessor,
			IAuthenticationManager authProcessor) {

		HttpURLConnection connection = null;
		BufferedReader reader = null;
		String errorStream = "";
		ServiceResponse serviceResponse = new ServiceResponse();
		String errorNumber = "";

		try {
			String url = end_point;
			URL geturl = new URL(url);
			connection = (HttpURLConnection) geturl.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("charset", "utf-8");
			// If a message body is present, POST instead of GET
			if (null != messageBody && !messageBody.isEmpty()) {
				connection.setRequestMethod("POST");
				connection.setRequestProperty("charset", "utf-8");
				connection.setRequestProperty("Content-Length",
						"" + Integer.toString(messageBody.getBytes().length));
				// connection.setRequestProperty("Content-Length",
				// Integer.toString(messageBody.length()));
			}
			if (null != headers && !headers.isEmpty()) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					if (!key.isEmpty() && !value.isEmpty())
						connection.setRequestProperty(key, value);
				}
			}
			// Set the content type and Accept headers
			messageProcessor.SetContentHeaders(connection);
			// Process any required authenticaiton
			if (null != authProcessor)
				authProcessor.AddAuthenticationToRequest(connection);
			// Send requeset
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(messageBody);
			wr.flush();
			wr.close();
			connection.disconnect();
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
		} catch (IOException ioe) {
			InputStream error = ((HttpURLConnection) connection)
					.getErrorStream();
			try {
				errorNumber = ((HttpURLConnection) connection)
						.getResponseCode() + "";
			} catch (IOException e) {
			}
			errorStream = GetErrorStream(error);
		}
		serviceResponse.setErrorStream(errorStream);
		serviceResponse.setReader(reader);
		serviceResponse.setErrorNumber(errorNumber);
		return serviceResponse;

	}

	/**
	 * Obtain the value of an error stream
	 * 
	 * @param error
	 *            - InputStream that contains an error message
	 * @return - string representation of the error stream
	 */
	private static String GetErrorStream(InputStream error) {

		String ErrorStrem = "";

		try {
			int data = error.read();
			while (data != -1) {

				ErrorStrem = ErrorStrem + (char) data;
				data = error.read();

			}
			error.close();
		} catch (Exception ex) {
			try {
				if (error != null) {
					error.close();
				}
			} catch (Exception e) {

			}
		}

		return ErrorStrem;
	}

}
