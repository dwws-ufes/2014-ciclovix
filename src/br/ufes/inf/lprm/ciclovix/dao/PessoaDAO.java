package br.ufes.inf.lprm.ciclovix.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import br.ufes.inf.lprm.ciclovix.entities.Pessoa;

@Stateless
public class PessoaDAO extends EntidadeDAO<Pessoa> {
	@PersistenceContext
	private EntityManager em;

	@Override
	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public Class<Pessoa> getDomainClass() {
		return Pessoa.class;
	}
	
	public Pessoa getByNameAndPass(String userName, String password){
		Pessoa user = new Pessoa();
		Query q = em.createQuery("SELECT u FROM Pessoa u WHERE u.nome = :login AND u.password = :pass");
        q.setParameter("login", userName);
        q.setParameter("pass", password);
        try{
          user = (Pessoa) q.getSingleResult();
          if (!userName.equalsIgnoreCase(user.getNome())&&password.equals(user.getPassword())) {             
             user = null;
          }
        }catch(Exception e){      
            return null;
        }
		return user;
		
	}

}
