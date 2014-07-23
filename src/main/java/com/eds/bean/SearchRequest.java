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

import java.util.HashMap;
import java.util.Map;

public class SearchRequest {
	public String includeFacets;
	public String[] facetFilters;
	public String sort;
	public Query query;
	public String[] limiters;
	public String searchmode;
	public String expander;
	public String view;
	public String resultsPerPage;
	public String pageNumber;
	public String highlight;
	public String[] actions;
	public String[] relatedContent;

	/**
	 * Takes the properties of this class and turns them into a map of
	 * key/values to send the the EDS API
	 * 
	 * @return The parameters to send to the EDS API
	 */
	public Map<String, String[]> getApiParameters() {
		Map<String, String[]> parameters = new HashMap<String, String[]>();

		if (null != includeFacets && !includeFacets.isEmpty())
			parameters.put(QueryStringParameterNames.includeFacets,
					new String[] { includeFacets });

		if (null != facetFilters && (0 < facetFilters.length))
			parameters.put(QueryStringParameterNames.facetFilter, facetFilters);

		if (null != sort && !sort.isEmpty())
			parameters.put(QueryStringParameterNames.sort,
					new String[] { sort });

		if (null != query)
			parameters.put(QueryStringParameterNames.query,
					new String[] { query.ToQueryStringParameter() });

		if (null != limiters && (0 < limiters.length))
			parameters.put(QueryStringParameterNames.limiter, limiters);

		if (null != searchmode && !searchmode.isEmpty())
			parameters.put(QueryStringParameterNames.searchmode,
					new String[] { searchmode });

		if (null != expander && !expander.isEmpty())
			parameters.put(QueryStringParameterNames.expander,
					new String[] { expander });

		if (null != view && !view.isEmpty())
			parameters.put(QueryStringParameterNames.view,
					new String[] { view });

		if (null != resultsPerPage && !resultsPerPage.isEmpty())
			parameters.put(QueryStringParameterNames.resultsPerPage,
					new String[] { resultsPerPage });

		if (null != pageNumber && !pageNumber.isEmpty())
			parameters.put(QueryStringParameterNames.pageNumber,
					new String[] { pageNumber });

		if (null != highlight && !highlight.isEmpty())
			parameters.put(QueryStringParameterNames.highlight,
					new String[] { highlight });

		if (null != actions && (0 < actions.length))
			parameters.put(QueryStringParameterNames.action, actions);
		
		if( null != relatedContent && (0 < relatedContent.length))
			parameters.put(QueryStringParameterNames.relatedContent,  relatedContent );
		
		return parameters;
	}
}
