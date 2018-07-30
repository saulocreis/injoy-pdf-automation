package injoy.pdf;

import java.text.DateFormat;
import java.util.Date;

public class Run {

	static public void main(String[] args) {
		
		String s = DateFormat.getDateInstance().format(new Date());
		
		System.out.println(s);
	}

}