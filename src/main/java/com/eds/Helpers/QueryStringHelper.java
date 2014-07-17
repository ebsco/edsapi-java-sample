/*
 * This class Escapes and Un-Escapes reserved EDS API characters
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

public class QueryStringHelper {

	/**
	 * Escapes reserved EDS API characters
	 * 
	 * @param value
	 *            - string value to replace special characters within
	 * @return a valid string with special characters replaced
	 */
	public static String Escape(String value) {
		if (value == null || value.trim().equals(""))
			return "";
		value = value.replace(",", "\\,");
		value = value.replace(":", "\\:");
		value = value.replace("(", "\\(");
		value = value.replace(")", "\\)");
		return value;
	}

	/**
	 * Removes escaping characters from a string
	 * 
	 * @param value
	 *            - string value containing escaped characters
	 * @return a valid string with escape characters removed
	 */
	public static String UnEscape(String value) {
		if (value == null || value.trim().equals(""))
			return "";
		value = value.replace("\\,", ",");
		value = value.replace("\\:", ":");
		value = value.replace("\\(", "(");
		value = value.replace("\\)", ")");
		return value;
	}

}
