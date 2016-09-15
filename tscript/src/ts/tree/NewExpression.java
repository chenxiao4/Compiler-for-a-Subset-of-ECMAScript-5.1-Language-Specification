package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST New expression node
 *
 */
public final class NewExpression extends Expression
{
	private Expression expression;

	public NewExpression(final Location loc, final Expression newExpression)
	{  
		super(loc);
		this.expression = newExpression;
	}

	public Expression getNewExpression()
	{
		return this.expression;
	}

	public <T> T apply(TreeVisitor<T> visitor) 
	{
		return visitor.visit(this);
	}
}