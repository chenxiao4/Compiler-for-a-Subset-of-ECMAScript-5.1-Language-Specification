
package ts.tree;
import java.util.List;
import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST while node
 *
 */
public final class WhileStatement extends Statement
{
	
	private Expression expression = null;
	private List<Statement> statement = null;
	
	public WhileStatement(final Location loc, final Expression expression,
			final List<Statement> statement)
			{
				super(loc);
				this.expression = expression;
				this.statement = statement;
			}
	
	
	public Expression getExp(){
		return expression;
	}
	
	public List<Statement> getStatement(){
		return statement;
	}
	
	public <T> T apply(TreeVisitor<T> visitor)
	{
		return visitor.visit(this);
	}
}