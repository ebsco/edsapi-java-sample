<html>
<head>
<%@page import="com.eds.bean.*" language="java"%>
<link rel="stylesheet" href="style/style.css" type="text/css"
	media="screen" />
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
	<% 
		ApiErrorMessage apiErrorMessage = (ApiErrorMessage)request.getSession().getAttribute("errorMessage");
		String errorCode = (null == apiErrorMessage) ? "" : apiErrorMessage.getErrorNumber();
		if(null == errorCode)
			errorCode = "";
		String description = (null == apiErrorMessage) ? "" : apiErrorMessage.getErrorDescription();
		if(null == description)
			description = "";
		String detailedDescription = (null == apiErrorMessage) ? "" : apiErrorMessage.getDetailedErrorDescription();
		if(null == detailedDescription)
			detailedDescription = "";
	%>
</head>
<body>
	<div class="container">
		<div class="header">
			<img src="style/springfield-logo.png" />
		</div>
		<div class="content">
			<span>
				An Error Occurred
				<br />
				Error Number: <%out.println(errorCode);%>
				<br />
				Error Description: <%out.println(description);%>
				<br />
				Detailed Error Description: <%out.println(detailedDescription);%>
			</span>
		</div>
		<div class="footer">
			<div
				style="text-align: right; font-size: 85%; color: lightgray; height: 10px; position: relative;">EDS
				API-Java Demo 1.0</div>
		</div>
	</div>
</body>
</html>