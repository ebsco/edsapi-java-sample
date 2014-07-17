<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="com.eds.bean.*" language="java"%>
<%@page import="com.eds.Helpers.*" language="java"%>
<%@page import="org.jsoup.Jsoup"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.*"%>
<%@page import="org.json.*"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.util.HashMap"%>


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
    $(function() {
        $("#applyLimiters").click(function() {
            var actions = "";
            $("#limiterOptions input.selected-limiter-option:not(:checked)").each(function()
                    {
                        actions += "&" + $(this).val();
                    });
            $("#limiterOptions input.unselected-limiter-option:checked").each(function()
                    {
                        actions += "&" + $(this).val();
                    });
            var currentPageState = $("#currentState").val();
            window.location = [location.protocol, "//", location.host, location.pathname, "?", currentPageState, actions].join('') ;
            return false;           
        });

        
        $("#applyExpanders").click(function() {
            var actions = "";
            $("#expanderOptions input.selected-expander-option:not(:checked)").each(function()
                    {
                        actions += "&" + $(this).val();
                    });
            $("#expanderOptions input.unselected-expander-option:checked").each(function()
                    {
                        actions += "&" + $(this).val();
                    });
            var currentPageState = $("#currentState").val();
            window.location = [location.protocol, "//", location.host, location.pathname, "?", currentPageState, actions].join('') ;
            return false;           
        });
        
        $('.actionSelect').change(function() {
            var currentPageState = $("#currentState").val();
            var viewAction = "&action=" + $(this).find(":selected").val();
            window.location = [location.protocol, "//", location.host, location.pathname, "?", currentPageState, viewAction].join('') ;
            return false;   
        });
        
        $(".flip").click(function(){       
            $(this).children('.panel').slideToggle("slow");
            var panelToUpdate = $(this).children('.plus');
            if(null != panelToUpdate && 0 < panelToUpdate.length() ){
                var $panel = panelToUpdate[0];
                if($panel.html() == '[+]'){
                    $panel.html('[-]');
                }else{
                    $panel.html('[+]');
                }            
            }
        }); 
    });  
    </script>
</head>
<body>
	<div class="container">
		<%SearchResponse resultList = (SearchResponse) request.getSession().getAttribute("resultsList"); 
 AuthToken authToken= (AuthToken)application.getAttribute("authToken");
 SessionToken sessionToken=(SessionToken)request.getSession().getAttribute("sessionToken");
 Info info = (Info)application.getAttribute("info");
 ArrayList<AvailableLimiter> availableLimiterList = new ArrayList<AvailableLimiter>();
 ArrayList<AvailableExpander> availableExpanderList = new ArrayList<AvailableExpander>();
 ArrayList<AvailableSort> availableSortList = new ArrayList<AvailableSort>();%>

		<div class="header">
			<img src="style/springfield-logo.png" />
		</div>
		<div class="content">
			<div id="toptabcontent">
				<div class="topSearchBox">
					<input type="hidden" name="currentState" id="currentState"
						value="<%=resultList.getQueryString() %>" />
					<form action="AdvancedSearch">
						<%
    QueryWithAction query = (null != resultList.getQueryList() && 0 < resultList.getQueryList().size()) ? resultList.getQueryList().get(0) : null;
    String value= (null == query) ? "" : query.getTerm();
  value=URLDecoder.decode(value).replace("\"", "&quot;");
  String displayValue = value;
    String defaultSelected = "";
  String fieldCode = (null == query || null == query.getFieldCode()) ? "" : query.getFieldCode();
  if(null == resultList || fieldCode.isEmpty())
     defaultSelected  = "selected=selected";
%>

						<p>

							<input type="text" name="query" id="query" value="<%=value%>" />
							<select name="fieldcode">
								<option id="type-" value="" <%=defaultSelected %>>Keyword</option>
								<% ArrayList<AvailableSearchField> availableSearchFieldList=info.getAvailableSearchFieldsList();
for(int i=0;i<availableSearchFieldList.size();i++){         
    AvailableSearchField availableSearchField=availableSearchFieldList.get(i);
    
    if(availableSearchField.getFieldCode().equals("TI") || availableSearchField.getFieldCode().equals("AU") )
    {
        String label = availableSearchField.getLabel();
        String code = availableSearchField.getFieldCode();
        String selected = (0 == fieldCode.compareTo(code))? " selected='selected'" : "";%>
								<option id="type-<%=code %>" value="<%=code %>" <%=selected %>>
									<%out.println(label); %>
								</option>
								<% }
} %>
							</select> <input type="submit" value="Search">
						</p>
					</form>
					<% 

if(resultList.getApierrormessage()!=null)
{
    ApiErrorMessage aem=resultList.getApierrormessage();
    String errorString=aem.getErrorDescription();
            out.println(errorString);
}
else{
%>


				</div>
				<div class="table">
					<div class="table-row">
						<div class="table-cell">
							<div>
								<h4>Refine Search</h4>
							</div>
							<!--  statistics -->
							<div class="statistics">
								<strong> <%out.println(resultList.getHits());%>Results
									for.... <br /> <%=value%>
								</strong>
							</div>
							<br />
							<br /> <br />
							<!--  end statistics -->

							<div class="filters">
								<strong>Applied Filters</strong>
								<!-------------------------------------  LIMITERS  -------------------------------------------->
								<ul class="filters">
									<%//Create a map of available limiters
    HashMap<String,Object> availableLimiterMap=new HashMap<String,Object>();   
    if(null != info && null != info.getAvailableLimitersList()){
        availableLimiterList=info.getAvailableLimitersList();
        for(AvailableLimiter al : availableLimiterList){            
            if(al.getType().equals("select"))
               availableLimiterMap.put(al.getId(), al.getLabel());  
            else        
                availableLimiterMap.put(al.getId(), al.getLimitervalues());
        }
    }
    //process selected limiters
    ArrayList<LimiterWithAction> selectedLimiterList=resultList.getSelectedLimiterList();
    HashMap<String,String> limiterRemovemap=new HashMap<String,String>();
    for(int i=0;i<selectedLimiterList.size();i++){  
        LimiterWithAction lwa=selectedLimiterList.get(i);
        Object obj= availableLimiterMap.get(lwa.getId());
        String limiterName = "";
        String multiselectValue="";
        if (obj instanceof String) {
            limiterName = (String) obj;  
        }else{
            ArrayList<LimiterValueWithAction>  lvwaList = lwa.getLvalist();
            for(LimiterValueWithAction lvwa : lvwaList){
                multiselectValue=multiselectValue+lvwa.getValue();  
            }
            limiterName = multiselectValue;
        }
        limiterRemovemap.put(lwa.getId(),lwa.getRemoveAction());%>
									<li><p>
											<a
												href="AdvancedSearch?<%=resultList.getQueryString() %>&action=<%=lwa.getRemoveAction()%>"><img
												src="style/delete.png" />
												<% out.println("Limiter: " + limiterName); %></a>
										</p></li>
									<%}
    
    HashMap<String,String> ExpanderMap=new HashMap<String,String>();   
    if(null != info && null != info.getAvailableExpandersList()){
        availableExpanderList=info.getAvailableExpandersList();
        for(AvailableExpander ae : availableExpanderList)
            ExpanderMap.put(ae.getId(), ae.getLabel()); 
    }
    
    ArrayList<ExpandersWithAction> expanderRemoveActionList=resultList.getExpanderwithActionList();
    HashMap <String,String>ExpanderRemovemap=new HashMap<String,String>();
    for(int i=0;i<expanderRemoveActionList.size();i++){
        ExpandersWithAction ewa=expanderRemoveActionList.get(i);
        String label=ExpanderMap.get(ewa.getId());
        ExpanderRemovemap.put(ewa.getId(), ewa.getRemoveAction());%>
									<li><p>
											<a
												href="AdvancedSearch?<%=resultList.getQueryString() + "&action=" + ewa.getRemoveAction()%>"><img
												src="style/delete.png" />
												<%out.println("Expander: " + label);%></a>
										</p></li>
									<%
        }
        ArrayList<FacetFilterWithAction> facetfiltersList=resultList.getFacetfiltersList();
        HashMap<String,String> facetMap=new HashMap<String,String>();
        for(int i=0;i<facetfiltersList.size();i++){
            FacetFilterWithAction ffwa=facetfiltersList.get(i);
            ArrayList<FacetValueWithAction> fvwalist=ffwa.getFacetValueWithAction();
            for(int j=0;j<fvwalist.size();j++){
                FacetValueWithAction fvwa=fvwalist.get(j);
                String removeAction=fvwa.getRemoveAction();
                EachFacetValue efv=fvwa.getEachfacetvalue();
                facetMap.put(efv.getValue(), removeAction);// here we treat facet value as a key. used to record chekbox status
    %>
									<li><p>
											<a
												href="AdvancedSearch?<%=resultList.getQueryString()+"&action=" + removeAction%>"><img
												src="style/delete.png" />
												<%out.println(efv.getId() + ": " + efv.getValue());%></a>
										</p></li>
									<%}
    }%>
								</ul>
							</div>


							<div id="limiterOptions" class="filterOptions"
								style="font-size: 80%">
								<%if(availableLimiterList!=null){%>
								<dl class="facet-label">
									<dt>Limit your results</dt>
								</dl>
								<dl class="facet-label">
									<%for(AvailableLimiter availableLimiter : availableLimiterList){
                if(availableLimiter.getType().equals("select")){
                    if(limiterRemovemap.containsKey(availableLimiter.getId())){ 
                        String removeAction = limiterRemovemap.get(availableLimiter.getId());%>
									<dd>
										<input class="selected-limiter-option" type="checkbox"
											name="<%=availableLimiter.getId()%>" checked="checked"
											value="<%="action=" + removeAction%>" />
										<%out.println(availableLimiter.getLabel());%>
									</dd>
									<%}else{%>
									<dd>
										<input class="unselected-limiter-option" type="checkbox"
											name="<%=availableLimiter.getId()%>"
											value="<%="action=" + availableLimiter.getAddAction()%>" />
										<%out.println(availableLimiter.getLabel());%>
									</dd>
									<%}
                }
            }%>
								</dl>
								<input id="applyLimiters" type="button" value="update" />
								<%}%>
							</div>
							<!-------------------------------------  END LIMITERS  -------------------------------------------->

							<!----------------------------------------- EXPANDERS ------------------------------------------>
							<div id="expanderOptions" class="filterOptions"
								style="font-size: 80%">
								<%if(availableExpanderList!=null){%>
								<dl class="facet-label">
									<dt>Expand your results</dt>
								</dl>
								<dl class="facet-label">
									<% for(AvailableExpander availableExpander : availableExpanderList){
            if(ExpanderRemovemap.containsKey(availableExpander.getId())){
                String removeAction = ExpanderRemovemap.get(availableExpander.getId());%>
									<dd>
										<input class="selected-expander-option" type="checkbox"
											name="<%=availableExpander.getId()%>" checked="checked"
											value="<%="action=" + removeAction%>" />
										<%out.println(availableExpander.getLabel());%>
									</dd>
									<%}else{%>
									<dd>
										<input class="unselected-expander-option" type="checkbox"
											name="<%=availableExpander.getId()%>"
											value="<%="action=" + availableExpander.getAddAction()%>" />
										<%out.println(availableExpander.getLabel());%>
									</dd>
									<%}
        }%>
								</dl>
								<input id="applyExpanders" type="button" value="update" />
								<%}%>
							</div>
							<!-------------------------------------- END EXPANDERS  --------------------------------------->

							<!--------------------------------------  FACETS ---------------------------------------------->
							<div id="facets">
								<% ArrayList<Facet> facetsList=resultList.getFacetsList();
    for(Facet facet : facetsList){%>
								<div class="facet flip">
									<dl class="facet-label">
										<dt>
											<span style="font-weight: lighter" class="plus">[+]</span>
											<%out.println(facet.getLabel());%>
										</dt>
									</dl>
									<dl class="facet-values panel">
										<%ArrayList<FacetValue> facetsValueList=facet.getFacetsValueList();
                int facetsize=facetsValueList.size();
                for(FacetValue facetValue : facetsValueList){
                    String key=facetValue.getValue();
                    String applyFacetURL = "AdvancedSearch?" + resultList.getQueryString() + "&action=" + facetValue.getAddAction();%>
										<dd>
											<a href="<%=applyFacetURL%>"><%=facetValue.getValue()%></a>
											<%out.println("("+facetValue.getCount()+")");%>
										</dd>
										<%}%>
									</dl>
								</div>
								<%}%>
							</div>
							<!---------------------------------------- END FACETS ---------------------------------------->


						</div>
						<div class="table-cell">
							<div class="top-menu">
								<div class="topbar-resultList">
									<div class="optionsControls">
										<ul>
											<!-----------------------------------  SORTS --------------------------------------->
											<li class="options-controls-li"><label>Sort</label> <select
												class='actionSelect' id="sortoption" name="sortoption">
													<%
    if(null != info && null != info.getAvailableSortsList())
        availableSortList = info.getAvailableSortsList();
    for(AvailableSort availableSort : availableSortList){
        String selected = "";
        if(resultList.getSelectedSort().compareToIgnoreCase(availableSort.getId())==0)
            selected = "selected=selected";
%>
													<option <%=selected%>
														value="<%=availableSort.getAddAction()%>">
														<%
                out.println(availableSort.getLabel());
            %>
													</option>
													<%
               }
           %>
											</select></li>
											<!-- ----------------------------------------- END SORTS --------------------------------------->


											<!------------------------------------ RESULTS PER PAGE --------------------------------------->
											<li class="options-controls-li"><label>Results
													per page</label> <select class='actionSelect' id="perpageoption"
												name="perpage">
													<%
        String resultsPerPage = resultList.getSelectedResultsPerPage();
        //display 6 options
        for(int i=1;i<6;i++){
            String current =i*5+"";
            String selected  = "";
            if(current.compareToIgnoreCase(resultsPerPage) ==0 )
                selected = "selected=selected";
    %>
													<option <%=selected%>
														value="<%="setresultsperpage("+current+")"%>">
														<%
               out.println(current);
           %>
													</option>
													<%
            }
        %>
											</select></li>
											<!--------------------------------------- END RESULTS PER PAGE ------------------------------------->

											<!----------------------------------   VIEW OPTIONS  ------------------------------------------->
											<li class="options-controls-li"><label>Page
													options</label> <select class='actionSelect' id="pageoption"
												name="pageoption">
													<%
    String pageOptionselect1 ="";
   String pageOptionselect2 ="";
   String pageOptionselect3 ="";
   String view= resultList.getSelectedView();
   //use default if view is empty
   if(null == view || view.isEmpty())
       view = (null != info.getViewResultSettings()) ? info.getViewResultSettings().getResultListView() : "detailed";
   if(view.compareToIgnoreCase("detailed")==0)
      pageOptionselect1 ="selected=selected";
   else if(view.compareToIgnoreCase("brief")==0)
      pageOptionselect2 ="selected=selected";
   else if(view.compareToIgnoreCase("title")==0)
      pageOptionselect3 ="selected=selected";
%>

													<option <%=pageOptionselect1%> value="setview(detailed)">
														<%
    out.println("Detailed");
%>
													</option>
													<option <%=pageOptionselect2%> value="setview(brief)">
														<%
    out.println("Brief");
%>
													</option>
													<option <%=pageOptionselect3%> value="setview(title)">
														<%
    out.println("Title");
%>
													</option>
											</select></li>
											<!------------------------------------------ END VIEW OPTIONS --------------------------------->
										</ul>
									</div>
								</div>
								<!------------------------------------------ PAGINATION --------------------------------->
								<div style="text-align: center">
									<div class="pagination">
										<p>
											<%
            String output= PageNumberHelper.BuildPageNumber(resultList.getHits(),resultList.getSelectedResultsPerPage(),resultList.getSelectedPageNumber(),"AdvancedSearch?" + resultList.getQueryString());
                    out.println(output);
        %>
										</p>
									</div>
								</div>
								<!------------------------------------------ END PAGINATION --------------------------------->

								<!------------------------------------------ RESULTS --------------------------------->
								<div class="results table">
									<%
    if(Integer.parseInt(resultList.getHits())<=0){
%>
									<div class="result-table-row">
										<div class="table-cell">
											<h2>
												<i>No results were found.</i>
											</h2>
										</div>
									</div>
									<%
        } else {
    %>
									<%
                     for(Result result : resultList.getResultsList()){
                 %>
									<!-- start tag of result content -->
									<div class="result table-row">
										<!-- ResultID -->
										<div class="record-id table-cell">
											<%
                    out.println(result.getResultId()+".");
                %>
										</div>

										<!-- PubType and image -->
										<%
                    if(result.getPubType()!= null){
                %>
										<div class="pubtype table-cell" style="text-align: center">
											<%
                        String bookJacket ="";
                                        if(result.getBookJacketList().size()>0){
                                            for(int b=0;b<result.getBookJacketList().size();b++){
                                                //obtain the thumb size image for display
                                                if(result.getBookJacketList().get(b).getSize().equals("thumb")){
                                                    bookJacket = result.getBookJacketList().get(b).getTarget();
                                                    break;
                                                } 
                                            }
                    %>
											<a
												href="Retrieve?dbid=<%=result.getDbId()%>&an=<%=result.getAn()%>&highlight=<%=URLEncoder.encode(query.getTerm())%>">
												<img src="<%=bookJacket%>" />
											</a>
											<%
                         }else{
                                                                 //PubType  Here we use PNG and pubID to get image information and link    
                                                                 String dicIcon=result.getPubTypeID();
                                                                 String pubIcon="pt-"+dicIcon;
                     %>
											<span class="pt-icon <%=pubIcon%>"></span>
											<%
                         }
                     %>
											<div>
												<%
                            out.println(result.getPubType());
                        %>
											</div>
										</div>
										<%
                    }
                %>

										<!-- Record data -->
										<!-- Get author,abstract,subject information -->
										<%
                    String title = "";
                                String author = "";
                                String abstracts = "";
                                String source ="";
                                String subject = "";
                                String Description="";
                                String Categories="";
                                String PublicationInformation="";
                                     
                                for(int c=0; c< result.getItemList().size();c++){
                                    Item item = result.getItemList().get(c);
                                    if(item.getGroup().equals("Ti"))                        
                                        title=item.getData();                
                                    if(item.getGroup().equals("Au")){
                                        author =item.getData();
                                        author=author.replace("<br />", ";  ");
                                    }
                                    if(item.getGroup().equals("Ab"))
                                        abstracts= item.getData();
                                            
                                    if(item.getGroup().equals("Su")){
                                        subject=item.getData();
                                        subject=subject.replace("<br />", ";  ");
                                    } 
                                }
                %>
										<div class="info table-cell">
											<div class="title" style="overflow: hidden">
												<a
													href="Retrieve?dbid=<%=result.getDbId()%>&an=<%=result.getAn()%>&highlight=<%=URLEncoder.encode(query.getTerm())%>"><%=title%></a>
											</div>
											<br />

											<!-- Author -->
											<%
                        if(!author.equals("")){
                                                                                    SearchLinkBuilder bsl=new SearchLinkBuilder();
                                                                                    String authorvalue=bsl.buildLink(author);
                    %>
											<div class="author">
												<span style="font-style: italic;">By:</span>
												<%=authorvalue%>
											</div>
											<br />
											<%
                        }
                    %>
											<!-- Subject -->
											<%
                        if(!subject.equals("")){
                                                                                    SearchLinkBuilder bsl=new SearchLinkBuilder();
                                                                                    String subjectvalue=bsl.buildLink(subject);
                    %>
											<div class="subject">
												<span style="font-style: italic;">Subject:</span>
												<%=subjectvalue%>
											</div>
											<br />
											<%
                        }
                    %>

											<!-- Abstract -->
											<%
                        if(!abstracts.equals("")){
                    %>
											<div class="abstract">
												<span style="font-style: italic;">Abstract:</span>
												<%=abstracts%>
											</div>
											<br />
											<%
                        }
                    %>
											<div class="links">
												<%
                            if(null != result.getHtmlAvailable() && result.getHtmlAvailable().equalsIgnoreCase("1")){
                        %>
												<a class="icon html fulltext"
													href="Retrieve?an= <%=result.getAn()%>&dbid=<%=result.getDbId()%>&highlight=<%=URLEncoder.encode(query.getTerm())%>#html">Full
													Text</a>
												<%
                            }
                        %>
												<%
                             if(null != result.getPdfAvailable()&& result.getPdfAvailable().equalsIgnoreCase("1")){
                         %>
												<a class="icon pdf fulltext"
													href="Retrieve?an= <%=result.getAn()%>&dbid=<%=result.getDbId()%>&highlight=<%=URLEncoder.encode(query.getTerm())%>&type=pdf">Full
													Text</a>
												<%
                            }
                        %>
												<%
                            if(!result.getOtherFullTextLinks().isEmpty()){
                                                    for(Link link : result.getOtherFullTextLinks()){
                        %>
												<a class="icon fulltext"
													href="Retrieve?an=<%=result.getAn()%>&dbid=<%=result.getDbId()%>&highlight=<%=URLEncoder.encode(query.getTerm())%>&type=<%=link.getType() %>"><%=link.getType()%>
													- Full Text</a>
												<%
                                }
                                                    }
                            %>

											</div>
										</div>
									</div>
									<!-- end of table row -->
									<%
            }
            }
        }
        %>
								</div>
								<!--  end results table -->
								<!------------------------------------------ END RESULTS --------------------------------->
								<!------------------------------------------ PAGINATION --------------------------------->
								<div style="text-align: center">
									<div class="pagination">
										<p>
											<%
            String output= PageNumberHelper.BuildPageNumber(resultList.getHits(),resultList.getSelectedResultsPerPage(),resultList.getSelectedPageNumber(), "AdvancedSearch?" + resultList.getQueryString());
                    out.println(output);
        %>
										</p>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="footer">
			<div
				style="text-align: right; font-size: 85%; color: lightgray; height: 10px; position: relative;">EDS
				API-Java Demo 1.0</div>
		</div>
	</div>
	<!--  end container -->
</body>
</html>