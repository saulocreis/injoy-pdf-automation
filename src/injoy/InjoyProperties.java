package injoy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class InjoyProperties {
	
	static private final String DEFAULT_WORKSPACE = "injoy.";
	static private final String DEFAULT_FILENAME = "propriedades.properties";
	
	static private Properties propriedades;
	static private boolean inicializado = false;
	
	static public boolean isSet(String property) {
		return get(property) != null;
	}
	
	static public String get(String property) {
		if(inicializar()) {
			return propriedades.getProperty(DEFAULT_WORKSPACE.concat(property));
		}
		return null;
	}
	
	static private boolean inicializar() {
		
		if(inicializado) return true;
		
		try {
			
			FileInputStream fis = new FileInputStream(DEFAULT_FILENAME);

			if(propriedades == null) {
				propriedades = new Properties();
			}
			
			propriedades.load(fis);
			fis.close();
		}
		catch (FileNotFoundException e) {
			propriedades = null;
			inicializado = false;
			e.printStackTrace();
		}
		catch (IOException e) {
			propriedades = null;
			inicializado = false;
			e.printStackTrace();
		}
		
		inicializado = (propriedades != null);
		return inicializado;
	}

}
