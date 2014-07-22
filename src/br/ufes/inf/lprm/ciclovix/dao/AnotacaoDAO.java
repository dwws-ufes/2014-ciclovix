package br.ufes.inf.lprm.ciclovix.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.ufes.inf.lprm.ciclovix.entities.Anotacao;
import br.ufes.inf.lprm.ciclovix.entities.Categoria;

@Stateless
public class AnotacaoDAO extends EntidadeDAO<Anotacao> {
	@PersistenceContext
	private EntityManager em;

	@Override
	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public Class<Anotacao> getDomainClass() {
		return Anotacao.class;
	}
}
