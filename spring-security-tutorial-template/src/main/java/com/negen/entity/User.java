package com.negen.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
/**
 * 用户/账号
 * @author Negen
 *
 */
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
