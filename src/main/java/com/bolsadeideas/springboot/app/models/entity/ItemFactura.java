package com.bolsadeideas.springboot.app.models.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="facturas_item")
public class ItemFactura implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Integer cantidad;
	
	//RELACION UNIDIRECIONAL(solo se necesita relaciona de este lado de itemFactura) CON PRODUCTO(muchos itemFactura un producto) 
	@ManyToOne(fetch=FetchType.EAGER) //EAGER traer los productos imendiantamente y no de forma peresosa con LAZY
	@JoinColumn(name="producto_id") //Campo de RElacion, tambien se puede omitir ya que por defecto lo tomaria 
	private Producto producto;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCantidad() {
		return cantidad;
	}
	
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	public Double CalcularImporte() {
		return cantidad.doubleValue() * producto.getPrecio();
	}
	
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
