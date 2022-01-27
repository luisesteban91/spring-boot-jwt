package com.bolsadeideas.springboot.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bolsadeideas.springboot.app.auth.handler.LoginSuccessHandler;
import com.bolsadeideas.springboot.app.auth.middleware.JWTAuthenticationFilter;
import com.bolsadeideas.springboot.app.auth.middleware.JWTAuthorizationFilter;
import com.bolsadeideas.springboot.app.auth.service.JWTService;
import com.bolsadeideas.springboot.app.models.service.JpaUserDetailsService;

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled=true) //habilitar la seguridad de forma global
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	
	/**
	 * inyectar LoginSuccessHandler
	 */
	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Autowired
	private JpaUserDetailsService userDetailService;
	
	/**
	 * inyectar Datasource para la conexicon a la BD
	 */
	//@Autowired
	//DataSource dataSource;
	
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JWTService jwtService;
      
	 /*
	  * configure metodo para configurar las rutas http
	  */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/* rutas que son de acceso public*/
		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar", "/locale").permitAll()
		/*Autenticacion por token*/
		.anyRequest().authenticated()
		.and()
		.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtService)) //registrar el midleware filter Authentication
		.addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtService)) //registrar el midleware filter Authorization
		.csrf().disable()//deshabilitar el csrf para api rest
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//deshabilitar el las sesiones
	}
	
	 @Autowired
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception{
		/*Configuracion con JPA*/
		 builder.userDetailsService(userDetailService)
		 .passwordEncoder(passwordEncoder);
		 
		 /* Configuracion con JDBC
		 //pasar el datasource como instancia
		 builder.jdbcAuthentication()
		 .dataSource(dataSource)
		 .passwordEncoder(passwordEncoder)
		 .usersByUsernameQuery("select username, password, enabled from users where username=?")
		 .authoritiesByUsernameQuery("select u.username, a.authority from authorities a inner join users u on (a.user_id=u.id) where u.username=?");
		 */
		 
		 /*
		PasswordEncoder encoder = this.passwordEncoder;
//		UserBuilder users = User.builder().passwordEncoder(password -> {
//			return encoder.encode(password);
//		});
		UserBuilder users = User.builder().passwordEncoder(encoder::encode);		
		
		//crear usuarios en memoria
		builder.inMemoryAuthentication()
		.withUser(users.username("admin").password("12345").roles("ADMIN","USER"))
		.withUser(users.username("luis").password("12345").roles("USER"));
		*/
	}
	 
	 
}
