package ts.support;

import ts.Message;

import java.util.Map;
import java.util.HashMap;


/*
 * TSObject
 */
public class TSObject extends TSValue
{
	protected final Map<TSString,TSValue> property;
	protected TSObject prototype = null;


	protected TSObject(TSObject prototype)
	{
		property = new HashMap<TSString,TSValue>();
		this.prototype = prototype;
	}


	public static TSObject create()
	{
		return new TSObject(null);
	}


	public static TSObject create(TSObject prototype)
	{
		return new TSObject(prototype);
	}


	public void putProperty(TSString name, TSValue value)
	{
		//put value for prototype
		if(name.isEqual(TSString.create("prototype")).getInternal()){
			if(value instanceof TSObject){
				prototype = (TSObject)value;
			}else{
				//make sure you give a TSObject to prototype
				Message.fatal("You are giving the internal prototype a non-TSObject value!!!");
			}
		} else
			property.put(name, value);	
	}


	public boolean hasPrototype()
	{	
		return prototype != null;
	}


	public boolean hasProperty(TSString name)
	{
		//search in current property first
		if(property.containsKey(name))
			return true;

		//if not, search through the chain
		if(hasPrototype())
			return prototype.hasProperty(name);
		else
			//return false if it is not in the chain
			return false;
	}


	public boolean hasOwnProperty(TSString name)
	{
		return property.containsKey(name);
	}


	public TSValue getProperty(TSString name)
	{
		//this method is a recursion
		if(name.isEqual(TSString.create("prototype")).getInternal()){
			if(hasPrototype())
				return prototype;
			else
				return null;
		}

		if(property.containsKey(name))
			return property.get(name);

		if(hasPrototype())
			return prototype.getProperty(name);
		else
			return TSUndefined.value;
	}


	public boolean deleteProperty(TSString name)
	{	
		//may need a boolean flag throw
		//delete own property
		return property.remove(name) != null;
	}


	//with hint number
	private TSValue getDefaultValue(final int hint){

		//hint 0: string
		//hint 1: number


		if (hint == 0){

			TSValue toString = this.getProperty(TSString.create("toString")).getValue();
			if (toString.isCallable())
			{
				TSValue str = ((TSFunctionObject)toString).call(this,null);
				if(str.isPrimitive())
					return str;
			}

			TSValue valueOf = this.getProperty(TSString.create("valueOf"));
			if (valueOf.isCallable())
			{
				TSValue val = ((TSFunctionObject)valueOf).call(this,null).getValue();
				if (val.isPrimitive())
					return val;
			}
		}//string first

		if(hint == 1){
			TSValue valueOf = this.getProperty(TSString.create("valueOf"));
			if (valueOf.isCallable())
			{
				TSValue val = ((TSFunctionObject)valueOf).call(this,null).getValue();
				if (val.isPrimitive())
					return val;
			}

			TSValue toString = this.getProperty(TSString.create("toString")).getValue();
			if (toString.isCallable())
			{
				TSValue str = ((TSFunctionObject)toString).call(this,null);
				if(str.isPrimitive())
					return str;
			}

		}//number first

		throw new TSException("TypeError: Object has no default value");

		//return TSUndefined.value;
	}


	//type conversion below	
	TSPrimitive toPrimitive()
	{
		return getDefaultValue(1).toPrimitive();	
	}

	public TSString toStr() 
	{
		return getDefaultValue(0).toStr();
	}

	public TSNumber toNumber() {

		return getDefaultValue(1).toNumber();
	}

	public TSBoolean toBoolean() {

		return TSBoolean.TRUE;
	}

}