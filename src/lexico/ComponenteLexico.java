package lexico;

public class ComponenteLexico {
	private String etiqueta;
	private String valor;

	
	public ComponenteLexico(String etiqueta) {
		this.etiqueta = etiqueta;
		this.valor = "";
	}
	public String getValor() {
		return this.valor;
	}
	
	public ComponenteLexico(String etiqueta, String valor) {
		this.etiqueta = etiqueta;
		this.valor = valor;
		
	}
	
	public String getEtiqueta() {
		return this.etiqueta;
		
	}
	public String toString() {
		if (this.valor.length() == 0)
			return "<" + this.etiqueta + ">";
		else
			return "<" + this.etiqueta + ", " + this.valor + ">";

	}	
	
}
