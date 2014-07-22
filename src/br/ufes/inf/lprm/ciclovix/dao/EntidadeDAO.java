package br.ufes.inf.lprm.ciclovix.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.ufes.inf.lprm.ciclovix.entities.Entidade;

public abstract class EntidadeDAO<T extends Entidade> {

	public abstract EntityManager getEntityManager();

	public abstract Class<T> getDomainClass();

	public T salvar(T obj) throws Exception {
		try {
			// getEntityManager().getTransaction().begin();
			if (obj.getId() == null) {
				getEntityManager().persist(obj);
			} else {
				obj = getEntityManager().merge(obj);
			}
			// getEntityManager().getTransaction().commit();
		} catch (Exception ex) {
			throw ex;
		}
		return obj;
	}

	public void excluir(Long id) throws Exception {
		try {
			// getEntityManager().getTransaction().begin();
			T obj = this.obter(id);
			getEntityManager().remove(obj);
			// getEntityManager().getTransaction().commit();
		} catch (Exception ex) {
			throw ex;
		}
	}

	public T obter(Long id) throws Exception {
		try {
			return (T) getEntityManager().find(getDomainClass(), id);
		} catch (Exception ex) {
			throw ex;
		}
	}

	public List<T> listar() throws Exception {
		try {
			Query q = getEntityManager()
					.createQuery(
							"SELECT t FROM " + getDomainClass().getSimpleName()
									+ " t ");
			return q.getResultList();
		} catch (Exception ex) {
			throw ex;
		}
	}
}
