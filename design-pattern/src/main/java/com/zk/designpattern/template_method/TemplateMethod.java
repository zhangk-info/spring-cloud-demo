package com.zk.designpattern.template_method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 模板方法模式
 * 定义一个操作的算法骨架，而将一些步骤延迟到子类中。
 * 使得子类可以不改变一个算法的结构即可重定义该算法的某些特定步骤
 */
public class TemplateMethod {

    public static void main(String[] args) {
        // 示例
        // sql执行 open command close
        // 子类不需要关心算法中的open close，

        // 其他例子
        HttpServlet servlet;
    }

    class ServletImpl extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doGet(req, resp);
        }
    }
}
