package injoy.jasper.bi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import injoy.InjoyProperties;
import injoy.dao.JasperDAO;
import injoy.jasper.JasperPDFGenerator;
import net.sf.jasperreports.engine.JRException;

public class CashFlow {
	// static private Properties propriedades = InjoyProperties.getProperties();
	private static final int DIA_RELATORIO = 4;
	private static final int NUM_SEMANAS = 8;
	private static final String [] LISTA_SLUG = {"carneiros", "jeri2019", "letspipa"};
	/* "carneiros", "jeri2019", "letspipa", "xama2019", "awe2019" */
	
	private static final String ARQUIVO_JRXML = "bi_cashflow";
	private static final String SUFIXO_ARQUIVO_PDF = "-cashflow";
	private static final DecimalFormat FORMATO_DINHEIRO = new DecimalFormat("#,##0.00");
	private static final SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("YYYY-MM-dd");
	
	//private static final String SQL_PEDIDOS_PADRAO = "SELECT DISTINCT idPedido FROM pedido_pagador WHERE status_interno IN (2)";
	//private static final String SQL_PEDIDOS_TODOS = "SELECT DISTINCT idPedido FROM pedido_pagador WHERE parcela IN (1) GROUP BY idPedido HAVING SUM(status_interno) <= 2*MAX(n)";	
	private static final String SQL_PEDIDOS_PAGO = "SELECT idPedido FROM pedido_pagador WHERE parcela IN (1) GROUP BY idPedido HAVING MIN(status_interno) IN (2) AND MAX(status_interno) IN (2)";
	private static final String SQL_PEDIDOS_PAGO_PARCIAL = "SELECT idPedido FROM pedido_pagador WHERE parcela IN (1) GROUP BY idPedido HAVING MIN(status_interno) IN (0, 1) AND MAX(status_interno) IN (2)";
	private static final String SQL_PEDIDOS_PREVENDA = "SELECT idPedido FROM pedido_pagador WHERE parcela IN (1) GROUP BY idPedido HAVING MIN(status_interno) IN (0, 1) AND MAX(status_interno) IN (0, 1)";
	
	public static void main(String[] args) throws SQLException, JRException {
		long startTime = Calendar.getInstance().getTimeInMillis();
		
		// PREPARANDO ITENS DA CONEXÃO COM BANCO DE DADOS
		System.out.println("Tentando conectar...");
		JasperDAO dao = JasperDAO.instance();
		System.out.println("Conectado.\n");
		
		
		for(String slug: LISTA_SLUG) {
			
			
			// CRIANDO PARÂMETROS DOS DADOS DOS RELATÓRIOS
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("slug_de", slug);		

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, DIA_RELATORIO - calendar.get(Calendar.DAY_OF_WEEK));
			double[] total = new double[NUM_SEMANAS+1], totalAcomodacao = new double[NUM_SEMANAS+1], totalFestasFeminino = new double[NUM_SEMANAS+1], 
					totalFestasMasculino = new double[NUM_SEMANAS+1], totalFestasUnissex = new double[NUM_SEMANAS+1], 
					totalAereo = new double[NUM_SEMANAS+1], totalVenda = new double[NUM_SEMANAS+1], 
					totalPagoParcial = new double[NUM_SEMANAS+1], totalPreVenda = new double[NUM_SEMANAS+1];
			
			for(int i = 0; i <= NUM_SEMANAS; i++) {
				
				String dataDoCalendarioAtual = FORMATO_DATA.format(calendar.getTime());
				parameters.put("dataSemana" + String.valueOf(i), dataDoCalendarioAtual);
				
				double venda = buscaValorFinal(dao.getConnection(), queryAcomodacoes(SQL_PEDIDOS_PAGO), slug, dataDoCalendarioAtual);
				String vendaAsString = FORMATO_DINHEIRO.format(venda);
				parameters.put("acomodacaoVendaSemana" + String.valueOf(i), vendaAsString);
				totalAcomodacao[i] += venda;
				totalVenda[i] += venda;
				total[i] += venda;
				
				double pagoParcial = buscaValorFinal(dao.getConnection(), queryAcomodacoes(SQL_PEDIDOS_PAGO_PARCIAL), slug, dataDoCalendarioAtual);
				String valorPagoParcialAsString = FORMATO_DINHEIRO.format(pagoParcial);
				parameters.put("acomodacaoPagoParcialSemana" + String.valueOf(i), valorPagoParcialAsString);
				totalAcomodacao[i] += pagoParcial;
				totalPagoParcial[i] += pagoParcial;
				total[i] += pagoParcial;
				
				double preVenda = buscaValorFinal(dao.getConnection(), queryAcomodacoes(SQL_PEDIDOS_PREVENDA), slug, dataDoCalendarioAtual);
				String valorPreVendaAsString = FORMATO_DINHEIRO.format(preVenda);
				parameters.put("acomodacaoPreVendaSemana" + String.valueOf(i), valorPreVendaAsString);
				totalAcomodacao[i] += preVenda;
				totalPreVenda[i] += preVenda;
				total[i] += preVenda;
				
				venda = buscaValorFinal(dao.getConnection(), queryFestasMasculino(SQL_PEDIDOS_PAGO), slug, dataDoCalendarioAtual);
				vendaAsString = FORMATO_DINHEIRO.format(venda);
				parameters.put("festaMasculinoVendaSemana" + String.valueOf(i), vendaAsString);
				totalFestasMasculino[i] += venda;
				totalVenda[i] += venda;
				total[i] += venda;
				
				pagoParcial = buscaValorFinal(dao.getConnection(), queryFestasMasculino(SQL_PEDIDOS_PAGO_PARCIAL), slug, dataDoCalendarioAtual);
				valorPagoParcialAsString = FORMATO_DINHEIRO.format(pagoParcial);
				parameters.put("festaMasculinoPagoParcialSemana" + String.valueOf(i), valorPagoParcialAsString);
				totalFestasMasculino[i] += pagoParcial;
				totalPagoParcial[i] += pagoParcial;
				total[i] += pagoParcial;
				
				preVenda = buscaValorFinal(dao.getConnection(), queryFestasMasculino(SQL_PEDIDOS_PREVENDA), slug, dataDoCalendarioAtual);
				valorPreVendaAsString = FORMATO_DINHEIRO.format(preVenda);
				parameters.put("festaMasculinoPreVendaSemana" + String.valueOf(i), valorPreVendaAsString);
				totalFestasMasculino[i] += preVenda;
				totalPreVenda[i] += preVenda;
				total[i] += preVenda;
				
				venda = buscaValorFinal(dao.getConnection(), queryFestasFeminino(SQL_PEDIDOS_PAGO), slug, dataDoCalendarioAtual);
				vendaAsString = FORMATO_DINHEIRO.format(venda);
				parameters.put("festaFemininoVendaSemana" + String.valueOf(i), vendaAsString);
				totalFestasFeminino[i] += venda;
				totalVenda[i] += venda;
				total[i] += venda;
				
				pagoParcial = buscaValorFinal(dao.getConnection(), queryFestasFeminino(SQL_PEDIDOS_PAGO_PARCIAL), slug, dataDoCalendarioAtual);
				valorPagoParcialAsString = FORMATO_DINHEIRO.format(pagoParcial);
				parameters.put("festaFemininoPagoParcialSemana" + String.valueOf(i), valorPagoParcialAsString);
				totalFestasFeminino[i] += pagoParcial;
				totalPagoParcial[i] += pagoParcial;
				total[i] += pagoParcial;
				
				preVenda = buscaValorFinal(dao.getConnection(), queryFestasFeminino(SQL_PEDIDOS_PREVENDA), slug, dataDoCalendarioAtual);
				valorPreVendaAsString = FORMATO_DINHEIRO.format(preVenda);
				parameters.put("festaFemininoPreVendaSemana" + String.valueOf(i), valorPreVendaAsString);
				totalFestasFeminino[i] += preVenda;
				totalPreVenda[i] += preVenda;
				total[i] += preVenda;
				
				venda = buscaValorFinal(dao.getConnection(), queryFestasUnissex(SQL_PEDIDOS_PAGO), slug, dataDoCalendarioAtual);
				vendaAsString = FORMATO_DINHEIRO.format(venda);
				parameters.put("festaUnissexVendaSemana" + String.valueOf(i), vendaAsString);
				totalFestasUnissex[i] += venda;
				totalVenda[i] += venda;
				total[i] += venda;
				
				pagoParcial = buscaValorFinal(dao.getConnection(), queryFestasUnissex(SQL_PEDIDOS_PAGO_PARCIAL), slug, dataDoCalendarioAtual);
				valorPagoParcialAsString = FORMATO_DINHEIRO.format(pagoParcial);
				parameters.put("festaUnissexPagoParcialSemana" + String.valueOf(i), valorPagoParcialAsString);
				totalFestasUnissex[i] += pagoParcial;
				totalPagoParcial[i] += pagoParcial;
				total[i] += pagoParcial;
				
				preVenda = buscaValorFinal(dao.getConnection(), queryFestasUnissex(SQL_PEDIDOS_PREVENDA), slug, dataDoCalendarioAtual);
				valorPreVendaAsString = FORMATO_DINHEIRO.format(preVenda);
				parameters.put("festaUnissexPreVendaSemana" + String.valueOf(i), valorPreVendaAsString);
				totalFestasUnissex[i] += preVenda;
				totalPreVenda[i] += preVenda;
				total[i] += preVenda;
				
				venda = buscaValorFinal(dao.getConnection(), queryAereo(SQL_PEDIDOS_PAGO), slug, dataDoCalendarioAtual);
				vendaAsString = FORMATO_DINHEIRO.format(venda);
				parameters.put("aereoVendaSemana" + String.valueOf(i), vendaAsString);
				totalAereo[i] += venda;
				totalVenda[i] += venda;
				total[i] += venda;
				
				pagoParcial = buscaValorFinal(dao.getConnection(), queryAereo(SQL_PEDIDOS_PAGO_PARCIAL), slug, dataDoCalendarioAtual);
				valorPagoParcialAsString = FORMATO_DINHEIRO.format(pagoParcial);
				parameters.put("aereoPagoParcialSemana" + String.valueOf(i), valorPagoParcialAsString);
				totalAereo[i] += pagoParcial;
				totalPagoParcial[i] += pagoParcial;
				total[i] += pagoParcial;
				
				preVenda = buscaValorFinal(dao.getConnection(), queryAereo(SQL_PEDIDOS_PREVENDA), slug, dataDoCalendarioAtual);
				valorPreVendaAsString = FORMATO_DINHEIRO.format(preVenda);
				parameters.put("aereoPreVendaSemana" + String.valueOf(i), valorPreVendaAsString);
				totalAereo[i] += preVenda;
				totalPreVenda[i] += preVenda;
				total[i] += preVenda;
				
				
				// TOTAIS
				parameters.put("acomodacaoSemana" + String.valueOf(i), FORMATO_DINHEIRO.format(totalAcomodacao[i]));
				parameters.put("festaMasculinoSemana" + String.valueOf(i), FORMATO_DINHEIRO.format(totalFestasMasculino[i]));
				parameters.put("festaFemininoSemana" + String.valueOf(i), FORMATO_DINHEIRO.format(totalFestasFeminino[i]));
				parameters.put("festaUnissexSemana" + String.valueOf(i), FORMATO_DINHEIRO.format(totalFestasUnissex[i]));
				parameters.put("aereoSemana" + String.valueOf(i), FORMATO_DINHEIRO.format(totalAereo[i]));
				parameters.put("totalVendaSemana" + String.valueOf(i), FORMATO_DINHEIRO.format(totalVenda[i]));
				parameters.put("totalPagoParcialSemana" + String.valueOf(i), FORMATO_DINHEIRO.format(totalPagoParcial[i]));
				parameters.put("totalPreVendaSemana" + String.valueOf(i), FORMATO_DINHEIRO.format(totalPreVenda[i]));
				parameters.put("totalSemana" + String.valueOf(i), FORMATO_DINHEIRO.format(total[i]));
				
				// ATUALIZA CALENDÁRIO PARA A SEMANA ANTERIOR
				calendar.add(Calendar.WEEK_OF_YEAR, -1);
			}
			
			// NOMES DOS ARQUIVOS A SEREM PROCESSADOS
			String resultPDFPath = InjoyProperties.getPDFFilePath(slug + SUFIXO_ARQUIVO_PDF);
			String jasperFile = InjoyProperties.getReportFilePath(ARQUIVO_JRXML);
			//parameters.forEach((key, value) -> System.out.println(key + " = " + value));
			JasperPDFGenerator.generate(dao.getConnection(), parameters, resultPDFPath, jasperFile);
			
		}
		

		
		
		// FINALIZAÇÃO
		long endTime = Calendar.getInstance().getTimeInMillis();
		long processingTime = endTime - startTime;
		System.out.println("\nFinalizado em " + processingTime + " milisegundos.");
		
	}
	
	
	private static double buscaValorFinal(Connection connection, String query, String slugDe, 
			String finalDate) throws SQLException {
			
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, finalDate);
			statement.setString(2, slugDe);
			ResultSet result = statement.executeQuery();
			
			if(result.next()) {
				return result.getDouble("valor");
			}
			return 0;
	}
	
	private static String queryAcomodacoes(String criteria) {
		return "SELECT SUM(pi.valor) + SUM(pi.desconto) as valor" +  
				" FROM pedido_item pi, pedido ped, produto_tipo pt, produto_subtipo ps, produto prod, acomodacao_quarto t" + 
				" WHERE pi.idPedido = ped.id AND pt.id = pi.tipo AND pi.subtipo = ps.id AND pi.idExterno = prod.id AND pi.idExterno2 = t.id AND" + 
				"	DATEDIFF(?, ped.data) >= 0 AND " + 
				"	ped.id IN ( " + 
				"		SELECT id FROM pedido WHERE idDe IN ( " + 
				"			SELECT id FROM de WHERE slug IN (?) " + 
				"		) " + 
				"	) " + 
				"	AND " + 
				"	ped.id IN ( " + criteria + " ) " + 
				" GROUP BY ped.idDe" + 
				";";
	}
	
	private static String queryAereo(String criteria) {
		return "SELECT SUM(pi.valor) + SUM(pi.desconto) as valor" +  
				" FROM pedido_item pi, pedido ped, produto_tipo pt, produto_subtipo ps, produto prod, aereo_trecho t" + 
				" WHERE pi.idPedido = ped.id AND pt.id = pi.tipo AND pi.subtipo = ps.id AND pi.idExterno = prod.id AND pi.idExterno2 = t.id AND" + 
				"	DATEDIFF(?, ped.data) >= 0 AND " + 
				"	ped.id IN ( " + 
				"		SELECT id FROM pedido WHERE idDe IN ( " + 
				"			SELECT id FROM de WHERE slug IN (?) " + 
				"		) " + 
				"	) " + 
				"	AND " + 
				"	ped.id IN ( " + criteria + " ) " + 
				" GROUP BY ped.idDe" + 
				";";
	}
	
	private static String queryFestasMasculino(String criteria) {
		return "SELECT SUM(pi.valor) + SUM(pi.desconto) as valor" +  
				" FROM pedido_item pi, pedido ped, produto_tipo pt, produto_subtipo ps, produto prod, experiencia_festadias t" + 
				" WHERE pi.idPedido = ped.id AND pt.id = pi.tipo AND pi.subtipo = ps.id AND pi.idExterno = prod.id AND pi.idExterno2 = t.id AND" + 
				" t.sexo IN ('Masculino') AND " +
				"	DATEDIFF(?, ped.data) >= 0 AND " + 
				"	ped.id IN ( " + 
				"		SELECT id FROM pedido WHERE idDe IN ( " + 
				"			SELECT id FROM de WHERE slug IN (?) " + 
				"		) " + 
				"	) " + 
				"	AND " + 
				"	ped.id IN ( " + criteria + " ) " + 
				" GROUP BY ped.idDe" + 
				";";
	}
	
	private static String queryFestasFeminino(String criteria) {
		return "SELECT SUM(pi.valor) + SUM(pi.desconto) as valor" +  
				" FROM pedido_item pi, pedido ped, produto_tipo pt, produto_subtipo ps, produto prod, experiencia_festadias t" + 
				" WHERE pi.idPedido = ped.id AND pt.id = pi.tipo AND pi.subtipo = ps.id AND pi.idExterno = prod.id AND pi.idExterno2 = t.id AND" + 
				" t.sexo IN ('Feminino') AND " +
				"	DATEDIFF(?, ped.data) >= 0 AND " + 
				"	ped.id IN ( " + 
				"		SELECT id FROM pedido WHERE idDe IN ( " + 
				"			SELECT id FROM de WHERE slug IN (?) " + 
				"		) " + 
				"	) " + 
				"	AND " + 
				"	ped.id IN ( " + criteria + " ) " + 
				" GROUP BY ped.idDe" + 
				";";
	}
	
	private static String queryFestasUnissex(String criteria) {
		return "SELECT SUM(pi.valor) + SUM(pi.desconto) as valor" +  
				" FROM pedido_item pi, pedido ped, produto_tipo pt, produto_subtipo ps, produto prod, experiencia_festadias t" + 
				" WHERE pi.idPedido = ped.id AND pt.id = pi.tipo AND pi.subtipo = ps.id AND pi.idExterno = prod.id AND pi.idExterno2 = t.id AND" + 
				" t.sexo IN ('Unissex') AND " +
				"	DATEDIFF(?, ped.data) >= 0 AND " + 
				"	ped.id IN ( " + 
				"		SELECT id FROM pedido WHERE idDe IN ( " + 
				"			SELECT id FROM de WHERE slug IN (?) " + 
				"		) " + 
				"	) " + 
				"	AND " + 
				"	ped.id IN ( " + criteria + " ) " + 
				" GROUP BY ped.idDe" + 
				";";
	}

}

/*

	
	private static HashMap<String, Object> buscaValores(Connection connection, String slugDe, String finalDate) throws SQLException {
		String somaValores = "somaValores";
		String somaDescontos = "somaDescontos";
		String valorFinal = "valorFinal";
		String query = "SELECT SUM(pi.valor) as " + somaValores + ", SUM(pi.desconto) as "+ somaDescontos + ", SUM(pi.valor) + SUM(pi.desconto) as " + valorFinal + 
			" FROM pedido_item pi, pedido ped, produto_tipo pt, produto_subtipo ps, produto prod, acomodacao_quarto aq" + 
			" WHERE pi.idPedido = ped.id AND pt.id = pi.tipo AND pi.subtipo = ps.id AND pi.idExterno = prod.id AND pi.idExterno2 = aq.id AND" + 
			"	DATEDIFF(?, ped.data) >= 0 AND " + 
			"	ped.id IN ( " + 
			"		SELECT id FROM pedido WHERE idDe IN ( " + 
			"			SELECT id FROM de WHERE slug IN (?) " + 
			"		) " + 
			"	) " + 
			"	AND " + 
			"	ped.id IN ( " + 
			"		SELECT DISTINCT idPedido FROM pedido_pagador WHERE status_interno IN (2)" + 
			"	) " + 
			" GROUP BY ped.idDe" + 
			";";
		
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, finalDate);
		statement.setString(2, slugDe);
		ResultSet result = statement.executeQuery();
		
		HashMap<String, Object> mapResults = new HashMap<String, Object>();
		if(result.next()) {
			mapResults.put(somaValores, FORMATO_DINHEIRO.format(result.getDouble(somaValores)));
			mapResults.put(somaDescontos, FORMATO_DINHEIRO.format(result.getDouble(somaDescontos)));
			mapResults.put(valorFinal, FORMATO_DINHEIRO.format(result.getDouble(valorFinal)));
			mapResults.put("dataFinal", finalDate);
			mapResults.put("slugDe", slugDe);
		}
		return mapResults;
	}

*/