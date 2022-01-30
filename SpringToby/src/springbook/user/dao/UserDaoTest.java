package springbook.user.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
//import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import springbook.user.domain.Level;
import springbook.user.domain.User;

@ExtendWith(SpringExtension.class)
//@ContextConfiguration(locations="applicationContext.xml")
@ContextConfiguration(locations="/applicationContext.xml")
public class UserDaoTest {
	//@Autowired
	//private ApplicationContext context;
	@Autowired
	private UserDao dao;
	@Autowired DataSource dataSource;
	
	private User user1;
	private User user2;
	private User user3;

	@BeforeEach
	public void setUp() {
		//ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		//ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml", UserDao.class);
		//System.out.println(this.context);
		//System.out.println(this);
		this.user1 = new User("gyumee", "박성철", "springno1", "gyumee@org.springbook", Level.BASIC, 1, 0);
		this.user2 = new User("leegw700", "이길원", "springno2", "leegw700@org.springbook", Level.SILVER, 55, 10);
		this.user3 = new User("bumjin", "박범진", "springno3", "bumjin@org.springbook", Level.GOLD, 100, 40);
		
		//this.dao = this.context.getBean("userDao", UserDao.class);
	}
	
	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException {
		
		//User user1 = new User("gyumee", "박성철", "springno1");
		//User user2 = new User("leegw700", "이길원", "springno2");
				
		dao.deleteAll();
		assertEquals(dao.getCount(), 0);
		
		dao.add(user1);
		dao.add(user2);
		assertEquals(dao.getCount(), 2);
		
		User userget1 = dao.get(user1.getId());
		checkSameUser(userget1, user1);
		
		User userget2 = dao.get(user2.getId());
		checkSameUser(userget2, user2);

	}
	
	@Test
	public void count() throws SQLException, ClassNotFoundException {
		
		//User user1 = new User("gyumee", "박성철", "springno1");
		//User user2 = new User("leegw700", "이길원", "springno2");
		//User user3 = new User("bumjin", "박범진", "springno3");
		
		dao.deleteAll();
		assertEquals(dao.getCount(), 0);
		
		dao.add(user1);
		assertEquals(dao.getCount(), 1);

		dao.add(user2);
		assertEquals(dao.getCount(), 2);

		dao.add(user3);
		assertEquals(dao.getCount(), 3);
	}
	
	@Test
	public void getUserFailure() throws SQLException, ClassNotFoundException {
		
		dao.deleteAll();
		assertEquals(dao.getCount(), 0);
		
		assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unknown_id"));
	}
	
	@Test
	public void getAll() {
		dao.deleteAll();

		List<User> users0 = dao.getAll();
		assertEquals(users0.size(), 0);
		
		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertEquals(users1.size(), 1);
		checkSameUser(user1, users1.get(0));

		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertEquals(users2.size(), 2);
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));

		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertEquals(users3.size(), 3);
		checkSameUser(user3, users3.get(0));
		checkSameUser(user1, users3.get(1));
		checkSameUser(user2, users3.get(2));

	}
	
	private void checkSameUser(User user1, User user2) {
		assertEquals(user1.getId(), user2.getId());
		assertEquals(user1.getName(), user2.getName());
		assertEquals(user1.getPassword(), user2.getPassword());
		assertEquals(user1.getLevel(), user2.getLevel());
		assertEquals(user1.getLogin(), user2.getLogin());
		assertEquals(user1.getRecommend(), user2.getRecommend());
	}
	
	@Test
	public void duplicateKey() {
		dao.deleteAll();
		
		dao.add(user1);
		assertThrows(DuplicateKeyException.class, () -> {
			dao.add(user1);
		});
	}
	
	@Test
	public void sqlExceptionTranslate() {
		dao.deleteAll();
		
		try {
			dao.add(user1);
			dao.add(user1);
		}
		catch(DuplicateKeyException ex) {
			SQLException sqlEx = (SQLException)ex.getRootCause();
			//System.out.println(sqlEx);
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			//System.out.println(set.translate(null, null, sqlEx));
			
			assertTrue(set.translate(null, null, sqlEx) instanceof DuplicateKeyException);
		}
	}
	
	@Test
	public void update() {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user2);
		
		user1.setName("오민규");
		user1.setPassword("springno6");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1, user1update);
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
		
	}
}
