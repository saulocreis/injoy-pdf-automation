package injoy.jasper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import net.sf.jasperreports.engine.JRException;

public class TesteJasper4 {

	public static void main(String[] args) throws JRException, FileNotFoundException, IOException {
		
		System.out.println("Iniciando...");
		
		// BarbecueReport, exemplo, report1, report2, carneiros2019, carneiros2019-reveillon
		
		PDFMergerUtility merger = new PDFMergerUtility();
		merger.setDestinationFileName(getPDFFilePath("FINAL"));
		/*
		HashMap<String, Object> params = new HashMap<String, Object>();
		JREmptyDataSource empty = new JREmptyDataSource();
		*/
		System.out.println("Processando: ".concat("carneiros2019"));
		// String jasperReportFilePath = getReportFilePath("teste1-carneiros2019");
		String pdfFilePath = getPDFFilePath("carneiros2019");/*
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperReportFilePath);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, empty);
		JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFilePath);*/
		merger.addSource(new File(pdfFilePath));
		
		System.out.println("Processando: ".concat("carneiros2019_1_reveillon"));
		// jasperReportFilePath = getReportFilePath("teste1-carneiros2019_1_reveillon");
		pdfFilePath = getPDFFilePath("carneiros2019_1_reveillon");/*
		jasperReport = JasperCompileManager.compileReport(jasperReportFilePath);
		jasperPrint = JasperFillManager.fillReport(jasperReport, params, empty);
		JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFilePath);*/
		merger.addSource(new File(pdfFilePath));
		
		System.out.println("Processando: ".concat("carneiros2019_2_pq_injoy"));
		// jasperReportFilePath = getReportFilePath("carneiros2019_2_pq_injoy");
		pdfFilePath = getPDFFilePath("carneiros2019_2_pq_injoy");/*
		jasperReport = JasperCompileManager.compileReport(jasperReportFilePath);
		jasperPrint = JasperFillManager.fillReport(jasperReport, params, empty);
		JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFilePath);*/
		merger.addSource(new File(pdfFilePath));
		
		System.out.println("Processando: ".concat("carneiros2019_3_info_venda"));
		// jasperReportFilePath = getReportFilePath("carneiros2019_2_pq_injoy");
		pdfFilePath = getPDFFilePath("carneiros2019_3_info_venda");/*
		jasperReport = JasperCompileManager.compileReport(jasperReportFilePath);
		jasperPrint = JasperFillManager.fillReport(jasperReport, params, empty);
		JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFilePath);*/
		merger.addSource(new File(pdfFilePath));
		

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
