package springbook.learningtest.jdk;

public class HelloUppercase implements Hello {

	Hello hello;
	
	public HelloUppercase(Hello hello) {
		this.hello = hello;
	}
	
	@Override
	public String sayHello(String name) {
		// TODO Auto-generated method stub
		return hello.sayHello(name).toUpperCase();
	}

	@Override
	public String sayHi(String name) {
		// TODO Auto-generated method stub
		return hello.sayHi(name).toUpperCase();
	}

	@Override
	public String sayThankYou(String name) {
		// TODO Auto-generated method stub
		return hello.sayThankYou(name).toUpperCase();
	}

	@Override
	public String saidHello(String name) {
		// TODO Auto-generated method stub
		return hello.saidHello(name).toUpperCase();
	}

}
