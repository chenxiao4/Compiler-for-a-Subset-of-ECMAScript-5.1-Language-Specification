package ts.support;

import java.util.List;
import java.util.ArrayList;

import ts.Message;
import ts.support.TSGlobalObject.GisNaN;


public class TSArray extends TSObject {

	private List<TSValue> array;

	private TSArray()
	{
		super(null);
		this.array = new ArrayList<TSValue>();
	}

	private TSArray(List<TSValue> list)
	{
		super(null);
		this.array = new ArrayList<TSValue>(list);
	}


	public static TSArray create()
	{
		TSArray ret =  new TSArray();
		
		ret.putProperty(TSString.create("length"), TSNumber.create(ret.size()));
		ret.putProperty(TSString.create("push"), TSFunctionObject.create(ret.new PUsh(),null));
		ret.putProperty(TSString.create("slice"), TSFunctionObject.create(ret.new SLice(),null));
		ret.putProperty(TSString.create("toString"), TSFunctionObject.create(ret.new ToString(),null));
		ret.putProperty(TSString.create("get"), TSFunctionObject.create(ret.new Get(),null));
		ret.putProperty(TSString.create("set"), TSFunctionObject.create(ret.new Set(),null));
		ret.putProperty(TSString.create("contains"), TSFunctionObject.create(ret.new Contains(),null));
			
		return ret;
	}

	public static TSArray create(List<TSValue> list)
	{
		TSArray ret =  new TSArray(list);
		
		ret.putProperty(TSString.create("length"), TSNumber.create(ret.size()));
		ret.putProperty(TSString.create("push"), TSFunctionObject.create(ret.new PUsh(),null));
		ret.putProperty(TSString.create("slice"), TSFunctionObject.create(ret.new SLice(),null));
		ret.putProperty(TSString.create("toString"), TSFunctionObject.create(ret.new ToString(),null));
		ret.putProperty(TSString.create("get"), TSFunctionObject.create(ret.new Get(),null));
		ret.putProperty(TSString.create("set"), TSFunctionObject.create(ret.new Set(),null));
		ret.putProperty(TSString.create("contains"), TSFunctionObject.create(ret.new Contains(),null));
		
		return ret;
	}

	
	public TSString toStr()
	{
		StringBuffer res = new StringBuffer("[");

		for(int i = 0; i < array.size(); i++){
			if(i == array.size()-1)
				res.append(array.get(i).toPrimitive().toStr().getInternal()+"]");
			else
				res.append(array.get(i).toPrimitive().toStr().getInternal()+", ");	
		}

		if(array.size() == 0)
			res.append("]");
		
		return TSString.create(res.toString());
	}

	
	public List<TSValue> getInternal()
	{
		return array;
	}
	
	public int size()
	{
		return array.size();
	}
	
	
	public void push(TSValue value)
	{
		this.array.add(value);
		this.putProperty(TSString.create("length"), TSNumber.create(array.size()));
	}
	
		
	public TSValue get(TSNumber index)
	{
		int ind = (int)index.getInternal();
		
		if(ind < 0 || ind >= array.size())
			Message.fatal("TSArray IndexError: index out of bound: " + ind);
		
		return array.get(ind);
	}
	
	
	public void set(TSNumber index, TSValue value)
	{
		int ind = (int)index.getInternal();
		if(ind < 0 || ind >= array.size())
			Message.fatal("TSArray IndexError: index out of bound: " + ind);
		
		array.set(ind, value);	
	}
	
	
	
	class ToString implements TSCode
	{
		public ToString() {}

		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			
			if(ths instanceof TSArray)
				return ((TSArray)(ths.getValue())).toStr();
			else
				Message.fatal("In TSArray ToString(TSCode): ths is not bound to itself");
			
			return TSUndefined.value;
		}
	}

	
	class PUsh implements TSCode
	{
		public PUsh() {}
		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			if(!(ths instanceof TSArray) || args == null)
				Message.fatal("In TSArray Push(TSCode): ths is not bound to itself or no value to be pushed");
			else{
				
				TSArray temp = (TSArray)ths;
				for (TSValue elem : args)
					temp.push(elem);
				
				return TSBoolean.TRUE;
			}
			
			return TSUndefined.value;
		}
	}
	
	
	
	class SLice implements TSCode
	{
		public SLice() {}
		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			if (!(ths instanceof TSArray) || args == null)
				Message.fatal("In TSArray Slice(TSCode): ths is not bound to itself or no value to be given");
			else{
				
				TSArray temp = (TSArray)ths;
				int fromIndex = (int)args[0].toNumber().getInternal();
				int toIndex = (args.length > 1)? (int)args[1].toNumber().getInternal() : temp.size();
				
				if(toIndex > temp.size())
					Message.fatal("Index out of Bound: toIndex = " + toIndex);
				
				List<TSValue> copy = temp.getInternal().subList(fromIndex, toIndex);
				TSArray ret = TSArray.create(copy);
				
				return ret;
			}
			
			return TSUndefined.value;
		}
	}
	

	
	class Get implements TSCode
	{
		public Get() {}
		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			if (!(ths instanceof TSArray) || args == null)
				Message.fatal("In TSArray Get(TSCode): ths is not bound to itself or no value to be given");
			else{
				
				TSArray temp = (TSArray)ths;
				return temp.get(args[0].toNumber());
			}
			
			return TSUndefined.value;
		}
		
	}
	
	
	class Set implements TSCode
	{
		public Set() {}
		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			if (!(ths instanceof TSArray) || args == null || args.length < 2)
				Message.fatal("In TSArray Set(TSCode): ths is not bound to itself or no value to be given");
			else{
				
				TSArray temp = (TSArray)ths;
				temp.set(args[0].toNumber(),args[1]);
				return TSBoolean.TRUE;
			}
			return TSUndefined.value;
		}
		
	}
	
	
	class Contains implements TSCode
	{
		public Contains() {}
		public TSValue execute(boolean isConstructorCall, TSValue ths,
				TSValue args[], TSLexicalEnvironment outerLexEnv)
		{
			if (!(ths instanceof TSArray) || args == null)
				Message.fatal("In TSArray Contains(TSCode): ths is not bound to itself or no value to be given");
			else{
				
				TSArray temp = (TSArray)ths;
				return TSBoolean.create(temp.getInternal().contains(args[0]));
			}
			return TSUndefined.value;
		}
		
	}
	

}
