package com.stillwaterinsurance.docusign.filter;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * Request Filter.
 *
 */
@Component(value = "requestFilter")
public class RequestFilter implements Filter {
	
	private static final long launchTimestamp = Calendar.getInstance().getTimeInMillis() / 1000;

	@Value("${context.self}")
	private String context;

	@Value("${context.static}")
	private String contextStatic;

	@Override
	public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		
		if (request instanceof HttpServletRequest) {
			final HttpServletRequest httpRequest = (HttpServletRequest) request;

			httpRequest.setAttribute("context", context);
			httpRequest.setAttribute("contextStatic", contextStatic);
			httpRequest.setAttribute("launchTimestamp", launchTimestamp);
			
			DateTime today = new DateTime();
			httpRequest.setAttribute("year", today.getYear());

			chain.doFilter(httpRequest, response);
		} else {
			chain.doFilter(request, response);
		}
		
	}

	/**
	 * Filter creation.
	 */
	@Override
	public void init(final FilterConfig config) throws ServletException {

	}

	/**
	 * Filter Destruction.
	 */
	@Override
	public void destroy() {

	}
	
}
