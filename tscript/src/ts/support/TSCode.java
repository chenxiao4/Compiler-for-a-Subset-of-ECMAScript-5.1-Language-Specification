package ts.support;

/*
 * TSCode interface
 */


public interface TSCode {
	
	public TSValue execute(boolean isConstrutorCall,
			TSValue ths,
			TSValue args[],
			TSLexicalEnvironment env);
}
