package voiceCare.config;

import voiceCare.interceptor.CorsInterceptor;
import voiceCare.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 *
 * 不用权限可以访问url    /api/v1/pub/
 * 要登录可以访问url    /api/v1/pri/
 */

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    CorsInterceptor corsInterceptor() {
        return new CorsInterceptor();
    }

    @Bean
    LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }

//    @Bean
//    public RestTemplate restTemplate() {
//
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getMessageConverters().add(new SzhMappingJackson2HttpMessageConverter());
//
//        return restTemplate;
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //拦截全部
        registry.addInterceptor(loginInterceptor()).addPathPatterns("/api/v1/pri/*/*/**")
                //不拦截哪些路径   斜杠一定要加
                .excludePathPatterns("/api/v1/pri/user/login","/api/v1/pri/user/register");

        WebMvcConfigurer.super.addInterceptors(registry);

    }
}
