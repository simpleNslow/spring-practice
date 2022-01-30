package springbook.learningtest.spring.web.atmvc;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;
import springbook.learningtest.spring.web.atmvc.AtControllerTest.ViewResolver;

public class PrototypePropertyEditorTest extends AbstractDispatcherServletTest {
	@Test
	public void fakeCodePropertyEditor() throws ServletException, IOException {
		setClasses(ViewResolver.class, UserController.class);
		initRequest("/add").addParameter("id", "1").addParameter("name", "Spring").addParameter("userType", "2");
		runService();
		User user = (User) getModelAndView().getModel().get("user");
		assertEquals(user.getUserType().getId(), 2);
		//assertNull(user.getUserType().getName());
		assertThrows(UnsupportedOperationException.class, () -> user.getUserType().getName());
		/*
		try {
			user.getUserType().getName();
			fail();
		}
		catch(UnsupportedOperationException e) {}
		*/
	}

	@Controller static class UserController {
		@InitBinder public void initBinder(WebDataBinder dataBinder) {
			dataBinder.registerCustomEditor(Code.class, new FakeCodePropertyEditor());
		}
		@RequestMapping("/add") public void add(@ModelAttribute User user) {
			System.out.println(user);
		}
	}

	static class FakeCodePropertyEditor extends PropertyEditorSupport {
		public void setAsText(String text) throws IllegalArgumentException {
			Code code = new FakeCode();
			code.setId(Integer.parseInt(text));
			setValue(code);
		}

		public String getAsText() {
			return String.valueOf(((Code)getValue()).getId());
		}
	}

	static class User {
		int id; String name; Code userType;
		public int getId() {	return id;	}
		public void setId(int id) { this.id = id; }
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public Code getUserType() { return userType; }
		public void setUserType(Code userType) { this.userType = userType; }
		@Override
		public String toString() {
			return "User [id=" + id + ", name=" + name + ", userType="
					+ userType + "]";
		}		
	}

	static class Code {
		int id;
		String name;
		public int getId() { return id; }
		public void setId(int id) { this.id = id; }
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public String toString() {
			return "Code [id=" + id + ", name=" + name + "]";
		}
	}

	static class FakeCode extends Code {
		public String getName() {
			throw new UnsupportedOperationException();
		}
		public void setName(String name) {
			throw new UnsupportedOperationException();
		}
	}
	
	@Test
	public void prototypePropertyEditor() throws ServletException, IOException {
		setClasses(ViewResolver.class, UserController2.class, CodePropertyEditor.class, CodeService.class);
		initRequest("/add").addParameter("id", "1").addParameter("name", "Spring").addParameter("userType", "2");
		runService();
		User user = (User) getModelAndView().getModel().get("user");
		assertEquals(user.getUserType().getName(), "name!!");
	}

	@Controller static class UserController2 {
		@Inject Provider<CodePropertyEditor> codeEditorProvider;
		
		@InitBinder public void initBinder(WebDataBinder dataBinder) {
			dataBinder.registerCustomEditor(Code.class, codeEditorProvider.get());
		}
		@RequestMapping("/add") public void add(@ModelAttribute User user) {
			System.out.println(user);
		}
	}

	@Scope("prototype")
	static class CodePropertyEditor extends PropertyEditorSupport {
		@Autowired CodeService codeService;

		public void setAsText(String text) throws IllegalArgumentException {
			setValue(codeService.getCode(Integer.parseInt(text)));
		}
		
		public String getAsText() {
			return String.valueOf(((Code)getValue()).getId()); 
		}		
	}

	static class CodeService {
		public Code getCode(int id) {
			Code c = new Code();
			c.setId(id);
			c.setName("name!!");
			return c;
		}
	}


}
