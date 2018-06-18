package injoy.jasper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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

public class TesteJasper {
	
	static private final String DEFAULT_WORKSPACE = "injoy.";
	static private final String DEFAULT_FILENAME = "src/jasperprops.properties";
	private static final String CONEXAO = "jdbc:mariadb://localhost:3306/injoy?user=root";
	static private Properties jasperprops;

	public static void main(String[] args) throws JRException, FileNotFoundException, IOException, SQLException {
		
		String[] listaArquivos = {
				"carneiros2019",
				"carneiros2019_0_ij", "carneiros2019_0_menu",
				"carneiros2019_1_reveillon",
				"carneiros2019_2_pq_injoy",
				"carneiros2019_3_info_venda",
				"carneiros2019_4_acomodacoes",
				"carneiros2019_5_ancoradouro_capa", "carneiros2019_5_ancoradouro_fotos", "carneiros2019_5_ancoradouro_precos",
				"carneiros2019_6_clubmeridional_capa", "carneiros2019_6_clubmeridional_fotos", "carneiros2019_6_clubmeridional_precos",
				"carneiros2019_7_sitioprainha_capa", "carneiros2019_7_sitioprainha_fotos", "carneiros2019_7_sitioprainha_precos"};
		
		System.out.println("Tentando conectar...");
		Connection connection = DriverManager.getConnection(CONEXAO);
		System.out.println("Conectado.");
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
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
		String nomeArquivoFinal = getPDFFilePath(get("carneiros.finalFileName"));
		SimpleOutputStreamExporterOutput eo = new SimpleOutputStreamExporterOutput(nomeArquivoFinal);
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setConfiguration(configuration);
		exporter.setExporterOutput(eo);
		exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
		System.out.println("Iniciando a producao do arquivo final...: ".concat(nomeArquivoFinal));
		
		exporter.exportReport();
		System.out.println("Finalizado.");
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
