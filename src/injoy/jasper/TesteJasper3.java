package injoy.jasper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import injoy.data.DadoFactory;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

public class TesteJasper3 {

	public static void main(String[] args) throws JRException, FileNotFoundException, IOException {
		System.out.println("Iniciando...");
		
		DadoFactory d = DadoFactory.instance();
		d.addDado("ancoradouro.apartirde", "R$ 10.000");
		
		JRMapArrayDataSource dataSrc = new JRMapArrayDataSource(d.getArray());

		HashMap<String, Object> params = new HashMap<String, Object>();
		
		// carneiros2019, carneiros2019_1_reveillon, carneiros2019_2_pq_injoy, carneiros2019_3_info_venda, carneiros2019_5_ancoradouro
		String nomeArquivo = "carneiros2019_5_ancoradouro"; 
		String jasperReportFilePath = getReportFilePath(nomeArquivo);
		String pdfFilePath = getPDFFilePath(nomeArquivo);

		JasperReport jasperReport = JasperCompileManager.compileReport(jasperReportFilePath);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSrc);
		
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
