package com.heydanno.xdust.test;

public class ModelPerson {
	
	public ModelPerson(String name) {
		this.name = name;
	}
	
	public ModelPerson(String name, int age) {
		this.name = name;
		this.age = age;
	}
	
	public ModelPerson(String name, String role, String[] degrees, ModelPerson user) {
		this.name = name;
		this.role = role;
		this.degrees = degrees;
		this.user = user;
	}
	
	public ModelPerson(String name, String role, String[] degrees, String type) {
		this.name = name;
		this.role = role;
		this.degrees = degrees;
		this.type = type;
	}

	String name;
	public int age;
	public String role;
	public String[] degrees;
	public ModelPerson user;
	public String type;

	public String getName() {
		return this.name;
	}
}
