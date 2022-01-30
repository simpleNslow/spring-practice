package springbook.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

//import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@ExtendWith(SpringExtension.class)
//@ContextConfiguration(locations="applicationContext.xml")
//@ContextConfiguration(locations="applicationAOPContext.xml")
//@ContextConfiguration(locations="applicationTXContext.xml")
@ContextConfiguration(locations="annotationTXContext.xml")
@Transactional
//@TransactionConfiguration(defaultRollback=false)   // deprecated
public class UserServiceTest {
	@Autowired
	UserService userService;
	@Autowired
	UserService testUserService;
	//@Autowired
	//UserServiceImpl userServiceImpl;
	@Autowired
	PlatformTransactionManager transactionManager;
	@Autowired
	UserDao userDao;
	@Autowired
	MailSender mailSender;
	@Autowired
	ApplicationContext context;
	
	List<User> users;
	
	@BeforeEach
	public void setUp() {
		users = Arrays.asList(
			new User("bumjin", "박범진", "p1", "bumjin@org.springbook", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
			new User("joytouch", "강명성", "p2", "joytouch@org.springbook", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
			new User("erwins", "신승한", "p3", "erwin@org.springbook", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1),
			new User("madnite1", "이상호", "p4", "madnite1@org.springbook", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
			new User("green", "오민규", "p5", "green@org.springbook", Level.GOLD, 100, Integer.MAX_VALUE)
		);
	}
	
	@Test
	public void bean() {
		assertNotNull(this.userService);
	}
	
	@Test
	@DirtiesContext
	public void upgradeLevels() throws Exception {
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		MockMailSender mockMailSender = new MockMailSender();
		userService.setMailSender(mockMailSender);
		
		userService.upgradeLevels();
		
		checkLevel(users.get(0), false);
		checkLevel(users.get(1), true);
		checkLevel(users.get(2), false);
		checkLevel(users.get(3), true);
		checkLevel(users.get(4), false);
		
		List<String> request = mockMailSender.getRequests();
		assertEquals(request.size(), 2);
		assertEquals(request.get(0), users.get(1).getEmail());
		assertEquals(request.get(1), users.get(3).getEmail());
		
	}
	
	private void checkLevel(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if (upgraded) {
			assertEquals(userUpdate.getLevel(), user.getLevel().nextLevel());
		}
		else {
			assertEquals(userUpdate.getLevel(), user.getLevel());
		}
	}
	
	@Test
	public void add() {
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);  // GOLD level
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertEquals(userWithLevelRead.getLevel(), userWithLevel.getLevel());
		assertEquals(userWithoutLevelRead.getLevel(), Level.BASIC);
	}
	
	@Test
	//@DirtiesContext
	public void upgradeAllOrNothing() throws Exception {
		
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try {
			this.testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		}
		catch(TestUserServiceException e) {
			
		}
		checkLevel(users.get(1), false);
	}
	
	static class TestUserService extends UserServiceImpl {
		private String id = "madnite1";
		
		/*
		private TestUserService(String id) {
			this.id = id;
		}
		*/
		
		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
		
		public List<User> getAll() {
			for(User user : super.getAll()) {
				super.update(user);
			}
			return null;
		}
	}
	
	static class TestUserServiceException extends RuntimeException {
	}
	
	static class MockUserDao implements UserDao {
		private List<User> users;
		private List<User> updated = new ArrayList<User>();
		
		private MockUserDao(List<User> users) {
			this.users = users;
		}
		
		public List<User> getUpdated() {
			return this.updated;
		}
		
		public List<User> getAll() {
			return this.users;
		}
		
		public void update(User user) {
			updated.add(user);
		}
		
		public void add(User user) { throw new UnsupportedOperationException(); }
		public void deleteAll() { throw new UnsupportedOperationException(); }
		public User get(String id) { throw new UnsupportedOperationException(); }
		public int getCount() { throw new UnsupportedOperationException(); }
		
	}
	
	@Test
	public void upgradeLevelsWithMock() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated();
		assertEquals(updated.size(), 2);
		checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
		checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);
		
		List<String> request = mockMailSender.getRequests();
		assertEquals(request.size(), 2);
		assertEquals(request.get(0), users.get(1).getEmail());
		assertEquals(request.get(1), users.get(3).getEmail());
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertEquals(updated.getId(), expectedId);
		assertEquals(updated.getLevel(), expectedLevel);
	}
	
	@Test
	public void advisorAutoProxyCreator() {
		assertTrue(testUserService instanceof java.lang.reflect.Proxy);
	}
	
	@Test
	public void readOnlyTransactionAttribute() {
		assertThrows(UncategorizedSQLException.class, () -> testUserService.getAll());
	}
	
	@Test
	public void transactionSync() {
		userDao.deleteAll();
		assertEquals(userDao.getCount(), 0);
		
		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
		//txDefinition.setReadOnly(true);
		
		TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
				
		userService.deleteAll();
		
		userService.add(users.get(0));
		userService.add(users.get(1));
		assertEquals(userDao.getCount(), 2);
		
		transactionManager.commit(txStatus);
		assertEquals(userDao.getCount(), 2);
		//transactionManager.rollback(txStatus);
		//assertEquals(userDao.getCount(), 0);
	}
	
	@Test
	public void transactionSync2() {
		
		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
		TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);

		try {
			userService.deleteAll();
			
			userService.add(users.get(0));
			userService.add(users.get(1));
		}
		finally {
			transactionManager.rollback(txStatus);
		}
	}
	
	@Test
	@Transactional   // after test, do rollback
	public void transactionSync3() {
		userService.deleteAll();
		userService.add(users.get(0));
		userService.add(users.get(1));
	}
	
	@Test
	@Transactional
	@Rollback(false)  // after test, do commit
	public void transactionSync4() {
		userService.deleteAll();
		userService.add(users.get(0));
		userService.add(users.get(1));
	}

}
