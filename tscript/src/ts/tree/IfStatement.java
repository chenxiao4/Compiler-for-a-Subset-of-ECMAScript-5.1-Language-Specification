
package ts.tree;

import java.util.List;
import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST if statement node
 *
 */
public final class IfStatement extends Statement
{

	private Expression expression;
	private List<Statement> ifstatement;
	private List<Statement> elsestatement;
	

	public IfStatement(final Location loc,final Expression exp,
			final List<Statement> ifstat,
			final List<Statement> elsestat)
	{
		super(loc);
		expression = exp;
		ifstatement = ifstat;
		elsestatement = elsestat;
	}

	public Expression getExp()
	{
		return expression;
	}
	
	public List<Statement> getIfStatement()
	{
		return ifstatement;
	}
	
	public List<Statement> getElseStatement()
	{
		return elsestatement;
	}
	
	public boolean hasElse()
	{
		return elsestatement != null;
	}
	
	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}