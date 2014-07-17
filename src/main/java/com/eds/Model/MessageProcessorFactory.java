/*
 * This factory class generates an message processor object to properly format
 * and parse messages to and from services
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
package com.eds.Model;

public class MessageProcessorFactory {

	/**
	 * Construct an message processor object
	 * 
	 * @param format
	 *            Format to process messages in (XML or JSON)
	 * @return A message processor object to handle messaging
	 */
	public static IMessageProcessor getFormatter(String format) {
		if (null == format)
			format = "XML";
		if (0 == format.compareToIgnoreCase("JSON"))
			return new JSONProcessor();
		else
			return new XMLProcessor();
	}
}
