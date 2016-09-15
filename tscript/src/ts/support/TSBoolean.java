
package ts.support;

/**
 * Represents (Tscript) Boolean values
 * (<a href="http://www.ecma-international.org/ecma-262/5.1/#sec-8.3">ELS
 * 8.3</a>).
 * <p>
 */
public class TSBoolean extends TSPrimitive{

	private final boolean value;
	public final static TSBoolean TRUE = new TSBoolean(true);
	public final static TSBoolean FALSE = new TSBoolean(false);
	
	// use the "create" method instead
	private TSBoolean(final boolean value)
	{
		this.value = value;
	}

	/** Get the value of the Boolean. */
	public boolean getInternal()
	{
		return value;
	}

	public static TSBoolean create(final boolean value)
	{
		return new TSBoolean(value);
	}

	public TSNumber toNumber()
	{
		return value? TSNumber.create(1.) : TSNumber.create(0.);
	}

	public TSString toStr()
	{
		return value? TSString.create("true") : TSString.create("false");
	}

	public TSBoolean toBoolean()
	{
		return this;
	}
}
