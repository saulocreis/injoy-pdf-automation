package injoy.pdf;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class Run {

	static public void main(String[] args) {
		
		String s = DateFormat.getDateInstance().format(new Date());
		
		Calendar calendar = Calendar.getInstance();
		String dia = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		String mesDia = String.valueOf(calendar.get(Calendar.MONTH)+1)+dia;
		String anoMesDia = String.valueOf(calendar.get(Calendar.YEAR))+mesDia;
		String segundo = String.valueOf(calendar.get(Calendar.SECOND));
		String minutoSegundo = String.valueOf(calendar.get(Calendar.MINUTE))+segundo;
		String horaMinutoSegundo = String.valueOf(calendar.get(Calendar.HOUR))+minutoSegundo;
		String data = anoMesDia+horaMinutoSegundo;
		
		System.out.println(data);
	}

}