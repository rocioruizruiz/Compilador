package compilador;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Vector;

import lexico.*;
import sintactico_semantico.*;



public class TestTraductor {

	//FUNCIONES DEL FICHERO

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
			} catch (IOException e){}
		}
		return s;
	}

	//MAIN

	public static void main(String[] args) throws FileNotFoundException {
		Scanner cin = new Scanner(System.in);
		

		if (existeFichero("lexemas.txt")) {}
		//System.out.println("El fichero lexemas.txt existe");
		else
			System.out.println("Error, no existe el fichero lexemas.txt");
		if (existeFichero("programa9.txt")) {
			String programa = contenidoFichero("programa9.txt", StandardCharsets.UTF_8);
			
			/*
			System.out.println("\n" + programa);
			
			boolean mostrarComponentesLexicos = true;
			Lexico lexico = new Lexico(programa);

			System.out.println("\n\nText Lexico \n");

			ComponenteLexico etiquetaLexica;

			//MOSTRAR TODOS LOS COMPONENTES LEXICOS PARA VERIFICAR SI RECONOCE TODOS LOS TOKENS

			int c = 0;
			if (mostrarComponentesLexicos) {

				do {
					etiquetaLexica = lexico.getComponenteLexico();

					System.out.println("<" + etiquetaLexica.toString() + ">");

					c++;

				} while (!etiquetaLexica.getEtiqueta().equals("end_program"));

				System.out.println("\nComponentes lexicos: " + c + ", lineas: " + lexico.getLineas());

			 */
			//AQUI EMPIEZA EL TRADUCTOR A OBEDECER SU GRAMATICA.
			Traductor compilador = new Traductor(new Lexico(programa));
			compilador.programa();
			if(compilador.getCorrect()) System.out.println("\nPrograma compilado correctamente\n");
			String tabla = compilador.getTablaSimbolos();
			System.out.println(tabla + "\n");

			if(compilador.getPrimitivos()) {
				
				System.out.println("¿Quieres generar el codigo intermedio? (si/no)");
				String dataIn = cin.nextLine(); 

				//----------------------------
				
				if(dataIn.equals("si")) {

					String idFichero = "/Users/rocioruizruiz/Documentos/Segundo/AmpliacionProgramacion/WorkspaceJava/Compilador/codigoGenerado.txt";
					Vector<String> myCode = compilador.getCodigo();
					PrintWriter ficheroSalida = new PrintWriter(idFichero);
					for (int i=0; i < myCode.size(); i++) { 
						ficheroSalida.print(myCode.elementAt(i)); 
					}
					ficheroSalida.close();
					File ficheroEntrada = new File (idFichero);
					if (ficheroEntrada.exists()) {
						Scanner datosFichero = new Scanner(ficheroEntrada); 
						System.out.println("Generacion de codigo: \n"); 
						while (datosFichero.hasNext()) {
							String [] numerosFichero = datosFichero.next().split(","); 
							for (int i=0; i < numerosFichero.length; i++)
								System.out.print(numerosFichero[i]); System.out.println("");
						}
						datosFichero.close(); 
					}
					else {
						
						System.out.println("¡El fichero no existe!");
					}
				}
			}
		}
	}
}

