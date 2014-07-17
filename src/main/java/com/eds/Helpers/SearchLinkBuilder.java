/*
 * This class build EDS API search links
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

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchLinkBuilder {

	/**
	 * Build a search link from EDS API response data for use within the web
	 * application
	 * 
	 * @param input
	 *            - search link data returned from the EDS API
	 * @return - a properly constructed search link for use within this
	 *         application
	 */
	@SuppressWarnings("deprecation")
	public static String buildLink(String input) {

		Pattern p = Pattern
				.compile("<searchLink fieldCode=\"([^\"]*)\" term=\"([^\"]*)\">");
		Matcher m = p.matcher(input);

		while (m.find()) {

			String link_value = "<searchLink fieldCode=\"([^\"]*)\" term=\"([^\"]*)\">";
			String queryValue = URLEncoder.encode(m.group(1) + " ")
					+ m.group(2).replace("\"", "%22");
			String link_html = "<a href='AdvancedSearch?query=" + queryValue
					+ "'>";

			input = input.replaceFirst(link_value, link_html);
			input = input.replace("</searchLink>", "</a>");

		}

		return input;

	}

}
