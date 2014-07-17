To use the demo:
Run 'mvn install' in the directory containing the pom.xml file to create a war file and deploy in Tomcat.

Configuring the Java Demo:
The Demo supports both UID and IP Authentication. To set the authentication mode, use the 'authentication_type'
parameter. The supported types are UID and IP. 
		&lt;init-param&gt;
			&lt;param-name&gt;authentication_type&lt;/param-name&gt;
			&lt;param-value&gt;UID&lt;/param-value&gt;
			&lt;!-- &lt;param-value&gt;IP&lt;/param-value&gt; --&gt;
		&lt;/init-param&gt;
		
		if using UID authentication, you must also provide a valid username and password:
			&lt;init-param&gt;
				&lt;param-name&gt;user_name&lt;/param-name&gt;
				&lt;param-value&gt;[your_user_name]&lt;/param-value&gt;
			&lt;/init-param&gt;
			&lt;init-param&gt;
				&lt;param-name&gt;password&lt;/param-name&gt;
				&lt;param-value&gt;[your_password]&lt;/param-value&gt;
			&lt;/init-param&gt;

The demo supports the processing of messages in JSON or XML. To set the message format, use the 'message_format'
parameter. The available formats are XML or JSON. 
		&lt;init-param&gt;
			&lt;param-name&gt;message_format&lt;/param-name&gt;
			&lt;param-value&gt;JSON&lt;/param-value&gt;
			&lt;!-- &lt;param-value&gt;XML&lt;/param-value&gt; --&gt;
		&lt;/init-param&gt;
		
To set the profile for the EDS API to work with, use the 'profile' parameter:
		&lt;init-param&gt;
			&lt;param-name&gt;profile&lt;/param-name&gt;
			&lt;param-value&gt;[your_profile]&lt;/param-value&gt;
		&lt;/init-param&gt;
