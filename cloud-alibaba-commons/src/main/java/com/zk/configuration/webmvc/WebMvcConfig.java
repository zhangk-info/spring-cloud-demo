package com.zk.configuration.webmvc;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {


//    /**
//     * 配置静态资源处理的两种方式，两种方式任选其一
//     * <p>
//     * 方式二：使用spring mvc处理静态资源
//     * <p>
//     * 发现如果继承了WebMvcConfigurationSupport，则在yml中配置的相关内容会失效。
//     * 需要重新指定静态资源
//     *
//     * @param registry
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**")
//                .addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//        super.addResourceHandlers(registry);
//    }
//
//    spring boot 2.4.0之后默认去掉了DefaultServelt即静态文件的加载和
//    现在的Spring Boot服务大都是REST服务，并无静态资源需要提供，因此就没有必要启用DefaultServletHttpRequestHandler和注册DefaultServlet来增加不必要的开销喽。
//
//    /**
//     * 配置静态资源处理的两种方式，两种方式任选其一
//     * <p>
//     * 方式一：使用默认servlet处理静态资源
//     * </p>
//     */
//    @Override
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//        configurer.enable();
//    }


    /**
     * 放开跨域请求
     */
    @Override
    protected void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**").allowedOrigins("*");

        super.addCorsMappings(registry);
    }

//    WebMvcConfigurer接口常用的方法：
//    /* 拦截器配置 */
//    void addInterceptors(InterceptorRegistry var1);
//    /* 视图跳转控制器 */
//    void addViewControllers(ViewControllerRegistry registry);
//    /**
//     *静态资源处理
//     **/
//    void addResourceHandlers(ResourceHandlerRegistry registry);
//    /* 默认静态资源处理器 */
//    void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer);
//    /**
//     *这里配置视图解析器
//     **/
//    void configureViewResolvers(ViewResolverRegistry registry);
//    /* 配置内容裁决的一些选项*/
//    void configureContentNegotiation(ContentNegotiationConfigurer configurer);
//    /** 解决跨域问题 **/
//    public void addCorsMappings(CorsRegistry registry) ;
}