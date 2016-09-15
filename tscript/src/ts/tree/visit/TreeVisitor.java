
package ts.tree.visit;

import ts.tree.*;

/**
 * All visitor classes for ASTs will implement this interface, which
 *   is parameterized by return type.
 *
 */
public interface TreeVisitor<T>
{
  T visit(BinaryOperator binaryOperator);

  T visit(UnaryOperator unaryOperator);

  T visit(ExpressionStatement expressionStatement);

  T visit(Identifier identifier);

  T visit(NumericLiteral numericLiteral);

  T visit(StringLiteral stringLiteral);

  T visit(NullLiteral nullLiteral);

  T visit(BooleanLiteral booleanLiteral);

  T visit(PrintStatement printStatement);

  T visit(VarStatement varStatement);
  
  T visit(EmptyStatement emptyStatement);
  
  T visit(Block block);
  
  T visit(WhileStatement whilestatement);
  
  T visit(IfStatement ifStatement);
  
  T visit(BreakStatement breakStatement);
  
  T visit(ContinueStatement continueStatement);
  
  T visit(ThrowStatement throwStatement);
  
  T visit(TryStatement tryStatement);
  
  T visit(CatchStatement catchStatement);
  
  T visit(FinallyStatement finallyStatement);
  
  T visit(Arguments arguments);
  
  T visit(FunctionExpression functionExpression);
  
  T visit(FunctionCall functionCall);
  
  T visit(ReturnStatement returnStatement);
  
  T visit(NewExpression newExpression);
  
  T visit(PropertyAccessor propertyAccessor);
  
  T visit(MemberExpression memberExpression);
  
  T visit(This ths);
  
  T visit(ArrayLiteral arrayLiteral);
}

