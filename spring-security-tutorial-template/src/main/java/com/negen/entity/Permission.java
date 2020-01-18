package com.negen.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限
 * @Author Negen
 * @Date 2020年1月17日 下午5:32:56
 */
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