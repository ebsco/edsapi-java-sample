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

import com.eds.Helpers.QueryStringHelper;

public class Query {
	private String Term = "";
	private String FieldCode = "";
	private String Operator = "";

	public String getTerm() {
		return this.Term;
	}

	public void setTerm(String term) {
		this.Term = term;
	}

	public String getFieldCode() {
		return this.FieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.FieldCode = fieldCode;
	}

	public String getOperator() {
		return this.Operator;
	}

	public void setOperator(String operator) {
		this.Operator = operator;
	}

	/**
	 * 
	 * Populate a query object from the 'query[-x]' querystring parameter
	 * 
	 * @param parameter query parameter to generate a Query object from
	 * @return Query object representing the query parameter
	 */
	public static Query PopulateFromQueryString(String parameter) {
		Query query = new Query();
		if (null == parameter || parameter.isEmpty())
			return query;

		// parse the operator
		int indexOfFirstComma = parameter.indexOf(',');
		if (indexOfFirstComma > -1
				&& 0 != parameter.substring(indexOfFirstComma - 1,
						indexOfFirstComma - 1).compareTo("\\")) {
			query.setOperator(parameter.substring(0, indexOfFirstComma));
			parameter = parameter.substring(indexOfFirstComma + 1);
		}
		// parse the field code
		int indexOfFirstColon = parameter.indexOf(":");
		if (indexOfFirstColon > -1
				&& 0 != parameter.substring(indexOfFirstColon - 1,
						indexOfFirstColon).compareTo("\\")) {
			query.setFieldCode(parameter.substring(0, indexOfFirstColon));
			parameter = parameter.substring(indexOfFirstColon + 1);
		}

		// set the search term
		query.setTerm(QueryStringHelper.UnEscape(parameter));
		return query;

	}
	
	/**
	 * Querystring parameter representation of this object
	 * 
	 * @return string representing a querystring parameter
	 */
	public String ToQueryStringParameter() {
		String parameter = "";
		if (null != this.getOperator() && !this.getOperator().isEmpty())
			parameter = this.getOperator() + ",";
		if (null != this.getFieldCode() && !this.getFieldCode().isEmpty())
			parameter = parameter + this.getFieldCode() + ":";
		return parameter + QueryStringHelper.Escape(this.getTerm());
	}
}
