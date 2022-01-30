package springbook.learningtest.spring.ioc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public class ScopeTest {
	@Test
	public void singletonScope() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(
				SingletonBean.class, SingletonClientBean.class);
		Set<SingletonBean> beans = new HashSet<SingletonBean>();
		beans.add(ac.getBean(SingletonBean.class));
		beans.add(ac.getBean(SingletonBean.class));
		assertEquals(beans.size(), 1);
		
		beans.add(ac.getBean(SingletonClientBean.class).bean1);
		beans.add(ac.getBean(SingletonClientBean.class).bean2);
		assertEquals(beans.size(), 1);
	}
	
	static class SingletonBean {}
	static class SingletonClientBean {
		@Autowired SingletonBean bean1;
		@Autowired SingletonBean bean2;
	}
	
	@Test
	public void prototypeScope() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(
				PrototypeBean.class, PrototypeClientBean.class);
		Set<PrototypeBean> beans = new HashSet<PrototypeBean>();
		
		beans.add(ac.getBean(PrototypeBean.class));
		assertEquals(beans.size(), 1);
		beans.add(ac.getBean(PrototypeBean.class));
		assertEquals(beans.size(), 2);
		
		beans.add(ac.getBean(PrototypeClientBean.class).bean1);
		assertEquals(beans.size(), 3);
		beans.add(ac.getBean(PrototypeClientBean.class).bean2);
		assertEquals(beans.size(), 4);
	}
	
	@Component("prototypeBean") @Scope("prototype")
	static class PrototypeBean {}
	static class PrototypeClientBean {
		@Autowired PrototypeBean bean1;
		@Autowired PrototypeBean bean2;
	}
	
	@Test
	public void objectFactory() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ObjectFactoryConfig.class);
		ObjectFactory<PrototypeBean> factoryBeanFactory = ac.getBean("prototypeBeanFactory", ObjectFactory.class);
		
		Set<PrototypeBean> beans = new HashSet<PrototypeBean>();
		for(int i = 1; i <= 4; i++) {
			beans.add(factoryBeanFactory.getObject());
			assertEquals(beans.size(), i);
		}
	}

	@Configuration
	static class ObjectFactoryConfig {
		@Bean public ObjectFactoryCreatingFactoryBean prototypeBeanFactory() {
			ObjectFactoryCreatingFactoryBean factoryBean = new ObjectFactoryCreatingFactoryBean();
			factoryBean.setTargetBeanName("prototypeBean");
			return factoryBean;
		}
	}
	
	@Test
	public void serviceLocatorFactoryBean() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ServiceLocatorConfig.class);
		PrototypeBeanFactory factory = ac.getBean(PrototypeBeanFactory.class);
		
		Set<PrototypeBean> beans = new HashSet<PrototypeBean>();
		for(int i = 1; i <= 4; i++) {
			beans.add(factory.getPrototypeBean());
			assertEquals(beans.size(), i);
		}
	}
	
	interface PrototypeBeanFactory { PrototypeBean getPrototypeBean(); }
	@Configuration
	static class ServiceLocatorConfig {
		@Bean public ServiceLocatorFactoryBean prototypeBeanFactory() {
			ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
			factoryBean.setServiceLocatorInterface(PrototypeBeanFactory.class);
			return factoryBean;
		}
	}
	
	
	@Test
	public void providerTest() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ProviderClient.class);
		ProviderClient client = ac.getBean(ProviderClient.class);
		
		Set<PrototypeBean> beans = new HashSet<PrototypeBean>();
		for(int i = 1; i <= 4; i++) {
			beans.add(client.prototypeBeanProvider.getObject());
			assertEquals(beans.size(), i);
		}
		
	}
	static class ProviderClient {
		@Autowired ObjectProvider<PrototypeBean> prototypeBeanProvider;
	}
}
