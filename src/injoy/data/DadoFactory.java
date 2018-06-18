package injoy.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DadoFactory {
	private static DadoFactory instance = new DadoFactory();
	
	private ArrayList<Map<String, Object>> lista;
	
	public static DadoFactory instance() {
		return instance;
	}
	
	public ArrayList<Map<String, Object>> getLista(){
		return lista;
	}
	
	public Object[] getArray() {
		return lista.toArray();
	}
	
	public void addDado(String key, String value) {
		int position = lista.size();
		addDado(key, value, position);
	}
	
	public void addDado(String key, String value, int position) {
		if(lista == null) {
			lista = new ArrayList<Map<String, Object>>();
		}
		if(position < 0) return;
		if(position >= lista.size()) {
			HashMap<String, Object> obj = new HashMap<String, Object>();
			lista.add(obj);
		}
		else {
			Map<String, Object> obj = lista.get(position);
			obj.put(key, value);
		}
	}
	
	private DadoFactory() {
		lista = new ArrayList<Map<String, Object>>();
	}
}
