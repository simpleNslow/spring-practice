package springbook.learningtest.spring.ioc.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springbook.learningtest.spring.ioc.bean.Hello;
import springbook.learningtest.spring.ioc.bean.Printer;
import springbook.learningtest.spring.ioc.bean.StringPrinter;

@Configuration
public class HelloConfig {
	@Value("#{systemProperties['os.name']}")
	private String name;
	
	@Bean
	public Hello hello() {
		Hello hello = new Hello();
		hello.setName(this.name);
		hello.setPrinter(printer());
		return hello;
	}
		
	@Bean
	public Printer printer() {
		return new StringPrinter();
	}
}
