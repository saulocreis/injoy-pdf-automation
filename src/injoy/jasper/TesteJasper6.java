package injoy.jasper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class TesteJasper6 {
	
	private static final String CONEXAO = "jdbc:mariadb://localhost:3306/injoy?user=root";

	public static void main(String[] args) throws JRException, FileNotFoundException, IOException, SQLException {
		
		/* report1
		 * carneiros2019
		 * carneiros2019_0_ij, carneiros2019_0_menu
		 * carneiros2019_1_reveillon
		 * carneiros2019_2_pq_injoy
		 * carneiros2019_3_info_venda
		 * carneiros2019_4_acomodacoes
		 * carneiros2019_5_ancoradouro_capa, carneiros2019_5_ancoradouro_fotos, carneiros2019_5_ancoradouro_precos
		 * carneiros2019_6_clubmeridional_capa, carneiros2019_6_clubmeridional_fotos, carneiros2019_6_clubmeridional_precos
		 * carneiros2019_7_sitioprainha_capa, carneiros2019_7_sitioprainha_fotos, carneiros2019_7_sitioprainha_precos
		 */
		String nomeArquivo = "report1"; 
		
		System.out.println("Tentando conectar...");
		Connection cn = DriverManager.getConnection(CONEXAO);
		
		/*
		Statement stmt = cn.createStatement();
		ResultSet rs = stmt.executeQuery(BUSCA_SQL);
		stmt.close();
		
		JRResultSetDataSource dataSrc = new JRResultSetDataSource(rs);
		*/
		System.out.println("Conectado.");
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		String jasperReportFilePath = getReportFilePath(nomeArquivo);
		String pdfFilePath = getPDFFilePath(nomeArquivo);

		System.out.println("Produzindo relatorio...");
		
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperReportFilePath);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, cn);
		
		JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFilePath);
		
		System.out.println("Terminado.");
	}
	
	private static String getReportFilePath(String reportName) {
		return "report/".concat(reportName).concat(".jrxml");
	}
	
	private static String getPDFFilePath(String pdfName) {
		return "pdf/".concat(pdfName).concat(".pdf");
	}
	
	public static String getJPGFilePath(String jpgName) {
		return "images/".concat(jpgName).concat(".jpg");
	}

}
