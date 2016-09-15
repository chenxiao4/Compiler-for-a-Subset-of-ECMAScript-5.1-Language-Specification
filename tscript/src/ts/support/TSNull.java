
package ts.support;

/**
 * Represents (Tscript) Null values
 * (<a href="http://www.ecma-international.org/ecma-262/5.1/#sec-8.2">ELS
 * 8.2</a>).
 * <p>
 */
public class TSNull extends TSPrimitive {

	private final Object value;
	public final static TSNull NULL = new TSNull(null);
	
	// use the "create" method instead
	private TSNull(final Object value)
	{
		this.value = value;
	}

	/** Get the value of the Boolean. */
	public Object getInternal()
	{
		return value;
	}

	public static TSNull create(final Object value)
	{
		return new TSNull(value);
	}

	public TSNumber toNumber()
	{
		return TSNumber.create(+0);
	}

	public TSString toStr()
	{
		return TSString.create("null");
	}

	public TSBoolean toBoolean()
	{
		return TSBoolean.FALSE;
	}
	
}
