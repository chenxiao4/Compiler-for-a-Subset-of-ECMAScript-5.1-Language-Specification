
package ts.support;

/**
 * Represents (Tscript) String values
 * (<a href="http://www.ecma-international.org/ecma-262/5.1/#sec-8.4">ELS
 * 8.4</a>).
 * <p>
 * This class only currently allows String values to be created and does
 * not yet support operations on them.
 */
public final class TSString extends TSPrimitive
{

  private final String value;

  // use the "create" method instead
  private TSString(final String value)
  {
    this.value = value;
  }

  /** Get the value of the String. */
  public String getInternal()
  {
    return value;
  }

  /** Overrides Object.equals because TSString used as key for Map */
  public boolean equals(Object anObject)
  {
    if (anObject instanceof TSString)
    {
      return value.equals(((TSString) anObject).getInternal());
    }
    return false;
  }

  /** Need to override Object.hashcode() when overriding Object.equals() */
  public int hashCode()
  {
    return value.hashCode();
  }

  /** Create a Tscript String from a Java String. */
  public static TSString create(final String value)
  {
    // could use hashmap to screen for common strings?
    return new TSString(value);
  }

  /** Convert String to Number. Not yet Implemented. */
  public TSNumber toNumber()
  {
    // System.out.println("Not implemented");
    //assert false : "not implemented";
    //remove whitespace
    //
    String tmp = value.replace(" ","");
    tmp.replace("\\n","");
    tmp.replace("\\r","");
  
    double ret = Double.NaN;

    if (tmp.toLowerCase().startsWith("0x") && tmp.length() > 2){

       try {
            ret = (double)Long.parseLong(tmp.substring(2), 16);
           }
       catch (NumberFormatException ept){
            //System.out.println("can convert TSString to TSNumber");
            ;
           }
    } else {
  
       try {
           ret = Double.parseDouble(tmp);
           }
       catch (NumberFormatException ept) {
           //System.out.println("can convert TSString to TSNumber"); 
           ;
           }
    }

    return TSNumber.create(ret);
  }


  public TSBoolean toBoolean()
  {
    
    return (value.length() > 0)? TSBoolean.TRUE : TSBoolean.FALSE;
		
  }

  /** Override in TSValue */
  public TSString toStr()
  {
    return this;
  }
}

