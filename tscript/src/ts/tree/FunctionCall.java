package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;
import java.util.List;
import java.util.ArrayList;
/**
 * AST Function call node
 *
 */
public final class FunctionCall extends Expression
{
	private Expression funcExp;
	private Arguments args;

	public FunctionCall(final Location loc, final Expression expression,
		final Arguments args)
	{
		super(loc);
		funcExp = expression;
		this.args = args;
	}

	public Expression getFunctionExpression() 
	{
		return funcExp;
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