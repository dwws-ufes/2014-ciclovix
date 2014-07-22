package br.ufes.inf.lprm.ciclovix.mb;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import br.ufes.inf.lprm.ciclovix.dao.CategoriaDAO;
import br.ufes.inf.lprm.ciclovix.entities.Categoria;

@ManagedBean
@SessionScoped
public class CategoriaMB implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * DAOs
	 */
	@EJB
	CategoriaDAO daoCategoria;

	Categoria categoria;
	DataModel<Categoria> listaCategorias;

	public Categoria getCategoria() {
		return this.categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public DataModel<Categoria> getListarCategorias() {
		try {
			this.listaCategorias = new ListDataModel(daoCategoria.listar());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.listaCategorias;
	}

	public String prepararAdicionarCategoria() {
		this.categoria = new Categoria();
		return "visualizar_categoria";
	}

	public String prepararAlterarCategoria() {
		this.categoria = (Categoria) (this.listaCategorias.getRowData());
		return "visualizar_categoria";
	}

	public String salvarCategoria() {
		try {
			this.daoCategoria.salvar(categoria);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "listar_categorias";
	}

	public String excluirCategoria() {
		Long idCategoria = ((Categoria) (this.listaCategorias.getRowData()))
				.getId();
		try {
			this.daoCategoria.excluir(idCategoria);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "listar_categorias";
	}

}
