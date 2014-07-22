package br.ufes.inf.lprm.ciclovix.mb;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import br.ufes.inf.lprm.ciclovix.dao.PessoaDAO;
import br.ufes.inf.lprm.ciclovix.entities.Pessoa;

@ManagedBean
@RequestScoped
public class UsuarioMB {

	@EJB
	PessoaDAO daoPessoa;

	Pessoa usuario = new Pessoa();

	DataModel<Pessoa> listaUsuarios;

	public Pessoa getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Pessoa usuario) {
		this.usuario = usuario;
	}

	public DataModel<Pessoa> getListarUsuarios() {
		try {
			this.listaUsuarios = new ListDataModel(this.daoPessoa.listar());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.listaUsuarios;
	}

	public String prepararAlterarUsuario() {
		this.usuario = (Pessoa) (this.listaUsuarios.getRowData());
		return "visualizar_usuario";
	}

	public String excluirUsuario() {
		Long idUsuario = ((Pessoa) (this.listaUsuarios.getRowData())).getId();
		try {
			this.daoPessoa.excluir(idUsuario);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "listar_usuarios";
	}

	public String salvarUsuario() {
		try {
			this.daoPessoa.salvar(usuario);
			usuario = new Pessoa();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "index";
	}

}