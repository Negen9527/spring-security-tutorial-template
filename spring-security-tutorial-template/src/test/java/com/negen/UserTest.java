package com.negen;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.negen.entity.Permission;
import com.negen.entity.Role;
import com.negen.entity.User;
import com.negen.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {
	@Autowired
	UserRepository userRepository;
	
	@Test
	public void testAddUser() {
		List<Role> roles = new ArrayList<Role>();
		List<Permission> permissions = new ArrayList<Permission>();
		User user = new User();
		Role role = new Role();
		Permission p1 = new Permission("create");
		Permission p2 = new Permission("delete");
		permissions.add(p1);
		permissions.add(p2);
		role.setRoleName("admin");
		role.setPermissions(permissions);
		roles.add(role);
		user.setUserName("Negen");
		user.setPassword(new BCryptPasswordEncoder().encode("123456"));
		user.setRoles(roles);
		userRepository.save(user);
		System.out.println("====>添加用户成功");
	}
}
