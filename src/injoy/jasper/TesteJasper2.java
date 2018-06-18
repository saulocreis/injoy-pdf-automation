package injoy.jasper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class TesteJasper2 {

	public static void main(String[] args) throws JRException, FileNotFoundException, IOException {
		
		// BarbecueReport, exemplo, report1, report2, carneiros2019, carneiros2019-reveillon
		ArrayList<String> listaArquivos = new ArrayList<String>();
		listaArquivos.add("teste1-carneiros2019");
		listaArquivos.add("teste1-carneiros2019_1_reveillon");
		listaArquivos.add("carneiros2019_2_pq_injoy");
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		JREmptyDataSource empty = new JREmptyDataSource();
		
		for(String arquivo: listaArquivos) {
			
			System.out.println("Processando: ".concat(arquivo));
			
			String jasperReportFilePath = getReportFilePath(arquivo);
			String pdfFilePath = getPDFFilePath(arquivo);
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperReportFilePath);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, empty);
			JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFilePath);
		}
		
		System.out.println("Criando pdf final...");
		
		PDFMergerUtility merger = new PDFMergerUtility();
		merger.setDestinationFileName(getPDFFilePath("FINAL"));
		
		for(String arquivo: listaArquivos) {
			String pdfFilePath = getPDFFilePath(arquivo);
			merger.addSource(new File(pdfFilePath));
		}
		merger.mergeDocuments(null);
		
		System.out.println("Terminado.");
		
		//JasperViewer.viewReport(jasperPrint);
	}

	private static String getReportFilePath(String reportName) {
		return "report/".concat(reportName).concat(".jrxml");
	}
	
	private static String getPDFFilePath(String pdfName) {
		return "pdf/".concat(pdfName).concat(".pdf");
	}
	
	private static String getJasperFilePath(String jpgName) {
		return "jasper/".concat(jpgName).concat(".jasper");
	}

}
