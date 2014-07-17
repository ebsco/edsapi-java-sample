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
</head>
<body>
	<div class="container">
		<div class="header">
			<img src="style/springfield-logo.png" />
		</div>
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