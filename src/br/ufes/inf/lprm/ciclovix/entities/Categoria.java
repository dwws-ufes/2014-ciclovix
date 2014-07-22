package br.ufes.inf.lprm.ciclovix.entities;

import javax.persistence.Entity;

@Entity
public class Categoria extends Entidade {
	String nome;
	int tipo; // PONTO, LINHA, AREA

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
}
