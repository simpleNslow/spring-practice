package springbook.learningtest.spring.web.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

public class InterceptorTest extends AbstractDispatcherServletTest {

	@Test
	public void preHandleReturnValue() throws ServletException, IOException {
		setRelativeLocations("handlerInterceptor.xml");		
		runService("/hello").assertViewName("controller1.jsp");
		assertEquals((Controller1)getBean(Interceptor1.class).handler, getBean(Controller1.class));
		assertEquals(getBean(Interceptor1.class).post, true);
		
		getBean(Interceptor1.class).ret = false;
		getBean(Interceptor1.class).post = false;
		assertNull(runService("/hello").getModelAndView());
		assertEquals(getBean(Interceptor1.class).post, false);
	}
	
	static class Controller1 implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("controller1.jsp");
		}
	}
	
	static class Interceptor1 implements HandlerInterceptor {
		Object handler; 
		boolean ret = true;
		boolean post = false;
		
		public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
			this.handler = handler;
			System.out.println(handler.getClass().getName());
			return ret;
		}
		public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
			post = true;
		}

	}
	
}
