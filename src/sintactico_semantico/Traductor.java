package sintactico_semantico;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import lexico.*;


public class Traductor {

	private ComponenteLexico componenteLexico;
	private Lexico lexico;
	private Hashtable<String,String> simbolos; //key, value
	private boolean correct;
	private boolean primitivos;
	private Vector<String> myCode;
	private int label;

	public Traductor(Lexico lexico) {
		this.lexico = lexico;
		this.componenteLexico = this.lexico.getComponenteLexico();
		this.simbolos = new Hashtable<String, String>();
		this.correct = true;
		this.primitivos = true;
		this.myCode = new Vector<String>();
		this.label = -1;
	}
	
	public Vector<String> getCodigo() {
		return this.myCode;
	}
	public boolean getCorrect() {
		return this.correct;
	}
	public boolean getPrimitivos() {
		return this.primitivos;
	}

	public void programa() {
		if(this.componenteLexico.getEtiqueta().equals("void")){
			compara("void");
			if(this.componenteLexico.getEtiqueta().equals("main")) {
				compara("main");
				if(this.componenteLexico.getEtiqueta().equals("open_bracket")) {
					compara("open_bracket");
					declaraciones();
					instrucciones();
					compara("closed_bracket");
					String code = "\nhalt";
					myCode.add(code);
					//el end program se pone para que puedas hacer el ultimo compara sin que haya outOfBounds
				}
			}
		}
	}

	private void declaraciones() {
		if(this.componenteLexico.getEtiqueta().equals("int") || this.componenteLexico.getEtiqueta().equals("float")|| this.componenteLexico.getEtiqueta().equals("boolean")) {
			declaracion_variable();
			declaraciones();
		}
	}

	private void compara(String etiquetaLexica) {

		if(this.componenteLexico.getEtiqueta().equals(etiquetaLexica)) {
			this.componenteLexico = lexico.getComponenteLexico();
		}else {
			System.out.println("ERROR. Se esperaba " + etiquetaLexica + "en la linea " + this.lexico.getLineas());
			this.correct = false;
		}

	}

	public String getTablaSimbolos() {
		String simb = "";

		Set<Map.Entry<String, String>> s = this.simbolos.entrySet();
		if(simbolos.isEmpty()) System.out.println("La tabla de simbolos esta vacia\n");
		for(Map.Entry<String, String> m : s) {
			simb = simb + "<'" + m.getKey() + "', " +
					m.getValue() + "> \n";
		}

		return simb;
	}
	
	private void declaracion_variable() {
		String tipo = tipo(); // te l devuelve  porq es tributo heredado. necesitas el tipo y el id toghether
		if(tipo != "null") {
			String id;
			if(tipo.length() > 7) { // es vector. un poco cutre la forma de hacerlo but...
				if(this.componenteLexico.getEtiqueta().equals("id")) {
					id = this.componenteLexico.getValor();
					
					compara("id");
					compara("semicolon");
					if(id != null && (!simbolos.containsKey(id))) { // comparo que el identificador no es null, y que no existe ya, para insertarlo en la tabla.
						simbolos.put(id, tipo);
					}
				}
			}else{
				lista_identificadores(tipo); //le pasas tipo para que se herede el tipo de dato al id y asi poder declararlo he insertarlo en la Hash
				compara("semicolon");
			}
		}
	}
	
	private String tipo() {
		String tipo;
		if(this.componenteLexico.getEtiqueta().equals("int")){
			tipo = "int";
			compara("int");
			int tamañoVector = tipoOpcional(); // nos aportaria el tamño del vector si es que es vector.
			if(tamañoVector != -1) { //ES VECTOR
				primitivos = false;
				String subtipo = "array(" + tipo + ", " + tamañoVector + ")";
				return subtipo;
			}else { //NO ES VECTOR
				return tipo;
			}
		}
		if(this.componenteLexico.getEtiqueta().equals("float")){
			tipo = "float";
			compara("float");
			int tamañoVector = tipoOpcional(); // nos aportaria el tamño del vector si es que es vector.
			if(tamañoVector != -1) { //ES VECTOR
				String subtipo = "array(" + tipo + ", " + tamañoVector + ")";
				return subtipo;
			}else { //NO ES VECTOR
				return tipo;
			}
		}
		if(this.componenteLexico.getEtiqueta().equals("boolean")){
			tipo = "boolean";
			compara("boolean");
			int tamañoVector = tipoOpcional(); // nos aportaria el tamño del vector si es que es vector.
			if(tamañoVector != -1) { //ES VECTOR
				String subtipo = "array(" + tipo + ", " + tamañoVector + ")";
				return subtipo;
			}else { //NO ES VECTOR
				return tipo;
			}
		}
		System.out.println("Tipo de dato expected here.");
		return "null";
	}
	private int tipoOpcional() {
		if(this.componenteLexico.getEtiqueta().equals("open_square_bracket")) { // es vector
			compara("open_square_bracket");
			int tamañoVector = Integer.parseInt(this.componenteLexico.getValor());
			compara("int");
			compara("closed_square_bracket");
			return tamañoVector;
		}else {
			//no es vector
			return -1;
		}
	}
	
	private void lista_identificadores(String tipo) {
		String id = this.componenteLexico.getValor();
		String code = "\nlvalue_" + id;
		myCode.add(code);
		compara("id");
		if(id != "null" && simbolos.containsKey(id)) {
			int lineas = this.lexico.getLineas();
			this.correct = false;
			System.out.println("Error en la linea " + lineas + ", identificador '" + id + "' ya declarado");
		}
		if(id != "null" && (!simbolos.containsKey(id))) { // comparo que el identificador no es null, y que no existe ya, para insertarlo en la tabla.
			simbolos.put(id, tipo);
		}
		asignacion_declaracion(id);
		mas_identificadores(tipo);
		
	}

	private void mas_identificadores(String tipo) {
		if(this.componenteLexico.getEtiqueta().equals("comma")) {
			compara("comma");
			String id = this.componenteLexico.getValor();
			String code = "\nlvalue_" + id;
			myCode.add(code);
			compara("id");
			if(id != null && (!simbolos.containsKey(id))) { // comparo que el identificador no es null, y que no existe ya, para insertarlo en la tabla.
				simbolos.put(id, tipo);
			}
			asignacion_declaracion(tipo);
			mas_identificadores(tipo);
		}
		//Epsilon
	}



	private void asignacion_declaracion(String id) { // tipo como parametro para la verificacion
		if(this.componenteLexico.getEtiqueta().equals("assignment")) {
			//aqui se realiza la verificacion de tipos
			
			compara("assignment");
			String tipo2 = expresion_logica();
			String code = "\n= ";
			myCode.add(code);
			if(simbolos.containsKey(id)) {
				if(simbolos.get(id)!= tipo2) { //mira el tipo de la variable en la tabla y t verifica la asignacion (tambn esta verificando q este en la tabla :v)
				System.out.println("Error de incompatibilidad de tipos en la instruccion de asignacion");
				this.correct = false;
				}
			}
		}
		else {
			myCode.remove(myCode.lastElement());
		}
		//sino epsilon, es decir no se le asigna ningun valor al id.
	}

	private String expresion_logica() {
		String tipo = termino_logico();
		String masTipos = mas_termino_logico();
		if(masTipos != "null") {
			if(tipo != masTipos){
				System.out.println("Error de incompatibilidad de tipos");
				this.correct = false;
			}
		}
		return tipo;
	}

	private String termino_logico() {
		String tipo = factor_logico();
		String masTipos = mas_factor_logico();
		if(masTipos != "null") {
			if(tipo != masTipos){
				System.out.println("Error de incompatibilidad de tipos");
				this.correct = false;
			}
		}
		return tipo;
	}

	private String mas_termino_logico() {
		if(this.componenteLexico.getEtiqueta().equals("or")){
			compara("or");
			String tipo = termino_logico();
			String masTipos = mas_termino_logico();
			if(masTipos != "null") {
				if(tipo != masTipos){
					System.out.println("Error de incompatibilidad de tipos");
					this.correct = false;
				}
			}
			return tipo;
		}else {
			return "null";
		}
	}

	private String mas_factor_logico() {
		if(this.componenteLexico.getEtiqueta().equals("and")) {
			compara("and");
			String tipo = factor_logico();
			String masTipos = mas_factor_logico();
			if(masTipos != "null") {
				if(tipo != masTipos){
					System.out.println("Error de incompatibilidad de tipos");
					this.correct = false;
				}
			}
			return tipo;
		}else {
			return "null";
		}
	}

	public String factor_logico() {
		//Si expresion
		String tipo = "";
		if(this.componenteLexico.getEtiqueta().equals("not")) {
			compara("not");
			tipo = factor_logico();
			return tipo;
			//Si num
		}else if(this.componenteLexico.getEtiqueta().equals("true")) {
			tipo = "boolean";
			compara("true");
			return tipo;
			//Si variable
		}else if(this.componenteLexico.getEtiqueta().equals("false")) {
			tipo = "boolean";
			compara("false");
			return tipo;
		}else{ // no hay condicion porque np hay epsilon, si no es las anteriores tiene que ser esta.
			tipo = expresion_relacional();
			return tipo;
		}
	}

	private String expresion_relacional() {
		String tipo = expresion();
		String masTipos = operacion_relacional_opcional();
		if(masTipos.equals("null")) {
			return tipo;
		}else if(tipo == masTipos) {
			return tipo;
		}else {
			System.out.println("Error en la verificacion de datos(expresion_relacional)");
			this.correct = false;
			return "null";
		}
	}

	private String operacion_relacional_opcional() {
		if(this.componenteLexico.getEtiqueta().equals("less_than") || this.componenteLexico.getEtiqueta().equals("greater_than") ||this.componenteLexico.getEtiqueta().equals("not_equals") || this.componenteLexico.getEtiqueta().equals("greater_equals") || this.componenteLexico.getEtiqueta().equals("less_equals") ||this.componenteLexico.getEtiqueta().equals("equals")){
			String op = operador_relacional();
			String tipo = expresion();
			myCode.add(op);
			return tipo;
		}
		//Si no epsilon
		return "null";
	}

	private String operador_relacional(){
		String code = "";
		if(this.componenteLexico.getEtiqueta().equals("less_than")) {
			compara("less_than");
		}else if(this.componenteLexico.getEtiqueta().equals("greater_than")) {
			code = "\n>";
			compara("greater_than");
			//Si variable
		}else if(this.componenteLexico.getEtiqueta().equals("greater_equals")) {
			code = "\n>=";
			compara("greater_equals");
		}else if(this.componenteLexico.getEtiqueta().equals("less_equals")) {
			code = "\n<=";
			compara("less_equals");
		}else if(this.componenteLexico.getEtiqueta().equals("equals")) {
			code = "\n==";
			compara("equals");
		}else if(this.componenteLexico.getEtiqueta().equals("not_equals")) {
			code = "\n!=";
			compara("not_equals");
		}
		return code;
	}

	private String expresion() {
		String tipo = termino();
		String masTipos = masTerminos();
		if(masTipos != "null") {
			if(tipo != masTipos){
				System.out.println("Error de incompatibilidad de tipos");
				this.correct = false;
			}
		}
		return tipo;
	}
	
	public String factor() {
		//Si expresion
		String tipo = "";
		if(this.componenteLexico.getEtiqueta().equals("open_parenthesis")) {
			compara("open_parenthesis");
			tipo = expresion();
			compara("closed_parenthesis");
			return tipo;
			//Si num
		}else if(this.componenteLexico.getEtiqueta().equals("int") || this.componenteLexico.getEtiqueta().equals("float")) {
			//System.out.print(this.componenteLexico.getValor() + " ");
			String code = "\npush_" + this.componenteLexico.getValor();
			myCode.add(code);
			if(this.componenteLexico.getEtiqueta().equals("int")){
				tipo = "int";
				compara("int");
				return tipo;
			}else if(this.componenteLexico.getEtiqueta().equals("float")){
				tipo = "float";
				compara("float");
				return tipo;
			}
			System.out.println("Si llegaa aqui es porq no es ni int ni float :o, sera boolean?");
			//Si variable
		}else if(this.componenteLexico.getEtiqueta().equals("id")) {
			String code = "\nrvalue_" + this.componenteLexico.getValor();
			myCode.add(code);
			tipo = variable();
			return tipo;
		}
		return "null";
	}
	
	public String termino() {
		String tipo = factor();
		String masTipos = masFactores();
		if(masTipos != "null") {
			if(tipo != masTipos){
				System.out.println("Error de incompatibilidad de tipos");
				this.correct = false;
			}
		}
		return tipo;
	}

	private String masTerminos() {
		if(this.componenteLexico.getEtiqueta().equals("add")){
			compara("add");
			String tipo = termino();
			String code = "\n+";
			myCode.add(code);
			String masTipos = masTerminos();
			if(masTipos != "null") {
				if(tipo != masTipos){
					System.out.println("Error de incompatibilidad de tipos");
					this.correct = false;
				}
			}
			return tipo;
		}else if(this.componenteLexico.getEtiqueta().equals("subtract")) {
			compara("subtract");
			String tipo = termino();
			String code = "\n-";
			myCode.add(code);
			String masTipos = masTerminos();
			if(masTipos != "null") {
				if(tipo != masTipos){
					System.out.println("Error de incompatibilidad de tipos");
					this.correct = false;
				}
			}
			return tipo;
		}
		return "null";
	}
	private String masFactores() {
		if(this.componenteLexico.getEtiqueta().equals("multiply")){
			compara("multiply");
			String tipo = factor();
			String code = "\n*";
			myCode.add(code);
			String masTipos = masFactores();
			if(masTipos != "null") {
				if(tipo != masTipos){
					System.out.println("Error de incompatibilidad de tipos");
					this.correct = false;
				}
			}
			return tipo;
		}else if(this.componenteLexico.getEtiqueta().equals("divide")) {
			compara("divide");
			String tipo = factor();
			String code = "\n/";
			myCode.add(code);
			String masTipos = masFactores();
			if(masTipos != "null") {
				if(tipo != masTipos){
					System.out.println("Error de incompatibilidad de tipos");
					this.correct = false;
				}
			}
			return tipo;
		}else if(this.componenteLexico.getEtiqueta().equals("remainder")) {
			compara("remainder");
			String tipo = factor();
			String code = "\n%";
			myCode.add(code);

			String masTipos = masFactores();
			if(masTipos != "null") {
				if(tipo != masTipos){
					System.out.println("Error de incompatibilidad de tipos");
					this.correct = false;
				}
			}
			return tipo;
		}
		return "null";
	}
	
	private String variable() {
		String id = this.componenteLexico.getValor();
		String tipo = "null";
		if(simbolos.containsKey(id)) {
			tipo = simbolos.get(id);
			if(tipo.contains("array")) {
				if(tipo.contains("int")) {
					tipo = "int";
				}else if(tipo.contains("float")){
					tipo = "float";
				}else if(tipo.contains("boolean")){
					tipo = "boolean";
				}
			}
		}else {
			int lineas = this.lexico.getLineas();
			System.out.println("Error en la linea " + lineas + ", identificador '" + id + "' no declarado");
			this.correct = false;
		}
		compara("id");
		expresion_opcional();
		return tipo;
	}
	
	private void expresion_opcional() {
		if(this.componenteLexico.getEtiqueta().equals("open_square_bracket")){
			compara("open_square_bracket");
			expresion();
			compara("closed_square_bracket");
		}
		//sino Epsilon
	}
//----------INSTRUCCIONES--------------------	
	private void instrucciones(){
		if(this.componenteLexico.getEtiqueta().equals("id") || this.componenteLexico.getEtiqueta().equals("int") || this.componenteLexico.getEtiqueta().equals("float") || 
			this.componenteLexico.getEtiqueta().equals("boolean") || this.componenteLexico.getEtiqueta().equals("if") || this.componenteLexico.getEtiqueta().equals("while") || 
			this.componenteLexico.getEtiqueta().equals("do") || this.componenteLexico.getEtiqueta().equals("print") || this.componenteLexico.getEtiqueta().equals("open_bracket"))
		{
			instruccion();
			instrucciones();
		}
		//sino Epsilon y cn suerte acaba el programa.
	}

	private void instruccion(){
		//caso variable = expresion_logica ;
		if(this.componenteLexico.getEtiqueta().equals("id")){
			String code = "\nlvalue_" + this.componenteLexico.getValor();
			myCode.add(code);
			String tipo = variable();
			
			compara("assignment");
			String tipo2 = expresion_logica();
			code = "\n= ";
			myCode.add(code);
			compara("semicolon"); 
			if(tipo != "null" && tipo != tipo2) {
				int lineas = this.lexico.getLineas() - 1; //doy por hecho que ddespues del semicolon siempre hay salto de linea, pero prefiero eso a lanzar este error antes del compara
				System.out.println("Error en la linea " + lineas +  ", incompatibilidad de tipos en la instruccion de asignacion");
				this.correct = false;
			}// podria haber un else para el compara, pero de momento no lo pongo para que no se atasque el programa si hay una mala verificacion de tipos.
			
		//caso while
		}else if(this.componenteLexico.getEtiqueta().equals("while")) {
			compara("while");
			int test = etiqueta();
			String code = "\nlabel_" + test + ":"; //bn
			myCode.add(code);
			compara("open_parenthesis");
			String tipo = expresion_logica();
			//podria hacer un else para el compara del closedparenthesis pero de momento no que se me atasca el problema.
			compara("closed_parenthesis");
			int out = etiqueta();
			code = "\ngofalse_label" + out; //bn
			myCode.add(code);
			instruccion(); 
			code = "\ngoto_label_" + test;
			myCode.add(code);
			code = "\nlabel_" + out + ":";
			myCode.add(code);
			
		//caso do while
		}else if(this.componenteLexico.getEtiqueta().equals("do")) {
			compara("do");
			int test = etiqueta();
			String code = "\nlabel_" + test + ":";
			myCode.add(code);
			instruccion();
			compara("while");
			compara("open_parenthesis");
			String tipo = expresion_logica();
			compara("closed_parenthesis");
			int out = etiqueta();
			code = "\ngofalse_label_" + out;
			myCode.add(code);
			code = "\ngoto_label_" + test;
			myCode.add(code);
			code = "\nlabel_" + out + ":";
			myCode.add(code);
			compara("semicolon");
			
			
		//caso declaracion de variables
		}else if(this.componenteLexico.getEtiqueta().equals("int") || this.componenteLexico.getEtiqueta().equals("float") || this.componenteLexico.getEtiqueta().equals("boolean")) {
			declaracion_variable();
			
		//caso print	
		}else if(this.componenteLexico.getEtiqueta().equals("print")) {
			compara("print");
			compara("open_parenthesis");
			String code = "\nprint_" + this.componenteLexico.getValor();
			myCode.add(code);
			String tipo = variable();
			if(tipo == "null" || tipo == "") {
				System.out.println("variable no previamente declarada(instruccion)");
				this.correct = false;
			}//podria poner else pero no quiero que se pare el prog
			compara("closed_parenthesis");
			compara("semicolon");
		//caso if (elseopcional)
		}else if(this.componenteLexico.getEtiqueta().equals("if")) {
			compara("if");
			compara("open_parenthesis");
			String tipo = expresion_logica();
			compara("closed_parenthesis");
			int els = etiqueta();
			String code = "\ngofalse_label_" + els;
			myCode.add(code);
			instruccion();
			else_opcional(els);
		//caso { instrucciones }
		}else if(this.componenteLexico.getEtiqueta().equals("open_bracket")) {
			compara("open_bracket");
			instrucciones();
			compara("closed_bracket");
		}		
	}

	private void else_opcional(int els) {
			int out = etiqueta();
			String code = "\ngoto_label_" + out; //bn
			myCode.add(code);
			code = "\nlabel_" + els + ":"; //bn
			myCode.add(code);
		if(this.componenteLexico.getEtiqueta().equals("else")) {
			compara("else");
			instruccion();
			code = "\nlabel_" + out + ":"; //bn
			myCode.add(code);
		}
	}
	public int etiqueta() {
		label++;
		return this.label;
	}
}

