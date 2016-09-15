package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST Return statement node
 *
 */
public final class ReturnStatement extends Statement
{
	private Expression exp;

	public ReturnStatement(final Location loc, final Expression exp)
	{
		super(loc);
		this.exp = exp;
	}
	
	public boolean hasReturnExpression()
	{
		return exp != null;
	}

	public Expression getReturnExpression()
	{
		return exp;
	}

	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}