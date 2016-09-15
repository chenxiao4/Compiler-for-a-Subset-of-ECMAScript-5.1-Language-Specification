package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST Member expression node
 *
 */
public final class MemberExpression extends Expression
{
	private Expression expression;
	private Arguments args;

	public MemberExpression(final Location loc, final Expression expression,
			final Arguments arguments)
	{  
		super(loc);
		this.expression = expression;
		this.args = arguments;
	}

	public Expression getMemberExpression()
	{
		return this.expression;
	}

	public boolean hasArguments()
	{
		return args.hasArgumentList();
	}
	
	public Arguments getArguments() 
	{
		if (!args.hasArgumentList()) {
			return null;
		}
		return args;
	}

	
	public <T> T apply(TreeVisitor<T> visitor) 
	{
		return visitor.visit(this);
	}
}