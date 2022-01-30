package springbook.learningtest.spring.web.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;
import springbook.learningtest.spring.web.exception.HandlerExceptionResolverTest.HelloCon;
import springbook.learningtest.spring.web.exception.HandlerExceptionResolverTest.NotInServiceException;
import springbook.learningtest.spring.web.view.ViewResolverTest.HelloController;

public class SimpleMappingExceptionResolverTest extends AbstractDispatcherServletTest {

	@Test
	public void annotationMethod() throws ServletException, IOException {
		setClasses(HelloCon.class);
		runService("/hello");
		//assertViewName("error/dao");
		System.out.println(getModelAndView().getModel().get("msg"));
	}
	@RequestMapping
	static class HelloCon {
		@RequestMapping("/hello")
		public void hello() {
			if (1==1) throw new DataRetrievalFailureException("hi");
		}		
	}
	
	@Test
	public void simpleExceptionTest() throws ServletException, IOException {
		setRelativeLocations("simplemappingER.xml").setClasses(HelloCon2.class);
		runService("/hello");
		//assertEquals(this.response.getForwardedUrl(), "/WEB-INF/view/hello.jsp");
		System.out.println(response.getStatus());
		System.out.println(response.getErrorMessage());

	}
	
	@RequestMapping
	static class HelloCon2 {
		@RequestMapping("/hello")
		public void hello() {
			if (1==1) throw new NotInServiceException();
		}
	}
	
	@ResponseStatus(value=HttpStatus.SERVICE_UNAVAILABLE, reason="Not in Service")
	static class NotInServiceException extends RuntimeException {
	}

}
