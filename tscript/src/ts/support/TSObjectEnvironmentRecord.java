package ts.support;


public class TSObjectEnvironmentRecord extends TSEnvironmentRecord {
	
	private final TSObject bindingObject;
	private final boolean provideThis;
	
	
	TSObjectEnvironmentRecord(final TSObject bind, final boolean ths)
	{
		bindingObject  = bind;
		provideThis = ths;
	}


	boolean hasBinding(TSString name) {
		
		return bindingObject.hasProperty(name);
	}


	void createMutableBinding(TSString name, boolean isDeletable) {
		
		assert (bindingObject.hasProperty(name) == false) : "binding already exists";
		
		//what is idDeletabe for, figure it out later
		bindingObject.putProperty(name, TSUndefined.value);
	}


	void setMutableBinding(TSString name, TSValue value) {
		//always mutable
		
		bindingObject.putProperty(name, value);
	}


	TSValue getBindingValue(TSString name) {
		
		return bindingObject.getProperty(name);
	}


	
	TSBoolean deleteBinding(TSString name) {
		
		boolean ret = bindingObject.deleteProperty(name);
		
		if(ret)
			return TSBoolean.TRUE;
		else
			return TSBoolean.FALSE;
	}


	TSValue implicitThisValue() {
		
		if(provideThis)
			return bindingObject;
		else
			return TSUndefined.value;
	}
	
	
}
