
package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST throw statement node
 *
 */
public final class ThrowStatement extends Statement
{
	
	private Expression exp;
	
	public ThrowStatement(final Location loc, final Expression expression)
	{
		super(loc);
		exp = expression;
	}

	public Expression getExp()
	{
		return exp;
	}

	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}