package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 *  * AST Null literal expression leaf node
 *   *
 *    */
public final class NullLiteral extends Expression
{
  private Object value;

  public NullLiteral(final Location loc, final Object value)
  {
    super(loc);
    this.value = null;
  }

  public Object getValue()
  {
    return null;
  }

  public <T> T apply(TreeVisitor<T> visitor)
  {
    return visitor.visit(this);
  }
}
