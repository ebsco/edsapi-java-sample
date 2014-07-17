/*
 * This class builds HTML control for paging through results
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

public class PageNumberHelper {

	/**
	 * Generate a paging control
	 * 
	 * @param recordCount
	 *            - the number of records to be displayed on each page
	 * 
	 * @param limit
	 *            - number of page numbers to display at a time
	 * @param pagenumber
	 *            - page number user result listing represents
	 * @param baseUrl
	 *            - search the current page represents
	 * @return HTML representing the current paging control
	 */
	public static String BuildPageNumber(String recordCount, String limit,
			String pagenumber, String baseUrl) {

		String pageNumberResults = "";
		int totalHits = 0;
		try {
			totalHits = Integer.parseInt(recordCount);
		} catch (NumberFormatException n) {
			totalHits = 0;
		}

		int pageLimit = 1;
		try {
			pageLimit = Integer.parseInt(limit);
		} catch (NumberFormatException n) {
			pageLimit = 1;
		}

		int page = 1;
		try {
			page = Integer.parseInt(pagenumber);
		} catch (NumberFormatException n) {
			page = 1;
		}

		int linkCount = (int) Math.ceil((totalHits / pageLimit));
		if (page <= 0) {

			page = 1;

		} else {

			if (page > linkCount) {

				page = linkCount;

			}
		}
		int f = 0;
		if (page % 10 != 0) {
			f = (int) Math.floor(page / 10);

		} else {
			f = (int) Math.floor(page / 10) - 1;
		}

		int s = page - 1;

		if (linkCount >= 1) {

			pageNumberResults = "<p>";

			if (s > 0) {
				pageNumberResults = pageNumberResults
						+ "<a href="
						+ baseUrl
						+ "&action=GOTOPage("
						+ s
						+ ")>"
						+ "<span class=results-paging-previous>&nbsp;&nbsp;&nbsp;&nbsp;</span></a>";
			}
			if (f < (int) Math.floor(linkCount / 10)) {

				for (int i = f * 10; i < f * 10 + 10; i++) {

					int p = i + 1;

					if (p != page) {

						pageNumberResults = pageNumberResults + "<a href="
								+ baseUrl + "&action=GOTOPage(" + p + ")>"
								+ "<u>" + p + "</u>" + "</a>";
					} else {
						pageNumberResults = pageNumberResults + "<strong>" + p
								+ "</strong>";
					}
				}
			} else {

				for (int i = f * 10; i < linkCount; i++) {

					int p = i + 1;

					if (p != page) {
						pageNumberResults = pageNumberResults + "<a href="
								+ baseUrl + "&action=GOTOPage(" + p + ")>" + p
								+ "</a>";

					} else {
						pageNumberResults = pageNumberResults + p;
					}
				}
			}

			int p1 = page + 1;

			if (p1 <= linkCount) {

				pageNumberResults = pageNumberResults
						+ "<a href="
						+ baseUrl
						+ "&action=GOTOPage("
						+ p1
						+ ")>"
						+ "<span class='results-paging-next'>&nbsp;&nbsp;&nbsp;&nbsp;</span>"
						+ "</a>";

			}

			pageNumberResults = pageNumberResults + "<br class=\"clear\" ></p>";

		}

		return pageNumberResults;
	}
}
