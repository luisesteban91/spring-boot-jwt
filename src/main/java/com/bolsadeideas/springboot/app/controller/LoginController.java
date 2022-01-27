package com.bolsadeideas.springboot.app.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
	
	@GetMapping("/login")
	public String login(
			@RequestParam(value="logout", required=false) String logout,
			@RequestParam(value="error", required=false) String error,
			Model model, Principal principal, RedirectAttributes flash) {
		
		if(principal != null) { //validar si el usuario no a iniciado sesion anteriormente
			flash.addAttribute("info", "Ya ha iniciado sessión anteriormente");
			return "redirect:/";
		}
		
		if(error != null) {
			model.addAttribute("error", "error en login: Nombre de usuario o cotraseña incorrecta");
		}
		
		if(logout != null) {
			model.addAttribute("success", "Ha cerrado cesión con éxito!");
		}
		
		return "login";
	}
}
