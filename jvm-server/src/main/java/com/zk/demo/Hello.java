package com.zk.demo;

import io.swagger.models.auth.In;

public class Hello {
//	public static void main(String[] args) {
//		Hello hello = new Hello();
//		System.out.println(hello.getClass().getClassLoader().getParent().getParent());
//		System.out.println(hello.getClass().getClassLoader().getParent());
//		System.out.println(hello.getClass().getClassLoader());
//	}

	public static void main(String[] args) {
		Integer[] t = new Integer[]{1, 2, 3, 4, 5, 10, 9, 6};

		//长度未知，倒数第n个
		Integer n = 3;
		//由于长度未知，所以对数据进行倒序
//		List<Integer> list = Arrays.stream(t).collect(Collectors.toList());
//		Collections.reverse(list);
		Integer[] t2 = new Integer[t.length];
		for (int i = 0; i < t.length - 1; i++) {
			t2[i] = t[t.length - i - 1];
		}
   
		System.out.println(t2[n - 1]);

		Integer a = new Integer(1);
		Integer b = new Integer(1);
		System.out.println(a == b);
	}
}
