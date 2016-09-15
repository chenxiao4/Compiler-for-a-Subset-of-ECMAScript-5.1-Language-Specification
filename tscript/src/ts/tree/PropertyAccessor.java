package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

/** 
 * AST Property Accessor node
 *
 */
public final class PropertyAccessor extends Expression
{
	private Expression expression;
	private String name;

	public PropertyAccessor(final Location loc, final Expression expression, 
			final String id)
	{
		super(loc);
		this.expression = expression;
		this.name = id;
	}

	public Expression getExpression() 
	{
		return this.expression;
	}

	public String getIDName() 
	{
		return this.name;
	}

	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}