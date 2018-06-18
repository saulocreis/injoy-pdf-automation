package injoy.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import injoy.InjoyProperties;

public class Run {
	static private PrintStream ps = System.out;

	static public void main(String[] args) throws InvalidFormatException, IOException {
		
		String DIRECTORY = (InjoyProperties.isSet("directory") ? InjoyProperties.get("directory") : System.getProperties().getProperty("user.dir"));
		String XLSX_FILE_NAME = InjoyProperties.get("xlsxFile");
		String XLSX_SHEET = InjoyProperties.get("xlsxSheet");
		String PDF_FILE_NAME = InjoyProperties.get("pdfFile");
		String XLSX_FILEPATH = DIRECTORY.concat("\\").concat(XLSX_FILE_NAME);
		String PDF_FILEPATH = DIRECTORY.concat("\\").concat(PDF_FILE_NAME);
		
		FileInputStream xlsxFile = new FileInputStream(XLSX_FILEPATH);
		Workbook wb = WorkbookFactory.create(xlsxFile);
		Sheet arquivos = wb.getSheet(XLSX_SHEET);
		
		PDFMergerUtility merger = new PDFMergerUtility();
		merger.setDestinationFileName(PDF_FILEPATH);
		
		for(int numRow = arquivos.getFirstRowNum(); numRow <= arquivos.getLastRowNum(); numRow++) {
			Row row = arquivos.getRow(numRow);
			if(row == null) {
				continue;
			}
			for(int numCol = row.getFirstCellNum(); numCol < row.getLastCellNum(); numCol++) {
				Cell cell = row.getCell(numCol);
				String cellValue = cell.getStringCellValue();
				
				String filePath = cellValue./*replaceAll("\\\\", "\\\\\\\\").*/concat(".pdf");
				
				File pdfFile = new File(filePath);
				if(!pdfFile.exists()){
					continue;
				}
				
				ps.print("Incluindo arquivo: ");
				ps.println(filePath);
				
				merger.addSource(pdfFile);
			}
		}
		
		ps.print("Montando arquivo final: ");
		ps.println(PDF_FILEPATH);
		
		merger.mergeDocuments(null);
		wb.close();
		
		ps.print("Finalizado.");
	}

}
/*

CellRangeAddress cra = CellRangeAddress.valueOf(RANGE);
for(int numRow = cra.getFirstRow(); numRow <= cra.getLastRow(); numRow++) {
	for(int numCol = cra.getFirstColumn(); numCol <= cra.getLastColumn(); numCol++) {
		
		Row row = arquivos.get
		Cell cell = row.getCell(numCol);
		String cellValue = cell.getStringCellValue();
		if("".equals(cellValue)) {
			continue;
		}
		
		
		
		CellReference cellRef = new CellReference(numRow, numCol);
		sb.append(cellRef.formatAsString());
		sb.append(": ");
		sb.append(cellValue);
		ps.println(sb.toString());
	}
}


CellReference cellRef = new CellReference(numRow, numCol);
ps.print(cellRef.formatAsString());
ps.print(" - ");
ps.println(filePath);

*/
