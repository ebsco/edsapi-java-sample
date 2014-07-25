<%@page import="com.eds.Helpers.JsonHelper"%>
<html>
<head>
<link rel="stylesheet" href="style/style.css" type="text/css"
	media="screen" />
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
<script type="text/javascript" src="JS/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="JS/jquery.js"></script>
<script src="http://code.jquery.com/jquery-1.8.2.js"></script>
<script src="http://code.jquery.com/ui/1.9.0/jquery-ui.js"></script>
<link rel="stylesheet" href="style/pubtype-icons.css" type="text/css"
	media="screen" />
<script>
	$(document).ready(function() {
		$("#query").keyup(function() {

		});
		$("#query").autocomplete({
			source : getJson,
			minTerms : 1
		});

	});
	function getJson() {
		var data = $("#info").html();
		var obj = eval(JSON.parse(data));
		$('#query').autocomplete("option", {
			source : obj
		});
	}
	



</script>
<%
	String url = "http://ehis.ebscohost.com/eds/autosuggest?iid=eds&iver=live&numterms=10&userinput=";
	String input = "a";
%>
</head>
<body>
	<div class="container">
		<div class="header">
			<form id="login" name="login" action="login.jsp" method="post">
				<input value=<%=request.getRequestURL().toString()%> name="hiddenURL"
					type="text" style="display: none">
			</form>
			<div>
				<a href="index.jsp" id="logo"></a>
			</div>
			<div class="guestbox">
			<%boolean loggedOut = (null == session.getAttribute("userId")||session.getAttribute("userId").equals("invalid"));%>
				<div>
				
					<%if(loggedOut){%>Hello, Guest. <a href="#" onclick="document.login.submit()">Login</a> for full
					access. <%}else{%>Welcome back, <% out.println(session.getAttribute("userId"));} %>
				</div>
				
			</div>
			<%if(loggedOut){ %>
			<div class="login">
				<a href="#" onclick="document.login.submit()">Login</a>
			</div>
			<%}else{ %>
			<div class="logout">
				<a href="logout">Logout</a>
			</div>
			<%}%>
		</div>




		<div style="display: none" id="info">
			<%=JsonHelper.readJsonFromUrl(url + input)%></div>
		<div class="content">
			<div id="toptabcontent">
				<div class="topSearchBox">
					<form action="AdvancedSearch">
						<p>
							<input type="text" name="query" id="query" /> <select
								name="fieldcode">
								<option id="type-keyword" value="">Keyword</option>
								<option id="type-title" value="TI">Title</option>
								<option id="type-author" value="AU">Author</option>
							</select> <input type="submit" value="Search">
						</p>
					</form>
				</div>
			</div>
		</div>
		<div class="footer">
			<div
				style="text-align: right; font-size: 85%; color: lightgray; height: 10px; position: relative;">EDS
				API-Java Demo 1.0</div>
		</div>
	</div>
</body>
</html>