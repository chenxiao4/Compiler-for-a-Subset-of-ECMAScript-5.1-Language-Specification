package ts.tree;
import java.util.List;
import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST catch node
 *
 */
public final class CatchStatement extends Statement
{
	private String id = null;
	private Statement block = null;


	public CatchStatement(final Location loc,
			String name,
			Statement block)
	{
		super(loc);
		id = name;
		this.block = block;

	}


	public String getIDName()
	{
		return id;
	}
	
	public Statement getBlock()
	{
		return block;
	}
	

	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}