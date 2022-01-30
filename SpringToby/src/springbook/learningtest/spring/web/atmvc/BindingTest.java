package springbook.learningtest.spring.web.atmvc;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CharsetEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import springbook.learningtest.spring.web.AbstractDispatcherServletTest;
import springbook.learningtest.spring.web.atmvc.AtControllerTest.ViewResolver;
import springbook.learningtest.spring.web.atmvc.PrototypePropertyEditorTest.Code;
import springbook.learningtest.spring.web.atmvc.PrototypePropertyEditorTest.CodePropertyEditor;
import springbook.learningtest.spring.web.atmvc.SessionAttributesTest.User;
import springbook.user.domain.Level;

public class BindingTest extends AbstractDispatcherServletTest {
	@Test
	public void defaultPropertyEditor() throws ServletException, IOException {
		setClasses(ViewResolver.class, DefaultPEController.class);
		initRequest("/hello").addParameter("charset", "UTF-8");
		runService();
		assertModel("charset", Charset.forName("UTF-8"));
	}

	@Controller static class DefaultPEController {
		@RequestMapping("/hello") public void hello(@RequestParam Charset charset, Model model) {
			model.addAttribute("charset", charset);
		}
	}

	@Test
	public void charsetEditor() {
		CharsetEditor charsetEditor = new CharsetEditor();
		charsetEditor.setAsText("UTF-8");
		assertInstanceOf(Charset.class, charsetEditor.getValue());
		assertEquals((Charset)charsetEditor.getValue(), Charset.forName("UTF-8"));
	}

	@Test
	public void levelPropertyEditor() {
		LevelPropertyEditor levelEditor = new LevelPropertyEditor();
		
		levelEditor.setValue(Level.BASIC);
		assertEquals(levelEditor.getAsText(), "1");
		
		levelEditor.setAsText("3");
		assertEquals((Level)levelEditor.getValue(), Level.GOLD);
	}

	static class LevelPropertyEditor extends PropertyEditorSupport {
		public String getAsText() {
			return String.valueOf(((Level)this.getValue()).intValue());
		}

		public void setAsText(String text) throws IllegalArgumentException {
			this.setValue(Level.valueOf(Integer.parseInt(text.trim())));
		}
	}
	
	@Test
	public void levelTypeParameter() throws ServletException, IOException {
		setClasses(SearchController.class);
		initRequest("/user/search").addParameter("level", "2");
		runService();
		assertModel("level", Level.SILVER);
	}
	
	@Controller static class SearchController {
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.registerCustomEditor(Level.class, new LevelPropertyEditor());
		}
		
		@RequestMapping("/user/search") public void search(@RequestParam Level level, Model model) {
			model.addAttribute("level", level);
		}
	}
	
	
	@Test
	public void memberTest() throws ServletException, IOException {
		setClasses(ViewResolver.class, MemberController.class);
		initRequest("/add").addParameter("id", "1000").addParameter("age", "1000");
		runService();
		assertEquals(((Member)getModelAndView().getModel().get("member")).getAge(), 200);
	}

	@Controller static class MemberController {
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.registerCustomEditor(int.class, "age", new MinMaxPropertyEditor(0, 200));
		}
		
		@RequestMapping("/add") public void add(@ModelAttribute Member member) {
			System.out.println(member.getId());
			System.out.println(member.getAge());
		}
	}

	static class MinMaxPropertyEditor extends PropertyEditorSupport {
		int min;
		int max;
		
		public MinMaxPropertyEditor(int min, int max) {
			this.min = min;
			this.max = max;
		}
		
		public String getAsText() {
			return String.valueOf((Integer)this.getValue());
		}
		
		public void setAsText(String text) throws IllegalArgumentException {
			Integer val = Integer.parseInt(text);
			if (val < min) val = min;
			else if (val > max) val = max;
			this.setValue(val);
		}
	}
	
	static class Member {
		int id;
		int age;
		
		public void setId(int id) { this.id = id; }
		public int getId() { return this.id; }
		public void setAge(int age) { this.age = age; }
		public int getAge() { return this.age; }
	}
	
	@Test
	public void webBindingInitializer() throws ServletException, IOException {
		setClasses(SearchController2.class, ConfigForWebBinidngInitializer.class);
		initRequest("/user/search").addParameter("level", "2");
		runService();
		assertModel("level", Level.SILVER);
	}
	@Controller static class SearchController2 {
		@RequestMapping("/user/search") public void search(@RequestParam Level level, Model model) {
			model.addAttribute("level", level);
		}
	}

	@Configuration static class ConfigForWebBinidngInitializer {
		@Bean public RequestMappingHandlerAdapter requestMappingHandlerAdaptor() {
			return new RequestMappingHandlerAdapter() {{
				setWebBindingInitializer(webBindingInitializer());
			}};
		}
		
		@Bean public WebBindingInitializer webBindingInitializer() {
			return new WebBindingInitializer() {
				public void initBinder(WebDataBinder binder) {
					binder.registerCustomEditor(Level.class,new LevelPropertyEditor());
					
				}
			};
		}
	}

	/*
	static class MyWebBindingInitializer implements WebBindingInitializer {
		public void initBinder(WebDataBinder binder) {
			binder.registerCustomEditor(Level.class,new LevelPropertyEditor());
		}
	}
	*/
	@Test
	public void conversionService() throws ServletException, IOException {
		setClasses(SearchController3.class, MyConversionService.class);
		initRequest("/user/search").addParameter("level", "2");
		runService();
		assertModel("level", Level.SILVER);
	}
	
	@Controller static class SearchController3 {
		@Autowired MyConversionService myConversionService;
		
		@InitBinder public void initBinder(WebDataBinder dataBinder) {
			dataBinder.setConversionService(this.myConversionService);
		}
		@RequestMapping("/user/search") public void search(@RequestParam Level level, Model model) {
			model.addAttribute("level", level);
		}
	}

	static class MyConversionService extends GenericConversionService {
		public MyConversionService() {
			this.addConverter(new LevelToStringConverter());
			this.addConverter(new StringToLevelConverter());
		}
	}

	static class LevelToStringConverter implements Converter<Level, String> {
		@Override
		public String convert(Level level) {
			return String.valueOf(level.intValue());
		}
	}
	
	static class StringToLevelConverter implements Converter<String, Level> {
		@Override
		public Level convert(String text) {
			return Level.valueOf(Integer.parseInt(text));
		}
	}
	
	@Test
	public void conversionServiceFactory() throws ServletException, IOException {
		setRelativeLocations("conversionServiceFactory.xml");
		setClasses(SearchController4.class);
		initRequest("/user/search").addParameter("level", "2");
		runService();
		assertModel("level", Level.SILVER);
	}

	@Controller static class SearchController4 {
		@Autowired ConversionService conversionService;
		
		@InitBinder public void initBinder(WebDataBinder dataBinder) {
			dataBinder.setConversionService(this.conversionService);
		}
		@RequestMapping("/user/search") public void search(@RequestParam Level level, Model model) {
			model.addAttribute("level", level);
		}
	}

	@Test
	public void configurableWebBinding() throws ServletException, IOException {
		setRelativeLocations("configurableWebBindingInitializer.xml");
		setClasses(SearchController5.class);
		initRequest("/user/search").addParameter("level", "2");
		runService();
		assertModel("level", Level.SILVER);
	}

	@Controller static class SearchController5 {
		@RequestMapping("/user/search") public void search(@RequestParam Level level, Model model) {
			model.addAttribute("level", level);
		}
	}

}
