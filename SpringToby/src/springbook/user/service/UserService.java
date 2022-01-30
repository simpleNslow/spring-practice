package springbook.user.service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.transaction.annotation.Transactional;

import springbook.user.domain.User;

@Transactional
public interface UserService {
	void add(User user);
	void deleteAll();
	void update(User user);
	void upgradeLevels();
	void setMailSender(MailSender mailSender);
	
	@Transactional(readOnly=true)
	User get(String id);
	
	@Transactional(readOnly=true)
	List<User> getAll();
}
