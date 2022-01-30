package springbook.sandbox;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import springbook.user.dao.UserDao;

public class SingletonTest {

	public static void main(String[] args) {
				
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml", UserDao.class);
		
		UserDao dao3 = context.getBean("userDao", UserDao.class);
		UserDao dao4 = context.getBean("userDao", UserDao.class);
		
		System.out.println(dao3);
		System.out.println(dao4);
		
	}

}
