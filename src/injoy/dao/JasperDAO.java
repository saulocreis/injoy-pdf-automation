package injoy.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import injoy.vo.EntidadeInjoy;

public class JasperDAO extends DAO implements Serializable {
	private static final long serialVersionUID = 1L;

	public List<EntidadeInjoy> getListDe() throws SQLException {
		ArrayList<EntidadeInjoy> list = new ArrayList<EntidadeInjoy>();
		String query = "SELECT id, nome, slug FROM de ORDER BY id DESC;";
		PreparedStatement statement = getConnection().prepareStatement(query);
		ResultSet result = statement.executeQuery();
		while(result.next()) {
			list.add(new EntidadeInjoy(result.getInt("id"), result.getString("nome"), result.getString("slug")));
		}
		result.close();	
		return list;
	}
	
	public List<EntidadeInjoy> getListEx(String slugDe) throws SQLException {
		ArrayList<EntidadeInjoy> list = new ArrayList<EntidadeInjoy>();
		String query = "SELECT id, nome, slug FROM produto WHERE idDe IN (SELECT id FROM de WHERE slug IN (?)) AND idProduto_Tipo IN (2) ORDER BY id DESC;";
		PreparedStatement statement = getConnection().prepareStatement(query);
		statement.setString(1, slugDe);
		ResultSet result = statement.executeQuery();
		while(result.next()) {
			list.add(new EntidadeInjoy(result.getInt("id"), result.getString("nome"), result.getString("slug")));
		}
		result.close();
		return list;
	}
	
	public List<EntidadeInjoy> getListExHib(int idHib) throws SQLException {
		ArrayList<EntidadeInjoy> list = new ArrayList<EntidadeInjoy>();
		String query = "SELECT prod.id as id, prod.nome as nome, prod.slug as slug FROM produto hib, hibrido h, produto prod, produto_tipo pt "
				+ "WHERE hib.id = h.idProduto AND prod.id = h.idSubproduto AND prod.idProduto_Tipo = pt.id AND "
				+ "prod.idProduto_Tipo IN (2) AND hib.id IN (?) ORDER BY id DESC;";
		PreparedStatement statement = getConnection().prepareStatement(query);
		statement.setInt(1, idHib);
		ResultSet result = statement.executeQuery();
		while(result.next()) {
			list.add(new EntidadeInjoy(result.getInt("id"), result.getString("nome"), result.getString("slug")));
		}
		result.close();
		return list;
	}
	
	public List<EntidadeInjoy> getListAe(String slugDe) throws SQLException {
		ArrayList<EntidadeInjoy> list = new ArrayList<EntidadeInjoy>();
		String query = "SELECT id, nome, slug FROM produto WHERE idDe IN (SELECT id FROM de WHERE slug IN (?)) AND idProduto_Tipo IN (5) ORDER BY id DESC;";
		PreparedStatement statement = getConnection().prepareStatement(query);
		statement.setString(1, slugDe);
		ResultSet result = statement.executeQuery();
		while(result.next()) {
			list.add(new EntidadeInjoy(result.getInt("id"), result.getString("nome"), result.getString("slug")));
		}
		result.close();
		return list;
	}
	
	public List<EntidadeInjoy> getListAeHib(int idHib) throws SQLException {
		ArrayList<EntidadeInjoy> list = new ArrayList<EntidadeInjoy>();
		String query = "SELECT prod.id as id, prod.nome as nome, prod.slug as slug FROM produto hib, hibrido h, produto prod, produto_tipo pt "
				+ "WHERE hib.id = h.idProduto AND prod.id = h.idSubproduto AND prod.idProduto_Tipo = pt.id AND "
				+ "prod.idProduto_Tipo IN (5) AND hib.id IN (?) ORDER BY id DESC;";
		PreparedStatement statement = getConnection().prepareStatement(query);
		statement.setInt(1, idHib);
		ResultSet result = statement.executeQuery();
		while(result.next()) {
			list.add(new EntidadeInjoy(result.getInt("id"), result.getString("nome"), result.getString("slug")));
		}
		result.close();
		return list;
	}
	
	public List<EntidadeInjoy> getListHib(String slugDe) throws SQLException {
		ArrayList<EntidadeInjoy> list = new ArrayList<EntidadeInjoy>();
		String query = "SELECT id, nome, slug FROM produto WHERE idDe IN (SELECT id FROM de WHERE slug IN (?)) AND idProduto_Tipo IN (4) ORDER BY id DESC;";
		PreparedStatement statement = getConnection().prepareStatement(query);
		statement.setString(1, slugDe);
		ResultSet result = statement.executeQuery();
		while(result.next()) {
			list.add(new EntidadeInjoy(result.getInt("id"), result.getString("nome"), result.getString("slug")));
		}
		result.close();
		return list;
	}
	
	public JasperDAO() {
		super();
	}
	
	public static JasperDAO instance() {
		return new JasperDAO();
	}

}
