

package backUps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * 启动自动扫描注解
 * 配置视图编码等资源信息
 * @author 志军
 */
@Configuration
@EnableWebMvc
@ComponentScan({"backUps"})
public class MvcComponentLoader extends WebMvcConfigurerAdapter {


    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        return characterEncodingFilter;
    }

    /**配置视图解析器*/
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolve = new InternalResourceViewResolver();
        resolve.setPrefix("/html/");
        resolve.setSuffix(".html");
        resolve.setOrder(0);
        resolve.setExposeContextBeansAsAttributes(true);

        return resolve;
    }

    /**配置静态资源的处理*/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/react/**.js").addResourceLocations("/react/");
        registry.addResourceHandler("/css/**.css").addResourceLocations("/css/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
