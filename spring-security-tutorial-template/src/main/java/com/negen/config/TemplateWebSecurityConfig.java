package com.negen.config;

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
