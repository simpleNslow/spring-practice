package springbook.learningtest.spring.web.atmvc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;
import springbook.learningtest.spring.web.atmvc.AtControllerTest.ViewResolver;

public class ValidatorTest extends AbstractDispatcherServletTest {

	@Test
	public void callValidate() throws ServletException, IOException {
		setClasses(ViewResolver.class, UserValidator.class, UserController.class);
		//initRequest("/add");
		//initRequest("/add").addParameter("name", " ");
		//initRequest("/add").addParameter("name", "ace");
		//initRequest("/add").addParameter("name", "ace").addParameter("age", "-2");
		initRequest("/add").addParameter("name", "ace").addParameter("age", "2");
		runService();
	}

	@Controller
	static class UserController {
		@Autowired UserValidator validator;
		
		@RequestMapping("/add")
		public void add(@ModelAttribute User user, BindingResult result) {
			this.validator.validate(user, result);
			if (result.hasErrors()) {
				List<ObjectError> errors = result.getAllErrors();
				errors.stream().forEach(System.out::println);
			}
			else {
				System.out.println("No Errors!");
			}
		}
	}
	static class UserValidator implements Validator {

		@Override
		public boolean supports(Class<?> clazz) {
			// TODO Auto-generated method stub
			return (User.class.isAssignableFrom(clazz));
		}

		@Override
		public void validate(Object target, Errors errors) {
			// TODO Auto-generated method stub
			User user = (User)target;
			//if (user.getName() == null || user.getName().length() == 0)
			//	errors.rejectValue("name", "field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required");
			ValidationUtils.rejectIfEmpty(errors, "age", "field.required");
			if (errors.getFieldError("age") == null)
				if (user.getAge() < 0) errors.rejectValue("name", "field.min", new Object[] {0}, "min default");
		}
		
	}
	
	public static class User {
		int id;
		//@NotNull
		String name;
		//@Min(0)
		Integer age;
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
		
		public Integer getAge() {
			return age;
		}
		public void setAge(Integer age) {
			this.age = age;
		}
		@Override
		public String toString() {
			return "User [age=" + age + ", id=" + id + ", name=" + name + "]";
		}
	}

}
