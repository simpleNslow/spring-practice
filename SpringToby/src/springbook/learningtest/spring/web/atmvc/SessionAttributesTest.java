package springbook.learningtest.spring.web.atmvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

public class SessionAttributesTest extends AbstractDispatcherServletTest {

	@Test
	public void sessionAttr() throws ServletException, IOException {
		setClasses(UserController.class);
		initRequest("/user/edit").addParameter("id", "1");
		runService();
		
		HttpSession session = request.getSession();
		assertEquals(session.getAttribute("user"), getModelAndView().getModel().get("user"));
		
		initRequest("/user/edit", "POST").addParameter("id", "1").addParameter("name", "Spring2");
		request.setSession(session);
		runService();
		assertEquals(((User)getModelAndView().getModel().get("user")).getEmail(), "mail@spring.com");
		assertNull(session.getAttribute("user"));

	}
	
	@Controller	
	@SessionAttributes("user")
	static class UserController {
		@RequestMapping(value="/user/edit", method=RequestMethod.GET) 
		public User form(@RequestParam int id) {
			return new User(1, "Spring", "mail@spring.com");
		}
		
		@RequestMapping(value="/user/edit", method=RequestMethod.POST) 
		public void submit(@ModelAttribute User user, SessionStatus sessionStatus) {
			sessionStatus.setComplete();
		}
	}

	static class User {
		int id; String name; String email;
		public User(int id, String name, String email) { this.id = id;			this.name = name;			this.email = email;		}
		public User() { }
		public int getId() { return id; }
		public void setId(int id) { this.id = id; }
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public String getEmail() { return email; }
		public void setEmail(String email) { this.email = email; }
		public String toString() { return "User [email=" + email + ", id=" + id + ", name=" + name + "]";	}		
	}

}
