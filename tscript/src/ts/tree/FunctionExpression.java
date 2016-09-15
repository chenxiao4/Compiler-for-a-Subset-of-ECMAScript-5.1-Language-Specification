package ts.tree;


import java.util.List;
import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST Function expression node
 *
 */
public final class FunctionExpression extends Expression
{
	private String name;
	private List<Statement> body;
	private List<String> parameterList;

	public FunctionExpression(final Location loc, final String name, 
			final List<String> parameterList,
			final List<Statement> body) 
	{
		super(loc);
		this.name = name;
		this.body = body;
		this.parameterList = parameterList; 
	}

	public String getIDName() 
	{
		return this.name;
	}

	public List<Statement> getFunctionBody() 
	{
		return this.body;
	}

	public boolean hasID()
	{
		return name != null;
	}

	public boolean hasFormalParameterList()
	{
		return parameterList != null;
	}

	public List<String> getFormalParameterList() 
	{
		return this.parameterList;
	}

	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}