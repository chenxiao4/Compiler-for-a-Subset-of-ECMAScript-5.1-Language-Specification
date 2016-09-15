package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST this expression node
 *
 */
public final class This extends Expression 
{
	public This(final Location loc)
	{
		super(loc);
	}

	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}