

package backUps.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
    }

    /**
     * 配置ContextComponentLoader
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{ContextComponentLoader.class};
    }

    /**
     * 配置DispatcherServlet
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{MvcComponentLoader.class};
    }

    /**将DispatcherServlet映射到指定路径
     配置ServletMappings*/
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }


}
