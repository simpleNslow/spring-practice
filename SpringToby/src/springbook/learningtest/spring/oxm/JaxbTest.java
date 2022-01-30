package springbook.learningtest.spring.oxm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class JaxbTest {
	@Test
	public void jaxb2MarshallerTest() {
		Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
		unmarshaller.setContextPath("springbook.user.sqlservice.jaxb");
		
		Source xmlSource = new StreamSource(
				getClass().getResourceAsStream("sqlmap.xml"));
			
			Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(xmlSource);
			
			List<SqlType> sqlList = sqlmap.getSql();
			assertEquals(sqlList.size(), 3);
			assertEquals(sqlList.get(0).getKey(), "add");
			assertEquals(sqlList.get(0).getValue(), "insert");
			assertEquals(sqlList.get(1).getKey(), "get");
			assertEquals(sqlList.get(1).getValue(), "select");
			assertEquals(sqlList.get(2).getKey(), "delete");
			assertEquals(sqlList.get(2).getValue(), "delete");
	}
	
	@Test
	public void jaxb2MarshallerTest2() {
		Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
		
	}
}
