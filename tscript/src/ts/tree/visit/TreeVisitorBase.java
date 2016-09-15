
package ts.tree.visit;

import ts.tree.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Base implementation for AST visitors. Performs complete traversal
 * but does nothing. Parameterized by return value.
 * <p>
 * The "visit" method is overloaded for every tree node type.
 */
public class TreeVisitorBase<T> implements TreeVisitor<T>
{
	// override to add pre- and/or post-processing
	protected T visitNode(final Tree node)
	{
		return node.apply(this);
	}

	// visit a list of ASTs and return list of results
	// use wildcard to allow general use, with list of Statements, list
	//   of Expressions, etc
	protected List<T> visitEach(final Iterable<?> nodes)
	{
		final List<T> visited = new ArrayList<T>();
		for (final Object node : nodes)
		{
			visited.add(visitNode((Tree) node));
		}
		return visited;
	}

	public T visit(final BinaryOperator binaryOperator)
	{
		visitNode(binaryOperator.getLeft());
		visitNode(binaryOperator.getRight());
		return null;
	}


	public T visit(final UnaryOperator unaryOperator)
	{
		visitNode(unaryOperator.getRight());
		return null;	
	}

	public T visit(final ExpressionStatement expressionStatement)
	{
		visitNode(expressionStatement.getExp());
		return null;
	}

	public T visit(final Identifier identifier)
	{
		return null;
	}

	public T visit(final NumericLiteral numericLiteral)
	{
		return null;
	}

	public T visit(final StringLiteral stringLiteral)
	{
		return null;
	}

	public T visit(final BooleanLiteral booleanLiteral)
	{
		return null;
	}

	public T visit(final NullLiteral nullLiteral)
	{
		return null;
	}

	public T visit(final PrintStatement printStatement)
	{
		visitNode(printStatement.getExp());
		return null;
	}

	public T visit(final VarStatement varStatement)
	{
		return null;
	}

	public T visit(final EmptyStatement emptyStatement)
	{
		return null;
	} 

	public T visit(final Block block)
	{
		visitEach(block.getStatementList());  
		return null;
	} 

	public T visit(final WhileStatement whileStatement)
	{
		visitNode(whileStatement.getExp());
		visitEach(whileStatement.getStatement());
		return null;
	} 

	public T visit(final IfStatement ifStatement)
	{
		visitNode(ifStatement.getExp());
		visitEach(ifStatement.getIfStatement());
		if (ifStatement.hasElse())
			visitEach(ifStatement.getElseStatement());
		return null;
	} 
	
	public T visit(final BreakStatement breakStatement)
	{
		return null;
	}
	
	public T visit(final ContinueStatement continueStatement)
	{
		return null;
	}
	
	public T visit(final ThrowStatement throwStatement)
	{
		return null;
	}
	
	public T visit(final TryStatement tryStatement)
	{
		visitNode(tryStatement.getBlock());
		if (tryStatement.hasCatch())
			visitNode(tryStatement.getCatch());
		if (tryStatement.hasFinally())
			visitNode(tryStatement.getFinally());
		return null;
	}
	
	
	public T visit(final CatchStatement catchStatement)
	{
		visitNode(catchStatement.getBlock());
		return null;
	}

	public T visit(final FinallyStatement finallyStatement)
	{
		visitNode(finallyStatement.getBlock());
		return null;
	}
	
	
	public T visit(final ReturnStatement returnStatement)
	{

		return null;
	}
	
	
	public T visit(final FunctionExpression functionExpression)
	{
		visitEach(functionExpression.getFunctionBody());
		return null;
	}
	
	
	public T visit(final FunctionCall functionCall)
	{
		
		return null;
	}
	
	public T visit(final Arguments arguments)
	{
		visitEach(arguments.getArgumentList());
		return null;
	}
	
	public T visit(final NewExpression newExpression)
	{
		visitNode(newExpression.getNewExpression());
		return null;
	}
	
	public T visit(final PropertyAccessor propertyAccessor)
	{
		visitNode(propertyAccessor.getExpression());
		return null;
	}
	
	public T visit(final MemberExpression memberExpression)
	{
		visitNode(memberExpression.getMemberExpression());
		return null;
	}
	
	
	public T visit(final This ths)
	{
		return null;
	}
	
	public T visit(final ArrayLiteral arrayLiteral)
	{
		if(!arrayLiteral.isEmpty())
			visitEach(arrayLiteral.getElementList());	
		return null;
	}
	
}

