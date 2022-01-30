package springbook.learningtest.spring.web.atmvc;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.util.NestedServletException;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

public class AtControllerTest extends AbstractDispatcherServletTest {
	@Test
	public void simple() throws ServletException, IOException {
		setClasses(ViewResolver.class, SimpleController.class);
		runService("/hello").assertViewName("hello");
		initRequest("/complex").addParameter("name", "Spring");
		//request.setCookies(new Cookie("auth", "ABCD"));
		runService();
		assertViewName("myview");
		//assertModel("info", "Spring/ABCD");
		assertModel("info", "Spring/NONE");
	}

	//@RequestMapping	static class SimpleController {
	@Controller	static class SimpleController {
		@RequestMapping("/hello") public void hello() { }
		@RequestMapping("/complex") 
		public String complex(@RequestParam("name") String name, @CookieValue(value="auth", required=false, defaultValue="NONE") String auth, ModelMap map) {
			map.put("info", name + "/" + auth);			
			return "myview";			
		}
	}
	static class ViewResolver extends InternalResourceViewResolver { {setSuffix(".jsp");} }

	@Test
	public void pathvar() throws ServletException, IOException {
		setClasses(PathController.class);
		runService("/hello/toby/view/1").assertViewName("toby/1");
		runService("/hello/toby/view/badtype");
		assertEquals(response.getStatus(), 400);
	}
	
	//@RequestMapping static class PathController {
	@Controller static class PathController {
		@RequestMapping("/hello/{user}/view/{id}")
		public String pathvar(@PathVariable("user") String user, @PathVariable("id") int id) {
			return user + "/" + id;
		}
	}
	
	@Test
	public void requestParamAndModelAttribute() throws ServletException, IOException {
		setClasses(RequestParamController .class);
		initRequest("/hello").addParameter("id", "10").runService().assertViewName("10");
		initRequest("/hello").runService();
		assertEquals(response.getStatus(), 400); // no required param
		initRequest("/hello").addParameter("id", "bad").runService();
		assertEquals(response.getStatus(), 400);

		initRequest("/hello2").addParameter("id", "11").runService().assertViewName("11");
		runService("/hello2").assertViewName("-1");	// with default param
		
		initRequest("/hello3").addParameter("id", "12").addParameter("name", "Spring");
		runService().assertViewName("12/Spring");
		
		initRequest("/hello4").addParameter("id", "15").runService().assertViewName("15");
		
		initRequest("/hello5").addParameter("id", "15").addParameter("name", "Spring");
		runService().assertViewName("15/Spring");
		
		initRequest("/hello5").addParameter("id", "bad").addParameter("name", "Spring").runService();		
		assertEquals(response.getStatus(), 400);
		/**
		try {
			runService().assertViewName("bad/Spring");
			fail();
		}
		catch(NestedServletException e) {
			assertEquals(e.getCause(), BindException.class);
		}
		**/
		
		initRequest("/hello6").addParameter("id", "bad").addParameter("name", "Spring").runService();
		assertEquals(response.getStatus(), 200);
		assertEquals(getModelAndView().getViewName(), "id");
	}
	
	//@RequestMapping 
	@Controller
	static class RequestParamController {
		@RequestMapping("/hello")
		public String hello(@RequestParam("id") int id) { return ""+id; }
		@RequestMapping("/hello2")
		public String hello2(@RequestParam(required=false, defaultValue="-1") int id) { return ""+id; }
		@RequestMapping("/hello3")
		public String hello3(@RequestParam Map<String, String> params) { 
			return params.get("id") + "/" + params.get("name");
		}
		@RequestMapping("/hello4")
		public String hello4(int id) { return ""+id; }

		@RequestMapping("/hello5") 
		//public String hello5(@ModelAttribute("user") User user) { return user.id + "/" + user.name; }
		public String hello5(@ModelAttribute("user") User user) { return user.getId() + "/" + user.getName(); }
		
		@RequestMapping("/hello6") 
		public String hello6(@ModelAttribute("user") User user, Errors errors) { 
			return errors.getFieldErrors().get(0).getField();
		}

	}
	
	static class User {
		public User() {}
		public User(int id, String name) { this.id = id; this.name = name; 	}
		int id; String name;
		public int getId() { return id; }  	public void setId(int id) { this.id = id; }
		public String getName() { return name; }	public void setName(String name) { this.name = name; }		
	}
	
	@Test
	public void modelmap() throws ServletException, IOException {
		setClasses(ViewResolver.class, ModelController.class);
		runService("/hello");
		assertEquals(((User)getModelAndView().getModel().get("user")).getName(), "Spring");
		assertEquals(((User)getModelAndView().getModel().get("us")).getId(), 123);
	}

	@Controller static class ModelController {
		@RequestMapping("/hello") public void hello(ModelMap model) {
			User u = new User(123, "Spring");
			model.addAttribute(u);
			model.addAttribute("us", u);
			model.addAttribute("Spring");
		}
	}
	
	@Test
	public void autoAddedModel() throws ServletException, IOException {
		setClasses(ViewResolver.class, ReturnController.class);
		
		initRequest("/hello1").addParameter("id", "123").addParameter("name", "Spring").runService();
		assertViewName("hello1.jsp");
		// for(String name : getModelAndView().getModel().keySet()) { System.out.println(name); }
		assertModel("mesg", "hi");
		assertModel("string", "string");
		assertModel("ref", "data");
		assertEquals(getModelAndView().getModel().size(), 5);
		assertEquals(((User)getModelAndView().getModel().get("user")).getId(), 123);
		assertEquals(((User)getModelAndView().getModel().get("user")).getName(), "Spring");
		
		runService("/hello2").assertModel("name", "spring");
		runService("/hello3").assertModel("name", "spring");
		runService("/hello4").assertModel("name", "spring");
		assertEquals(runService("/hello5").getContentAsString(), "<html><body>Hello Spring</body></html>");
	}
	
	@Controller static class ReturnController {
		@ModelAttribute("ref") public String ref() { return "data"; }
		
		@RequestMapping("/hello1") public ModelAndView hello1(User u, Model model) {
			model.addAttribute("string");
			return new ModelAndView("hello1.jsp").addObject("mesg", "hi");
		}
		
		@RequestMapping("/hello2") public Model hello2() { return new ExtendedModelMap().addAttribute("name", "spring"); }
		@RequestMapping("/hello3") public ModelMap hello3() { return new ModelMap().addAttribute("name", "spring"); }
		@RequestMapping("/hello4") public Map<String, String> hello4() { Map<String, String> map = new HashMap<String, String>(); map.put("name", "spring"); return map; }
		
		@RequestMapping("/hello5")
		@ResponseBody
		public String hello5() {
			return "<html><body>Hello Spring</body></html>";
		}
	}
	
	@Test
	public void value() throws ServletException, IOException {
		setClasses(ValueController.class);
		runService("/hello").assertViewName(System.getProperty("os.name"));
	}

	@Controller static class ValueController {
		@RequestMapping("/hello") public String hello(@Value("#{systemProperties['os.name']}") String osName) {
			return osName;
		}
	}



}
