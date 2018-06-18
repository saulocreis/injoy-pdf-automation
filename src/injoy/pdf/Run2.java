package injoy.pdf;

import java.io.IOException;

public class Run2 {

	static public void main(String[] args) throws IOException, InterruptedException {
		
		//ProcessBuilder pb = new ProcessBuilder("htmldoc", "htmldoc/index.html", "-f", "pdf/index.pdf").inheritIO();
		ProcessBuilder pb = new ProcessBuilder("htmldoc", "htmldoc/molde.html", "--landscape", "--no-toc", "-f", "pdf/molde.pdf").inheritIO();
		
		Process p = pb.start();
		
		int exit = p.waitFor();
		
		System.exit(exit);
	}

}
/*



*/
