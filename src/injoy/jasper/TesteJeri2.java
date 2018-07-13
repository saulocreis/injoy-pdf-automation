package injoy.jasper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TesteJeri2 {
	
	static private final String CONEXAO = "jdbc:mariadb://localhost:3306/injoy?user=root";
	static private final String SLUG_DE = "jeri2019";

	public static void main(String[] args) throws SQLException {
		
		System.out.println("Tentando conectar...");
		Connection connection = DriverManager.getConnection(CONEXAO);
		PreparedStatement statement = null;
		String query;
		ResultSet result;
		System.out.println("Conectado.");
		
		
		query = "SELECT subprod.slug as slugSubprod, " + 
				"MIN(ROUND(DATEDIFF(aq.data_final, aq.data_inicial) * aq.valor / aq.hospedes, 0)) as menorValorPessoa " + 
				"FROM acomodacao_quarto aq, produto subprod, produto_subproduto ps, produto prod, produto_tipo pt WHERE " + 
				"aq.idProduto = subprod.id AND ps.idSubproduto = subprod.id AND ps.idProduto = prod.id AND " + 
				"ps.idProduto_Tipo = pt.id AND aq.estoque > 0 AND " + 
				"ps.idProduto IN (" + 
				" SELECT id FROM produto WHERE " + 
				"idProduto_Status IN (" + 
				"  SELECT id FROM produto_status WHERE nome IN ('Normal')" + 
				") AND " + 
				"idProduto_Tipo IN (" + 
				"  SELECT id FROM produto_tipo WHERE tabela IN ('pacote')" + 
				") AND " + 
				"idDe IN (" + 
				"  SELECT id FROM de WHERE slug IN (?)" + 
				")" + 
				") " + 
				"GROUP BY subprod.slug " + 
				"ORDER BY menorValorPessoa ASC" + 
				";";
		System.out.println("query: " + query);
		
		
		
		statement = connection.prepareStatement(query, ResultSet.FETCH_FORWARD, ResultSet.CLOSE_CURSORS_AT_COMMIT);
		statement.setString(1, SLUG_DE);
		result = statement.executeQuery();

		int i = 1;
		while(result.next()) {
			
			String iAsString = String.valueOf(i);
			System.out.println("iAsString: " + iAsString);
			
			i++;
		}
		
		System.out.println("Saindo");

	}

}
