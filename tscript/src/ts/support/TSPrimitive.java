
package ts.support;

/**
 * The super class for all Tscript primitive values.
 */
public abstract class TSPrimitive extends TSValue
{
	
	public boolean isPrimitive()
	{
		return true;
	}
	
	 TSPrimitive toPrimitive()
	 {
		 return this;
	 }
}


