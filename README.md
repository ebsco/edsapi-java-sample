To use the demo:
Run 'mvn install' in the directory containing the pom.xml file to create a war file and deploy in Tomcat.

Configuring the Java Demo:
The Demo supports both UID and IP Authentication. To set the authentication mode, use the "authentication_type"
parameter. The supported types are UID and IP. 
		<init-param>
			<param-name>authentication_type</param-name>
			<param-value>UID</param-value>
			<!-- <param-value>IP</param-value> -->
		</init-param>
		
if using UID authentication, you must also provide a valid username and password:
			<init-param>
				<param-name>user_name</param-name>
				<param-value>[your_user_name]</param-value>
			</init-param>
			<init-param>
				<param-name>password</param-name>
				<param-value>[your_password]</param-value>
			</init-param>

The demo supports the processing of messages in JSON or XML. To set the message format, use the "message_format"
parameter. The available formats are XML or JSON. 
		<init-param>
			<param-name>message_format</param-name>
			<param-value>JSON</param-value>
			<!-- <param-value>XML</param-value> -->
		</init-param>
		
To set the profile for the EDS API to work with, use the "profile" parameter:
		<init-param>
			<param-name>profile</param-name>
			<param-value>[your_profile]</param-value>
		</init-param>
