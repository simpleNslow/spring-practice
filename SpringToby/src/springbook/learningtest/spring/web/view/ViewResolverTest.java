package springbook.learningtest.spring.web.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

public class ViewResolverTest extends AbstractDispatcherServletTest {
	@Test
	public void jstlView() throws ServletException, IOException {
		setRelativeLocations("jstlview.xml").setClasses(HelloController.class);
		runService("/hello");
		assertEquals(this.response.getForwardedUrl(), "/WEB-INF/view/hello.jsp");
	}
	@Component("/hello")
	public static class HelloController implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("hello").addObject("message", "Hello Spring");
		}
	}
	
}
