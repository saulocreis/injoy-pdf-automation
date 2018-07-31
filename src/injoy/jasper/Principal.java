package injoy.jasper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

public class Principal {
	
	static private final int MAX_RESULTS = 20;
	static private final String DEFAULT_WORKSPACE = "injoy.";
	static private final String DEFAULT_FILENAME = "src/jasperprops.properties";
	static private final String CONEXAO = "jdbc:mariadb://localhost:3306/injoy?user=root";
	static private final String SEP = System.getProperty("file.separator");
	static private final String SEP2 = "/"; 
	static private final String SLUG_DE = "letspipa";
	static private final String IMG_DIR = "images".concat(SEP).concat(SLUG_DE).concat(SEP);
	static private final String SLUG_EXPERIENCIA = "pacote-de-festas-pipa-2019";
	static private final String SLUG_AEREO_NORMAL = "aereo-reveillon-pipa-2019";
	static private final String SLUG_AEREO_COMBO_FEMININO = "combo-aereo-pacote-feminino-6-festas-lets-pipa-2019";
	static private final String SLUG_AEREO_COMBO_MASCULINO = "combo-aereo-pacote-masculino-6-festas-lets-pipa-2019";
	
	
	static private Properties jasperprops;
	

	public static void main(String[] args) throws SQLException {
		Calendar calendar = Calendar.getInstance();
		long startTime = calendar.getTimeInMillis(); /*
		String dia = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		String mesDia = String.valueOf(calendar.get(Calendar.MONTH))+dia;
		String anoMesDia = String.valueOf(calendar.get(Calendar.YEAR))+mesDia;
		String segundo = String.valueOf(calendar.get(Calendar.SECOND));
		String minutoSegundo = String.valueOf(calendar.get(Calendar.MINUTE))+segundo;
		String horaMinutoSegundo = String.valueOf(calendar.get(Calendar.HOUR))+minutoSegundo;
		String data = anoMesDia+horaMinutoSegundo; */
		String nomeArquivoPDF = SLUG_DE + "-" + String.valueOf(startTime) + ".pdf";
		
		
		// PREPARANDO ITENS DA CONEXÃO COM BANCO DE DADOS
		System.out.println("Tentando conectar...");
		Connection connection = DriverManager.getConnection(CONEXAO);
		System.out.println("Conectado.");

		
		// CRIANDO PARÂMETROS DOS DADOS DOS RELATÓRIOS
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("sep", SEP);
		parameters.put("img_dir", IMG_DIR);
		parameters.put("slug_de", SLUG_DE);
			
		
		// RELATÓRIOS A SEREM USADOS NA CRIAÇÃO DO PDF
		ArrayList<String> listaArquivos = new ArrayList<String>();
		listaArquivos.add(SLUG_DE + "_capa");
		listaArquivos.add(SLUG_DE + "_quemsomos");
		listaArquivos.add(SLUG_DE + "_0_ij");
		listaArquivos.add(SLUG_DE + "_0_menu");
		listaArquivos.add(SLUG_DE + "_1_reveillon");
		listaArquivos.add(SLUG_DE + "_2_pqinjoy"); 
		listaArquivos.add(SLUG_DE + "_3_infovenda"); 
		listaArquivos.add(SLUG_DE + "_4_acomodacoes");
		listaArquivos.add(SLUG_DE + "_4_resumopacotes");
		
		// BUSCA DOS DADOS DOS PARÂMETROS NO BANCO DE DADOS
		searchDataFromConnection(connection, parameters, listaArquivos);
		
		listaArquivos.add(SLUG_DE + "_festas");
		listaArquivos.add(SLUG_DE + "_final");
		
		
		// PRODUÇÃO DO PDF
		try {
			producePDF(connection, parameters, nomeArquivoPDF, listaArquivos);
		} catch (JRException e) {
			e.printStackTrace();
		}
		
		// FINALIZAÇÃO
		long endTime = Calendar.getInstance().getTimeInMillis();
		long processingTime = endTime - startTime;
		System.out.println("Finalizado em " + processingTime + " milisegundos.");
	}
	
	
	private static void searchDataFromConnection(Connection connection, HashMap<String, Object> parameters,
			ArrayList<String> listaArquivos) throws SQLException {
		
		String query = "SELECT" + 
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS capa," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS quemsomos," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS ijreveillon," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS menu," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS reveillon," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS pqinjoy," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS infovenda," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS menuacomodacoes," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS resumopacotes," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS festas," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS final" +
				";";
		PreparedStatement statement = connection.prepareStatement(query);
		
		String baseParameter = "jr_" + SLUG_DE + "_";
		String parameterCapa = baseParameter + "capa";
		String parameterQuemSomos = baseParameter + "quemsomos";
		String parameterIJReveillon = baseParameter + "ijreveillon";
		String parameterMenu = baseParameter + "menu";
		String parameterReveillon = baseParameter + "reveillon";
		String parameterPqInjoy = baseParameter + "pqinjoy";
		String parameterInfovenda = baseParameter + "infovenda";
		String parameterMenuAcomodacoes = baseParameter + "menuacomodacoes";
		String parameterResumoPacotes = baseParameter + "resumopacotes";
		String parameterFestas = baseParameter + "festas";
		String parameterFinal = baseParameter + "final";
		
		statement.setString(1, parameterCapa);
		statement.setString(2, parameterQuemSomos);
		statement.setString(3, parameterIJReveillon);
		statement.setString(4, parameterMenu);
		statement.setString(5, parameterReveillon);
		statement.setString(6, parameterPqInjoy);
		statement.setString(7, parameterInfovenda);
		statement.setString(8, parameterMenuAcomodacoes);
		statement.setString(9, parameterResumoPacotes);
		statement.setString(10, parameterFestas);
		statement.setString(11, parameterFinal);
		ResultSet result = statement.executeQuery();
		
		result.next();
		parameters.put(parameterCapa, result.getString("capa"));
		parameters.put(parameterQuemSomos, result.getString("quemsomos"));
		parameters.put(parameterIJReveillon, result.getString("ijreveillon"));
		parameters.put(parameterMenu, result.getString("menu"));
		parameters.put(parameterReveillon, result.getString("reveillon"));
		parameters.put(parameterPqInjoy, result.getString("pqinjoy"));
		parameters.put(parameterInfovenda, result.getString("infovenda"));
		parameters.put(parameterMenuAcomodacoes, result.getString("menuacomodacoes"));
		parameters.put(parameterResumoPacotes, result.getString("resumopacotes"));
		parameters.put(parameterFestas, result.getString("festas"));
		parameters.put(parameterFinal, result.getString("final"));
		result.close();
		
		
		
		
		
		query = "SELECT (" + 
				"	SELECT ROUND(efd.valor, 0) " + 
				"	FROM experiencia_festadias efd " + 
				"	WHERE efd.sexo IN ('Feminino') AND " + 
				"		efd.categoria IN ('Principal') AND " + 
				"		efd.idProduto IN ( " + 
				"		SELECT id " + 
				"		FROM produto p WHERE " + 
				"			slug IN (?) " + 
				"		)  " + 
				"	) AS valorExperienciaFeminino, ( " + 
				"	SELECT ROUND(efd.valor, 0) " + 
				"	FROM experiencia_festadias efd " + 
				"	WHERE efd.sexo IN ('Masculino') AND " + 
				"		efd.categoria IN ('Principal') AND " + 
				"		efd.idProduto IN ( " + 
				"		SELECT id " + 
				"		FROM produto p WHERE " + 
				"			slug IN (?) " + 
				"		) " + 
				"	) AS valorExperienciaMasculino, (" + 
				"	SELECT ROUND(efd.valor,0)\r\n" + 
				"	FROM experiencia_festadias efd WHERE " + 
				"		efd.categoria IN ('Secundário') AND " + 
				"		efd.idProduto IN ( " + 
				"		SELECT id FROM produto WHERE slug IN (?) " + 
				"	) " + 
				") AS valorExperienciaAvulsa" + 
				";";

		statement = connection.prepareStatement(query);
		statement.setString(1, SLUG_EXPERIENCIA);
		statement.setString(2, SLUG_EXPERIENCIA);
		statement.setString(3, SLUG_EXPERIENCIA);
		result = statement.executeQuery();
		
		result.next();
		int valorExperienciaAvulsa = result.getInt("valorExperienciaAvulsa");
		int valorExperienciaFeminino = result.getInt("valorExperienciaFeminino")  + valorExperienciaAvulsa;
		int valorExperienciaMasculino = result.getInt("valorExperienciaMasculino") + valorExperienciaAvulsa;
		result.close();
		
		
		
		
		query = "SELECT (" + 
				"	SELECT ROUND(valor + taxa, 0) " + 
				"	FROM aereo_trecho WHERE " + 
				"		idProduto IN ( " + 
				"			SELECT id FROM produto WHERE slug IN (?) " + 
				"		) " + 
				"	) as valorAereo, ( " + 
				"	SELECT ROUND(valor + taxa, 0) " + 
				"	FROM aereo_trecho WHERE " + 
				"		idProduto IN ( " + 
				"			SELECT id FROM produto WHERE slug IN (?) " + 
				"		) " + 
				"    ) AS valorAereoComboFeminino, ( " + 
				"	SELECT ROUND(valor + taxa, 0) " + 
				"	FROM aereo_trecho WHERE " + 
				"		idProduto IN ( " + 
				"			SELECT id FROM produto WHERE slug IN (?) " + 
				"		) " + 
				"    ) AS valorAereoComboMasculino" + 
				";";
		statement = connection.prepareStatement(query);
		statement.setString(1, SLUG_AEREO_NORMAL);
		statement.setString(2, SLUG_AEREO_COMBO_FEMININO);
		statement.setString(3, SLUG_AEREO_COMBO_MASCULINO);
		result = statement.executeQuery();
		
		result.next();
		DecimalFormat formatoSemCentavosComCifra = new DecimalFormat("R$ #,##0");
		int valorAereo = result.getInt("valorAereo");
		int valorAereoComboFeminino = result.getInt("valorAereoComboFeminino");
		int valorAereoComboMasculino = result.getInt("valorAereoComboMasculino");
		parameters.put("valorAereo", formatoSemCentavosComCifra.format(valorAereo));
		parameters.put("valorAereoComboFeminino", formatoSemCentavosComCifra.format(valorAereoComboFeminino));
		parameters.put("valorAereoComboMasculino", formatoSemCentavosComCifra.format(valorAereoComboMasculino));
		result.close();
		
		
		
		
		
		query = "SELECT prod.slug as slugPacote, subprod.nome as nomeProduto, subprod.slug as slugProduto," + 
				"	MIN(ROUND(DATEDIFF(aq.data_final, aq.data_inicial) * aq.valor / aq.hospedes, 0)) as menorValorPessoa " + 
				"	FROM acomodacao_quarto aq, produto subprod, produto_subproduto ps, produto prod, produto_tipo pt WHERE " + 
				"	aq.idProduto = subprod.id AND ps.idSubproduto = subprod.id AND ps.idProduto = prod.id AND " + 
				"   ps.idProduto_Tipo = pt.id AND aq.estoque > 0 AND " + 
				"	ps.idProduto IN (" + 
				"		SELECT id FROM produto WHERE " + 
				"			idProduto_Status IN (" + 
				"				SELECT id FROM produto_status WHERE nome IN ('Normal')" + 
				"			) AND " + 
				"			idProduto_Tipo IN (" + 
				"				SELECT id FROM produto_tipo WHERE tabela IN ('pacote')" + 
				"			) AND " + 
				"			idDe IN (" + 
				"				SELECT id FROM de WHERE slug IN (?)" + 
				"			)" + 
				"	)" + 
				"GROUP BY subprod.slug " + 
				"ORDER BY menorValorPessoa ASC " + 
				";";
		
		statement = connection.prepareStatement(query);
		statement.setString(1, SLUG_DE);
		result = statement.executeQuery();
		
		
		String baseParameterDe = "jr_".concat(SLUG_DE);
		String injoyLinkDESobre = get("link").concat(SLUG_DE).concat(SEP2).concat("sobre").concat(SEP2);
		String esgotado = "ESGOTADO";
		String tagAcomodacoes = get("tag.acomodacoes");
		String imageVazio = get("image.vazio");
		
		int i = 1;
		while(result.next() && i <= MAX_RESULTS) {
			
			String slugPacote = result.getString("slugPacote");
			String nomeProduto = result.getString("nomeProduto");
			String slugProduto = result.getString("slugProduto");
			
			// <slugDe>_ac_<slugProduto>_capa_fotos, <slugDe>_ac_<slugProduto>_precos
			String nomeArquivoCapaFotos = SLUG_DE.concat("_ac_").concat(slugProduto).concat("_capa_fotos");
			String nomeArquivoPrecos = SLUG_DE.concat("_ac_").concat(slugProduto).concat("_precos");
			 
			listaArquivos.add(nomeArquivoCapaFotos); 
			listaArquivos.add(nomeArquivoPrecos); 
			
			int menorValorPessoa = result.getInt("menorValorPessoa");
			DecimalFormat formatoSemCentavosSemCifra = new DecimalFormat("#,##0");
			String menorValorPessoaAsString = formatoSemCentavosSemCifra.format(menorValorPessoa);
			
			String iAsString = String.valueOf(i);
			String parameter = baseParameterDe.concat("_menuacomodacoes_link").concat(iAsString);
			System.out.println(parameter + " -> " + slugProduto);
			parameters.put(parameter, slugProduto);
			
			parameter = baseParameterDe.concat("_resumopacotes_nome").concat(iAsString);
			System.out.println(parameter + " -> " + nomeProduto);
			parameters.put(parameter, nomeProduto.toUpperCase());
			
			parameter = baseParameterDe.concat("_resumopacotes_pacoteac").concat(iAsString);
			System.out.println(parameter + " -> " + menorValorPessoaAsString);
			parameters.put(parameter, menorValorPessoaAsString);
			
			parameter = baseParameterDe.concat("_resumopacotes_pacoteaereo").concat(iAsString);
			int menorValorPessoaComAereo = menorValorPessoa + valorAereo;
			String menorValorPessoaComAereoAsString = formatoSemCentavosSemCifra.format(menorValorPessoaComAereo);
			System.out.println(parameter + " -> " + menorValorPessoaComAereoAsString);
			parameters.put(parameter, menorValorPessoaComAereoAsString);
			
			parameter = baseParameterDe.concat("_resumopacotes_pacotefeminino").concat(iAsString);
			int menorValorPessoaFeminino = menorValorPessoa + valorExperienciaFeminino;
			String menorValorPessoaFemininoAsString = formatoSemCentavosSemCifra.format(menorValorPessoaFeminino);
			System.out.println(parameter + " -> " + menorValorPessoaFemininoAsString);
			parameters.put(parameter, menorValorPessoaFemininoAsString);
			
			parameter = baseParameterDe.concat("_resumopacotes_pacotemasculino").concat(iAsString);
			int menorValorPessoaMasculino = menorValorPessoa + valorExperienciaMasculino;
			String menorValorPessoaMasculinoAsString = formatoSemCentavosSemCifra.format(menorValorPessoaMasculino);
			System.out.println(parameter + " -> " + menorValorPessoaMasculinoAsString);
			parameters.put(parameter, menorValorPessoaMasculinoAsString);
			
			parameter = baseParameterDe.concat("_resumopacotes_completofeminino").concat(iAsString);
			int menorValorPessoaCompletoFeminino = menorValorPessoa + valorAereoComboFeminino;
			String menorValorPessoaCompletoFemininoAsString = formatoSemCentavosSemCifra.format(menorValorPessoaCompletoFeminino);
			System.out.println(parameter + " -> " + menorValorPessoaCompletoFemininoAsString);
			parameters.put(parameter, menorValorPessoaCompletoFemininoAsString);

			parameter = baseParameterDe.concat("_resumopacotes_completomasculino").concat(iAsString);
			int menorValorPessoaCompletoMasculino = menorValorPessoa + valorAereoComboMasculino;
			String menorValorPessoaCompletoMasculinoAsString = formatoSemCentavosSemCifra.format(menorValorPessoaCompletoMasculino);
			System.out.println(parameter + " -> " + menorValorPessoaCompletoMasculinoAsString);
			parameters.put(parameter, menorValorPessoaCompletoMasculinoAsString);
			
			parameter = baseParameterDe.concat("_resumopacotes_link").concat(iAsString);
			String linkPacote = injoyLinkDESobre.concat(slugPacote);
			System.out.println(parameter + " -> " + linkPacote);
			parameters.put(parameter, linkPacote);
			
			String baseParameterDeAc = baseParameterDe.concat("_ac_");
			parameter = baseParameterDeAc.concat(slugProduto).concat("_anchor");
			System.out.println(parameter + " -> " + slugProduto);
			parameters.put(parameter, slugProduto);
			
			parameter = baseParameterDeAc.concat(slugProduto).concat("_linkSobre");
			System.out.println(parameter + " -> " + linkPacote);
			parameters.put(parameter, linkPacote);
			
			parameter = baseParameterDeAc.concat(slugProduto).concat("_menorValorPessoa");
			menorValorPessoaAsString = formatoSemCentavosComCifra.format(menorValorPessoa);
			System.out.println(parameter + " -> " + menorValorPessoaAsString);
			parameters.put(parameter, menorValorPessoaAsString);
			
			parameter = baseParameterDeAc.concat(slugProduto).concat("_pacotefemininoaereo");
			System.out.println(parameter + " -> " + menorValorPessoaCompletoFemininoAsString);
			parameters.put(parameter, menorValorPessoaCompletoFemininoAsString);
			
			parameter = baseParameterDeAc.concat(slugProduto).concat("_pacotemasculinoaereo");
			System.out.println(parameter + " -> " + menorValorPessoaCompletoMasculinoAsString);
			parameters.put(parameter, menorValorPessoaCompletoMasculinoAsString);
			
		
			
			query = "SELECT ( " + 
					"	SELECT arquivo FROM pdf " + 
					"	WHERE slug IN (?) " + 
					") AS capa, ( " + 
					"	SELECT arquivo FROM pdf " + 
					"	WHERE slug IN (?) " + 
					") AS fotos1, ( " +
					"	SELECT arquivo FROM pdf " + 
					"	WHERE slug IN (?) " + 
					") AS fotos2, ( " +
					"	SELECT arquivo FROM pdf " + 
					"	WHERE slug IN (?) " + 
					") AS menu, ( " +
					"	SELECT arquivo FROM pdf " + 
					"	WHERE slug IN (?) " + 
					") AS precos, (" +
					"	SELECT arquivo FROM pdf " + 
					"	WHERE slug IN (?) " + 
					") AS precos2" +
					";";
			statement = connection.prepareStatement(query);
			
			baseParameter = baseParameterDeAc + slugProduto + "_";
			parameterCapa = baseParameter + "capa";
			String parameterFotos1 = baseParameter + "fotos1";
			String parameterFotos2 = baseParameter + "fotos2";
			parameterMenu = baseParameter + "menu";
			String parameterPrecos = baseParameter + "precos";
			String parameterPrecos2 = baseParameter + "precos2";
			statement.setString(1, parameterCapa);
			statement.setString(2, parameterFotos1);
			statement.setString(3, parameterFotos2);
			statement.setString(4, parameterMenu);
			statement.setString(5, parameterPrecos);
			statement.setString(6, parameterPrecos2);
			ResultSet result2 = statement.executeQuery();
			
			while(result2.next()) {
				String arquivoMenu = result2.getString("menu");
				parameter = baseParameterDe.concat("_menuacomodacoes_imagem").concat(iAsString);
				System.out.println(parameter + " -> " + arquivoMenu);
				parameters.put(parameter, arquivoMenu);
				
				String arquivoCapa = result2.getString("capa");
				System.out.println(parameterCapa + " -> " + arquivoCapa);
				parameters.put(parameterCapa, arquivoCapa);
				
				String arquivoFotos1 = result2.getString("fotos1");
				System.out.println(parameterFotos1 + " -> " + arquivoFotos1);
				parameters.put(parameterFotos1, arquivoFotos1);
				
				String arquivoFotos2 = result2.getString("fotos2");
				System.out.println(parameterFotos2 + " -> " + arquivoFotos2);
				parameters.put(parameterFotos2, arquivoFotos2);
				
				String arquivoPrecos = result2.getString("precos");
				System.out.println(parameterPrecos + " -> " + arquivoPrecos);
				parameters.put(parameterPrecos, arquivoPrecos);
				
				String arquivoPrecos2 = result2.getString("precos2");
				System.out.println(parameterPrecos2 + " -> " + arquivoPrecos2);
				parameters.put(parameterPrecos2, arquivoPrecos2);
			}
			result2.close();
			
			
			// PREENCHIMENTO DO SLIDE DE PREÇOS

			query = "SELECT aq.nome as nomeAc, aq.estoque, ROUND(aq.valor, 0) AS valor, " + 
					"	aq.hospedes, ROUND(DATEDIFF(aq.data_final, aq.data_inicial) * aq.valor / aq.hospedes, 0) as valorPessoa " + 
					"FROM acomodacao_quarto aq WHERE " + 
					"	DATEDIFF(aq.data_final, aq.data_inicial) = 7 AND " +
					"	estoque > 0 AND " + 
					"	idProduto IN ( " + 
					"        SELECT id FROM produto WHERE " + 
					"			slug IN (?) AND " + 
					"			idDe IN ( " + 
					"				SELECT id FROM de WHERE slug IN (?) " + 
					"			) " + 
					"    ) " + 
					" " + 
					"UNION " + 
					"SELECT aq.nome as nomeAc, aq.estoque, ROUND(aq.valor, 0) AS valor, " + 
					"	aq.hospedes, 'ESGOTADO' as valorPessoa " + 
					"FROM acomodacao_quarto aq WHERE " + 
					"	DATEDIFF(aq.data_final, aq.data_inicial) = 7 AND " +
					"	aq.estoque <= 0 AND " + 
					"	aq.idProduto IN ( " + 
					"        SELECT id FROM produto WHERE " + 
					"			slug IN (?) AND " + 
					"			idDe IN ( " + 
					"				SELECT id FROM de WHERE slug IN (?) " + 
					"			) " + 
					"    ) " + 
					";";
			statement = connection.prepareStatement(query);
			statement.setString(1, slugProduto);
			statement.setString(2, SLUG_DE);
			statement.setString(3, slugProduto);
			statement.setString(4, SLUG_DE);
			result2 = statement.executeQuery();
			
			while(result2.next()) {
				
				int estoque = result2.getInt("estoque");
				
				String nomeAc = result2.getString("nomeAc").toLowerCase().replace(' ', '-');
				String hospedes = result2.getString("hospedes");
				nomeAc = nomeAc + "_" + hospedes;
				
				String valorPessoaAsString = (estoque <= 0 ? result2.getString("valorPessoa") :
						formatoSemCentavosSemCifra.format(result2.getInt("valorPessoa")));
				parameter = baseParameterDeAc.concat(slugProduto).concat("_pacoteac_").concat(nomeAc);
				System.out.println(parameter + " -> " + valorPessoaAsString);
				parameters.put(parameter, valorPessoaAsString);
				
				String valorPacoteFemininoAsString = (estoque <= 0 ? result2.getString("valorPessoa") :
						formatoSemCentavosSemCifra.format(result2.getInt("valorPessoa") + valorExperienciaFeminino));
				parameter = baseParameterDeAc.concat(slugProduto).concat("_pacotefeminino_").concat(nomeAc);
				System.out.println(parameter + " -> " + valorPacoteFemininoAsString);
				parameters.put(parameter, valorPacoteFemininoAsString);
				
				String valorPacoteMasculinoAsString = (estoque <= 0 ? result2.getString("valorPessoa") :
						formatoSemCentavosSemCifra.format(result2.getInt("valorPessoa") + valorExperienciaMasculino));
				parameter = baseParameterDeAc.concat(slugProduto).concat("_pacotemasculino_").concat(nomeAc);
				System.out.println(parameter + " -> " + valorPacoteMasculinoAsString);
				parameters.put(parameter, valorPacoteMasculinoAsString);

			}
			result2.close();
			
			
			i++;
		}
		result.close();
		
		
		
		
		
		
		if(i <= MAX_RESULTS) {
			
			
			// EM BREVE
			query = "SELECT prod.slug as slugPacote, subprod.nome as nomeProduto, subprod.slug as slugProduto" + 
					"	FROM acomodacao_quarto aq, produto subprod, produto_subproduto ps, produto prod, produto_tipo pt WHERE " + 
					"	aq.idProduto = subprod.id AND ps.idSubproduto = subprod.id AND ps.idProduto = prod.id AND " + 
					"   ps.idProduto_Tipo = pt.id AND " + /*
					+ " AND aq.estoque <= 0 AND " + */
					"	ps.idProduto IN (" + 
					"		SELECT id FROM produto WHERE " + 
					"			idProduto_Status IN (" + 
					"				SELECT id FROM produto_status WHERE nome IN ('Em Breve')" + 
					"			) AND " + 
					"			idProduto_Tipo IN (" + 
					"				SELECT id FROM produto_tipo WHERE tabela IN ('pacote')" + 
					"			) AND " + 
					"			idDe IN (" + 
					"				SELECT id FROM de WHERE slug IN (?)" + 
					"			)" + 
					"	)" + 
					"GROUP BY subprod.slug " +
					";";
			
			statement = connection.prepareStatement(query);
			statement.setString(1, SLUG_DE);
			result = statement.executeQuery();
			
			while(result.next() && i <= MAX_RESULTS) {
				String iAsString = String.valueOf(i);
				// String slugPacote = result.getString("slugPacote");
				String nomeProduto = result.getString("nomeProduto");
				String slugProduto = result.getString("slugProduto");
				String emBreve = "EM BREVE";
				
				String parameter = baseParameterDe.concat("_menuacomodacoes_link").concat(iAsString);
				System.out.println(parameter + " -> " + slugProduto);
				parameters.put(parameter, slugProduto);
				
				parameter = baseParameterDe.concat("_resumopacotes_nome").concat(iAsString);
				System.out.println(parameter + " -> " + nomeProduto);
				parameters.put(parameter, nomeProduto.toUpperCase());
				
				parameter = baseParameterDe.concat("_resumopacotes_pacoteac").concat(iAsString);
				System.out.println(parameter + " -> " + emBreve);
				parameters.put(parameter, emBreve);
				
				parameter = baseParameterDe.concat("_resumopacotes_pacoteaereo").concat(iAsString);
				System.out.println(parameter + " -> " + emBreve);
				parameters.put(parameter, emBreve);
				
				parameter = baseParameterDe.concat("_resumopacotes_pacotefeminino").concat(iAsString);
				System.out.println(parameter + " -> " + emBreve);
				parameters.put(parameter, emBreve);
				
				parameter = baseParameterDe.concat("_resumopacotes_pacotemasculino").concat(iAsString);
				System.out.println(parameter + " -> " + emBreve);
				parameters.put(parameter, emBreve);				
				
				parameter = baseParameterDe.concat("_resumopacotes_completofeminino").concat(iAsString);
				System.out.println(parameter + " -> " + emBreve);
				parameters.put(parameter, emBreve);
				
				parameter = baseParameterDe.concat("_resumopacotes_completomasculino").concat(iAsString);
				System.out.println(parameter + " -> " + emBreve);
				parameters.put(parameter, emBreve);
				
				query = "SELECT arquivo FROM pdf WHERE slug IN ('jr_"
						+ SLUG_DE
						+ "_ac_"
						+ slugProduto
						+ "_menu_embreve');";
				statement = connection.prepareStatement(query);
				ResultSet result2 = statement.executeQuery();
				
				while(result2.next()) {
					String arquivo = result2.getString("arquivo");
					parameter = baseParameterDe.concat("_menuacomodacoes_imagem").concat(iAsString);
					System.out.println(parameter + " -> " + arquivo);
					parameters.put(parameter, arquivo);
				}
				result2.close();			
				
				i++;
			}
			
			
			
			
			
			// ESGOTADOS
			query = "SELECT prod.slug as slugPacote, subprod.nome as nomeProduto, subprod.slug as slugProduto" + 
					"	FROM acomodacao_quarto aq, produto subprod, produto_subproduto ps, produto prod, produto_tipo pt WHERE " + 
					"	aq.idProduto = subprod.id AND ps.idSubproduto = subprod.id AND ps.idProduto = prod.id AND " + 
					"   ps.idProduto_Tipo = pt.id AND " + /*
					+ " AND aq.estoque <= 0 AND " + */
					"	ps.idProduto IN (" + 
					"		SELECT id FROM produto WHERE " + 
					"			idProduto_Status IN (" + 
					"				SELECT id FROM produto_status WHERE nome IN ('Esgotado')" + 
					"			) AND " + 
					"			idProduto_Tipo IN (" + 
					"				SELECT id FROM produto_tipo WHERE tabela IN ('pacote')" + 
					"			) AND " + 
					"			idDe IN (" + 
					"				SELECT id FROM de WHERE slug IN (?)" + 
					"			)" + 
					"	)" + 
					"GROUP BY subprod.slug " +
					";";
			
			statement = connection.prepareStatement(query);
			statement.setString(1, SLUG_DE);
			result = statement.executeQuery();
			
			while(result.next() && i <= MAX_RESULTS) {
				String iAsString = String.valueOf(i);
				// String slugPacote = result.getString("slugPacote");
				String nomeProduto = result.getString("nomeProduto");
				String slugProduto = result.getString("slugProduto");
				
				
				String parameter = baseParameterDe.concat("_menuacomodacoes_link").concat(iAsString);
				System.out.println(parameter + " -> " + slugProduto);
				parameters.put(parameter, slugProduto);
				
				parameter = baseParameterDe.concat("_resumopacotes_nome").concat(iAsString);
				System.out.println(parameter + " -> " + nomeProduto);
				parameters.put(parameter, nomeProduto.toUpperCase());
				
				parameter = baseParameterDe.concat("_resumopacotes_pacoteac").concat(iAsString);
				System.out.println(parameter + " -> " + esgotado);
				parameters.put(parameter, esgotado);
				
				parameter = baseParameterDe.concat("_resumopacotes_pacoteaereo").concat(iAsString);
				System.out.println(parameter + " -> " + esgotado);
				parameters.put(parameter, esgotado);
				
				parameter = baseParameterDe.concat("_resumopacotes_pacotefeminino").concat(iAsString);
				System.out.println(parameter + " -> " + esgotado);
				parameters.put(parameter, esgotado);
				
				parameter = baseParameterDe.concat("_resumopacotes_pacotemasculino").concat(iAsString);
				System.out.println(parameter + " -> " + esgotado);
				parameters.put(parameter, esgotado);
				
				parameter = baseParameterDe.concat("_resumopacotes_completofeminino").concat(iAsString);
				System.out.println(parameter + " -> " + esgotado);
				parameters.put(parameter, esgotado);
				
				parameter = baseParameterDe.concat("_resumopacotes_completomasculino").concat(iAsString);
				System.out.println(parameter + " -> " + esgotado);
				parameters.put(parameter, esgotado);
				
				query = "SELECT arquivo FROM pdf WHERE slug IN ('jr_"
						+ SLUG_DE
						+ "_ac_"
						+ slugProduto
						+ "_menu_esgotado');"; /*
						+ "_menu');"; */
				statement = connection.prepareStatement(query);
				ResultSet result2 = statement.executeQuery();
				
				while(result2.next()) {
					String arquivo = result2.getString("arquivo");
					parameter = baseParameterDe.concat("_menuacomodacoes_imagem").concat(iAsString);
					System.out.println(parameter + " -> " + arquivo);
					parameters.put(parameter, arquivo);
				}
				result2.close();			
				
				i++;
			}
			
			
			
			
			
			
			
			
			
			
			while(i <= MAX_RESULTS) {
				String iAsString = String.valueOf(i);
				
				String parameter = baseParameterDe.concat("_menuacomodacoes_imagem").concat(iAsString);
				System.out.println(parameter + " -> " + imageVazio);
				parameters.put(parameter, imageVazio);
				
				parameter = baseParameterDe.concat("_menuacomodacoes_link").concat(iAsString);
				System.out.println(parameter + " -> " + tagAcomodacoes);
				parameters.put(parameter, tagAcomodacoes);
				
				parameter = baseParameterDe.concat("_resumopacotes_pacoteac").concat(iAsString);
				System.out.println(parameter + " -> " + "");
				parameters.put(parameter, "");
				
				parameter = baseParameterDe.concat("_resumopacotes_pacoteaereo").concat(iAsString);
				System.out.println(parameter + " -> " + "");
				parameters.put(parameter, "");
				
				parameter = baseParameterDe.concat("_resumopacotes_pacotefeminino").concat(iAsString);
				System.out.println(parameter + " -> " + "");
				parameters.put(parameter, "");
				
				parameter = baseParameterDe.concat("_resumopacotes_pacotemasculino").concat(iAsString);
				System.out.println(parameter + " -> " + "");
				parameters.put(parameter, "");
				
				parameter = baseParameterDe.concat("_resumopacotes_completofeminino").concat(iAsString);
				System.out.println(parameter + " -> " + "");
				parameters.put(parameter, "");
				
				parameter = baseParameterDe.concat("_resumopacotes_completomasculino").concat(iAsString);
				System.out.println(parameter + " -> " + "");
				parameters.put(parameter, "");
				
				parameter = baseParameterDe.concat("_resumopacotes_link").concat(iAsString);
				System.out.println(parameter + " -> " + injoyLinkDESobre);
				parameters.put(parameter, injoyLinkDESobre);
				
				i++;
			}
			
		}
		
		
	}
	
	
	
	
	private static void producePDF(Connection connection, HashMap<String, Object> parameters, 
			String nomeArquivoPDF, ArrayList<String> listaArquivos) throws JRException {
		
		System.out.println("Iniciando a compilacao dos relatorios.");
		ArrayList<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		for(String arquivo: listaArquivos) {
			System.out.print("Processando: " + arquivo + "... ");
			String caminhoArquivoJasper = getReportFilePath(arquivo);
			JasperReport jasperReport = JasperCompileManager.compileReport(caminhoArquivoJasper);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
			jasperPrintList.add(jasperPrint);
			System.out.println("Processado.");
		}
		
		System.out.println("Configurando a exportacao.");
		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		configuration.setCreatingBatchModeBookmarks(true);
		String nomeArquivoFinal = getPDFFilePath(nomeArquivoPDF);
		SimpleOutputStreamExporterOutput eo = new SimpleOutputStreamExporterOutput(nomeArquivoFinal);
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setConfiguration(configuration);
		exporter.setExporterOutput(eo);
		exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
		System.out.println("Iniciando a producao do arquivo final...: " + nomeArquivoFinal);
		
		exporter.exportReport();
		
	}
	
	
	
	private static String getReportFilePath(String reportName) {
		return "report/".concat(reportName).concat(".jrxml");
	}
	
	private static String getPDFFilePath(String pdfName) {
		return "pdf/".concat(pdfName);
	}
	
	static public String get(String property) {
		if(inicializar()) {
			return jasperprops.getProperty(DEFAULT_WORKSPACE.concat(property));
		}
		return null;
	}
	
	static private boolean inicializar() {
		if(jasperprops != null) {
			return true;
		}
		try {
			FileInputStream fis = new FileInputStream(DEFAULT_FILENAME);
			if(jasperprops == null) {
				jasperprops = new Properties();
			}
			jasperprops.load(fis);
			fis.close();
		}
		catch (FileNotFoundException e) {
			jasperprops = null;
			e.printStackTrace();
		}
		catch (IOException e) {
			jasperprops = null;
			e.printStackTrace();
		}
		return (jasperprops != null);
	}

}
