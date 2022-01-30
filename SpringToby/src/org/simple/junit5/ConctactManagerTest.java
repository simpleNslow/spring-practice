package org.simple.junit5;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@TestInstance(Lifecycle.PER_CLASS)
public class ConctactManagerTest {

	private static ContactManager contactManager;
	
	@BeforeAll
	public static void setupAll() {
		System.out.println("Should Print Before All Tests");
		contactManager = new ContactManager();	
	}
	
	@BeforeEach
	public void setup() {
		System.out.println("Instantiating Contact Manager");
		contactManager.clear();
	}
	@Test
	@DisplayName("Should Create Contact")
	public void shouldCreateContact() {
		contactManager.addContact("John", "Doe", "0123456789");
		assertFalse(contactManager.getAllContacts().isEmpty());
		assertEquals(1, contactManager.getAllContacts().size());
	}
	
	@Test
	@DisplayName("Should Not Create Contact When First Name is Null")
	public void shouldThrowRuntimeExceptionWhenFirstNameIsNull() {
		assertThrows(RuntimeException.class, () -> {
			contactManager.addContact(null, "Doe", "01234567789");
		});
	}

	@Test
	@DisplayName("Should Not Create Contact When Last Name is Null")
	public void shouldThrowRuntimeExceptionWhenLastNameIsNull() {
		assertThrows(RuntimeException.class, () -> {
			contactManager.addContact("John", null, "01234567789");
		});
	}

	@Test
	@DisplayName("Should Not Create Contact When Phone Number is Null")
	@EnabledOnOs(value=OS.LINUX)
	public void shouldThrowRuntimeExceptionWhenPhoneNumberIsNull() {
		assertThrows(RuntimeException.class, () -> {
			contactManager.addContact("John", "Doe", null);
		});
	}
	
	@Test
	@DisplayName("Test Contact Creation on Developer Machine")
	public void shouldTestContactCreationOnDEV() {
	    Assumptions.assumeTrue("DEV".equals(System.getProperty("ENV")));
	    contactManager.addContact("John", "Doe", "0123456789");
	    assertFalse(contactManager.getAllContacts().isEmpty());
	    assertEquals(1, contactManager.getAllContacts().size());
	}
	
	@DisplayName("Repeat Contact Creation Test 5 Times")
	@RepeatedTest(5)
	public void shouldTestContactCreationRepeatedly() {
	    contactManager.addContact("John", "Doe", "0123456789");
	    assertFalse(contactManager.getAllContacts().isEmpty());
	    assertEquals(1, contactManager.getAllContacts().size());
	}
	
	@Nested
	class ParameterizedTests {
		@DisplayName("Phone Number should match the required Format")
		@ParameterizedTest
		@ValueSource(strings = {"0123456789", "1234567890", "+0123456789"})
		public void shouldTestPhoneNumberFormat(String phoneNumber) {
		    contactManager.addContact("John", "Doe", phoneNumber);
		    assertFalse(contactManager.getAllContacts().isEmpty());
		    assertEquals(1, contactManager.getAllContacts().size());
		}
		
		@DisplayName("Method Source Case - Phone Number should match the required Format")
		@ParameterizedTest
		@MethodSource("phoneNumberList")
		public void shouldTestPhoneNumberFormatUsingMethodSource(String phoneNumber) {
		    contactManager.addContact("John", "Doe", phoneNumber);
		    assertFalse(contactManager.getAllContacts().isEmpty());
		    assertEquals(1, contactManager.getAllContacts().size());
		}
	
		private List<String> phoneNumberList() {
		    return Arrays.asList("0123456789", "1234567890", "+0123456789");
		}
		
		@DisplayName("CSV Source Case - Phone Number should match the required Format")
		@ParameterizedTest
		@CsvSource({"0123456789", "1234567890","+0123456789"})
		public void shouldTestPhoneNumberFormatUsingCSVSource(String phoneNumber) {
		    contactManager.addContact("John", "Doe", phoneNumber);
		    assertFalse(contactManager.getAllContacts().isEmpty());
		    assertEquals(1, contactManager.getAllContacts().size());
		}
		
		@DisplayName("CSV File Source Case - Phone Number should match the required Format")
		@ParameterizedTest
		@CsvFileSource(resources = "data.dat")
		public void shouldTestPhoneNumberFormatUsingCSVFileSource(String phoneNumber) {
		    contactManager.addContact("John", "Doe", phoneNumber);
		    assertFalse(contactManager.getAllContacts().isEmpty());
		    assertEquals(1, contactManager.getAllContacts().size());
		}
	}
	
	@Test
	@DisplayName("Test Should Be Disabled")
	@Disabled
	public void shouldBeDisabled() {
	    throw new RuntimeException("Test Should Not be executed");
	}
	
	@AfterEach
	public void tearDown() {
		System.out.println("Should Execute After Each Test");
	}

	@AfterAll
	public static void tearDownAll() {
		System.out.println("Should be executed at the end of the Test");
		contactManager = null;
	}
}
