package springbook.learningtest.spring.web.hello;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.servlet.ModelAndView;

import springbook.learningtest.spring.web.ConfigurableDispatcherServlet;
public class HelloControllerTest {
	@Test
	public void helloController() throws ServletException, IOException {
		ConfigurableDispatcherServlet servlet = new ConfigurableDispatcherServlet();
		servlet.setRelativeLocations(getClass(), "spring-servlet.xml");
		servlet.setClasses(HelloSpring.class);
		
		servlet.init(new MockServletConfig("spring"));
		
//		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/app/hello");
//		req.setAttribute("javax.servlet.include.servlet_path", "/app");
		
		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
		req.addParameter("name", "Spring");
		MockHttpServletResponse res = new MockHttpServletResponse();
		
		servlet.service(req, res);
		
		ModelAndView mav = servlet.getModelAndView();
		assertEquals(mav.getViewName(), "/WEB-INF/view/hello.jsp");
		assertEquals((String)mav.getModel().get("message"), "Hello Spring");
	}
}
