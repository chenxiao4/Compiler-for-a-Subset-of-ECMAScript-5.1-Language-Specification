package ts.support;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import ts.Message;

public class TSGlobalObject extends TSObject{


	public static TSGlobalObject GLOBAL = create();
	private static BufferedReader in;

	private TSGlobalObject()
	{
		super(null);	
	}


	public static TSGlobalObject create()
	{
		TSGlobalObject ret = new TSGlobalObject();
		in = new BufferedReader(new InputStreamReader(System.in));

		//install NaN Infinity undefined
		ret.putProperty(TSString.create("NaN"), TSNumber.create(Double.NaN));
		ret.putProperty(TSString.create("Infinity"), TSNumber.create(Double.POSITIVE_INFINITY));
		ret.putProperty(TSString.create("undefined"), TSUndefined.value);
		ret.putProperty(TSString.create("isNaN"), TSFunctionObject.create(ret.new GisNaN(),null));
		ret.putProperty(TSString.create("isFinite"), TSFunctionObject.create(ret.new GisFinite(),null));
		ret.putProperty(TSString.create("readln"), TSFunctionObject.create(ret.new Greadln(),null));
		ret.putProperty(TSString.create("split"), TSFunctionObject.create(ret.new Gsplit(),null));
		ret.putProperty(TSString.create("trim"), TSFunctionObject.create(ret.new Gtrim(),null));
		ret.putProperty(TSString.create("length"), TSFunctionObject.create(ret.new Glength(),null));

		//this part is for testThis
		//put "printXYZ" property
		TSObject printobj = TSObject.create();
		printobj.putProperty(TSString.create("printXYZ"), TSFunctionObject.create(ret.new GprintXYZ(), null));	
		//craete testThis Function Object
		TSObject test = TSFunctionObject.create(ret.new GtestThis(), null);
		//put property "xyz"
		//test.putProperty(TSString.create("xyz"),TSNumber.create(42));
		//put prototype 
		test.putProperty(TSString.create("prototype"), printobj);
		
		//put property "this" in global object
		ret.putProperty(TSString.create("testThis"),test);

		return ret;
	}


	//build-in isNaN
	class GisNaN implements TSCode
	{
		public GisNaN() {}

		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			if(args.length > 0){	
				return TSBoolean.create(Double.isNaN(args[0].toPrimitive().toNumber().getInternal()));	
			}
			return TSBoolean.FALSE;
		}
	}


	//build-in isFinite
	class GisFinite implements TSCode
	{
		public GisFinite() {}

		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			if(args.length > 0){	
				return TSBoolean.create(!Double.isInfinite(args[0].toPrimitive().toNumber().getInternal())
						&& !Double.isNaN(args[0].toPrimitive().toNumber().getInternal()));	
			}
			//return TSBoolean.FALSE;
			return TSUndefined.value;
		}
	}



	//build-in readln
	class Greadln implements TSCode
	{
		public Greadln() {}

		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{

			try {
				String input = in.readLine();

				//EOF
				if(input == null)
					input = "";
				else
					//add new line
					input += "\n";
				
				//System.out.println("In readln(): " + input);
				return TSString.create(input);
			} catch (IOException e) {

				Message.executionError("I/O error occured at readln() in the Global Object");;
			}

			//should not be here
			return TSUndefined.value;

		}
	}



	//build-in testThis

	class GtestThis implements TSCode
	{
		public GtestThis() {};

		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			//used for test
			if(!(ths instanceof TSObject))
				Message.fatal("testThis: ths arg is not bond to an object");

			((TSObject)ths).putProperty(TSString.create("xyz"),TSNumber.create(42));
			
			return ths;
		}
	}


	class GprintXYZ implements TSCode
	{
		public GprintXYZ() {}
		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			
			if(!(ths instanceof TSObject))
				Message.fatal("printXYZ: ths arg is not bond to an object");
			
			System.out.println((int)(((TSObject)ths).getProperty(TSString.create("xyz")).toNumber().getInternal()));
			
			return TSUndefined.value;
		}
	}
	
	
	
	//Dont want to implement string object, be lazy
	
	class Gsplit implements TSCode
	{
		public Gsplit() {}
		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			if(args == null || !(args[0] instanceof TSString))
				Message.fatal("In GlobalObject: Can not split an non-string type ");
			
			TSArray ret = TSArray.create();
			if(args.length == 1) {
				ret.push(args[0]);
			} else{
				
				String temp = args[0].toStr().getInternal();
				String regex = args[1].toStr().getInternal();
				String[] split = temp.split(regex);
				
				for(String s : split)
					ret.push(TSString.create(s));
			}
			
			return ret;
			
		}
		
	}
	
	
	class Gtrim implements TSCode
	{
		public Gtrim() {}
		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			if(args == null || !(args[0] instanceof TSString))
				Message.fatal("In GlobalObject: Can not split an non-string type ");	
			return TSString.create(args[0].toStr().getInternal().trim());
		}
		
	}
	
	
	class Glength implements TSCode
	{
		public Glength() {}
		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			if(args == null || !(args[0] instanceof TSString))
				Message.fatal("In GlobalObject: Can not Length() an non-string type ");	
			
			return TSNumber.create(args[0].toStr().getInternal().length());
		}
		
		
		
	}
	
	
	
}
