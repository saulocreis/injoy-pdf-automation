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

public class TesteJeri {
	
	static private final int MAX_RESULTS = 16;
	static private final String DEFAULT_WORKSPACE = "injoy.";
	static private final String DEFAULT_FILENAME = "src/jasperprops.properties";
	static private final String CONEXAO = "jdbc:mariadb://localhost:3306/injoy?user=root";
	static private final String SEP = System.getProperty("file.separator");
	static private final String SEP2 = "/"; 
	static private final String SLUG_DE = "jeri2019";
	static private final String SLUG_EXPERIENCIA = "festas-john-john-rocks-jeri-2019";
	static private final String SLUG_AEREO_NORMAL = "aereo-fretado-1-reveillon-jeri-2019";
	static private final String SLUG_AEREO_COMBO_FEMININO = "combo-aereo-pacote-feminino-5-festas-jeri-2019";
	static private final String SLUG_AEREO_COMBO_MASCULINO = "combo-aereo-pacote-masculino-5-festas-jeri-2019";
	static private final String IMG_DIR = "images".concat(SEP).concat(SLUG_DE).concat(SEP);
	
	static private Properties jasperprops;

	public static void main(String[] args) throws JRException, FileNotFoundException, IOException, SQLException {
		/*
		String[] arrayArquivos = {
				"jeri2019_inicio", 
				"jeri2019_0_ij", "jeri2019_0_menu", 
				"jeri2019_1_reveillon",
				"jeri2019_2_pqinjoy",
				"jeri2019_3_infovenda", 
				"jeri2019_4_acomodacoes", "jeri2019_4_resumopacotes", 
				"jeri2019_5_ac_pousada-cabana_capa_fotos", 
				"jeri2019_5_ac_hotel-jeri_capa_fotos", 
				"jeri2019_5_ac_pousada-do-norte_capa_fotos", 
				"jeri2019_5_ac_jeri-village-hotel_capa_fotos", 
				"jeri2019_5_ac_pousada-capitao-thomaz_capa_fotos", 
				"jeri2019_5_ac_vila-bijupira_capa_fotos",
				"jeri2019_5_ac_pousada-carcara_capa_fotos",
				"jeri2019_5_ac_pousada-naquela-jericoacoara_capa_fotos",
				"jeri2019_final"  };
		*/
		
		ArrayList<String> listaArquivos = new ArrayList<String>();
		listaArquivos.add("jeri2019_inicio");
		listaArquivos.add("jeri2019_0_ij");
		listaArquivos.add("jeri2019_0_menu");
		listaArquivos.add("jeri2019_1_reveillon");
		listaArquivos.add("jeri2019_2_pqinjoy");
		listaArquivos.add("jeri2019_3_infovenda");
		listaArquivos.add("jeri2019_4_acomodacoes");
		listaArquivos.add("jeri2019_4_resumopacotes");
		
		System.out.println("Tentando conectar...");
		Connection connection = DriverManager.getConnection(CONEXAO);
		System.out.println("Conectado.");
		
		PreparedStatement statement = null;
		String query;
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("sep", SEP);
		parameters.put("img_dir", IMG_DIR);
		parameters.put("slug_de", SLUG_DE);
		
		String tagAcomodacoes = get("tag.acomodacoes");
		String imageVazio = get("image.vazio");
		String nomeArquivoPDF = get("file.".concat(SLUG_DE));
		String injoyLinkDESobre = get("link").concat(SLUG_DE).concat(SEP2).concat("sobre").concat(SEP2);
		
		//DecimalFormat formatoComCentavosComCifra = new DecimalFormat("#,##0.00");
		DecimalFormat formatoSemCentavosSemCifra = new DecimalFormat("R$ #,##0");
		
		String capa = "capa";
		String quemsomos = "quemsomos";
		String ijreveillon = "ijreveillon";
		String menu = "menu";
		String reveillon = "reveillon";
		String pqinjoy = "pqinjoy";
		String infovenda = "infovenda";
		String menuacomodacoes = "menuacomodacoes";
		String resumopacotes = "resumopacotes";
		String festas = "festas";
		String finalString = "final";

		query = "SELECT" + 
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS " + capa + "," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS " + quemsomos + "," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS " + ijreveillon + "," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS " + menu + "," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS " + reveillon + "," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS " + pqinjoy + "," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS " + infovenda + "," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS " + menuacomodacoes + "," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS " + resumopacotes + "," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS " + festas + "," +
				"(SELECT arquivo FROM pdf WHERE slug IN (?)" + 
				") AS " + finalString +
				";";
		statement = connection.prepareStatement(query);
		
		String pBase = "jr_" + SLUG_DE + "_";
		String parameterCapa = pBase + capa;
		String parameterQuemSomos = pBase + quemsomos;
		String parameterIJReveillon = pBase + ijreveillon;
		String parameterMenu = pBase + menu;
		String parameterReveillon = pBase + reveillon;
		String parameterPqInjoy = pBase + pqinjoy;
		String parameterInfovenda = pBase + infovenda;
		String parameterMenuAcomodacoes = pBase + menuacomodacoes;
		String parameterResumoPacotes = pBase + resumopacotes;
		String parameterFestas = pBase + festas;
		String parameterFinal = pBase + finalString;
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
		
		System.out.println(statement);
		
		//statement.setString(1, "jr_jeri2019_capa");
		ResultSet result = statement.executeQuery();
		if(result.next()) {
			parameters.put(parameterCapa, result.getString(capa));
			parameters.put(parameterQuemSomos, result.getString(quemsomos));
			parameters.put(parameterIJReveillon, result.getString(ijreveillon));
			parameters.put(parameterMenu, result.getString(menu));
			System.out.println(parameterReveillon + " -> " + result.getString(reveillon));
			parameters.put(parameterReveillon, result.getString(reveillon));
			parameters.put(parameterPqInjoy, result.getString(pqinjoy));
			parameters.put(parameterInfovenda, result.getString(infovenda));
			parameters.put(parameterMenuAcomodacoes, result.getString(menuacomodacoes));
			parameters.put(parameterResumoPacotes, result.getString(resumopacotes));
			parameters.put(parameterFestas, result.getString(festas));
			parameters.put(parameterFinal, result.getString(finalString));
		}
		result.close();
		

// jeri2019_4_resumopacotes
		query = "SELECT (\r\n" + 
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
				"	) AS valorExperienciaMasculino" + 
				";";

		statement = connection.prepareStatement(query);
		statement.setString(1, SLUG_EXPERIENCIA);
		statement.setString(2, SLUG_EXPERIENCIA);
		result = statement.executeQuery();
		
		String valorExperienciaFemininoAsString = "0", valorExperienciaMasculinoAsString = "0";
		int valorExperienciaFeminino = 0, valorExperienciaMasculino = 0;
		while(result.next()) {
			valorExperienciaFemininoAsString = result.getString("valorExperienciaFeminino");
			valorExperienciaFeminino = Integer.parseInt(valorExperienciaFemininoAsString);
			valorExperienciaFemininoAsString = formatoSemCentavosSemCifra.format(valorExperienciaFeminino);
			valorExperienciaMasculinoAsString = result.getString("valorExperienciaMasculino");
			valorExperienciaMasculino = Integer.parseInt(valorExperienciaMasculinoAsString);
			valorExperienciaMasculinoAsString = formatoSemCentavosSemCifra.format(valorExperienciaMasculino);
		}
		result.close();
		
		


// jeri2019_4_acomodacoes, jeri2019_4_resumopacotes
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

		int i = 1;
		while(result.next() && i <= MAX_RESULTS) {
			String parameter;
			String iAsString = String.valueOf(i);
			String slugPacote = result.getString("slugPacote");
			String nomeProduto = result.getString("nomeProduto");
			String slugProduto = result.getString("slugProduto");
			
			// jeri2019_5_ac_<slugProduto>_capa_fotos, jeri2019_5_ac_<slugProduto>_precos
			String nomeArquivoCapaFotos = "jeri2019_5_ac_".concat(slugProduto).concat("_capa_fotos");
			String nomeArquivoPrecos = "jeri2019_5_ac_".concat(slugProduto).concat("_precos");
			listaArquivos.add(nomeArquivoCapaFotos);
			//listaArquivos.add(nomeArquivoPrecos);
			
			String menorValorPessoaAsString = result.getString("menorValorPessoa");
			int menorValorPessoa = Integer.parseInt(menorValorPessoaAsString);
			menorValorPessoaAsString = formatoSemCentavosSemCifra.format(menorValorPessoa);
			
			parameter = "jr_".concat(SLUG_DE).concat("_menuacomodacoes_link").concat(iAsString);
			System.out.println(parameter + " -> " + slugProduto);
			parameters.put(parameter, slugProduto);
			
			parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_nome").concat(iAsString);
			System.out.println(parameter + " -> " + nomeProduto);
			parameters.put(parameter, nomeProduto.toUpperCase());
			
			parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_pacoteac").concat(iAsString);
			System.out.println(parameter + " -> " + menorValorPessoaAsString);
			parameters.put(parameter, menorValorPessoaAsString);
			
			parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_pacotefeminino").concat(iAsString);
			int menorValorPessoaFeminino = menorValorPessoa + valorExperienciaFeminino;
			String menorValorPessoaFemininoAsString = formatoSemCentavosSemCifra.format(menorValorPessoaFeminino);
			System.out.println(parameter + " -> " + menorValorPessoaFemininoAsString);
			parameters.put(parameter, menorValorPessoaFemininoAsString);
			
			parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_pacotemasculino").concat(iAsString);
			int menorValorPessoaMasculino = menorValorPessoa + valorExperienciaMasculino;
			String menorValorPessoaMasculinoAsString = formatoSemCentavosSemCifra.format(menorValorPessoaMasculino);
			System.out.println(parameter + " -> " + menorValorPessoaMasculinoAsString);
			parameters.put(parameter, menorValorPessoaMasculinoAsString);
			
			parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_link").concat(iAsString);
			String linkPacote = injoyLinkDESobre.concat(slugPacote);
			System.out.println(parameter + " -> " + linkPacote);
			parameters.put(parameter, linkPacote);
			
			parameter = "jr_".concat(SLUG_DE).concat("_ac_").concat(slugProduto).concat("_anchor");
			System.out.println(parameter + " -> " + slugProduto);
			parameters.put(parameter, slugProduto);
			
			parameter = "jr_".concat(SLUG_DE).concat("_ac_").concat(slugProduto).concat("_menorValorPessoa");
			System.out.println(parameter + " -> " + menorValorPessoaAsString);
			parameters.put(parameter, menorValorPessoaAsString);
			
			String fotos1 = "fotos1";
			String fotos2 = "fotos2";
			String precos = "precos";
			
			query = "SELECT ( " + 
					"	SELECT arquivo FROM pdf " + 
					"	WHERE slug IN (?) " + 
					") AS " + capa + ", ( " + 
					"	SELECT arquivo FROM pdf " + 
					"	WHERE slug IN (?) " + 
					") AS " + fotos1 + ", ( " +
					"	SELECT arquivo FROM pdf " + 
					"	WHERE slug IN (?) " + 
					") AS " + fotos2 + ", ( " +
					"	SELECT arquivo FROM pdf " + 
					"	WHERE slug IN (?) " + 
					") AS " + menu + ", ( " +
					"	SELECT arquivo FROM pdf " + 
					"	WHERE slug IN (?) " + 
					") AS " + precos +
					";";
			statement = connection.prepareStatement(query);
			
			pBase = "jr_" + SLUG_DE + "_ac_" + slugProduto + "_";
			parameterCapa = pBase + capa;
			String parameterFotos1 = pBase + fotos1;
			String parameterFotos2 = pBase + fotos2;
			parameterMenu = pBase + menu;
			String parameterPrecos = pBase + precos;
			statement.setString(1, parameterCapa);
			statement.setString(2, parameterFotos1);
			statement.setString(3, parameterFotos2);
			statement.setString(4, parameterMenu);
			statement.setString(5, parameterPrecos);
			ResultSet result2 = statement.executeQuery();
			
			while(result2.next()) {
				String arquivoMenu = result2.getString("menu");
				parameter = "jr_".concat(SLUG_DE).concat("_menuacomodacoes_imagem").concat(iAsString);
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
			}
			result2.close();
			
			i++;
		}
		result.close();
		
		if(i <= MAX_RESULTS) {
			
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
				String esgotado = "ESGOTADO";
				
				String parameter = "jr_".concat(SLUG_DE).concat("_menuacomodacoes_link").concat(iAsString);
				System.out.println(parameter + " -> " + slugProduto);
				parameters.put(parameter, slugProduto);
				
				parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_nome").concat(iAsString);
				System.out.println(parameter + " -> " + nomeProduto);
				parameters.put(parameter, nomeProduto.toUpperCase());
				
				parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_pacoteac").concat(iAsString);
				System.out.println(parameter + " -> " + esgotado);
				parameters.put(parameter, esgotado);
				
				parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_pacotefeminino").concat(iAsString);
				System.out.println(parameter + " -> " + esgotado);
				parameters.put(parameter, esgotado);
				
				parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_pacotemasculino").concat(iAsString);
				parameters.put(parameter, esgotado);
				
				/*
				parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_link").concat(iAsString);
				String linkPacote = injoyLinkDESobre.concat(slugPacote);
				System.out.println(parameter + " -> " + linkPacote);
				parameters.put(parameter, linkPacote);
				*/				
				
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
					parameter = "jr_".concat(SLUG_DE).concat("_menuacomodacoes_imagem").concat(iAsString);
					System.out.println(parameter + " -> " + arquivo);
					parameters.put(parameter, arquivo);
				}
				result2.close();			
				
				i++;
			}
			
			while(i <= MAX_RESULTS) {
				String iAsString = String.valueOf(i);
				
				String parameter = "jr_".concat(SLUG_DE).concat("_menuacomodacoes_imagem").concat(iAsString);
				System.out.println(parameter + " -> " + imageVazio);
				parameters.put(parameter, imageVazio);
				
				parameter = "jr_".concat(SLUG_DE).concat("_menuacomodacoes_link").concat(iAsString);
				System.out.println(parameter + " -> " + tagAcomodacoes);
				parameters.put(parameter, tagAcomodacoes);
				
				parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_pacoteac").concat(iAsString);
				System.out.println(parameter + " -> " + "");
				parameters.put(parameter, "");
				
				parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_pacotefeminino").concat(iAsString);
				System.out.println(parameter + " -> " + "");
				parameters.put(parameter, "");
				
				parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_pacotemasculino").concat(iAsString);
				parameters.put(parameter, "");
				
				parameter = "jr_".concat(SLUG_DE).concat("_resumopacotes_link").concat(iAsString);
				System.out.println(parameter + " -> " + injoyLinkDESobre);
				parameters.put(parameter, injoyLinkDESobre);
				
				i++;
			}
			
		}
		
		listaArquivos.add("jeri2019_final");
		
		/* */
		System.out.println("Iniciando a compilacao dos relatorios.");
		ArrayList<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		for(String arquivo: listaArquivos) {
			System.out.println("Processando: ".concat(arquivo).concat("..."));
			String caminhoArquivoJasper = getReportFilePath(arquivo);
			JasperReport jasperReport = JasperCompileManager.compileReport(caminhoArquivoJasper);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
			jasperPrintList.add(jasperPrint);
			System.out.println("Processado ".concat(arquivo).concat("."));
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
		System.out.println("Iniciando a producao do arquivo final...: ".concat(nomeArquivoFinal));
		
		exporter.exportReport();
		System.out.println("Finalizado.");
		/* */
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
