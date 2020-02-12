package lexico;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Lexico {

	private PalabrasReservadas palabras;
	private int posicion;
	private int lineas;
	private char caracter;
	private String programa;
	

	public Lexico(String programa) {
		this.posicion = 0;
		this.lineas = 1;
		this.palabras = new PalabrasReservadas("lexemas.txt");
		this.programa = programa + "#";
	}

	private static boolean existeFichero(String fichero) {
		File ficheroEntrada = new File (fichero);

		return ficheroEntrada.exists();
	}
	

	private static String contenidoFichero(String fichero, Charset codificacion) {
		String s = null;

		if(existeFichero(fichero)) {
			try {
				byte [] contenido = Files.readAllBytes(Paths.get(fichero));

				s = new String(contenido, codificacion);
			} catch (IOException e) { }
		}
		return s;
	}

	public Lexico(String ficheroEntrada, Charset codificacion) {
		this(contenidoFichero(ficheroEntrada, codificacion));
	}

	private char extraeCaracter() {
		return this.programa.charAt(this.posicion++);

	}

	public ComponenteLexico getComponenteLexico() {
		// el analizador lexico descarta los espacios (codigo 32), tabuladores (codigo 9) y saltos de linea (10 y 13)
		String etiqueta;
		while (true) {
			this.caracter = extraeCaracter();

			if(this.caracter == ' ' || (int) this.caracter == 9 || (int) this.caracter == 13)
				continue;
			else if((int) this.caracter == 47 ){
				char v = extraeCaracter();
				if ((int) v == 47) {
					while(this.extraeCaracter() != 10) {
						continue;
					}
				}else if((int) v == 42) {
					char q= ' ';
					v = extraeCaracter();
					while ( v!=47 || q != 42) {
							q = v;
							v = extraeCaracter();
							if(v == 10) this.lineas++;
							continue;
					}
				}else {
					String lexema = "";
					lexema = lexema + this.caracter;
					etiqueta = palabras.getEtiquetaLexica(lexema);
					devuelveCaracter();
					return new ComponenteLexico(etiqueta);
				}
				this.lineas++;
			}
			else if((int) this.caracter == 10)
				this.lineas++;
			else
				break;
		}

		// secuencias de digitos de numeros enteros o reales 

		if (Character.isDigit(this.caracter)) {	
			String numero = "" ;

			do { 
				numero = numero + this.caracter;

				this.caracter = extraeCaracter();
			} while (Character.isDigit(this.caracter));

			if(this.caracter != '.') {
				devuelveCaracter();

				return new ComponenteLexico("int", Integer.parseInt(numero) + "");
			}

			do {
				numero = numero + this.caracter;

				this.caracter = extraeCaracter();
			} while (Character.isDigit(this.caracter));

			devuelveCaracter();

			return new ComponenteLexico("float", Float.parseFloat(numero) + "");
		}


		// identifcadores y palabras reservadas
		if(this.caracter == 38) {
			char v = this.caracter;
			if(extraeCaracter() == 38) {
				String lexema = "";
				lexema = "" + v + this.caracter;
				etiqueta = palabras.getEtiquetaLexica(lexema);
				return new ComponenteLexico(etiqueta);
			}
		}
		
		if(this.caracter == 124) {
			char v = this.caracter;
			char w = extraeCaracter();
			if(w == 124) {
				String lexema = "";
				lexema =  ""+ v + w + "";
				etiqueta = palabras.getEtiquetaLexica(lexema);
				return new ComponenteLexico(etiqueta);
			}
		}

		if (Character.isLetter(this.caracter)) {
			String lexema = "";
//38 & y 124 |
			do {

				lexema = lexema + this.caracter;

				this.caracter = extraeCaracter();

			} while(Character.isLetterOrDigit(this.caracter) || this.caracter == '_');

			devuelveCaracter();
			
			etiqueta = palabras.getEtiquetaLexica(lexema);

			if (etiqueta == null)
				return new ComponenteLexico("id", lexema);
			else
				return new ComponenteLexico(etiqueta);

		}
		String lexema = "", lexemaAlternativo, etiquetaAlternativa;
		do {
			lexema = lexema + this.caracter;
			etiqueta = palabras.getEtiquetaLexica(lexema);
			
			if(etiqueta.equals("end_program"))
				return new ComponenteLexico(etiqueta);
			lexemaAlternativo = lexema;
			this.caracter = extraeCaracter();
			
			lexemaAlternativo = lexemaAlternativo + this.caracter;
			
			etiquetaAlternativa = palabras.getEtiquetaLexica(lexemaAlternativo);
			
			if(etiquetaAlternativa != null)
				etiqueta = etiquetaAlternativa;
		}while(etiquetaAlternativa != null);
		devuelveCaracter();
		return new ComponenteLexico(etiqueta);
	}


	private void devuelveCaracter() {
		this.posicion--;
	}

	public int getLineas() {
		return this.lineas;
	}
}