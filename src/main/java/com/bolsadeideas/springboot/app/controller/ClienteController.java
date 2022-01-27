package com.bolsadeideas.springboot.app.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.IClienteService;
import com.bolsadeideas.springboot.app.models.service.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente") //PARA SAVER EL ID DEL CLIENTE AL MOMENTO DE GUARDAR O ACTUALIZAR
public class ClienteController {
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	//@Autowired//BUSCAR UN COMPONENETE REGISTRADO EN EL CONTENEDOR CON BEANS
	//@Qualifier("clienteDaoJPA")//indicar cual ejecutara primero (esta en la clase ClienteDaoImpl)
	
	@Autowired
	private IClienteService clienteService;//interfas Dao en los modelos
	
	@Autowired
	private IUploadFileService uploadFileService;//insertar el servicio
	
	
	//METODO PARA OBTENER LA RUTA DE LA IMAGEN
	@Secured({"ROLE_USER", "OTRO_ROLE"}) //valiar el rol del usuario
	@GetMapping(value="/uploads/{filename:.+}") // .+ exprecion regular para que no se trunque la extencion de la imagen //th:src="@{'/uploads/' + ${cliente.foto}}"
	public ResponseEntity<Resource> verFoto(@PathVariable String filename){
		
		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
		
	}
	
	
	//METODO PARA OBTENER DETALLES DEl CLIENTE POR MEDIO DEL ID
	//@Secured("ROLE_USER") //valiar el rol del usuario
	@PreAuthorize("hasAnyRole('ROLE_USER', 'OTRO_ROLE')") //otra forma de autorizar
	@GetMapping(value="/ver/{id}")
	public String ver(@PathVariable(name = "id") long id, Map<String, Object> model, RedirectAttributes flash) {
		
		//Cliente cliente = clienteService.findOne(id);
		Cliente cliente = clienteService.fetchByIdWithFacturas(id);
		if(cliente == null){
				flash.addFlashAttribute("error", "El Cliente no existe en la base de datos");
				return "redirect:/listar";
		}
		
		model.put("cliente", cliente);
		model.put("titulo", "Detalle cliente: " + cliente.getNombre());
		
		
		return "ver";
	}
	
	@GetMapping(value = "/clientes/listar")
	public @ResponseBody List<Cliente> listarRest(){
		return clienteService.findAll();//solo retorna JSON
		//return new ClienteList(clienteService.findAll());//para que retorne JSON y XML
	}
	
	@RequestMapping(value={"/listar", "/"}, method=RequestMethod.GET)
	public String listar(
			@RequestParam(name="page", defaultValue="0") int page, 
			Model model,
			Authentication authentication, //importar la authetication
			HttpServletRequest request
	) {
//		if(authentication != null) {
//			logger.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
//		}
		
		/*
		 * Obtener la autentication de forma estatica
		 */
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null) {
			logger.info("Hola usuario autenticado, tu username es: ".concat(auth.getName()));
		}
		
		if(hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat("tienes acceso"));
		}else {
			logger.info("Hola ".concat(auth.getName()).concat("no tienes acceso"));
		}
		
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
		
		if(securityContext.isUserInRole("ADMIN")) {
			logger.info("Forma usarndo SecurityContextHolderAwareRequestWrapper: Hola ".concat(auth.getName()).concat("tienes acceso"));
		}else {
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper: Hola ".concat(auth.getName()).concat("no tienes acceso"));
		}
		
		
		if(request.isUserInRole("ROLE_ADMIN")) {
			logger.info("Forma usando HttpServletRequest: Hola ".concat(auth.getName()).concat("tienes acceso"));
		}else {
			logger.info("Forma usando HttpServletRequest: Hola ".concat(auth.getName()).concat("no tienes acceso"));
		}
		
		Pageable pageRequest = PageRequest.of(page, 4); //import para la paginacion
		Page<Cliente> clientes = clienteService.findAll(pageRequest); //crea el objeto cliente ya poginado
		
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes); //se crea el obejto con su url de paginador
				
		model.addAttribute("titulo", "Listado de Clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		return "listar";
	}
	
	
	//METODO PARA CREAR CLIENTE
	@Secured("ROLE_ADMIN") //valiar el rol del usuario
	@RequestMapping(value="/form")
	public String crear(Map<String, Object> model) { //MAPA DE JAVA TIPO STRING Y RETORNA TIPO OBJECTO->MODEL
		
		Cliente cliente = new Cliente();
		model.put("cliente", cliente);//PUT	
		model.put("titulo", "Formulario de Cliente");
		return "form";
	}
	
	//@Secured("ROLE_ADMIN") //valiar el rol del usuario
	@PreAuthorize("hasRole('ROLE_USER')") //otra forma de autotizar
	@RequestMapping(value = "/form/{id}")
	public String editar(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		
		Cliente cliente = null;
		
		if(id > 0){
			cliente = clienteService.findOne(id);
			if(cliente == null) {
				flash.addFlashAttribute("error", "El Id del cliente no existe en la bdd!");
				return "redirect:/listar";
			}
		}else {
			flash.addFlashAttribute("error", "El Id del cliente no puede ser nulo!");
			return "redirect:/listar";
		}
		
		model.put("cliente", cliente);//PUT	
		model.put("titulo", "Editar Cliente");
		return "form";
	}
	
	//METODO PARA PROCESAR LOS DATOS DEL FORMULARIO
	@Secured("ROLE_ADMIN") //valiar el rol del usuario
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String guardar (@Valid Cliente cliente, BindingResult result, RedirectAttributes flash,/*siempre juntos y aqui los demas parametros*/ Model model, @RequestParam("fileAvatar") MultipartFile foto, SessionStatus status) { //@VALID NECESARIO PARA QUE REALIZE LAS VALIDACIONES //BindingResult para cachar los errores
		
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}
		
		if(!foto.isEmpty()) {
			
			//eliminar foto para update
			if(cliente.getId() != null
					&& cliente.getId() > 0 //validar que cliente exista
					&& cliente.getFoto()!=null 
					&& cliente.getFoto().length() > 0) {
				
				uploadFileService.delete(cliente.getFoto());
			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			flash.addFlashAttribute("info", "Has subido correctamente ' " + uniqueFilename + "'");
			cliente.setFoto(uniqueFilename);
	
		}
		
		String mensajeFlash = (cliente.getId() != null)? "Cliente Editado con éxito!" : " Cliente Creado con exito!"; 
		
		clienteService.save(cliente);
		status.setComplete();//ELIMAN EL OBJETO CLIENTE DE LA SESSION Y TERMINA EL PROCESO DE EDITAR O GUARDAR
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:listar";
	}
	
	//METODO ELIMINAR 
	@Secured("ROLE_ADMIN") //valiar el rol del usuario
	@RequestMapping(value="/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id, RedirectAttributes flash) {
		if(id > 0) {
			Cliente cliente = clienteService.findOne(id);
			
			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente Eliminado con éxito!");
			
			if(uploadFileService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + " eliminada con exito!");
			}
		}
		return "redirect:/listar";
	}
	
	/*
	 * Metodo para validar si el usuario tiene ese rol
	 */
	public boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();
		
		if(context == null) {
			return false;
		}
		
		Authentication auth = context.getAuthentication();
		
		if(auth == null) {
			return false;
		}
		
		//cualquier tipo de objecto que impemente esta interfaz o herede de ella
		Collection <? extends GrantedAuthority> authorities =  auth.getAuthorities();
		
		//otra forma de validar el rol
		//return authorities.contains(new SimpleGrantedAuthority(role));
		
		for(GrantedAuthority autority: authorities) {
			if(role.equals(autority.getAuthority())) {//validar si tiene el rol
				logger.info("Hola usuario ".concat(auth.getName()).concat("tu rol es ").concat(autority.getAuthority()));
				return true;
			}
		}
		
		return false;
	}

	
}
