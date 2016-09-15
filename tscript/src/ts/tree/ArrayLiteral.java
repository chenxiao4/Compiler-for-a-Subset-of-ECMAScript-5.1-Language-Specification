package ts.tree;

import java.util.List;
import java.util.ArrayList;

import ts.Location;
import ts.tree.visit.TreeVisitor;


public final class ArrayLiteral extends Expression 
{
	private List<Expression> elements;

	public ArrayLiteral(final Location loc, 
			final List<Expression> elements)
	{
		super(loc);
		this.elements = elements;
	} 

	public List<Expression> getElementList() 
	{
		return this.elements;
	}

	public boolean isEmpty()
	{
		return elements == null;
	}
	
	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}