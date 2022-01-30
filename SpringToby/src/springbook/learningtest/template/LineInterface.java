package springbook.learningtest.template;

@FunctionalInterface
public interface LineInterface<T> {
	T doSomethingWithLine(String line, T value);
}
