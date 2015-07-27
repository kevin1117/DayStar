package com.sanrenxing.mapper;

import java.util.List;

import com.sanrenxing.entity.User;


public interface UserMapper {

	public List selectAll(String username);

	public void save(User user);

}
