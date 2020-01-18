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

/**
 * 角色
 * @Author Negen
 * @Date 2020年1月17日 下午5:34:29
 */
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
