package injoy.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test {
	
	private static final int REPORT_DAY = 4;

	public static void main(String[] args) {
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/YYYY");
		
		Calendar calendar = Calendar.getInstance();		
		calendar.add(Calendar.DAY_OF_YEAR, REPORT_DAY - calendar.get(Calendar.DAY_OF_WEEK));
		Date dateCalendar = calendar.getTime();
		println(formatador.format(dateCalendar));
		println(calendar.get(Calendar.DAY_OF_WEEK));

	}
	
	private static void println(Object o) {
		System.out.println(o);
	}
	
	private static void print(Object o) {
		System.out.print(o);
	}

}
