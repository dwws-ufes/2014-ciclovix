package br.ufes.inf.lprm.ciclovix.mb;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import br.ufes.inf.lprm.ciclovix.dao.AnotacaoDAO;
import br.ufes.inf.lprm.ciclovix.dao.CategoriaDAO;
import br.ufes.inf.lprm.ciclovix.dao.PessoaDAO;
import br.ufes.inf.lprm.ciclovix.entities.Anotacao;
import br.ufes.inf.lprm.ciclovix.entities.Categoria;
import br.ufes.inf.lprm.ciclovix.entities.Pessoa;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileManager;

@ManagedBean
@SessionScoped
public class AnotacaoMB implements Serializable {

	private static final long serialVersionUID = 1L;
	/*
	 * DAOs
	 */
	@EJB
	AnotacaoDAO daoAnotacao;
	@EJB
	CategoriaDAO daoCategoria;
	@EJB
	PessoaDAO daoPessoa;

	@ManagedProperty(value = "#{dbpediaMB}")
	DbpediaMB dbpediaMB;

	Anotacao anotacao = new Anotacao();
	long categoria;
	List<SelectItem> categorias;
	List<Anotacao> markers;
	DataModel<Anotacao> listaAnotacoes;

	private Marker marker;
	private MapModel simpleModel = new DefaultMapModel();

	private List<String> items;
	List<Categoria> cats;
	private Map<String, Boolean> checkMap = new HashMap<String, Boolean>();

	private String dump;

	public String getDump() {
		return dump;
	}

	public void setDump(String dump) {
		this.dump = dump;
	}

	public DbpediaMB getDbpediaMB() {
		return dbpediaMB;
	}

	public void setDbpediaMB(DbpediaMB dbpediaMB) {
		this.dbpediaMB = dbpediaMB;
	}

	@PostConstruct
	public void init() {
		simpleModel = new DefaultMapModel();
		try {
			items = new ArrayList<String>();
			cats = this.daoCategoria.listar();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Categoria c : cats) {
			items.add(c.getNome());
		}
		for (String item : items) {
			checkMap.put(item, Boolean.TRUE);
		}

		try {
			markers = daoAnotacao.listar();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Anotacao a : markers) {
			simpleModel.addOverlay(new Marker(new LatLng(a.getLongitude(), a
					.getLatitude()), a.getNome(), a));
		}
	}

	public Anotacao getAnotacao() {
		return this.anotacao;
	}

	public void setAnotacao(Anotacao anotacao) {
		this.anotacao = anotacao;
	}

	public long getCategoria() {
		return categoria;
	}

	public void setCategoria(long categoria) {
		this.categoria = categoria;
	}

	public List<SelectItem> getCategorias() {
		this.categorias = new ArrayList<SelectItem>();
		try {
			List<Categoria> categorias = this.daoCategoria.listar();
			for (Categoria categoria : categorias) {
				this.categorias.add(new SelectItem(categoria.getId(), categoria
						.getNome()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.categorias;
	}

	public DataModel<Anotacao> getListarAnotacoes() {
		try {
			this.listaAnotacoes = new ListDataModel(daoAnotacao.listar());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.listaAnotacoes;
	}

	public MapModel getMarkers() {
		return simpleModel;
	}

	public void onMarkerSelect(OverlaySelectEvent event) {
		marker = (Marker) event.getOverlay();

		Anotacao anotacao = (Anotacao) marker.getData();
		this.dbpediaMB.latlng = anotacao.getLongitude() + ","
				+ anotacao.getLatitude();

		this.dbpediaMB.snippet();
	}

	public Marker getMarker() {
		return marker;
	}

	public String prepararAdicionarAnotacao() {
		this.anotacao = new Anotacao();
		return "visualizar_anotacao";
	}

	public String prepararAlterarAnotacao() {
		this.anotacao = (Anotacao) (this.listaAnotacoes.getRowData());
		this.categoria = this.anotacao.getCategoria().getId();
		return "visualizar_anotacao";
	}

	public String salvarAnotacao() {
		try {
			System.out.println("SALVAR ANOTAÇÂO");
			this.anotacao.setCategoria(daoCategoria.obter(this.categoria));
			this.daoAnotacao.salvar(this.anotacao);
			simpleModel.addOverlay(new Marker(new LatLng(anotacao
					.getLongitude(), anotacao.getLatitude()), anotacao
					.getNome(), anotacao));
			anotacao = new Anotacao();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "index.xhtml";
	}

	public String excluirAnotacao() {
		Long idAnotacao = ((Anotacao) (this.listaAnotacoes.getRowData()))
				.getId();
		try {
			this.daoAnotacao.excluir(idAnotacao);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "listar_anotacoes";
	}

	public List<String> getItems() {
		return items;
	}

	public Map<String, Boolean> getCheckMap() {
		return checkMap;
	}

	public String filtro() {
		for (Marker m : simpleModel.getMarkers()) {
			Anotacao note = (Anotacao) m.getData();

			if (!checkMap.get(note.getCategoria().getNome())) {
				m.setVisible(false);
			} else {
				m.setVisible(true);
			}
		}
		return "index";
	}

	public void onPointSelect(PointSelectEvent event) {
		LatLng latlng = event.getLatLng();

		this.anotacao.setLatitude(latlng.getLng());
		this.anotacao.setLongitude(latlng.getLat());

		addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Point Selected", "Lat:" + latlng.getLat() + ", Lng:"
						+ latlng.getLng()));
	}

	public void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String dumpAnotacoes() {

		String dump = "";

		String ns = "http://lprm.inf.ufes.br/ciclovix#";
		String nsGeo = "http://www.w3.org/2003/01/geo/wgs84_pos#";

		OntModel model = ModelFactory.createOntologyModel();
		model.setNsPrefix("ciclovix", ns);
		model.setNsPrefix("foaf", FOAF.getURI());
		model.setNsPrefix("geo", nsGeo);

		Model geo = FileManager.get().loadModel(nsGeo);

		Resource resAutor = model.createClass(ns + "autor");
		Resource resAnotacao = model.createClass(ns + "anotacao");

		Property anotacao_latitude = geo.getProperty(nsGeo + "lat");
		Property anotacao_longitude = geo.getProperty(nsGeo + "long");

		Property autor_id = model.createProperty(ns + "autor_id");
		Property autor_tipo = model.createProperty(ns + "autor_tipo");
		Property autor_twitter = model.createProperty(ns + "autor_twitter");

		Property anotacao_id = model.createProperty(ns + "anotacao_id");
		Property anotacao_nome = model.createProperty(ns + "anotacao_nome");
		Property anotacao_autor = model.createProperty(ns + "anotacao_autor");
		Property anotacao_foto = model.createProperty(ns + "anotacao_foto");
		Property anotacao_link = model.createProperty(ns + "anotacao_link");
		Property anotacao_categoria = model.createProperty(ns + "anotacao_categoria");

		try {
			List<Anotacao> anotacoes = daoAnotacao.listar();
			List<Pessoa> autores = daoPessoa.listar();

			for (Pessoa autor : autores) {
				Resource res = model
						.createResource(
								ns + autor.getNome() + "_"
										+ autor.getSobrenome(), resAutor)
						.addLiteral(autor_id, autor.getId())
						.addLiteral(autor_tipo, autor.getTipo())
						.addProperty(FOAF.name, autor.getNome())
						.addProperty(FOAF.family_name, autor.getSobrenome())
						.addLiteral(autor_twitter, autor.getContaTwitter());

			}

			for (Anotacao anotacao : anotacoes) {
				Resource res = model
						.createResource(
								ns + anotacao.getNome() + "_"
										+ anotacao.getLatitude() + "_"
										+ anotacao.getLongitude(), resAnotacao)
						.addLiteral(anotacao_id, anotacao.getId())
						.addLiteral(anotacao_nome, anotacao.getNome())
						.addLiteral(anotacao_latitude, anotacao.getLatitude())
						.addLiteral(anotacao_longitude, anotacao.getLongitude())
						.addLiteral(anotacao_foto, anotacao.getFoto())
						.addLiteral(anotacao_link, anotacao.getLink())
						.addLiteral(anotacao_categoria, anotacao.getCategoria().getNome());
				Pessoa autor = anotacao.getAutor();
				if (autor != null) {
					Resource resA = model.getResource(ns + autor.getNome()
							+ "_" + autor.getSobrenome());
					res.addProperty(anotacao_autor, resA);
				}
			}

			StringWriter writer = new StringWriter();
			model.write(writer);
			dump = writer.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		this.dump = dump;

		return "dump.xhtml";
	}

}
