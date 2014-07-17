/*
 * This class pulls named parameters values from a querystring
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
package com.eds.Helpers;

import com.eds.bean.QueryStringParameterNames;

public class QueryStringParameterHelper {

	/**
	 * Obtains the value of a querystring parameter
	 * 
	 * @param parameterName
	 *            - Parameter to pull from querystring
	 * @param queryString
	 *            - Querystring to pull name/values pairs from
	 * @return - string value of the @parameterName requested
	 */
	public static String GetValue(String parameterName, String queryString) {
		String value = "";
		if (null == parameterName || parameterName.isEmpty()
				|| null == queryString || queryString.isEmpty())
			return value;
		int position = queryString.toLowerCase().indexOf(parameterName);
		if (-1 < position) {
			int len = parameterName.length();
			String rest = queryString.substring(position + len + 1);
			int eov = rest.indexOf('&');
			if (eov < 0)
				eov = rest.length();
			value = rest.substring(0, eov);
		}
		return value;
	}

	/**
	 * Obtains the sort querystring parameter value
	 * 
	 * @param queryString
	 *            - querystring to obtain the value from
	 * @return - the sort value
	 */
	public static String GetSort(String queryString) {
		return GetValue(QueryStringParameterNames.sort, queryString);
	}

	/**
	 * Obtains the view querystring parameter value
	 * 
	 * @param queryString
	 *            - querystring to obtain the value from
	 * @return - the view value
	 */
	public static String GetView(String queryString) {
		return GetValue(QueryStringParameterNames.view, queryString);
	}

	/**
	 * Obtains the results per page querystring parameter value
	 * 
	 * @param queryString
	 *            - querystring to obtain the value from
	 * @return - results per page number value. If not present or empty, 1 is
	 *         returned
	 */
	public static String GetResultPerPage(String queryString) {
		String value = GetValue(QueryStringParameterNames.resultsPerPage,
				queryString);
		if (value.isEmpty())
			value = "1";
		return value;
	}

	/**
	 * Obtains the page number querystring parameter value
	 * 
	 * @param queryString
	 *            - querystring to obtain the value from
	 * @return - page number value. If not present or empty, 1 is returned
	 */
	public static String GetPageNumber(String queryString) {
		String pageNum = GetValue(QueryStringParameterNames.pageNumber,
				queryString);
		if (pageNum.isEmpty())
			pageNum = "1";
		return pageNum;
	}
}
