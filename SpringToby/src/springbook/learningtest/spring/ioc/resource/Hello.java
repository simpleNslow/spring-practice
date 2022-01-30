package springbook.learningtest.spring.ioc.resource;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Hello {
	@Value("Spring")
	private String name;
	
	//@Resource
	@Autowired
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
	
	public void setName(String name) {
		this.name = name;
	}
	
	/* printer 필드에 @Resource를 사용시 set함수는 필요없음.
	/*
	@Resource(name="printer")
	public void setPrinter(Printer printer) {
		this.printer = printer;
	}
	*/

	public Printer getPrinter() {
		return printer;
	}
}