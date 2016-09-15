
package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST var statement node
 *
 */
public final class VarStatement extends Statement
{
  private String name;

  public VarStatement(final Location loc, final String name)
  {
    super(loc);
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public <T> T apply(TreeVisitor<T> visitor)
  {
    return visitor.visit(this);
  }
}

