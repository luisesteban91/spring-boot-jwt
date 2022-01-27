package com.bolsadeideas.springboot.app.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

@Repository("clienteDaoJPA") // PARA MARCAR LA CLASE COMO COMPONENETE DE PERSISTENCIA
public class _ClienteDaoImpl implements _IClienteDao {
	
	@PersistenceContext
	private EntityManager em; //CONSULTAS DE JPA, PERCISTENCIA

	@SuppressWarnings("unchecked")//Suprimir el mensaje
	
	@Override
	public List<Cliente> findAll() {
		// TODO Auto-generated method stub
		return em.createQuery("from Cliente").getResultList();
	}

	@Override
	public void save(Cliente cliente) {
		if(cliente.getId() != null && cliente.getId() >0) { //PARA VALIDAR SI SE VA A ACTUALIZAR
			em.merge(cliente);
		}else { //PARA GUARDAR NUEVO
			em.persist(cliente);
		}
	}

	@Override
	public Cliente findOne(Long id) {
		// TODO Auto-generated method stub
		return em.find(Cliente.class, id);
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		Cliente cliente = findOne(id);
		
		em.remove(cliente);
		
	}

}