package springbook.learningtest.spring.ioc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springbook.learningtest.spring.ioc.bean.Hello;
import springbook.learningtest.spring.ioc.bean.Printer;
import springbook.learningtest.spring.ioc.bean.StringPrinter;

@Configuration
public class HelloConfig2 {
	@Bean
	public Hello hello(Printer printer) {
		Hello hello = new Hello();
		hello.setName("Spring");
		hello.setPrinter(printer);
		return hello;
	}
		
	@Bean
	public Printer printer() {
		return new StringPrinter();
	}
}