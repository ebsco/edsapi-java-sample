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
			<div>
				<a href="index.jsp" id="logo"></a>
			</div>
			<div class="guestbox">
				<div>
					Hello, Guest. <a href="login.jsp">Login</a> for full
					access.
				</div>
			</div>
			<div class="login">
				<a href="login.jsp">Login</a>
			</div>

		</div>


		<div class="content">
			<div class="clearfix" id="toptabcontent">
				<div class="loginform">
					<h2 style="font-size:">Login</h2>
					<div class="loginfailed">Invalid login -- please try again</div>
					<form method="post" action="auth.php">
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
									<td><input id= "subButton" type="submit" value="Login"></td>
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