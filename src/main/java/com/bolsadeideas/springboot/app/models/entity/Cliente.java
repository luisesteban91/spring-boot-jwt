package com.bolsadeideas.springboot.app.models.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity //indicar que es una clase con metodos set y get mapeado a una tabla  JPA o Hibernate
@Table(name="clientes") //INDICAR EL NOMBRE DE LA TABLA
public class Cliente implements Serializable{ //Serializable RECOMENDADO CUANDO SE VA A TRABAJAR CON OBJETOS DE DATOS PARA PODER TRASPORTARLO A UNA BASE DE DATOS, JSON, XML. SESIONES HTTP
	
	@Id//INDICAR QUE ES LLAVE PRIMARIA
	@GeneratedValue(strategy=GenerationType.IDENTITY)//INDICAR QUE TIPO DE DATO ES(INCREMENTABLE, SEQUENCE, ETC.)
	private Long id;
	
	//@Column(name="nombre_cliente", ) //si es difente al de la base
	@NotEmpty //REQUERIDO ###ES REQUERIDO AGREGAR EN LA EL METODO GAURDAR LA VALIDACION##
	@Size(min=4)
	private String nombre;
	@NotEmpty
	private String apellido;
	@NotEmpty
	@Email
	private String email;
	
	@NotNull
	@Column(name="create_at")
	@Temporal(TemporalType.DATE)//INDICAR EN QUE FORMATO SE VA A GUARDAR
	@DateTimeFormat(pattern="yyyy-MM-dd") //INDICAR FORMARTO DE FECHA
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")//dar formato a la fecha en en json
	private Date createAt; //Date de java.util
	
	private String foto;
	
	/*@PrePersist//SE VA A LLAMAR AL METODO JUSTO ANTES DE CREARLO EN LA BASE DE DATOS
	public void prePersist() {
		createAt = new Date();
	}*/
	
	//######RELACION CON FACTURA
	//UN CLIENTE PUEDE TENER MUCHAS FACTURAS
	@OneToMany(mappedBy="cliente", /*variable=a la de factura*/ fetch=FetchType.LAZY, cascade=CascadeType.ALL)//LAZY(para que cargue solo los clientes al momento de consultar clientes)//CASCADETYPE(para que persistan todos sus elementos fijos)
	@JsonManagedReference //administrar las realaciones con clientes
	private List<Factura> facturas;	
	//CONSTRUCTOR(inicializar facturas)
	public Cliente() {
		facturas = new ArrayList<Factura>();
	}
	//#######FIN RELACION CON FACTURA
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}



	public String getNombre() {
		return nombre;
	}



	public void setNombre(String nombre) {
		this.nombre = nombre;
	}



	public String getApellido() {
		return apellido;
	}



	public void setApellido(String apellido) {
		this.apellido = apellido;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public Date getCreateAt() {
		return createAt;
	}



	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	
	
	

	public String getFoto() {
		return foto;
	}


	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	
	

	public List<Factura> getFacturas() {
		return facturas;
	}


	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}
	
	//AGREGAR FACTURA X FACTURA EN LA CLASE CLIENTE
	public void addFactura(Factura factura) {
		facturas.add(factura);
	}

	@Override
	public String toString() {
		return nombre + " apellido=" + apellido;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 1L;
	
	
}
