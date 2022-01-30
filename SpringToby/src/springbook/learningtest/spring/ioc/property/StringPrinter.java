package springbook.learningtest.spring.ioc.property;

import org.springframework.stereotype.Component;

@Component("printer")
public class StringPrinter implements Printer {
	private StringBuffer buffer = new StringBuffer();

	public void print(String message) {
		this.buffer.append(message);
	}

	public String toString() {
		return this.buffer.toString();
	}
}

