/**
 * Traverse an AST to generate Java code.
 *
 */

package ts.tree.visit;

import ts.Message;
import ts.Main;
import ts.support.TSValue;
import ts.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

/**
 * Does a traversal of the AST to generate Java code to execute the program
 * represented by the AST.
 * <p>
 * Uses a static nested class, Encode.ReturnValue, for the type parameter.
 * This class contains two String fields: one for the temporary variable
 * containing the result of executing code for an AST node; one for the
 * code generated for the AST node.
 * <p>
 * The "visit" method is overloaded for each tree node type.
 */
public final class Encode extends TreeVisitorBase<Encode.ReturnValue>
{
	/**
	 * Static nested class to represent the return value of the Encode methods.
	 * <p>
	 * Contains the following fields:
	 * <p>
	 * <ul>
	 * <li> a String containing the result operand name<p>
	 * <li> a String containing the code to be generated<p>
	 * </ul>
	 * Only expressions generate results, so the result operand name
	 * will be null in other cases, such as statements.
	 */
	static public class ReturnValue
	{
		public String result;

		public String code;


		// initialize both fields
		private ReturnValue()
		{
			result = null;
			code = null;
		}

		// for non-expressions
		public ReturnValue(final String code)
		{
			this();
			this.code = code;
		}

		// for most expressions
		public ReturnValue(final String result, final String code)
		{
			this();
			this.result = result;
			this.code = code;
		}
	}


	//a stack class for local use only
	private class Stack<T> {
		private LinkedList<T> data = new LinkedList<T>();
		public void push(T v) {data.addFirst(v);}
		public T top() {return data.getFirst();}
		public T bot() {return data.getLast();}
		public T pop() {return data.removeFirst();}
		public void settop(T value) {data.set(0,value);}
		public boolean isEmpty() {return data.isEmpty();}
		public String toString() {return data.toString();}
	}


	//wrapper class
	private class ExeContext extends Stack<String> {
		public ExeContext() {}
	}


	//wrapper class
	private class GlobalExeContext extends Stack<ExeContext>{
		public GlobalExeContext(){}
	}


	//update info when get into new execution context
	private void enterNewContextUpdate()
	{

		//create new execution context and push it to the global execution context record
		ExeContext newContext = new ExeContext();
		currentExeContext = newContext;
		ExeContextRec.push(newContext);

		//push the new lex env into current execontext
		currentExeContext.push(getLexEnv());
		currentOutermostLexEnv = currentExeContext.bot();

		//outermost lexenv, its the car lexenv
		currentLexEnv = currentExeContext.top();

		whileloop = 0;
		loopRec.push(whileloop);

	}


	//update info when get out of the new execution context
	private void exitNewContextUpdate()
	{

		//pop the execontext form the stack

		ExeContextRec.pop();

		//check if we exit the main, if it is the global execution context is empty
		if(!ExeContextRec.isEmpty()){
			currentExeContext = ExeContextRec.top();
			currentLexEnv = currentExeContext.top();
			currentOutermostLexEnv = currentExeContext.bot();
		}

		loopRec.pop();
		if(!loopRec.isEmpty())
			whileloop = loopRec.top();

	}


	private void enterNewLexEnvUpdate(String newLexEnv)
	{

		currentExeContext.push(newLexEnv);
		currentLexEnv = currentExeContext.top();

	}


	private void exitNewLexEnvUpdate()
	{
		currentExeContext.pop();
		currentLexEnv = currentExeContext.top();
	}



	// data block is declared here
	private int nextTemp = 0;
	private int whileloop = 0;
	private int infunction = 0;
	private int lexEnvNumber = 0;
	private int funcNumber = 0;
	private String currentLexEnv = null;
	private String currentOutermostLexEnv = null;
	private String GlobalLexEnv = null;
	private String thisBinding = "thisBinding";
	private boolean debug = false;
	private Stack<Integer> loopRec = new Stack<Integer>();
	private ExeContext currentExeContext = new ExeContext();
	private GlobalExeContext ExeContextRec = new GlobalExeContext();
	private List<Encode.ReturnValue> funcRec = new ArrayList<Encode.ReturnValue>();



	// return string for name of next expression temp
	public String getTemp()
	{
		String ret = "temp" + nextTemp;
		nextTemp += 1;
		return ret;
	}

	private String getLexEnv()
	{
		String ret = "lexEnviron" + lexEnvNumber;
		lexEnvNumber += 1;
		return ret;
	}

	private void createFuncInfo(String code)
	{
		String name = "Func"+funcNumber;
		funcRec.add(new Encode.ReturnValue(name,code));
		funcNumber += 1;
	}

	public List<Encode.ReturnValue> getFuncRec()
	{
		return funcRec;
	}



	// by default start output indented 2 spaces and increment
	// indentation by 2 spaces
	public Encode()
	{
		this(2, 2);
	}

	// initial indentation value
	private final int initialIndentation;

	// current indentation amount
	private int indentation;

	// how much to increment the indentation by at each level
	// using an increment of zero would mean no indentation
	private final int increment;

	// increase indentation by one level
	private void increaseIndentation()
	{
		indentation += increment;
	}

	// decrease indentation by one level
	private void decreaseIndentation()
	{
		indentation -= increment;
	}


	private Encode.ReturnValue blockCode(String code, List<Statement> statement){
		code += indent() + "{\n";
		String res = null;
		increaseIndentation();
		List<Encode.ReturnValue> stat = visitEach(statement);
		for (final Encode.ReturnValue rv : stat){
			code += rv.code;
			res = rv.result;
		}
		decreaseIndentation();
		code += indent() + "}\n";

		return new Encode.ReturnValue(res,code);
	}

	public Encode(final int initialIndentation, final int increment)
	{
		// setup indentation
		this.initialIndentation = initialIndentation;
		this.indentation = initialIndentation;
		this.increment = increment;
	}

	// generate a string of spaces for current indentation level
	private String indent()
	{
		String ret = "";
		for (int i = 0; i < indentation; i++)
		{
			ret += " ";
		}
		return ret;
	}

	// generate main method signature
	public String mainMethodSignature()
	{
		return "public static void main(String args[])";
	}


	public String funcMethodSignature()
	{
		return "public TSValue execute(boolean isConstructorCall, TSValue ths, " +
				"TSValue args[], TSLexicalEnvironment outerLexEnv)";
	}


	// generate and return prologue code for the main method body
	public String mainPrologue(String filename)
	{
		String ret = "";
		ret += indent() + "{\n";


		increaseIndentation();
		ret += indent() + "try {\n";	

		enterNewContextUpdate();

		increaseIndentation();


		//thisBinding = "TSGlobalObject.GLOBAL";
		ret += indent() + "TSValue " + thisBinding + " = " + "TSGlobalObject.GLOBAL;\n";

		ret += indent() + "TSLexicalEnvironment "+currentLexEnv + " = " + 
				"TSLexicalEnvironment.newObjectEnvironment(TSGlobalObject.GLOBAL,null);\n";

		//record this for later use maybe
		GlobalLexEnv = currentLexEnv;

		//		ret += indent() + "TSLexicalEnvironment "+GlobalLexEnv + " = " + 
		//				"TSLexicalEnvironment.newObjectEnvironment(TSGlobalObject.GLOBAL,null);\n";
		//
		//
		//		ret += indent() + "TSLexicalEnvironment " + currentLexEnv + " = " +
		//				"TSLexicalEnvironment.newDeclarativeEnvironment("+GlobalLexEnv+");\n";
		//"TSLexicalEnvironment.newDeclarativeEnvironment(null);\n";

		//insert undefined type into the lexical enviroment
		//ret += indent() + currentLexEnv +".declareVariable(TSString.create(\"undefined\"),false);\n";

		return ret;
	}

	// generate and return epilogue code for main method body
	public String mainEpilogue()
	{
		decreaseIndentation();
		String ret = "";

		String tmp = getTemp();
		ret += indent() + "} catch (TSException "+tmp+") {\n";
		increaseIndentation();
		ret += indent() + "Message.executionError(" + tmp
				+".getMessage());\n";
		decreaseIndentation();
		ret += indent()+"}\n";

		decreaseIndentation();
		ret += indent() + "}";

		exitNewContextUpdate();
		return ret;
	}


	// visit a list of ASTs and generate code for each of them in order
	// use wildcard for generality: list of Statements, list of Expressions, etc
	public List<Encode.ReturnValue> visitEach(final Iterable<?> nodes)
	{
		List<Encode.ReturnValue> ret = new ArrayList<Encode.ReturnValue>();

		for (final Object node : nodes)
		{
			ret.add(visitNode((Tree) node));
		}
		return ret;
	}

	// gen and return code for a binary operator
	public Encode.ReturnValue visit(final BinaryOperator binaryOperator)
	{
		String leftResult;

		// generate code to evaluate left subtree
		Encode.ReturnValue leftReturnValue = visitNode(binaryOperator.getLeft());
		String code = leftReturnValue.code;

		// don't want to deref the left subtree if the operator is ASSIGN
		if (binaryOperator.getOp() == Binop.ASSIGN)
		{ 
			leftResult = leftReturnValue.result;
		}
		else
		{
			// if not ASSIGN, generate code to do deref
			leftResult = getTemp();
			code += indent() + "TSValue " + leftResult + " = " +
					leftReturnValue.result + ".getValue();\n";
		}

		// generate code to evaluate right subtree
		Encode.ReturnValue rightReturnValue = visitNode(binaryOperator.getRight());
		code += rightReturnValue.code;

		// always generate code to deref right subtree
		String rightResult = getTemp(); 
		code += indent() + "TSValue " + rightResult + " = " +
				rightReturnValue.result + ".getValue();\n";

		// finally generate code to do the binary operator itself
		String result = getTemp();
		String methodName = getMethodNameForBinaryOperator(binaryOperator);
		code += indent() + "TSValue " + result + " = " + leftResult +
				"." + methodName + "(" + rightResult + ");\n";

		return new Encode.ReturnValue(result, code);
	}

	// support routine for handling binary operators
	private static String getMethodNameForBinaryOperator(
			final BinaryOperator opNode)
	{
		final Binop op = opNode.getOp();

		switch (op) {
		case ADD:
			return "add";
		case ASSIGN:
			return "simpleAssignment";
		case MULTIPLY:
			return "multiply";
		case LOGICAL_EQUAL:
			return "isEqual";
		case LESS_THAN:
			return "lessThan";
		case GREATER_THAN:
			return "greaterThan";
		case SUBTRACT:
			return "subtract";
		case DIVID:
			return "divide";
		default:
			assert false: "unexpected operator: " + opNode.getOpString();
		}
		// cannot reach
		return null;
	}


	// gen and return code for a unary operator

	public Encode.ReturnValue visit(final UnaryOperator unaryOperator)
	{
		String result = getTemp();

		// Encode.ReturnValue leftReturnValue = visitNode(binaryOperator.getLeft());
		// String code = leftReturnValue.code;

		Encode.ReturnValue rightReturnValue = visitNode(unaryOperator.getRight());
		String code = rightReturnValue.code;

		String methodName = getMethodNameForUnaryOperator(unaryOperator);
		code += indent() + "TSValue " + result + " = " + rightReturnValue.result +
				"." + methodName + "("+");\n";

		return new Encode.ReturnValue(result, code);
	}



	//support routine for handling unary operator
	private static String getMethodNameForUnaryOperator(final UnaryOperator opNode)
	{
		final Unaop op = opNode.getOp();

		switch (op) {
		case NOT:
			return "uNot";
		case MINUS:
			return "uMinus";
		default:
			assert false: "unexpected operator" + opNode.getOpString();
		}

		return null;
	} 


	// process an expression statement
	public Encode.ReturnValue visit(final ExpressionStatement
			expressionStatement)
	{
		Encode.ReturnValue exp = visitNode(expressionStatement.getExp());
		String code = indent() + "Message.setLineNumber(" +
				expressionStatement.getLineNumber() + ");\n";
		code += exp.code;
		return new Encode.ReturnValue(code);
	}

	public Encode.ReturnValue visit(final Identifier identifier)
	{
		String result = getTemp();
		String code = "";


		if (debug ){
			//for debug purpose
			code += indent() + "TSValue " + result+" = null;\n";
			code += indent() + "try {\n";
			increaseIndentation();
			code += indent() + result +
					" = " + currentLexEnv +
					".getIdentifierReference(TSString.create(\"" +
					identifier.getName() + "\"));\n";

			code += indent() + result + ".getValue();\n";
			decreaseIndentation();

			String tmp = getTemp();
			code += indent() + "} catch (TSException "+tmp+") {\n";
			increaseIndentation();
			code += indent() + "Message.executionError(" + tmp
					+".getMessage());\n";


			decreaseIndentation();
			code += indent() + "}\n";
		} else 
			code = indent() + "TSValue " + result +
			" = " + currentLexEnv +
			".getIdentifierReference(TSString.create(\"" +
			identifier.getName() + "\"));\n";

		return new Encode.ReturnValue(result, code);
	}

	public Encode.ReturnValue visit(final NumericLiteral numericLiteral)
	{
		String result = getTemp();
		String code = "";

		if (Double.isNaN(numericLiteral.getValue())){

			code += indent() + "TSValue " + result + " = " + "TSNumber.create" +
					"(" + "Double.NaN" + ");\n";

		} else if (Double.isInfinite(numericLiteral.getValue())) {

			code += indent() + "TSValue " + result + " = " + "TSNumber.create" +
					"(" + "Double.POSITIVE_INFINITY" + ");\n";

		} else {

			code += indent() + "TSValue " + result + " = " + "TSNumber.create" +
					"(" + numericLiteral.getValue() + ");\n";
		}
		return new Encode.ReturnValue(result, code);
	}


	public Encode.ReturnValue visit(final StringLiteral stringLiteral)
	{
		String result = getTemp();

		String arg = stringLiteral.getValue().replace("\"","\\\"");

		String code = indent() + "TSValue " + result + "=" + "TSString.create" + 
				"(" + "\"" + arg + "\"" + ");\n";

		return new Encode.ReturnValue(result, code);
	}


	public Encode.ReturnValue visit(final BooleanLiteral booleanLiteral)
	{
		String result = getTemp();
		String code = indent() + "TSValue " + result + "=" + "TSBoolean.create" +
				"(" + booleanLiteral.getValue() + ");\n";

		return new Encode.ReturnValue(result, code);	  

	}


	public Encode.ReturnValue visit(final NullLiteral nullLiteral)
	{
		String result = getTemp();
		String code = indent() + "TSNull " + result + "=" + "TSNull.create" +
				"(" + "\"" + nullLiteral.getValue() + "\"" + ");\n";

		return new Encode.ReturnValue(result,code);

	}


	public Encode.ReturnValue visit(final PrintStatement printStatement)
	{
		Encode.ReturnValue exp = visitNode(printStatement.getExp());
		String code = indent() + "Message.setLineNumber(" +
				printStatement.getLineNumber() + ");\n";
		code += exp.code;
		code += indent() + "System.out.println(" + exp.result +
				".toStr().getInternal());\n";
		return new Encode.ReturnValue(code);
	}

	public Encode.ReturnValue visit(final VarStatement varStatement)
	{
		String code = indent() + "Message.setLineNumber(" +
				varStatement.getLineNumber() + ");\n";

		code += indent() + currentOutermostLexEnv +".declareVariable(TSString.create" +
				"(\"" + varStatement.getName() + "\"), false);\n";

		return new Encode.ReturnValue(code);
	}

	public Encode.ReturnValue visit(final EmptyStatement emptyStatement)
	{
		String code = indent() + "Message.setLineNumber(" +
				emptyStatement.getLineNumber() + ");\n";

		return new Encode.ReturnValue(code);
	}

	public Encode.ReturnValue visit(final Block block)
	{

		String code = "";
		if (!block.isEmpty()){
			code += indent() + "{\n";
			increaseIndentation();

			List<Encode.ReturnValue> lstat = visitEach(block.getStatementList());
			for (final Encode.ReturnValue rv : lstat){
				code += rv.code;
			}
			decreaseIndentation();
			code += indent() + "}\n";
		}
		return new Encode.ReturnValue(code);
	}

	public Encode.ReturnValue visit(final WhileStatement whileStatement)
	{
		String code = indent() + "Message.setLineNumber(" +
				whileStatement.getLineNumber() + ");\n";

		//while block
		code += indent() + "while(true)\n";
		code += indent() + "{\n";
		increaseIndentation();

		whileloop++;
		loopRec.settop(whileloop);

		//if true then stays in the loop
		Encode.ReturnValue tmp = visitNode(whileStatement.getExp());
		code += tmp.code;
		code += indent() + "if(" +tmp.result +".toBoolean().getInternal()"+"){\n";
		code = blockCode(code,whileStatement.getStatement()).code;

		//else break
		code += indent() + "} else\n";
		increaseIndentation();
		code += indent() + "break;\n";
		decreaseIndentation();

		decreaseIndentation();
		code += indent() + "}\n";

		whileloop--;
		loopRec.settop(whileloop);

		return new Encode.ReturnValue(code);

	}

	public Encode.ReturnValue visit(final IfStatement ifStatement)
	{
		String code = indent() + "Message.setLineNumber(" +
				ifStatement.getLineNumber() + ");\n";

		if(ifStatement.getIfStatement().get(0) instanceof BreakStatement 
				&& whileloop == 0){
			//code += indent() + "System.err.println("+
			//		ifStatement.getLineNumber()
			//		+ "+\" : breakStatement ignored because syntax error: break not in IterationStatement or SwitchStatement\");\n";
			Message.error(ifStatement.getLoc(), "breakStatement ignored because syntax error: break not in IterationStatement or SwitchStatement");
		}

		if(ifStatement.getIfStatement().get(0) instanceof ContinueStatement 
				&& whileloop == 0){
			//code += indent() + "System.err.println("+
			//		ifStatement.getLineNumber()
			//		+"+\" : continueStatement ignored because syntax error: break not in IterationStatement\");\n";
			Message.error(ifStatement.getLoc(), "continueStatement ignored because syntax error: continue not in IterationStatement");
		}


		Encode.ReturnValue tmp = visitNode(ifStatement.getExp());
		code += tmp.code;
		code += indent() + "if(" +tmp.result +".toBoolean().getInternal()"+")\n";

		code = blockCode(code,ifStatement.getIfStatement()).code;

		if (ifStatement.hasElse()){
			code += indent() + "else\n";
			code = blockCode(code,ifStatement.getElseStatement()).code;
		}

		return new Encode.ReturnValue(code);
	}


	public Encode.ReturnValue visit(final BreakStatement breakStatement)
	{
		String code = indent() + "Message.setLineNumber(" +
				breakStatement.getLineNumber() + ");\n";

		if (whileloop > 0)
			code += indent() + "if(true) break;\n";
		else {
			Message.error(breakStatement.getLoc(), "breakStatement ignored because syntax error: break not in IterationStatement or SwitchStatement");
			//code += indent() + "System.err.println("+breakStatement.getLineNumber()+
			//		"+\" : breakStatement ignored because syntax error: break not in IterationStatement or SwitchStatement\");\n";
		}
		return new Encode.ReturnValue(code);
	}


	public Encode.ReturnValue visit(final ContinueStatement continueStatement)
	{
		String code = indent() + "Message.setLineNumber(" +
				continueStatement.getLineNumber() + ");\n";
		if (whileloop > 0)
			code += indent() + "if(true) continue;\n";
		else
			Message.error(continueStatement.getLoc(), "continueStatement ignored because syntax error: continue not in IterationStatement");
		//code += indent() + "System.err.println("+continueStatement.getLineNumber()+"+\" : continueStatement ignored because syntax error: continue not in IterationStatement\");\n";			

		return new Encode.ReturnValue(code);
	}

	public Encode.ReturnValue visit(final ThrowStatement throwStatement)
	{
		String code = indent() + "Message.setLineNumber(" +
				throwStatement.getLineNumber() + ");\n";

		Encode.ReturnValue tmp = visitNode(throwStatement.getExp());
		code += tmp.code;
		code += indent() + "if(true) throw new TSException(" + tmp.result + ");\n";

		return new Encode.ReturnValue(code);
	}

	public Encode.ReturnValue visit(final TryStatement tryStatement)
	{
		String code = indent() + "Message.setLineNumber(" +
				tryStatement.getLineNumber() + ");\n";

		code += indent() + "try";
		Encode.ReturnValue tmp = visitNode(tryStatement.getBlock());
		code += tmp.code;

		if (tryStatement.hasCatch())
			code += visitNode(tryStatement.getCatch()).code;

		if (tryStatement.hasFinally())
			code += visitNode(tryStatement.getFinally()).code;


		return new Encode.ReturnValue(code);
	}

	public Encode.ReturnValue visit(final CatchStatement catchStatement)
	{

		String code = "";
		String res = getTemp();
		code += indent() + "catch( TSException "+ res + ") {\n";
		increaseIndentation();

		String lexn = getLexEnv();

		code += indent() + "TSLexicalEnvironment " + lexn + " = " +
				"TSLexicalEnvironment.newDeclarativeEnvironment("+currentLexEnv+");\n";

		enterNewLexEnvUpdate(lexn);

		//decl parameter 
		code += indent() + 
				currentLexEnv+".declareParameter(" +"\"" + 
				catchStatement.getIDName() + "\"," + res+".getValue());\n";

		Encode.ReturnValue tmp = visitNode(catchStatement.getBlock());
		code += tmp.code;


		decreaseIndentation();
		code += indent() + "}\n";

		//delete the new lexenv before get out of catch
		exitNewLexEnvUpdate();

		return new Encode.ReturnValue(code);
	}

	public Encode.ReturnValue visit(final FinallyStatement finallyStatement)
	{

		String code = "";
		code += indent() + "finally ";
		Encode.ReturnValue tmp = visitNode(finallyStatement.getBlock());
		code += tmp.code;

		return new Encode.ReturnValue(code);
	}


	public Encode.ReturnValue visit(final ReturnStatement returnStatement)
	{

		String code = "";
		String res = "";
		if(infunction > 0){
			if(returnStatement.hasReturnExpression()){
				Encode.ReturnValue tmp = visitNode(returnStatement.getReturnExpression());
				code += tmp.code;
				res = tmp.result;
				code += indent() + "if(true) return " + res +";\n";
			} else {
				res = "TSUndefined.value";
				code += indent() + "if(true) return " + res + ";\n";
			}
		} else {
			Message.error(returnStatement.getLoc(), "returnStatement ignored because syntax error: return not in FunctionBody");

			//code += indent() + "System.err.println("+returnStatement.getLineNumber()+
			//		"+\" : returnStatement ignored because syntax error: return not in FunctionBody\");\n";			
		}

		return new Encode.ReturnValue(res,code);
	}



	public Encode.ReturnValue visit(final FunctionExpression functionExpression)
	{
		//step1. create instance of class implementing TSCode 
		enterNewContextUpdate();
		infunction++;

		String fcode = funcMethodSignature() + "{\n";
		increaseIndentation();

		fcode += indent() + "TSValue " + thisBinding + " = ths.getValue();\n";
		fcode += indent() + "if(ths.isUndefined() || ths == null)\n";
		increaseIndentation();
		fcode += indent() + thisBinding + " = TSGlobalObject.GLOBAL;\n";
		decreaseIndentation();


		//capture the runtimeerror in the outermost env in the current execution context
		fcode += indent() + "try {\n";
		increaseIndentation();

		fcode += indent() + "TSLexicalEnvironment " + currentLexEnv + " = " +
				"TSLexicalEnvironment.newDeclarativeEnvironment(outerLexEnv);\n";

		//decl parameters in the new function execution context
		if(functionExpression.hasFormalParameterList()){
			int i = 0;
			List<String> plist = functionExpression.getFormalParameterList();
			fcode += indent() + "String[] pname = new String["+plist.size()+"];\n";
			for (String p : plist){
				fcode += indent() + "pname["+i+"] = " + "\""+ p+"\"" +";\n";
				i++;
			}	


			//bind parameter to actual values from args
			fcode += indent() + "if(args != null){\n";
			increaseIndentation();
			fcode += indent() + "int i = 0;\n";
			fcode += indent() + "for(i = 0; i < args.length && i < "+plist.size()+"; i++)\n";
			increaseIndentation();
			fcode += indent()+currentLexEnv+".declareParameter(pname[i], args[i]);\n";
			decreaseIndentation();

			fcode += indent() + "for(int j = i;j < "+plist.size()+"; j++)\n";
			increaseIndentation();
			fcode += indent() + currentLexEnv+".declareParameter(pname[j],TSUndefined.value);\n";	
			decreaseIndentation();

			decreaseIndentation();
			fcode += indent()+ "} else {\n";

			increaseIndentation();
			fcode += indent() +  "for(int j = 0;j < "+plist.size()+"; j++)\n";

			increaseIndentation();
			fcode += indent() + currentLexEnv+".declareParameter(pname[j],TSUndefined.value);\n";
			decreaseIndentation();

			decreaseIndentation();
			fcode += indent() + "}\n";

		}


		//get the code for the function body
		if (functionExpression.getFunctionBody().size() > 0){
			Encode.ReturnValue tmp = blockCode(fcode,functionExpression.getFunctionBody());
			fcode = tmp.code;
		}

		decreaseIndentation();
		String err = getTemp();
		fcode += indent() + "} catch (TSException "+err+") {\n";
		increaseIndentation();
		fcode += indent() + "Message.executionError(" + err
				+".getMessage());\n";
		decreaseIndentation();
		fcode += indent()+"}\n";
		//if no return statement, return a undefined value
		fcode += indent() + "return TSUndefined.value;\n";
		decreaseIndentation();
		fcode += "}\n";

		createFuncInfo(fcode);
		exitNewContextUpdate();	




		//step2. below code block will be shown in the main method
		String code = "";
		String ftmp = getTemp();
		String fotmp = getTemp();
		String tmplex = currentLexEnv;

		//create new lexenv, but do not enter the new env
		if (functionExpression.hasID()){			
			String lexn = getLexEnv();
			tmplex = lexn;
			code += indent() + "TSLexicalEnvironment " + lexn + " = " +
					"TSLexicalEnvironment.newDeclarativeEnvironment("+currentLexEnv+");\n";

			//do not enter the new lexenv for function expression, but do for function definition
			//enterNewLexEnvUpdate(lexn);
		}

		//create function object in current execution context
		code += indent() +"TSCode "+ ftmp+ " = new " + funcRec.get(funcNumber-1).result + "();\n";
		code += indent() + "TSValue " +fotmp+" = TSFunctionObject.create("+
				ftmp+","+tmplex+");\n";
		//below is for function definition
		//ftmp+","+currentLexEnv+");\n";


		//and bind function object to it.
		if (functionExpression.hasID()){
			//declareFunctionName(final String rawName, TSValue value)
			code += indent() + tmplex + ".declareFunctionName(\""+
					functionExpression.getIDName()+"\","+fotmp
					+");\n";
		}


		infunction--;

		return new Encode.ReturnValue(fotmp,code);
	}


	public Encode.ReturnValue visit(final FunctionCall functionCall)
	{

		String code = "";

		Encode.ReturnValue fe = visitNode(functionCall.getFunctionExpression());
		String fret = getTemp();
		code += fe.code;
		code += indent() + "TSValue "+ fret + " = " + fe.result + ".getValue();\n";


		String fcret = getTemp();
		//arguments installation
		if (functionCall.hasArguments() && functionCall.getArguments() != null){

			Encode.ReturnValue argret = visitNode(functionCall.getArguments());
			String arguments = argret.result;
			code += argret.code;

			code += indent() + "TSValue "+fcret+" = TSUndefined.value;\n";
			code += indent() + "if(" + fret+ " instanceof TSFunctionObject)\n";
			increaseIndentation();
			code += indent() + fcret + " = ((TSFunctionObject)"+fret+ ").call("+thisBinding+","+arguments+");\n";
			//code += indent() + fcret + " = ((TSFunctionObject)"+fret+ ").call(false,null,"+arguments+");\n";
			decreaseIndentation();

			code += indent() + "else\n";
			increaseIndentation();
			code += indent() + "Message.executionError(\"FunctionCall must have a TSFunctioObject\");\n";
			decreaseIndentation();


		} else {
			code += indent() + "TSValue "+fcret+" = TSUndefined.value;\n";
			code += indent() + "if(" + fret+ " instanceof TSFunctionObject)\n";
			increaseIndentation();
			code += indent() + fcret + " = ((TSFunctionObject)"+fret+ ").call("+thisBinding+",null);\n";
			//code += indent() + fcret + " = ((TSFunctionObject)"+fret+ ").call(false,null,null);\n";
			decreaseIndentation();

			code += indent() + "else\n";
			increaseIndentation();
			code += indent() + "Message.executionError(\"FunctionCall must have a TSFunctioObject\");\n";
			decreaseIndentation();
		}

		return new Encode.ReturnValue(fcret,code);
	}



	public Encode.ReturnValue visit(final Arguments arguments)
	{

		String code = "";
		if(arguments.hasArgumentList()){
			int i = 0;
			List<Expression> arglist = arguments.getArgumentList();
			String args = getTemp();
			code += indent() + "TSValue "+args+"[] = new TSValue["+arglist.size()+"];\n";
			List<Encode.ReturnValue> val = visitEach(arglist);
			for (Encode.ReturnValue tmp : val){
				code += tmp.code;
				code += indent() + args +"["+i+"] = "+tmp.result+".getValue();\n";
				i++;
			}

			return new Encode.ReturnValue(args,code);
		}

		return new Encode.ReturnValue(null,code);
	}



	public Encode.ReturnValue visit(final NewExpression newExpression)
	{


		Encode.ReturnValue tmp = visitNode(new MemberExpression(newExpression.getLoc(),newExpression.getNewExpression(),new Arguments(newExpression.getLoc(),null)));

		return new Encode.ReturnValue(tmp.result,tmp.code);
	}


	public Encode.ReturnValue visit(final PropertyAccessor propertyAccessor)
	{

		String code = "";
		Encode.ReturnValue tmp = visitNode(propertyAccessor.getExpression());
		String baseReference = tmp.result;

		code += tmp.code; 
		String reftemp = getTemp();

		//thisBinding = baseReference+".getValue()";
		code += indent() + thisBinding + " = " + baseReference+".getValue();\n";

		code += indent() + "TSValue "+reftemp + " = TSUndefined.value;\n";
		//code += "System.out.println("+baseReference+".getValue().getClass().getName());\n";
		code += indent() + "if("+baseReference+".getValue() instanceof TSObject)\n";
		increaseIndentation();
		code += indent() + reftemp + " = new TSPropertyReference(TSString.create(\""
				+propertyAccessor.getIDName()+"\"),"+baseReference+".getValue());\n";
		decreaseIndentation();

		code += indent() + "else\n";
		increaseIndentation();
		code += indent() + "if(true) throw new TSException(\"TypeError: base of a PropertyAccessor is not an object\");\n";
		decreaseIndentation();

		return new Encode.ReturnValue(reftemp,code);
	}


	public Encode.ReturnValue visit(final MemberExpression memberExpression)
	{

		String code = "";

		Encode.ReturnValue ref = visitNode(memberExpression.getMemberExpression());
		String tmp = getTemp();

		code += ref.code;
		code += indent() + "TSObject "+tmp+ " = null;\n";
		code += indent() + "if ("+ref.result+".getValue() instanceof TSFunctionObject) {\n";
		increaseIndentation();
		if (memberExpression.hasArguments()){
			Encode.ReturnValue argtmp = visitNode(memberExpression.getArguments());
			code += argtmp.code;
			code += indent()+ tmp + " = ((TSFunctionObject)" + ref.result+".getValue()).construct("+argtmp.result+");\n";
		} else {
			code += indent()+ tmp + " = ((TSFunctionObject)" + ref.result+".getValue()).construct(null);\n";
		}
		decreaseIndentation();
		//process if ref is not a object anyway
		code += indent() + "} else\n";
		increaseIndentation();
		code += indent() + tmp + " = TSObject.create();\n";
		decreaseIndentation();

		//code += indent() +"TSObject "+tmp+ " = TSObject.create();\n";

		code += indent() + "if("+ref.result+".getValue() instanceof TSObject && ((TSObject)"+ref.result+".getValue()).hasPrototype())\n";
		increaseIndentation();
		code += indent() + tmp + ".putProperty(TSString.create(\"prototype\"),((TSObject)"+
				ref.result+".getValue()).getProperty(TSString.create(\"prototype\")));\n";
		decreaseIndentation();

		code += indent() + thisBinding + " = " + tmp+".getValue();\n";

		return new Encode.ReturnValue(tmp,code);

	}


	public Encode.ReturnValue visit(final This ths)
	{

		return new Encode.ReturnValue(thisBinding,"");
	}



	public Encode.ReturnValue visit(final ArrayLiteral arrayLiteral)
	{
		String code = "";

		String array = getTemp();
		code += indent() + "TSArray " + array + " = TSArray.create();\n";
		
		if(arrayLiteral.isEmpty()){
			code += indent() + array + ".putProperty(TSString.create(\"length\"),TSNumber.create(0.));\n";
		} else {
			
			List<Encode.ReturnValue> values = visitEach(arrayLiteral.getElementList());
			for(Encode.ReturnValue elem : values){
				code += elem.code;
				code += indent() + array + ".push("+ elem.result +");\n";
			}
		}

		return new Encode.ReturnValue(array,code);
	}


}

