package com.bolsadeideas.springboot.app.view.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

@XmlRootElement(name="clientes")//indicar que esta es la clase root xml
public class ClienteList {
	
	@XmlElement(name="clientes")
	public List<Cliente> clientes;
	
	public ClienteList() {} //importante declarar el constructor vacio

	public ClienteList(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}
		
}
