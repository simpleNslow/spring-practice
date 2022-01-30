package springbook.learningtest.spring.ioc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import springbook.learningtest.spring.ioc.property.Hello;
import springbook.learningtest.spring.ioc.property.HelloConfig;

public class PropertyTest {
	private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/";

	@Test
	public void valueTest() {
		GenericApplicationContext ac = new GenericXmlApplicationContext(basePath + "propertyValueContext.xml");
		
		Hello hello = ac.getBean("hello", Hello.class);
		
		hello.print();
		System.out.println(ac.getBean("printer").toString());
		assertEquals(ac.getBean("printer").toString(), "Hello Everyone");
		ac.close();
	}
	
	@Test
	public void configurationTest() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(HelloConfig.class);
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		System.out.println(hello.sayHello());
		System.out.println(ac.getBean("printer").toString());
		
		//assertEquals(hello.sayHello(), "Hello Spring");
	}
}
