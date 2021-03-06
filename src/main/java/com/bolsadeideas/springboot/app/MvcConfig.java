package com.bolsadeideas.springboot.app;

import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig  implements WebMvcConfigurer {
	
	//private final Logger log = LoggerFactory.getLogger(getClass());

	//@Override
	//public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
	//	WebMvcConfigurer.super.addResourceHandlers(registry);
		
		
	//	String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();
	//	log.info(resourcePath);
	//	registry.addResourceHandler("/uploads/**")
	//	.addResourceLocations(resourcePath);
	//}
	
	/*
	 * controlador que cargara la vista.
	 * 
	*/
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/error_403").setViewName("error_403");
		
	}
	
	@Bean
	 public BCryptPasswordEncoder passwordEncoder() {
		 return new BCryptPasswordEncoder();
	 }
	
	//Marshaller=convertir objeto a xml
	@Bean //Bean importante para convertir la respuesta en XML
	public Jaxb2Marshaller jaxb2Marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		
		marshaller.setClassesToBeBound(new Class[] {
			com.bolsadeideas.springboot.app.view.xml.ClienteList.class//asignar la clase root de configuracion para xml del cliente
		});
		
		return marshaller;
	}
}
