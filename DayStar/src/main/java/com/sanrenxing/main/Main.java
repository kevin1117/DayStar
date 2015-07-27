package com.sanrenxing.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sanrenxing.entity.User;
import com.sanrenxing.mapper.UserMapper;

public class Main {
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		UserMapper mapper = context.getBean(UserMapper.class);
		System.out.println("select from  read DB:" + mapper.selectAll("zfj").size());

		User user = new User();
		user.setOpName("write11" + System.currentTimeMillis());
		mapper.save(user);
		System.out.println("write to write DB !!");
	}
}
