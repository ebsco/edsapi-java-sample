/*
 * This class takes markup returned from the EDS API and creates valid HTML tags
 * for them.
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

public class TransDataToHTML {

	/**
	 * Replace markup with standard HTML tags
	 * 
	 * @param map
	 *            - String containing markup
	 * @return - string containing valid HTML tags
	 */
	public static String transDataToHTML(String map) {
		map = map.replace("<jsection", "<section");
		map = map.replace("<jsection", "</section");
		map = map.replace("<highlight", "<span class=highlight");
		map = map.replace("</highlight>", "</span>");
		map = map.replace("<text", "<div");
		map = map.replace("</text", "</div");
		map = map.replace("<title", "<h2");
		map = map.replace("</title", "</h2");
		map = map.replace("<anid", "<p");
		map = map.replace("</anid", "</p");
		map = map.replace("<aug", "<strong");
		map = map.replace("</aug", "</strong");
		map = map.replace("<hd", "<h3");
		map = map.replace("</hd", "</h3");
		map = map.replace("<linebr", "<br");
		map = map.replace("</linebr", "");
		map = map.replace("<olist", "<ol");
		map = map.replace("</olistt", "</ol");
		map = map.replace("<reflink", "<a");
		map = map.replace("</reflink", "</a");
		map = map.replace("<blist", "<p class=blist");
		map = map.replace("</blist", "<a");
		map = map.replace("<bibl", "</section");
		map = map.replace("</bibl", "</a");
		map = map.replace("<bibtext", "<span");
		map = map.replace("</bibtext", "</span");
		map = map.replace("<ref", "<div class=ref");
		map = map.replace("</ref", "</div");
		map = map.replace("<relatesTo", "<sup");
		map = map.replace("</relatesTo", "</sup");
		map = map.replace("<superscript", "<sup");
		map = map.replace("</superscript", "</sup");
		map = map.replace("<script", "");
		map = map.replace("</script", "");

		return map;
	}

}
