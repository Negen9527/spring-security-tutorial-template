# 【晓时代】SpringBoot + Jpa + Security 简单实例

### 零、项目结构
![图片.png](https://cdn.nlark.com/yuque/0/2020/png/266876/1579316732977-894ae747-0ef0-41bf-9a4f-34e1dbd55530.png#align=left&display=inline&height=703&name=%E5%9B%BE%E7%89%87.png&originHeight=703&originWidth=337&size=27524&status=done&style=none&width=337)

### 一、创建数据库
![图片.png](https://cdn.nlark.com/yuque/0/2020/png/266876/1579313113213-e6187f3f-c084-42a4-b298-31a14879ebec.png#align=left&display=inline&height=226&name=%E5%9B%BE%E7%89%87.png&originHeight=226&originWidth=404&size=5585&status=done&style=none&width=404)

### 二、创建项目
1、基础配置
![图片.png](https://cdn.nlark.com/yuque/0/2020/png/266876/1579313142274-e808852f-9637-4c71-96cb-e812ceca8109.png#align=left&display=inline&height=466&name=%E5%9B%BE%E7%89%87.png&originHeight=466&originWidth=541&size=15982&status=done&style=none&width=541)

2、选择基础依赖
![图片.png](https://cdn.nlark.com/yuque/0/2020/png/266876/1579313160171-af7ac4b2-be6e-48c7-b4e8-b5c0fab60ef1.png#align=left&display=inline&height=571&name=%E5%9B%BE%E7%89%87.png&originHeight=571&originWidth=536&size=19310&status=done&style=none&width=536)

3、引入 LomBok 依赖，让开发跑的飞起
```xml
		<dependency>
		    <groupId>org.projectlombok</groupId>
		    <artifactId>lombok</artifactId>
		    <scope>provided</scope>
		</dependency>
```

4、去掉 mysql 依赖的 scope 标签
![图片.png](https://cdn.nlark.com/yuque/0/2020/png/266876/1579253318299-e29e1e7e-a26e-42c3-8f92-f51872eb177f.png#align=left&display=inline&height=133&name=%E5%9B%BE%E7%89%87.png&originHeight=133&originWidth=702&size=7002&status=done&style=none&width=702)

5、完整依赖
```xml
<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<!-- <scope>runtime</scope> -->
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-tomcat</artifactId>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
<!-- 			<exclusions>
				<exclusion>
					<groupId>org.junit.vintage</groupId>
					<artifactId>junit-vintage-engine</artifactId>
				</exclusion>
			</exclusions> -->
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
		<dependency>
		    <groupId>org.projectlombok</groupId>
		    <artifactId>lombok</artifactId>
		    <scope>provided</scope>
		</dependency>
```

### 三、修改application.properties 文件名为 application.yml 并写入如下配置
```yaml
server:
  port: 8081
spring:
  datasource:
    #mysql驱动类
    driver-class-name: com.mysql.cj.jdbc.Driver 
    #数据库连接地址
    url: jdbc:mysql://127.0.0.1:3306/db_spring_security_tutorial?serverTimezone=Asia/Shanghai&autoReconnect=true
    #数据库账号
    username: root
    #密码
    password: 123456
  jpa:
    hibernate:
      #自动建表
      ddl-auto: update
    #方言
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
    #显示sql语句
    show-sql: true
```

### 四、创建实体类，并创建 UserRepository.java
1、Permission.java
```java
@Entity
@Table(name = "permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;
	String permissionName;	//权限名称
	
	public Permission(String permissionName){
		this.permissionName = permissionName;
	}
}
```

2、Role.java
```java
@Entity
@Table(name = "role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;
	String roleName;	//角色名称
	@OneToMany(cascade = {CascadeType.ALL} , fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id")
	List<Permission> permissions;
}
```

3、User.java
```java
@Entity
@Table(name = "user")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;
	String userName;	//账号
	String password;	//密码
	String salt;		//盐
	@OneToMany(cascade = {CascadeType.ALL} , fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	List<Role> roles;
}
```

4、UserRepository.java

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	User findByUserName(String username);
}
```

### 五、更改账号验证方式，自定义UserDetailsService
--创建 TemplateUserDetailsService.java
```java
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.negen.entity.Permission;
import com.negen.entity.Role;
import com.negen.entity.User;
import com.negen.repository.UserRepository;
@Service
public class TemplateUserDetailsService implements UserDetailsService{
	@Autowired
	UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User loginUser = userRepository.findByUserName(username);
		if (null == loginUser) {
			//账号不存在，抛出异常
			throw new UsernameNotFoundException(username);
		} else {
			//用户存在，创建 SimpleGrantedAuthority集合
			List<SimpleGrantedAuthority> authorities = 
					new ArrayList<SimpleGrantedAuthority>();
			//遍历角色
			for(Role role:loginUser.getRoles()) {
				//遍历权限
				for(Permission permission:role.getPermissions()) {
					//根据权限名称创建 SimpleGrantedAuthority
					SimpleGrantedAuthority authority = 
							new SimpleGrantedAuthority(permission.getPermissionName());
					authorities.add(authority);
				}
			}
			return new org.springframework.security.core.userdetails.User(
					username,	//用户名
					loginUser.getPassword(),	//用户密码
					authorities		//权限集合
					);
		}
	}
}
```

### 六、自定义配置类
1、创建 TemplateWebSecurityConfig.java 并继承 WebSecurityConfigurerAdapter
2、重写两个 configure

![图片.png](https://cdn.nlark.com/yuque/0/2020/png/266876/1579311444379-a74e8ce4-0aae-40da-a693-a8a89c2497d6.png#align=left&display=inline&height=230&name=%E5%9B%BE%E7%89%87.png&originHeight=230&originWidth=445&size=11930&status=done&style=none&width=445)

3、完整代码 TemplateWebSecurityConfig.java 如下：
```java
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.negen.repository.UserRepository;
import com.negen.service.impl.TemplateUserDetailsService;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TemplateWebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserRepository userRepository;
	@Autowired
	TemplateUserDetailsService templateUserDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().authorizeRequests()
				.antMatchers("/user/register",
						"/swagger*//**",
						"/v2/api-docs",
						"/webjars*//**").permitAll()    //过滤 swagger2		
				.anyRequest().authenticated()    //配置所有除上面以为的所有请求必须认证（登录）后才能访问
				.and()
				.formLogin()
				.loginPage("/user/login")
				.loginProcessingUrl("/login")    //登录接口地址
				.successHandler(authenticationSuccessHandler())  //登录成功处理
				.failureHandler(authenticationFailureHandler())  //登录失败处理
				.permitAll();
	}

	// 密码加密方式
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setHideUserNotFoundExceptions(false); // 设置是否隐藏 UserNotFoundException
		provider.setUserDetailsService(templateUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	// 认证成功处理
	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		// 认证（登录）成功
		return new AuthenticationSuccessHandler() {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				response.setContentType("application/json;charset=utf-8");
				PrintWriter out = response.getWriter();
				out.write("登录成功");
				out.flush();
			}
		};
	}

	// 认证失败处理
	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new AuthenticationFailureHandler() {

			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				response.setContentType("application/json;charset=utf-8");
				PrintWriter out = response.getWriter();
				if (exception instanceof UsernameNotFoundException) {
					// 账号不存在
					out.write("账号不存在");
					out.flush();
					return;
				}
				// 密码错误
				out.write("密码错误");
				out.flush();
			}
		};
	}
}
```

### 七、创建测试类新增一条用户记录
1、UserTest.java 完整代码如下：
```java
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
```

2、运行单元测试添加一位用户

### 八、测试
打开postman进行登录接口测试
1、账号不存在
![图片.png](https://cdn.nlark.com/yuque/0/2020/png/266876/1579316590060-09f0da81-6ae1-44ed-aa5a-60a3e583f1fc.png#align=left&display=inline&height=262&name=%E5%9B%BE%E7%89%87.png&originHeight=262&originWidth=1068&size=22719&status=done&style=none&width=1068)
2、密码错误
![图片.png](https://cdn.nlark.com/yuque/0/2020/png/266876/1579316621541-ea74397a-068f-4ec2-b51c-10b0ca7a2bb8.png#align=left&display=inline&height=259&name=%E5%9B%BE%E7%89%87.png&originHeight=259&originWidth=1073&size=23067&status=done&style=none&width=1073)
3、登录成功
![图片.png](https://cdn.nlark.com/yuque/0/2020/png/266876/1579316658557-880b4e7b-4e14-4a42-a6c7-807961725b8b.png#align=left&display=inline&height=251&name=%E5%9B%BE%E7%89%87.png&originHeight=251&originWidth=1084&size=24137&status=done&style=none&width=1084)

### 九、示例下载地址
### [https://github.com/Negen9527/spring-security-tutorial-template](https://github.com/Negen9527/spring-security-tutorial-template)




