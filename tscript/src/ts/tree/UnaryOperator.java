package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 *   AST unary operator node
 *  
 *    */
public class UnaryOperator extends Expression
{
  private Unaop op;
  private Expression right;

  public UnaryOperator(final Location loc, final Unaop op,
     final Expression right)
  {
    super(loc);
    this.op = op;
    this.right = right;
  }

  public Unaop getOp()
  {
    return op;
  }

  /** Convert operator kind to (Java) String for displaying. */
  public String getOpString()
  {
    return op.toString();
  }

  public Expression getRight()
  {
    return right;
  }

  public <T> T apply(TreeVisitor<T> visitor)
  {
    return visitor.visit(this);
  }
}

