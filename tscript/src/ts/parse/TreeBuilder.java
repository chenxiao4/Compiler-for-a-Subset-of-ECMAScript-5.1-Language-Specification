package ts.parse;
import java.util.List;
import ts.Location;
import ts.Message;
import ts.tree.*;

/**
 * Provides static methods for building AST nodes
 */
public class TreeBuilder
{

	/** Build a "var" statement.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  name name of variable being declared.
	 */
	public static Statement buildVarStatement(final Location loc,
			final String name)
	{
		Message.log("TreeBuilder: VarStatement (" + name + ")");
		return new VarStatement(loc, name);
	}

	/** Build a expression statement.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  exp  expression subtree
	 */
	public static Statement buildExpressionStatement(final Location loc,
			final Expression exp)
	{
		Message.log("TreeBuilder: ExpressionStatement");
		return new ExpressionStatement(loc, exp);
	}


	/** Build a empty statement.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 */
	public static Statement buildEmptyStatement(final Location loc)
	{
		Message.log("TreeBuilder: EmptyStatement");
		return new EmptyStatement(loc);
	} 


	/** Build a block.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 */
	public static Statement buildBlock(final Location loc,
			final List<Statement> statements)
	{
		Message.log("TreeBuilder: Block");
		return new Block(loc,statements);
	} 


	/** Build a while.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  expression  expression subtree
	 *  @param  statement   statement list
	 */
	public static Statement buildWhileStatement(final Location loc,
			final Expression expression,
			final List<Statement> statement)
	{
		Message.log("TreeBuilder: WhileStatement");
		return new WhileStatement(loc,expression,statement);	

	}


	/** Build a ifstatement.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  expression  expression subtree
	 *  @param  statement1   statement list of if
	 *  @param  statement2   statement list of else
	 */
	public static Statement buildIfStatement(final Location loc,
			final Expression expression,
			final List<Statement> statement1,
			final List<Statement> statement2)
	{
		Message.log("TreeBuilder: IfStatement");
		return new IfStatement(loc,expression,statement1,statement2);	

	}


	/** Build a breakstatement.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  id  identifier
	 */
	public static Statement buildBreakStatement(final Location loc,
			final String id)
	{

		Message.log("TreeBuilder: BreakStatement (break)");
		return new BreakStatement(loc,id);	

	}


	/** Build a continuestatement.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  id  identifier
	 */
	public static Statement buildContinueStatement(final Location loc,
			final String id)
	{

		Message.log("TreeBuilder: ContinueStatement (continue)");
		return new ContinueStatement(loc,id);	

	}



	/** Build a expression statement.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  exp  expression subtree
	 */
	public static Statement buildThrowStatement(final Location loc,
			final Expression exp)
	{
		Message.log("TreeBuilder: ThrowStatement (throw)");
		return new ThrowStatement(loc, exp);
	}	


	/** Build a trystatement.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  block  statement
	 *  @param  cat  statement
	 *  @param  fin  statement
	 */	

	public static Statement buildTryStatement(final Location loc,
			final Statement block,
			final Statement cat,
			final Statement fin)
	{
		Message.log("TreeBuilder: TryStatement (try)");
		return new TryStatement(loc,block,cat,fin);
	}



	/** Build a catch production.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  block  block statement
	 *  @param name  id name
	 */
	public static Statement buildCatchStatement(final Location loc,
			String name, final Statement block)
	{
		Message.log("TreeBuilder: CatchProduction (catch)");
		return new CatchStatement(loc, name, block);
	}	



	/** Build a finally production.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  block  block statement
	 */
	public static Statement buildFinallyStatement(final Location loc,
			final Statement block)
	{
		Message.log("TreeBuilder: FinallyProduction (finally)");
		return new FinallyStatement(loc, block);
	}	



	/** Build a return statement.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  exp  Expression
	 */
	public static Statement buildReturnStatement(final Location loc,
			final Expression exp)
	{
		Message.log("TreeBuilder: ReturnStatement");
		return new ReturnStatement(loc, exp);
	}	




	/** Build a Arguments.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  argumentList  Expression list
	 */
	public static Arguments buildArguments(final Location loc,
			final List<Expression> argumentList){

		Message.log("TreeBuilder: Arguments");
		return new Arguments(loc, argumentList);

	}

	/** Build a FunctionExpresison.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  name  id
	 *  @param  formalParameterList formalParameterList
	 *  @param  functionBody functionBody
	 */
	public static Expression buildFunctionExpression(final Location loc,
			String name, List<String> formalParameterList,
			List<Statement> functionBody){

		Message.log("TreeBuilder: FunctionExpression");
		return new FunctionExpression(loc,name,formalParameterList,functionBody);
	}


	/** Build a FunctionCall.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  memberExpression  memberExpression
	 *  @param  arguments arguments
	 */
	public static Expression buildFunctionCall(final Location loc,
			Expression memberExpression, Arguments arguments)
	{

		Message.log("TreeBuilder: FunctionCall");
		return new FunctionCall(loc,memberExpression,arguments);
	}


	/** Build a NewExpression.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  newExpression  newExpression
	 */
	public static Expression buildNewExpression(final Location loc,
			final Expression newExpression)
	{
		Message.log("TreeBuilder: NewExpression");
		return new NewExpression(loc,newExpression);
	}


	/** Build a PropertyAccessor.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  expression  Expression
	 *  @param  id identifier
	 */
	public static Expression buildPropertyAccessor(final Location loc,
			final Expression expression, final String id)
	{
		Message.log("TreeBuilder: PropertyAccessor");
		return new PropertyAccessor(loc,expression,id);
	}


	/** Build a MemberExpression.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  memberExpression  memberExpression
	 *  @param  args arguments
	 */
	public static Expression buildMemberExpression(final Location loc,
			final Expression memberExpression,
			final Arguments args)
	{

		Message.log("TreeBuilder: MemberExpression");
		return new MemberExpression(loc,memberExpression,args);
	}


	/** Build a ThisExpression.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 */
	public static Expression buildThis(final Location loc)
	{
		Message.log("TreeBuilder: this");
		return new This(loc);
	}


	/** Build a binary operator.
	 *
	 *  @param  loc   location in source code (file, line, column)
	 *  @param  op    the binary operator
	 *  @param  left  the left subtree
	 *  @param  right the right subtree
      @see Binop
	 */
	public static Expression buildBinaryOperator(final Location loc,
			final Binop op,
			final Expression left, final Expression right)
	{
		Message.log("TreeBuilder: BinaryOperator " + op.toString());

		return new BinaryOperator(loc, op, left, right);
	}


	/** Build a unary operator.
	 *    
	 *    @param  loc   location in source code (file, line, column)
	 *    @param  op    the unary operator
	 *    @param  right  the right subtree
	 *    @see Unaop
	 */
	public static Expression buildUnaryOperator(final Location loc,
			final Unaop op,
			final Expression right)
	{
		Message.log("TreeBuilder: UnaryOperator " + op.toString());

		return new UnaryOperator(loc, op, right);
	}


	/** Build a identifier expression.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  name name of the identifier.
	 */
	public static Expression buildIdentifier(final Location loc,
			final String name)
	{
		Message.log("TreeBuilder: Identifier (" + name + ")");
		return new Identifier(loc, name);
	}

	/** Build a numeric literal expression. Converts the String for
	 *  the value to a double.
	 *
	 *  @param  loc   location in source code (file, line, column)
	 *  @param  value value of the literal as a String
	 */
	public static Expression buildNumericLiteral(final Location loc,
			final String value)
	{
		double d = 0.0;


		//parse hexdecimal

		if ((value.length() > 2) && value.toLowerCase().startsWith("0x")){

			try{
				//System.out.println("in parsr hex: " + value);
				d = (double)Long.parseLong(value.substring(2),16);
			}
			catch(NumberFormatException nfe)
			{
				Message.bug(loc, "numeric literal not parsable");
			}	
		} else {

			//parse double
			try
			{ 
				//System.out.println(value);
				d = Double.parseDouble(value);
			}
			catch(NumberFormatException nfe)
			{
				Message.bug(loc, "numeric literal not parsable");
			}	
		}

		Message.log("TreeBuilder: NumericLiteral " + d);
		return new NumericLiteral(loc, d);
	}

	/** Build a string literal expression.
	 *     
	 *  @param  loc   location in source code (file, line, column)
	 *  @param  value value of the string literal as a String
	 *                */
	public static Expression buildStringLiteral(final Location loc,
			final String value)
	{
		String s = value.substring(1,value.length()-1);
		Message.log("TreeBuilder: StringLiteral " + s);
		return new StringLiteral(loc, s);
	}

	/** build a null literal expression
	 *
	 *   @param loc location in the source code
	 *   
	 */
	public static Expression buildNullLiteral(final Location loc)
	{
		Message.log("TreeBuilder: NullLiteral null");
		return new NullLiteral(loc, null);
	}


	/** build a null literal expression
	 *
	 *   @param loc location in the source code
	 *   @param elementList element list
	 */
	public static Expression buildArrayLiteral(final Location loc, 
			final List<Expression> elementList)
	{
		Message.log("TreeBuilder: ArrayLiteral");
		return new ArrayLiteral(loc,elementList);
	}



	/** build a boolean literal expression
	 *
	 *  @param loc location in the  source code
	 *  @param value value of the boolean literal as a string
	 */
	public static Expression buildBooleanLiteral(final Location loc,
			final String value)
	{
		Message.log("TreeBuilder: BooleanLiteral " + value); 
		boolean b = Boolean.parseBoolean(value);
		return new BooleanLiteral(loc,b);
	}



	/** Build a print statement.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  exp  expression subtree.
	 */
	public static Statement buildPrintStatement(final Location loc,
			final Expression exp)
	{
		Message.log("TreeBuilder: PrintStatement");
		return new PrintStatement(loc, exp);
	}

	//
	// methods to detect "early" (i.e. semantic) errors
	//

	// helper function to detect "reference expected" errors
	private static boolean producesReference(Node node)
	{
		if (node instanceof Identifier || node instanceof PropertyAccessor)
		{
			return true;
		}
		return false;
	}

	/** Used to detect non-references on left-hand-side of assignment.
	 *
	 *  @param  loc  location in source code (file, line, column)
	 *  @param  node tree to be checked
	 */
	public static void checkAssignmentDestination(Location loc, Node node)
	{
		if (!producesReference(node))
		{
			Message.error(loc, "assignment destination must be a Reference");
		}
	}

}
