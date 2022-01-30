package springbook.learningtest.spring.ioc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import springbook.learningtest.spring.ioc.resource.Hello;

public class AnnotationConfigurationTest {
	private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/";

	@Test
	public void atResource() {
		ApplicationContext ac = new GenericXmlApplicationContext(basePath + "resource.xml");

		Hello hello = ac.getBean("hello", Hello.class);
		
		hello.print();
		
		assertEquals(ac.getBean("printer").toString(), "Hello Spring");
	}

	@Test
	public void atAutowiredCollection() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(Client.class, ServiceA.class, ServiceB.class);
		Client client = ac.getBean(Client.class);
		assertEquals(client.beanBArray.length, 2);
		assertEquals(client.beanBSet.size(), 2);
		assertEquals(client.beanBMap.entrySet().size(), 2);
		assertEquals(client.beanBList.size(), 2);
		assertEquals(client.beanBCollection.size(), 2);
	}
	
	// atAutowiredCollection test 
	static class Client {
		@Autowired Set<Service> beanBSet;
		@Autowired Service[] beanBArray;		
		@Autowired Map<String, Service> beanBMap;		
		@Autowired List<Service> beanBList;		
		@Autowired Collection<Service> beanBCollection;		
	}	
	interface Service {}	
	static class ServiceA implements Service {}	
	static class ServiceB implements Service {}

	@Test
	public void atQualifier() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(QClient.class, QServiceA.class, QServiceB.class);
		QClient qclient = ac.getBean(QClient.class);
		assertTrue(qclient.service instanceof QServiceA);
	}
	
	static class QClient {
		@Autowired @Qualifier("main") Service service;
	}
	
	@Qualifier("main")
	static class QServiceA implements Service {}
	static class QServiceB implements Service {}

}
