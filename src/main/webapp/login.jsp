<html>
<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type">
<title>Login</title>
<link rel="stylesheet" href="style/style.css" type="text/css"
	media="screen" />
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
<link href="web/favicon.ico" rel="shortcut icon">
<script src="web/jquery.js" type="text/javascript"></script>
<script type="text/javascript" src="JS/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="JS/jquery.js"></script>
<script src="http://code.jquery.com/jquery-1.8.2.js"></script>
<script src="http://code.jquery.com/ui/1.9.0/jquery-ui.js"></script>

<script>
$(document).ready(function() {
	
});
</script>
</head>

<body>
	<div class="container">
			<div class="header">
		<form  id = "login" name= "login" action="login.jsp" method="post" >
			<input value=<%=request.getRequestURL().toString()%> name="hiddenURL" type="text" style="display:none">
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



		<div class="content">
			<div class="clearfix" id="toptabcontent">
				<div class="loginform">
					<h2 style="font-size:">Login</h2>
					<% if(null!= session.getAttribute("userId")&& session.getAttribute("userId").equals("invalid")){ %>
					<div class="loginfailed">Invalid login -- please try again</div>
					<%session.setAttribute("userId",null);} %>
					<form action="CheckLogin" method = "get" >
						<table>
							<tbody>
								<tr>
									<td><label style="font-size: 80%"><b>Username:</b>
									</label></td>
									<td><input value="" name="userId" type="text"></td>
						
									
								</tr>
								<tr>
									<td><label style="font-size: 80%"><b>Password:</b>
									</label></td>
									<td><input type="password" value="" name="password"></td>
								</tr>
								<tr>
									<td></td>
											<td><input value=<%=request.getParameter("hiddenURL")%> name="hiddenURL" type="text" style="display:none"></td>
									<td><input type="submit" value="Login"></td>
								</tr>
							</tbody>
						</table>
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