package springbook.learningtest.spring.ioc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import springbook.learningtest.spring.ioc.bean.Hello;
import springbook.learningtest.spring.ioc.property.HelloConfig;

public class PropertyTest2 {
	private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/";
	
	@Test
	public void configurationTest() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(HelloConfig.class);
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		//System.out.println(hello.sayHello());
		//System.out.println(ac.getBean("printer").toString());
		
		assertEquals(hello.sayHello(), "Hello Linux");
	}
}
