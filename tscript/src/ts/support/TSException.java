package ts.support;

public class TSException extends RuntimeException{
	
	private TSValue value = null;
	
	public TSException(String msg)
	{
		super(msg);
		value = TSString.create(msg);
	}
	
	public TSException(TSValue value)
	{
		this.value = value;
	}
	
	
	public TSException(double value)
	{
		this.value = TSNumber.create(value);
	}
	
	public TSValue getValue()
	{
		return value;
	}
	
	public String getMessage()
	{
		return value.toPrimitive().toStr().getInternal();
	}

}
