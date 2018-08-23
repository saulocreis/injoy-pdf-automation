package injoy.jasper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

public class JasperPDFGenerator {
	
	static public void generate(Connection connection, HashMap<String, Object> parameters, String resultPDFPath, 
			String[] jasperFilePathList) throws JRException {
		
		System.out.println("Iniciando a compilacao dos relatorios.");
		ArrayList<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		for(String filePath: jasperFilePathList) {
			System.out.print("Processando: " + filePath + "... ");
			JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
			jasperPrintList.add(jasperPrint);
			System.out.println("Processado.");
		}
		
		System.out.println("Configurando a exportacao.");
		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		configuration.setCreatingBatchModeBookmarks(true);
		SimpleOutputStreamExporterOutput eo = new SimpleOutputStreamExporterOutput(resultPDFPath);
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setConfiguration(configuration);
		exporter.setExporterOutput(eo);
		exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
		System.out.println("Iniciando a producao do arquivo final...: " + resultPDFPath);
		
		exporter.exportReport();
	}
	
	static public void generate(Connection connection, HashMap<String, Object> parameters, String resultPDFPath, 
			String jasperFile) throws JRException {
		String[] jasperFilePathList = {jasperFile};
		generate(connection, parameters, resultPDFPath, jasperFilePathList);
	}

}
