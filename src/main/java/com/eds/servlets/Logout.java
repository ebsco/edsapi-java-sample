package com.eds.servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eds.Authentication.AuthenticationFactory;
import com.eds.Authentication.IAuthenticationManager;
import com.eds.Model.EDSAPI;
import com.eds.bean.ApiErrorMessage;
import com.eds.bean.SessionToken;

@WebServlet(
        description = "Logout Servlet",
        urlPatterns = { "/logout" })

public class Logout extends HttpServlet {

	private static final long serialVersionUID = 1L;



	/**
	 * Reads and set configuration settings from the servlet configuration and
	 * stores them in the properties
	 * 
	 * @param config
	 *            Servlet configuration to obtain settings from
	 */

	/**
	 * Sets the servlet configuration properties as attributes in the the
	 * request servlets' context. This method includes the appropriate sequence
	 * of calls to obtain data from the info method. 1. call the authentication
	 * service to obtain an authentication token if necessary (see documentation
	 * regarding different authentication types). 2. Call the EDS API CREATE
	 * SESSION method to generate a new session for the call to INFO, 3. Call
	 * the EDS API INFO method, 4. Call the EDS API END SESSION method. This
	 * application assumes that all users will be using the same profile, and
	 * this will be sharing the data returned from the INFO method. If you
	 * application allows the user of more than one profile, then INFO needs to
	 * be stored at the session level.
	 * 
	 * @param request
	 *            context in which to set the application attributes on
	 * @param response
	 * 
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Sets the servlet configuration properties as attributes in the the
	 * request servlets' context. This method includes the appropriate sequence
	 * of calls to obtain data from the info method. 1. call the authentication
	 * service to obtain an authentication token if necessary (see documentation
	 * regarding different authentication types). 2. Call the EDS API CREATE
	 * SESSION method to generate a new session for the call to INFO, 3. Call
	 * the EDS API INFO method, 4. Call the EDS API END SESSION method. This
	 * application assumes that all users will be using the same profile, and
	 * this will be sharing the data returned from the INFO method.
	 * 
	 * @param request
	 *            context in which to set the application attributes on
	 * @param response
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		ServletContext application = request.getServletContext();
		HttpSession session = request.getSession();
		
			ApiErrorMessage errorMessage = null;
			session.setAttribute("userId", null);
			try {

			// obtain the end_point and messageFormat from java
			// servlet application scope
			String edsapi_end_point = (String) application
					.getAttribute("edsapi_end_point");
			String profile = (String) application
					.getAttribute("profile");
			String message_format = (String) application
					.getAttribute("message_format");

			// generate an authentication manager to handle authentication
			// method the application is configured to use
			IAuthenticationManager authManager = AuthenticationFactory
					.getAuthenticationManager(request.getServletContext());

			// Obtain (if set) the session token associated with this user
			// session
			
			// Generate an eds api object from the above settings
			EDSAPI api = new EDSAPI(edsapi_end_point, message_format,
					authManager, null);
			//Generate Session as Guest
			SessionToken sessionToken = api.createSession(profile, "y");
			session.setAttribute("session_token", sessionToken.getSessionToken());
			//Remove history of last page visited
			session.setAttribute("lastPageVisited", null);
			response.sendRedirect("index.jsp");
			}catch (Exception e) {
				errorMessage = new ApiErrorMessage();
				errorMessage.setErrorDescription("An unknown error occurred");
			}
		
	

	}

}
