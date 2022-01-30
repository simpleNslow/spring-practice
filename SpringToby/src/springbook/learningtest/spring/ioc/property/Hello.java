package springbook.learningtest.spring.ioc.property;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Hello {
	//@Value("#{systemProperties['os.name']}")
	//@Value("Everyone")
	private String name;
	private Printer printer;
	
	public Hello() {
	}

	public Hello(String name, Printer printer) {
		this.name = name;
		this.printer = printer;
	}

	public String sayHello() {
		return "Hello " + name;
	}
	
	public void print() {
		this.printer.print(this.sayHello());
	}
	
	@Value("Everyone")
	public void setName(String name) {
		this.name = name;
	}
	
	@Resource(name="printer")
	public void setPrinter(Printer printer) {
		this.printer = printer;
	}

	public Printer getPrinter() {
		return printer;
	}
}