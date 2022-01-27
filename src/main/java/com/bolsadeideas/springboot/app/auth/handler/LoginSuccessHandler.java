package com.bolsadeideas.springboot.app.auth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

/*
 * Para verificar el inicio de session
 * Inyectar a spring para poder usarla en SpringSecurityConfig
 */
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		/*
		 * Obtener el session flash map manager para poder registrar un flashmanager
		 * un arreglo de java que contiene los mensajes flash
		 */
		SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
		FlashMap flashMap = new FlashMap();
		
		flashMap.put("success", "Hola " +authentication.getName()+" has iniciado sesión con éxito!");
		flashMapManager.saveOutputFlashMap(flashMap, request, response);
		
		if(authentication != null) {
			logger.info("El usuario " +authentication.getName()+" ha iniciado sesión con éxito!");
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
}
