package ts.tree;

import java.util.List;
import ts.Location;
import ts.tree.visit.TreeVisitor;

public final class Arguments extends Expression 
{
	private final List<Expression> argumentList;

	public Arguments(final Location loc, final List<Expression> argumentList)
	{
		super(loc);
		this.argumentList = argumentList;
	}

	
	public boolean hasArgumentList()
	{
		return argumentList != null;
	}
	
	public List<Expression> getArgumentList()
	{
		return this.argumentList;
	}

	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}