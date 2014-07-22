package br.ufes.inf.lprm.ciclovix.mb;

import java.io.Serializable;
import java.net.URI;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

@ManagedBean
@SessionScoped
public class DbpediaMB implements Serializable {

	String latlng;

	RDFNode qRes;
	String qName;
	String qOfficial;
	String qComment;
	String qArea;
	String qPopulation;
	String qThumb;

	public String getLatlng() {
		return latlng;
	}

	public void setLatlng(String latlng) {
		this.latlng = latlng;
	}

	public RDFNode getqRes() {
		return qRes;
	}

	public void setqRes(RDFNode qRes) {
		this.qRes = qRes;
	}

	public String getqName() {
		return qName;
	}

	public void setqName(String qName) {
		this.qName = qName;
	}

	public String getqOfficial() {
		return qOfficial;
	}

	public void setqOfficial(String qOfficial) {
		this.qOfficial = qOfficial;
	}

	public String getqComment() {
		return qComment;
	}

	public void setqComment(String qComment) {
		this.qComment = qComment;
	}

	public String getqArea() {
		return qArea;
	}

	public void setqArea(String qArea) {
		this.qArea = qArea;
	}

	public String getqPopulation() {
		return qPopulation;
	}

	public void setqPopulation(String qPopulation) {
		this.qPopulation = qPopulation;
	}

	public String getqThumb() {
		return qThumb;
	}

	public void setqThumb(String qThumb) {
		this.qThumb = qThumb;
	}

	public void snippet() {
		String geo = this.geocode(this.latlng);
		dbpedia(geo);

		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null, new FacesMessage("Successful", qComment));
	}

	private String geocode(String latlng) {

		String cidade = null;
		String estado = null;

		try {

			/*
			 * HTTP REQUEST
			 */

			URI url = (new URIBuilder(
					"http://maps.googleapis.com/maps/api/geocode/json"))
					.addParameter("latlng", this.latlng).build();

			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			get.addHeader("Content-Type", "application/json");

			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();

			/*
			 * JSON PARSING
			 */

			String input = EntityUtils.toString(entity);
			JSONParser parser = new JSONParser();
			JSONObject root = (JSONObject) parser.parse(input);
			JSONArray results = (JSONArray) root.get("results");
			JSONObject result_0 = (JSONObject) results.get(0);
			JSONArray address_components = (JSONArray) result_0
					.get("address_components");
			for (Object obj : address_components) {
				JSONObject address = (JSONObject) obj;
				JSONArray types = (JSONArray) address.get("types");

				if (types.get(0).equals("administrative_area_level_2")) {
					cidade = (String) address.get("long_name");
					cidade = cidade.split(",")[0];
				} else if (types.get(0).equals("administrative_area_level_1")) {
					estado = (String) address.get("long_name");
					estado = estado.split(",")[0];
				}

				if (cidade != null && estado != null) {
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return cidade + ";" + estado;
	}

	private void dbpedia(String geo) {

		String[] local = geo.split(";");
		String cidade = local[0];
		String estado = local[1];

		String format = "PREFIX schema: <http://schema.org/>"
				+ "  PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"
				+ "  PREFIX dbpprop: <http://dbpedia.org/property/>"
				+ "  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "  SELECT DISTINCT ?res ?name ?official ?comment ?area ?population ?thumb"
				+ "  WHERE {"
				+ "    ?res rdf:type dbpedia-owl:Place;"
				+ "      rdf:type dbpedia-owl:Settlement;"
				+ "      rdfs:comment ?comment;"
				+ "      dbpedia-owl:areaTotal ?area;"
				+ "      dbpedia-owl:populationTotal ?population;"
				+ "      rdfs:label ?city"
				+ "  FILTER ( regex(?city, \"%s\", \"i\" ) && langMatches( lang(?comment), \"PT\" ) && regex(?comment, \"%s\", \"i\") )"
				+ "  OPTIONAL { ?res dbpprop:name ?name }"
				+ "  OPTIONAL { ?res dbpprop:officialName ?official }"
				+ "  OPTIONAL { ?res dbpedia-owl:thumbnail ?thumb }" + "}";
		String queryString = String.format(format, cidade, estado);

		System.out.println(queryString);

		Query query = QueryFactory.create(queryString);

		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				"http://dbpedia.org/sparql", query);

		try {
			ResultSet rs = qexec.execSelect();
			if (rs.hasNext()) {
				QuerySolution sol = rs.nextSolution();
				qRes = sol.get("res");
				qName = sol.contains("name") ? sol.get("name").asLiteral()
						.getString() : "";
				qOfficial = sol.contains("official") ? sol.get("official")
						.asLiteral().getString() : "";
				qComment = sol.get("comment").asLiteral().getString();
				qArea = sol.get("area").asLiteral().getString();
				qPopulation = sol.get("population").asLiteral().getString();
				qThumb = sol.contains("thumb") ? sol.get("thumb").asResource()
						.getURI() : "";
			} else {
				qComment = "Cidade desconhecida...";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			qexec.close();
		}
	}
}
