
package springbook.learningtest.spring.web.view;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

public class RedirectViewTest extends AbstractDispatcherServletTest {
	@Test
	public void redirectView() throws ServletException, IOException {
		setClasses(HelloController.class);		
		runService("/hello");
		assertEquals(this.response.getRedirectedUrl(), "/main?name=Spring");
	}
	@Component("/hello")
	public static class HelloController implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView(new RedirectView("/main", true)).addObject("name", "Spring");
			//return new ModelAndView("redirect:/main").addObject("name", "Spring");
		}
		
	}
}
