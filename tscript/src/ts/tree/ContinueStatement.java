
package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST break statement node
 *
 */
public final class ContinueStatement extends Statement
{
	
	private String id;
	
	public ContinueStatement(final Location loc, final String id)
	{
		super(loc);
		this.id = id;
	}

	public String getIDName()
	{
		return id;
	}

	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}