package ts.support;

public class TSPropertyReference extends TSReference {
	
	private TSValue base;
	
	public TSPropertyReference(final TSString name, TSValue baseValue)
	{
		super(name);
		base = baseValue;
	}
	

	boolean isPropertyReference() {
		
		return (base instanceof TSObject);
	}

	boolean isUnresolvableReference() {

		return base.isUndefined();
	}
	
	public TSValue getValue()
	{
		
		if (isUnresolvableReference())
			throw new TSException("Unresolvable Reference: "+this.getReferencedName().getInternal());
		
		return ((TSObject)base).getProperty(this.getReferencedName());
	}
	
	public void putValue(TSValue value)
	{
		if(isUnresolvableReference())
			TSGlobalObject.GLOBAL.putProperty(this.getReferencedName(), value);
		else
			((TSObject)base).putProperty(this.getReferencedName(), value);
	}
	
	
	

}
