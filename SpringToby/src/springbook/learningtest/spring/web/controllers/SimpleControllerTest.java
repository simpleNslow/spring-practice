package springbook.learningtest.spring.web.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

public class SimpleControllerTest extends AbstractDispatcherServletTest {

	@Test
	public void helloSimpleController() throws ServletException, IOException {
		this.setClasses(HelloController.class)
		.initRequest("/hello").addParameter("name", "Spring")
		.runService()
		.assertModel("message", "Hello Spring")
		.assertViewName("/WEB-INF/view/hello.jsp");
	}

	@Test
	public void noParameterHelloSimpleController() throws ServletException, IOException {
		assertThrows(Exception.class, () -> 
			this.setClasses(HelloController.class)
			.initRequest("/hello")
			.runService()
		);
	}

	@Test
	public void helloControllerUnitTest() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", "Spring");
		Map<String, Object> model = new HashMap<String, Object>();
		
		new HelloController().control(params, model);
		
		assertEquals((String)model.get("message"), "Hello Spring");
	}

	//@RequestMapping("/hello")
	@Component("/hello")
	static public class HelloController extends SimpleController {

		public HelloController() {
			this.setRequiredParams(new String[] {"name"});
			this.setViewName("/WEB-INF/view/hello.jsp");
		}
		
		@Override
		public void control(Map<String, String> params, Map<String, Object> model) throws Exception {
			model.put("message", "Hello " + params.get("name"));
		}

		
	}
	
	static public abstract class SimpleController implements Controller {
		private String[] requiredParams;
		private String viewName;
		
		public void setRequiredParams(String[] requiredParams) {
			this.requiredParams = requiredParams;
		}
		
		public void setViewName(String viewName) {
			this.viewName = viewName;
		}
		
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

			if (viewName == null) throw new IllegalStateException();
			
			Map<String, String> params = new HashMap<String, String>();
			for(String param : requiredParams) {
				String value = req.getParameter(param);
				if (value == null) throw new IllegalStateException();
				params.put(param, value);
			}
			
			Map<String, Object> model = new HashMap<String, Object>();
			
			this.control(params, model);
			
			return new ModelAndView(this.viewName, model);
		}
		
		
		public abstract void control(Map<String, String> params, Map<String, Object> model) throws Exception;
	}
}
