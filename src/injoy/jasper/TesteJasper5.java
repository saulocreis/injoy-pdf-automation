package injoy.jasper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class TesteJasper5 {

	public static void main(String[] args) throws JRException, FileNotFoundException, IOException, SQLException {
		System.out.println("Iniciando...");

		JRDataSource dataSrc = new JREmptyDataSource();
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		// carneiros2019, carneiros2019_1_reveillon, carneiros2019_2_pq_injoy, carneiros2019_3_info_venda, carneiros2019_5_ancoradouro
		String nomeArquivo = "carneiros2019"; // report1, report2
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

}
