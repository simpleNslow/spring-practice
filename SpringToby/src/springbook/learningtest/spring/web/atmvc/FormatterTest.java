package springbook.learningtest.spring.web.atmvc;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;
import springbook.learningtest.spring.web.atmvc.AtControllerTest.ViewResolver;

public class FormatterTest extends AbstractDispatcherServletTest {
	@Test
	public void formatTest() throws ServletException, IOException {
		setRelativeLocations("mvc-annotation.xml");
		setClasses(ViewResolver.class, UserController.class);
		initRequest("/hello").addParameter("money", "$1,234,567,890.12").addParameter("date", "01/02/1999");;
		runService();
		assertEquals(((User)getModelAndView().getModel().get("user")).getDate().toString(), "Mon Feb 01 00:00:00 KST 1999");
		assertEquals(((User)getModelAndView().getModel().get("user")).getMoney().toString(), "1234567890.12");
	}

	static class User {
		@DateTimeFormat(pattern="dd/MM/yyyy")
		Date date;
		public Date getDate() { return date; }
		public void setDate(Date date) { this.date = date; }
		
		@NumberFormat(pattern="$###,##0.00")
		BigDecimal money;
		public BigDecimal getMoney() { return money; }
		public void setMoney(BigDecimal money) { this.money = money; }
	}
	
	@Controller static class UserController {
		@RequestMapping("/hello") void hello(User user) {
			System.out.println(user.date);
			System.out.println(user.money);
		}
	}

}
