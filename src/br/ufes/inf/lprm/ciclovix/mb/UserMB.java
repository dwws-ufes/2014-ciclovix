package br.ufes.inf.lprm.ciclovix.mb;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import br.ufes.inf.lprm.ciclovix.dao.PessoaDAO;
import br.ufes.inf.lprm.ciclovix.entities.Pessoa;

@ManagedBean(name="auth")
@SessionScoped
public class UserMB {
     
     private String userName;
     private String password;
     private boolean logado = false;
     private boolean deslogado = true;

	 private Pessoa pessoa = null;
     
     @EJB
 	 PessoaDAO daoPessoa;
     
     public UserMB() { }
     
     public String getUserName() { 
    	 return this.userName; 
     }
     public void setUserName(String userName) { 
    	 this.userName=userName; 
     }
     
     public String getPassword() { 
    	 return this.password; 
     }
     public void setPassword(String password) { 
    	 this.password=password;     
     }
       
     public boolean isLogado() {
		return logado;
	}

	public void setLogado(boolean logado) {
		this.logado = logado;
	}
	
	
	
	public boolean isDeslogado() {
		return deslogado;
	}

	public void setDeslogado(boolean deslogado) {
		this.deslogado = deslogado;
	}

	public Pessoa getPessoa() {
		return this.pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	public String validate() {          
    	 this.pessoa = daoPessoa.getByNameAndPass(this.userName,this.password);
         if(this.pessoa == null){
        	 return "failure";
         }else{
        	 System.out.println("DEBUG MANUAL - " + this.pessoa.getNome());
        	 this.logado = true; 
        	 this.deslogado = false;
        	return "index.xhtml";
         }
     }
     
     public String invalidate() {          
    	 this.pessoa = null;
    	 this.logado = false;
    	 this.deslogado = true;
    	 return "index.xhtml";
     }
}
