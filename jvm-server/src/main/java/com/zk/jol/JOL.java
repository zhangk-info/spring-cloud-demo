package com.zk.jol;

import org.openjdk.jol.info.ClassLayout;

/**
 * 打印对象信息
 * 刚new出来的Object是16 bytes
 * markword 前8字节
 * classpointer 指向Class的指针
 * 成员变量
 * 对齐：被8个字节整除
 *
 */
public class JOL {
	public static void main(String[] args) {
		Object o = new Object();
		System.out.println(ClassLayout.parseInstance(o).toPrintable());
	}
}
