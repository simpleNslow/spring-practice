package springbook.learningtest.spring.ioc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import springbook.learningtest.spring.ioc.bean.AnnotatedHello;
import springbook.learningtest.spring.ioc.bean.AnnotatedHello2;
import springbook.learningtest.spring.ioc.bean.AnnotatedHelloConfig;
import springbook.learningtest.spring.ioc.bean.Hello;
import springbook.learningtest.spring.ioc.bean.Printer;
import springbook.learningtest.spring.ioc.bean.StringPrinter;
import springbook.learningtest.spring.ioc.config.HelloConfig;
import springbook.learningtest.spring.ioc.config.HelloConfig2;
import springbook.learningtest.spring.ioc.config.HelloService;

public class ApplicationContextTest {
	private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/";

	@Test
	public void registerBean() {
		StaticApplicationContext ac = new StaticApplicationContext();
		ac.registerBean("hello1", Hello.class);
		
		Hello hello1 = ac.getBean("hello1", Hello.class);
		assertNotNull(hello1);
		
		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");
		ac.registerBeanDefinition("hello2", helloDef);
		
		Hello hello2 = ac.getBean("hello2", Hello.class);
		assertEquals(hello2.sayHello(), "Hello Spring");
		
		assertNotSame(hello1, hello2);
		assertEquals(ac.getBeanFactory().getBeanDefinitionCount(), 2);
		
		ac.close();
	}
	
	@Test
	public void registerBeanWithDependency() {
		StaticApplicationContext ac = new StaticApplicationContext();
		
		ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));
		
		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");
		helloDef.getPropertyValues().addPropertyValue("printer", new RuntimeBeanReference("printer"));
		ac.registerBeanDefinition("hello", helloDef);
		
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		
		assertEquals(ac.getBean("printer").toString(), "Hello Spring");
		
		ac.close();
	}

	@Test
	public void genericApplicatonContext() {
		GenericApplicationContext ac = new GenericApplicationContext();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
		reader.loadBeanDefinitions("springbook/learningtest/spring/ioc/genericApplicationContext.xml");
		
		ac.refresh();
		
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		
		assertEquals(ac.getBean("printer").toString(), "Hello Spring");
	}
	
	@Test
	public void genericXmlApplicatonContext() {
		GenericApplicationContext ac = new GenericXmlApplicationContext(basePath + "genericApplicationContext.xml");
		
		Hello hello = ac.getBean("hello", Hello.class);
		
		hello.print();
		
		assertEquals(ac.getBean("printer").toString(), "Hello Spring");
		ac.close();
	}
	
	@Test
	public void createContextWithoutParent() {
		assertThrows(BeanCreationException.class, () -> {
			ApplicationContext child = new GenericXmlApplicationContext(basePath + "childContext.xml");
		});
	}

	@Test
	public void contextHierarchy() {
		ApplicationContext parent = new GenericXmlApplicationContext(basePath + "parentContext.xml");
		
		GenericApplicationContext child = new GenericApplicationContext(parent);
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
		reader.loadBeanDefinitions(basePath + "childContext.xml");
		child.refresh();
		
		Printer printer = child.getBean("printer", Printer.class);
		assertNotNull(printer);
		
		Hello hello = child.getBean("hello", Hello.class);
		assertNotNull(hello);
		
		hello.print();
		assertEquals(printer.toString(), "Hello Child");
	}
	
	@Test
	public void simpleBeanScanning() {
		ApplicationContext ac = new AnnotationConfigApplicationContext("springbook.learningtest.spring.ioc.bean");
		AnnotatedHello hello = ac.getBean("annotatedHello", AnnotatedHello.class);
		assertNotNull(hello);		
	}
	
	@Test
	public void simpleBeanScanning2() {
		ApplicationContext ac = new AnnotationConfigApplicationContext("springbook.learningtest.spring.ioc.bean");
		AnnotatedHello2 hello = ac.getBean("myAnnotatedHello2", AnnotatedHello2.class);
		assertNotNull(hello);		
	}

	@Test
	public void filteredBeanScanning() {
		ApplicationContext ctx = new GenericXmlApplicationContext(basePath + "filteredScanningContext.xml");
		Hello hello = ctx.getBean("hello", Hello.class);
		assertNotNull(hello);
	}
	
	@Test
	public void configurationTest() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(AnnotatedHelloConfig.class);
		AnnotatedHello hello = ac.getBean("annotatedHello", AnnotatedHello.class);
		assertNotNull(hello);
		
		AnnotatedHelloConfig config = ac.getBean("annotatedHelloConfig", AnnotatedHelloConfig.class);
		assertNotNull(config);
		
		assertSame(config.annotatedHello(), hello);
		
		System.out.println(ac.getBean("systemProperties").getClass());
	}
	
	@Test
	public void configurationTest2() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(HelloConfig.class);
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		assertEquals(hello.sayHello(), "Hello Spring");
		
		Hello hello2 = ac.getBean("hello2", Hello.class); 
		hello2.print();
		assertEquals(hello2.sayHello(), "Hello Spring2");
		
		assertNotSame(hello, hello2);
		
		Printer printer = hello.getPrinter();
		Printer printer2 = hello2.getPrinter();
		assertSame(printer, printer2);
	}

	@Test
	public void configurationTest3() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(HelloService.class);
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		assertEquals(hello.sayHello(), "Hello Spring");
		
		Hello hello2 = ac.getBean("hello2", Hello.class); 
		hello2.print();
		assertEquals(hello2.sayHello(), "Hello Spring2");
		
		assertNotSame(hello, hello2);
		
		Printer printer = hello.getPrinter();
		Printer printer2 = hello2.getPrinter();
		assertNotSame(printer, printer2);
	}

	@Test
	public void configurationTest4() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(HelloConfig2.class);
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		assertEquals(hello.sayHello(), "Hello Spring");
	}

	@Test
	public void constructorArgName() {
		ApplicationContext ac = new GenericXmlApplicationContext(basePath + "constructorInjection.xml");
		
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		
		assertEquals(ac.getBean("printer").toString(), "Hello Spring");
	}

}
