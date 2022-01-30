package springbook.learningtest.spring.web.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

public class SimpleHttpRequestHandlerTest extends AbstractDispatcherServletTest {
	
	@Test
	public void httpRequestHandlerTest() throws ServletException, IOException 
	{
		setClasses(HelloHttpRequestHandler.class);
		initRequest("/hello").addParameter("name", "Spring");
		
		assertEquals(runService().getContentAsString(), "Hello Spring");
	}
	
	@Component("/hello")
	static class HelloHttpRequestHandler implements HttpRequestHandler {
		@Override
		public void handleRequest(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			String name = request.getParameter("name");
			response.getWriter().print("Hello " + name);
		}
	}

}
