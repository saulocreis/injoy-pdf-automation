package injoy.jasper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class TesteJeri2 {
	
	static private final int MAX_RESULTS = 16;
	static private final String DEFAULT_WORKSPACE = "injoy.";
	static private final String DEFAULT_FILENAME = "src/jasperprops.properties";
	static private final String CONEXAO = "jdbc:mariadb://localhost:3306/injoy?user=root";
	static private final String SEP = System.getProperty("file.separator");
	static private final String SLUG_DE = "jeri2019";
	static private final String IMG_DIR = "images".concat(SEP).concat(SLUG_DE).concat(SEP);
	
	static private Properties jasperprops;

	public static void main(String[] args) throws JRException, FileNotFoundException, IOException, SQLException {
		
		String[] listaArquivos = {/*
				"jeri2019_inicio", 
				"jeri2019_0_ij", "jeri2019_0_menu", 
				"jeri2019_1_reveillon",
				"jeri2019_2_pqinjoy",
				"jeri2019_3_infovenda", */
				"jeri2019_4_acomodacoes", /*
				"jeri2019_5_ac_pousada-cabana_capa_fotos",
				"jeri2019_5_ac_hotel-jeri_capa_fotos",
				"jeri2019_5_ac_pousada-do-norte_capa_fotos",
				"jeri2019_5_ac_jeri-village-hotel_capa_fotos",
				"jeri2019_5_ac_pousada-capitao-thomaz_capa_fotos",
				"jeri2019_5_ac_vila-bijupira_capa_fotos",
				"jeri2019_5_ac_pousada-carcara_capa_fotos",
				"jeri2019_5_ac_pousada-naquela-jericoacoara_capa_fotos",
				"jeri2019_5_ac_pousada-kanaloa_capa_fotos", 
				"jeri2019_final" */ };
		
		System.out.println("Tentando conectar...");
		Connection connection = DriverManager.getConnection(CONEXAO);
		PreparedStatement statement = null;
		String query;
		System.out.println("Conectado.");
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("sep", SEP);
		parameters.put("img_dir", IMG_DIR);
		parameters.put("slug_de", SLUG_DE);
		
		
		query = "SELECT" + 
				"(SELECT arquivo FROM pdf WHERE slug IN ('jr_jeri2019_capa')" + 
				") AS jr_jeri2019_capa," + 
				"(SELECT arquivo FROM pdf WHERE slug IN ('jr_jeri2019_quemsomos')" + 
				") AS jr_jeri2019_quemsomos," + 
				"(SELECT arquivo FROM pdf WHERE slug IN ('jr_jeri2019_ijreveillon')" + 
				") AS jr_jeri2019_ijreveillon," + 
				"(SELECT arquivo FROM pdf WHERE slug IN ('jr_jeri2019_menu')" + 
				") AS jr_jeri2019_menu," + 
				"(SELECT arquivo FROM pdf WHERE slug IN ('jr_jeri2019_reveillon')" + 
				") AS jr_jeri2019_reveillon," + 
				"(SELECT arquivo FROM pdf WHERE slug IN ('jr_jeri2019_pqinjoy')" + 
				") AS jr_jeri2019_pqinjoy," + 
				"(SELECT arquivo FROM pdf WHERE slug IN ('jr_jeri2019_infovenda')" + 
				") AS jr_jeri2019_infovenda," + 
				"(SELECT arquivo FROM pdf WHERE slug IN ('jr_jeri2019_festas')" + 
				") AS jr_jeri2019_festas," + 
				"(SELECT arquivo FROM pdf WHERE slug IN ('jr_jeri2019_final')" + 
				") AS jr_jeri2019_final" + 
				";";
		
		statement = connection.prepareStatement(query);
		//statement.setString(1, "jr_jeri2019_capa");
		ResultSet result = statement.executeQuery();
		if(result.next()) {
			parameters.put("jr_jeri2019_capa", result.getString("jr_jeri2019_capa"));
			parameters.put("jr_jeri2019_quemsomos", result.getString("jr_jeri2019_quemsomos"));
			parameters.put("jr_jeri2019_ijreveillon", result.getString("jr_jeri2019_ijreveillon"));
			parameters.put("jr_jeri2019_menu", result.getString("jr_jeri2019_menu"));
			parameters.put("jr_jeri2019_reveillon", result.getString("jr_jeri2019_reveillon"));
			parameters.put("jr_jeri2019_pqinjoy", result.getString("jr_jeri2019_pqinjoy"));
			parameters.put("jr_jeri2019_infovenda", result.getString("jr_jeri2019_infovenda"));
			parameters.put("jr_jeri2019_festas", result.getString("jr_jeri2019_festas"));
			parameters.put("jr_jeri2019_final", result.getString("jr_jeri2019_final"));
		}
		result.close();
		

		
		query = "SELECT subprod.slug as slugSubprod," + 
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
			String iAsString = String.valueOf(i);
			String slugSubprod = result.getString("slugSubprod");
			String menorValorPessoa = result.getString("menorValorPessoa");
			String parameter = "jr_".concat(SLUG_DE).concat("_menuacomodacoes_link").concat(iAsString);
			System.out.println(parameter + " -> " + slugSubprod);
			parameters.put(parameter, slugSubprod);
			parameter = "jr_".concat(SLUG_DE).concat("_ac_").concat(slugSubprod).concat("_menorValorPessoa");
			System.out.println(parameter + " -> " + menorValorPessoa);
			parameters.put(parameter, menorValorPessoa);
			
			query = "SELECT arquivo FROM pdf WHERE slug IN ('jr_"
					+ SLUG_DE
					+ "_ac_"
					+ slugSubprod
					+ "_menu');";
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
		result.close();
		
		if(i <= MAX_RESULTS) {
			
			query = "SELECT subprod.slug as slugSubprod" + 
					"	FROM acomodacao_quarto aq, produto subprod, produto_subproduto ps, produto prod, produto_tipo pt WHERE " + 
					"	aq.idProduto = subprod.id AND ps.idSubproduto = subprod.id AND ps.idProduto = prod.id AND " + 
					"   ps.idProduto_Tipo = pt.id AND aq.estoque <= 0 AND " + 
					"	ps.idProduto IN (" + 
					"		SELECT id FROM produto WHERE " + /*
					"			idProduto_Status IN (" + 
					"				SELECT id FROM produto_status WHERE nome IN ('Normal')" + 
					"			) AND " + */
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
				String slugSubprod = result.getString("slugSubprod");
				String parameter = "jr_".concat(SLUG_DE).concat("_menuacomodacoes_link").concat(iAsString);
				System.out.println(parameter + " -> " + slugSubprod);
				parameters.put(parameter, slugSubprod);
				
				query = "SELECT arquivo FROM pdf WHERE slug IN ('jr_"
						+ SLUG_DE
						+ "_ac_"
						+ slugSubprod /*
						+ "_menu_esgotado');"; */
						+ "_menu');";
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
		}
		

		
		
		
		

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
		String nomeArquivoFinal = getPDFFilePath(get("file.jeri2019"));
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
