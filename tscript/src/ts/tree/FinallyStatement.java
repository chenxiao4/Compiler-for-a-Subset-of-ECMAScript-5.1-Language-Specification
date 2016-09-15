package ts.tree;
import java.util.List;
import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST finally node
 *
 */
public final class FinallyStatement extends Statement
{
	private Statement block = null;

	public FinallyStatement(final Location loc,
			Statement block)
	{
		super(loc);
		this.block = block;

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