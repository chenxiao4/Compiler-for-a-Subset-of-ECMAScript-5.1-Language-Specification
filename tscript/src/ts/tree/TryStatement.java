package ts.tree;
import java.util.List;
import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST try node
 *
 */
public final class TryStatement extends Statement
{
	private Statement block = null;
	private Statement cat = null;
	private Statement fin = null;

	public TryStatement(final Location loc,
			final Statement block,
			final Statement cat,
			final Statement fin)
	{
		super(loc);
		this.block = block;
		this.cat = cat;
		this.fin = fin;		
	}


	public Statement getBlock()
	{
		return this.block;
	}

	public Statement getCatch()
	{
		return this.cat;
	}
	
	public Statement getFinally()
	{
		return this.fin;
	}

	
	public boolean hasCatch()
	{
		return this.cat != null;
	}

	public boolean hasFinally()
	{
		return this.fin != null;
	}
	
	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}