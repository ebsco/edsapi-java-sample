<!DOCTYPE html >
<html>
<head>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="com.eds.bean.*" language="java"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%><%@page
	import="com.eds.Helpers.*"%>

<% RetrieveResponse retrieveResponse = (RetrieveResponse)request.getSession().getAttribute("record");
   Result record = retrieveResponse.getResult();
   String picurl=""; %>

<link rel="stylesheet" href="style/style.css" type="text/css"
	media="screen" />

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
			<div class="toptabcontent">
				<div class="topbar">
					<div style="padding-top: 6px; float: left">
						<a href="javascript:history.back();" class="back">
							<%out.println("<< Results List");%>
						</a>
					</div>
				</div>

				<div class="record table">
					<% if(retrieveResponse.getApiErrorMessage()!=null){
                    ApiErrorMessage aem=retrieveResponse.getApiErrorMessage();
                    String errorString=aem.getErrorDescription();%>
					<h1>
						<%out.println(errorString);%>
					</h1>
					<%}else{
                    ArrayList<Item> itemList=record.getItemList();
                    String title="";
                    for(Item dItem : itemList){
                	   if(dItem.getGroup().equals("Ti")){
                            title = dItem.getData();
                            break;
                        }
                    } %>
					<h1>
						<%out.println(title);%>
					</h1>
					<div class="table-cell floatleft">
						<ul class="table-cell-box">
							<li><a
								href="http://search.ebscohost.com/login.aspx?direct=true&site=eds-live&db=<%=record.getDbId()%>&AN=<%=record.getAn()%>">
									View in EDS </a></li>
						</ul>
						<%if(record.getPdfAvailable().equals("1") || record.getHtmlAvailable().equals("1") || !record.getOtherFullTextLinks().isEmpty()){ %>
						<ul class="table-cell-box">
							<label>Full Text:</label>
							<hr>
							<%if(record.getPdfAvailable().equals("1")){ %>
							<li><a class="icon pdf fulltext"
								href=<%=record.getPdfLink()%>>PDF Full Text</a></li>
							<%} 
                            if(record.getHtmlAvailable().equals("1")){%>
							<li><a class="icon html fulltext" href="#html">HTML Full
									Text</a></li>
							<%}
                        	if(!record.getOtherFullTextLinks().isEmpty()){
                        	    for(Link link : record.getOtherFullTextLinks()){ %>
							<li><a class="icon fulltext" href="<%=link.getUrl()%>"><%=link.getType()%>
									- Full Text</a></li>
							<%}
                        	
                        }%>
						</ul>
						<%} %>
					</div>
					<div class="table-cell span-15">
						<table>
							<tbody>
								<%for(Item item : itemList){
                            String data =item.getData();
                            if(item.getGroup().equals("Au")||item.getGroup().equals("Su")||item.getGroup().equals("Ca")){
                        	   String value=SearchLinkBuilder.buildLink(item.getData());%>
								<tr>
									<td style="width: 150px; vertical-align: top"><strong>
											<%out.println(item.getLabel()+":");%>
									</strong></td>
									<td><%=value%></td>
								</tr>
								<%}else if(!item.getGroup().equals("Ti")){ %>
								<tr>
									<td style="width: 150px; vertical-align: top"><strong>
											<% out.println(item.getLabel()+":");%>
									</strong></td>
									<td>
										<% out.println(data);%>
									
									<td>
								</tr>
								<%
                            }
                         }%>
							</tbody>
						</table>
						<%if(record.getHtmlAvailable().equals("1")){ %>
						<div id="html" style="margin-top: 30px">
							<%out.println(record.getHtmlFullText()); %>
						</div>
						<%} %>
					</div>

					<% String bookJacket ="";
                   if(record.getBookJacketList().size()>0){
                       for(int b=0;b<record.getBookJacketList().size();b++){
                	      if(record.getBookJacketList().get(b).getSize().equals("medium")){
                		     bookJacket = record.getBookJacketList().get(b).getTarget();
                             break;
                           } 
                	   }
                   }
                   if(!bookJacket.isEmpty()){%>
					<div class="jacket">
						<img src="<%=bookJacket%>" width="150px" height="200px" />
					</div>
					<%
                  }
                 }
                 %>
				</div>
				<!-- end record table -->
			</div>
			<!-- end topatabcontent -->
		</div>
		<!--  end content -->
	</div>
	<!--  end container  -->
</body>
</html>