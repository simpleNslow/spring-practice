package practice.java.lang;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ClassTest {
	@Test
	public void isAssignableFromTest() {
		assertTrue(User.class.isAssignableFrom(UserSub.class));
	}
	
	static class User {}
	static class UserSub extends User {}

}
