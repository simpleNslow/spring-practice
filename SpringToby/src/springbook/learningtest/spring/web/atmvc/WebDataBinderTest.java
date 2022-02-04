package springbook.learningtest.spring.web.atmvc;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;
import springbook.learningtest.spring.web.atmvc.AtControllerTest.ViewResolver;

public class WebDataBinderTest extends AbstractDispatcherServletTest {
	
	@Test
	public void allowed() throws ServletException, IOException {
		setClasses(ViewResolver.class, UserController.class);
		initRequest("/add", "POST").addParameter("id", "1").addParameter("name", "name");
		runService();
		User user = (User) getModelAndView().getModel().get("user");
		assertEquals(user.getId(), 1);
		assertNull(user.getName());
	}

	@Controller static class UserController {
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			//dataBinder.setAllowedFields("id");
			dataBinder.setDisallowedFields("name");
		}		
		@RequestMapping("/add") public void add(@ModelAttribute User user) {
		}
	}

	@Test
	public void required() throws ServletException, IOException {
		setClasses(ViewResolver.class, UserController2.class);
		initRequest("/add", "POST").addParameter("name", "name");
		runService();
		//User user = (User) getModelAndView().getModel().get("user");
		//assertEquals(user.getId(), 1);
		//assertNull(user.getName());
	}

	@Controller static class UserController2 {
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.setRequiredFields("id");
		}		
		@RequestMapping("/add") public void add(@ModelAttribute User user, BindingResult result) {
			if (result.hasErrors()) {
				System.out.println("BindingResult Error!!");
			}
		}
	}
	
	@Test
	public void required2() throws ServletException, IOException {
		setClasses(ViewResolver.class, UserController3.class);
		initRequest("/add", "POST").addParameter("id", "1").addParameter("name", "name");
		runService();
		User user = (User) getModelAndView().getModel().get("user");
		assertEquals(user.getId(), 1);
		assertEquals(user.getName(), "name");
	}

	@Controller static class UserController3 {
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.setRequiredFields("name");
		}		
		@RequestMapping("/add") public void add(@ModelAttribute User user, BindingResult result) {
			if (result.hasErrors()) {
				System.out.println("BindingResult Error!!");
			}
		}
	}

	@Test
	public void prefix() throws ServletException, IOException {
		setClasses(ViewResolver.class, UserController4.class);
		initRequest("/add").addParameter("_flag", "").addParameter("!type", "member");
		runService();
		User user = (User) getModelAndView().getModel().get("user");
		assertEquals(user.isFlag(), false);
		assertEquals(user.getType(), "member");
	}
	
	@Controller static class UserController4 {
		@RequestMapping("/add") public void add(@ModelAttribute User user) {
		}
	}

	static class User {
		int id;
		String name;
		boolean flag = true;
		String type;
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public boolean isFlag() {
			return flag;
		}
		public void setFlag(boolean flag) {
			this.flag = flag;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		@Override
		public String toString() {
			return "User [flag=" + flag + ", id=" + id + ", name=" + name
					+ ", type=" + type + "]";
		}
	}
}
