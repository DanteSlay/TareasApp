package com.javi.tareas.AppConfig;


import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Configuración para la internacionalización y manejo de mensajes
 */
@Configuration
public class MessageConfig implements WebMvcConfigurer {

    /**
     * Configura de dónde vienen los mensajes usados en la página.
     *
     * @return El lugar donde se encuentran los mensajes.
     */
    @Bean
    public MessageSource messageResource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();

        // Aquí se dice dónde están guardados los mensajes
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }

    /**
     * Configura la forma en que se valida la información y los mensajes que se muestran en caso de error.
     *
     * @return La herramienta que valida y muestra los mensajes de error.
     */
    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();

        // Usa los mensajes configurados para mostrar errores
        bean.setValidationMessageSource(messageResource());
        return bean;
    }

    /**
     * Configura el idioma por defecto que verá el usuario.
     *
     * @return El idioma que se mostrará por primera vez.
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();

        // Establece el idioma por defecto como Español
        sessionLocaleResolver.setDefaultLocale(new Locale("es", "ES"));
        return sessionLocaleResolver;
    }

    /**
     * Configura una herramienta que permite cambiar el idioma en la página web.
     *
     * @return Una forma de cambiar el idioma en la página.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();

        // Define el parámetro en la URL para cambiar el idioma
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    /**
     * Añade la herramienta para cambiar el idioma al conjunto de herramientas utilizadas por la página web.
     *
     * @param registry Conjunto de herramientas de la página web.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
