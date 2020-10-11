package com.zk.demo;

public class Hello {
	public static void main(String[] args) {
		Hello hello = new Hello();
		System.out.println(hello.getClass().getClassLoader().getParent().getParent());
		System.out.println(hello.getClass().getClassLoader().getParent());
		System.out.println(hello.getClass().getClassLoader());
	}
}
