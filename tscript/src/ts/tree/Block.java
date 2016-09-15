
package ts.tree;
import java.util.List;
import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST Block node
 *
 */
public final class Block extends Statement
{

	private List<Statement> statementList = null;

	public Block(final Location loc, final List<Statement> statements)
	{
		super(loc);
		if (statements != null && !statements.isEmpty()){
			//statementLast = statements.remove(statements.size()-1);
			statementList = statements;
		}

	}


	public List<Statement> getStatementList(){
		return statementList;
	}

	public boolean isEmpty(){
		return statementList == null;
	}

	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}