package lexico;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class PalabrasReservadas {
	/*
	 * La clase PalabrasREservadas almacena los pares <lexema, etiqueta-lexica(token)> del lenguaje
	 */
	
	private Hashtable<String, String> lexemas = new Hashtable<String, String>();
	
	public PalabrasReservadas(String lexemas) {
		leeLexemas(lexemas);
	}

	public String getEtiquetaLexica(String lexema) {
		return this.lexemas.get(lexema);
	}
	public String getLexema(String etiquetaLexica) {
		String lexema = null;
		
		Set<Map.Entry<String, String>> s = this.lexemas.entrySet();
		
		for(Map.Entry<String,String> m : s)
			if(m.getValue().contentEquals(etiquetaLexica)) {
				lexema = m.getKey();
				break;
			}
		return lexema;
	}
	private static boolean existeFichero(String fichero) {
		File ficheroEntrada = new File (fichero);

		return ficheroEntrada.exists();
	}
	private static String etiqueta(String s) {
		return s.substring(0, s.indexOf("\t")).trim();
	}
	private static String lexema(String s) {
		return s.substring(s.lastIndexOf("\t")+1).trim();
	}
	public void leeLexemas(String lexemas) {
		if(existeFichero(lexemas)) {
			try {
				Scanner fichero = new Scanner(new File(lexemas), "UTF-8");
				
				String parEtiquetaLexema, lexema, etiqueta;
				
				while(fichero.hasNext()) {
					parEtiquetaLexema = fichero.nextLine();
					if(parEtiquetaLexema.length() > 0 && parEtiquetaLexema.charAt(0) != '[' && parEtiquetaLexema.charAt(0) != '/') {
						lexema = lexema(parEtiquetaLexema);
						etiqueta = etiqueta(parEtiquetaLexema);
						
						this.lexemas.put(lexema, etiqueta);
					}
				}
				fichero.close();
			}catch(IOException e){}
		}
	}
}

