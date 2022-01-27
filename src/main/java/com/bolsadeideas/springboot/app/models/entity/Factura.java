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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="facturas")
public class Factura implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty
	private String descripcion;
	private String observacion;
	
	@Temporal(TemporalType.DATE)
	@Column(name="create_at")
	private Date createAt;
	
	//RELACION CON CLIENTE (CLIENTE TIENE MUCHAS FACTURAS)
	@ManyToOne(fetch=FetchType.LAZY)//LAZY(para que cargue solo las facturas al momento de consultar facturas y para eliminarla se eliminen sus relaciones)
	@JsonBackReference //parte posterior de la relacion y se omitira para generar un loop infinito de relacion con factura
	private Cliente cliente;
	//FIN RELACION CON CLIENTE
	
	//RELACION CON ItemFactura (uno a muchos)
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)//LAZY(para que cargue solo las facturas al momento de consultar facturas y para eliminarla se eliminen sus relaciones)
	@JoinColumn(name="factura_id")//LLAVE RELACIONADA CON ITEMFACTURA nombre en la tabla MYSQL
	private List<ItemFactura> itemsFactura;
	
	//CONSTRUCTOR
	public Factura() {
		this.itemsFactura = new ArrayList<ItemFactura>();
	}

	//METODO PARA GENERAR LA FECHA
	@PrePersist
	public void prePersist() {
		createAt = new Date();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date creteAt) {
		this.createAt = creteAt;
	}

	/* @XmlTransient
	 * bloquear el acceso al cliente al momento de convertir a xml
	 * y evitar el cliclo infinito por la relacion bidirecional con el cliente
	*/
	@XmlTransient
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	
	public List<ItemFactura> getItemsFactura() {
		return itemsFactura;
	}

	public void setItemsFactura(List<ItemFactura> itemsFactura) {
		this.itemsFactura = itemsFactura;
	}
	
	
	//METOD PARA AGREGAR LOS ITEMFACTURA
	public void  addItemsFactura(ItemFactura item) {
		this.itemsFactura.add(item);
	}
	
	//METODO PARA CALCULAR CADA ITEMFACTURA POR SU PRECIO
	public Double getTotal() {
		Double total = 0.0;
		
		int size = itemsFactura.size();
		
		for(int i=0; i< size; i++) {
			total += itemsFactura.get(i).CalcularImporte();
		}
		
		return total;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
