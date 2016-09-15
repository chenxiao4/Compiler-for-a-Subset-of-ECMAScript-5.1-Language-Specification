package ts.support;

import ts.Message;
import ts.tree.*;
import ts.tree.visit.*;

import java.util.List;
import java.util.ArrayList;

public final class TSFunctionObject extends TSObject{

	private TSCode code;
	private TSLexicalEnvironment scope;


	private TSFunctionObject(final TSCode code,
			final TSLexicalEnvironment scope)
	{
		super(null);
		this.code = code;
		this.scope = scope;
		
	}

	public static TSFunctionObject create(final TSCode code,
			final TSLexicalEnvironment scope)
	{
		return new TSFunctionObject(code, scope);
	}


	public TSCode getFunctionTSCode()
	{
		return code;
	}
	
	public TSValue call(boolean isConstrutorCall, TSValue ths, TSValue args[]){
		
		return code.execute(isConstrutorCall, ths, args, scope);
		
	}
	
	
	public TSValue call(TSValue ths, TSValue args[]){
		
		return code.execute(false, ths, args, scope);
		
	}
	
	
	public TSValue call(TSValue args[]){
		
		return code.execute(false, TSUndefined.value, args, scope);
		
	}
	
	
	
	//spec 13.2.2[[construct]]
	public TSObject construct(TSValue args[]){
		
		TSObject obj = TSObject.create();
		
		if(this.hasPrototype())
			obj.putProperty(TSString.create("prototype"), this.getProperty(TSString.create("prototype")));
		
		TSValue ret = this.call(obj,args);
		if (ret instanceof TSObject)
			return (TSObject)ret;
		
		return obj;
	}
	
	
	
	public TSString toStr() 
	{
		return TSString.create(this.getClass().getName());
	}

	public boolean isCallable(){
		return true;
	}
	
}
