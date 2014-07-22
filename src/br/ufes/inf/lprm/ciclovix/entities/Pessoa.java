package br.ufes.inf.lprm.ciclovix.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Pessoa extends Entidade {
	String nome;
	String password;
	String sobrenome;
	String contaFace;
	String contaTwitter;
	int tipo;
	@OneToMany(mappedBy = "autor", fetch = FetchType.EAGER)
	List<Anotacao> anotacoes;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String pass) {
		this.password = pass;
	}

	public String getSobrenome() {
		return sobrenome;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}

	public String getContaFace() {
		return contaFace;
	}

	public void setContaFace(String contaFace) {
		this.contaFace = contaFace;
	}

	public String getContaTwitter() {
		return contaTwitter;
	}

	public void setContaTwitter(String contaTwitter) {
		this.contaTwitter = contaTwitter;
	}

	public List<Anotacao> getAnotacoes() {
		return anotacoes;
	}

	public void setAnotacoes(List<Anotacao> anotacoes) {
		this.anotacoes = anotacoes;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
}
