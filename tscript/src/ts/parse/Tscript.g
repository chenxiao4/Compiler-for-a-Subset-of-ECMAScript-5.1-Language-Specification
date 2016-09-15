//
// an ANTLR parser specification for a Tscript subset
//

grammar Tscript;

@header {
  package ts.parse;
  import ts.Location;
  import ts.tree.*;
  import static ts.parse.TreeBuilder.*;
  import java.util.List;
  import java.util.ArrayList;
}

@members {
  // grab location info (filename/line/column) from token
  // in order to stick into AST nodes for later error reporting
  public Location loc(final Token token)
  {
    return new Location(getSourceName(), token.getLine(),
      token.getCharPositionInLine());
  }

  // a program is a list of statements
  // i.e. root of AST is stored here
  // set by the action for the start symbol
  private List<Statement> semanticValue;
  public List<Statement> getSemanticValue()
  {
    return semanticValue;
  }
}

// grammar proper

program
  : sl=statementList EOF
    { semanticValue = $sl.lval; }
  ;

statementList
  returns [ List<Statement> lval ]
  : // empty rule
    { $lval = new ArrayList<Statement>(); }
  | sl=statementList s=statement
    { $sl.lval.addAll($s.lval);
      $lval = $sl.lval; }
  ;


/* Statement :
 *     Block
 *     VariableStatement
 *     EmptyStatement
 *     ExpressionStatement
 *     IfStatement
 *     IterationStatement
 *     ContinueStatement
 *     BreakStatement
 *     ReturnStatement
 *     WithStatement
 *     LabelledStatement
 *     SwitchStatement
 *     ThrowStatement
 *     TryStatement
 *     DebuggerStatement
 */
statement
  returns [ List<Statement> lval ]
  : v=varStatement
    { $lval = $v.lval; }
  | e=expressionStatement
    { $lval = new ArrayList<Statement>();
      $lval.add($e.lval); }
  | p=printStatement
    { $lval = new ArrayList<Statement>(); 
      $lval.add($p.lval); }
  | t=emptyStatement
    { $lval = new ArrayList<Statement>(); 
      $lval.add($t.lval); }
  | b=block
    { $lval = new ArrayList<Statement>(); 
      $lval.add($b.lval); }
  | w=whileStatement
    { $lval = new ArrayList<Statement>(); 
      $lval.add($w.lval); }   
  | i=ifStatement
    { $lval = new ArrayList<Statement>(); 
      $lval.add($i.lval); }  
  | br=breakStatement
    { $lval = new ArrayList<Statement>(); 
      $lval.add($br.lval); } 
  | c=continueStatement
    { $lval = new ArrayList<Statement>(); 
      $lval.add($c.lval); }
  | th=throwStatement
    { $lval = new ArrayList<Statement>(); 
      $lval.add($th.lval); }
  | tr=tryStatement
   { $lval = new ArrayList<Statement>(); 
      $lval.add($tr.lval); }  
  | re=returnStatement
   { $lval = new ArrayList<Statement>(); 
      $lval.add($re.lval); }            
  ;


/*Syntax
* VarStatement:
*	var VarDeclarationList ;
*/
varStatement
  returns [ List<Statement> lval ]
  : VAR v=varDeclarationList SEMICOLON
    { $lval = $v.lval; }
  ;


/*Syntax
*VarDeclarationList:
*	VarDeclaration
*	VarDeclarationList , VarDeclaration
*/
varDeclarationList
  returns [ List<Statement> lval]
  : v=varDeclaration
   { $lval = $v.lval;}
  | vl=varDeclarationList COMMA v=varDeclaration
   { $vl.lval.addAll($v.lval);
     $lval = $vl.lval;
   }
  ; 


/*syntax:
*VarDeclaration:
*	Identifier Initialiser_opt
*/
varDeclaration
  returns [ List<Statement> lval]
  : IDENTIFIER
    { $lval = new ArrayList<Statement>();	
      Statement s = buildVarStatement(loc($start),$IDENTIFIER.text);
      $lval.add(s);
    }
  | IDENTIFIER i=initialiser
    { //$lval = buildVarStatement(loc($start),$IDENTIFIER.text);
      // this is a variable declararion statement
      $lval = new ArrayList<Statement>();
      Expression l = buildIdentifier(loc($start),$IDENTIFIER.text);
      checkAssignmentDestination(loc($start),l);
      Statement s = buildVarStatement(loc($start),$IDENTIFIER.text);
      $lval.add(s);

      // add the assignment expression node( expression statement)
      Expression es = buildBinaryOperator(loc($start),Binop.ASSIGN,l,$i.lval);
      Statement ss = buildExpressionStatement(loc($start),es);    
      $lval.add(ss);
    }
  ;
  

/*Syntax
*Initialiser:
*	= AssignmentExpression
*/
initialiser
  returns [ Expression lval]
  : EQUAL a=assignmentExpression
    {$lval = $a.lval;}
  ;


expressionStatement
  returns [ Statement lval ]
  : e=expression SEMICOLON
    { $lval = buildExpressionStatement(loc($start), $e.lval); }
  ;

printStatement
  returns [ Statement lval ]
  : PRINT e=expression SEMICOLON
    { $lval = buildPrintStatement(loc($start), $e.lval); }
  ;


/*EmptyStatement :
 *   ;
 */
emptyStatement
  returns [Statement lval]
  : SEMICOLON
  {$lval = buildEmptyStatement(loc($start));}
  ;


/*Block :
 *    { StatementList? }
 */
block
  returns [Statement lval]
  :'{' '}'
  {$lval = buildBlock(loc($start),null);}
  |'{' s=statementList '}'
  {$lval = buildBlock(loc($start),$s.lval);}
  ;


/* IterationStatement :
 *     do Statement while ( Expression );
 *     while ( Expression ) Statement
 *     for ( Expression? ; Expression? ; Expression? ) Statement
 *     for ( var VariableDeclarationList ; Expression? ; Expression? ) Statement
 *     for ( LeftHandSideExpression in Expression ) Statement
 *     for ( var VariableDeclaration in Expression ) Statement
 */
 
whileStatement
  returns [Statement lval]
  : WHILE LPAREN e=expression RPAREN s=statement
  {$lval = buildWhileStatement(loc($start),$e.lval,$s.lval);}
  ;
  
  
/* IfStatement :
 *     if ( Expression ) Statement else Statement
 *     if ( Expression ) Statement  
 */ 
ifStatement
   returns [Statement lval]
   : IF LPAREN e=expression RPAREN s=statement ELSE st=statement
   {$lval = buildIfStatement(loc($start),$e.lval,$s.lval,$st.lval);}
   | IF LPAREN e=expression RPAREN s=statement
   {$lval = buildIfStatement(loc($start),$e.lval,$s.lval,null);} 
   ;


/* BreakStatement :
 *     break ;
 *     break [no LineTerminator here] Identifier ;
 */
breakStatement
   returns [Statement lval]
   : BREAK SEMICOLON
   {$lval = buildBreakStatement(loc($start),null);}
   ;


/* ContinueStatement :
 *     continue ;
 *     continue [no LineTerminator here] Identifier ;
 */
continueStatement
   returns [Statement lval]
   : CONTINUE SEMICOLON
   {$lval = buildContinueStatement(loc($start),null);}
   ;
 
 
/* ThrowStatement :
 *     throw [no LineTerminator here] Expression ;
 */
throwStatement
   returns [Statement lval]
   : THROW e=expression
   {$lval = buildThrowStatement(loc($start),$e.lval);}
   ;
 


/* TryStatement :
 *     try Block Catch
 *     try Block Finally
 *     try Block Catch Finally
 */
tryStatement
  returns [Statement lval]
  : TRY b=block c=catchStatement
  {$lval = buildTryStatement(loc($start),$b.lval,$c.lval,null);}
  | TRY bb=block f=finallyStatement
  {$lval = buildTryStatement(loc($start),$bb.lval,null,$f.lval);}
  |TRY bbb=block cc=catchStatement ff=finallyStatement
  {$lval = $lval = buildTryStatement(loc($start),$bbb.lval,$cc.lval,$ff.lval);}
  ;
  
  
/* Catch :
 *     catch ( Identifier ) Block
 */
catchStatement
  returns [Statement lval]
  : CATCH LPAREN IDENTIFIER RPAREN b=block
  {$lval = buildCatchStatement(loc($start),$IDENTIFIER.text,$b.lval);}
  ;

/* Finally :
 *     finally Block
 */
finallyStatement
  returns [Statement lval]
  : FINALLY b=block
  {$lval = buildFinallyStatement(loc($start),$b.lval);}
  ;



returnStatement
  returns [Statement lval]
  :RETURN SEMICOLON
  {$lval = buildReturnStatement(loc($start),null);}
  |RETURN e=expression SEMICOLON
  {$lval = buildReturnStatement(loc($start),$e.lval);}
  ;


/*Syntax
 *Expression:
 *	 AssignmentExpression
 *	 Expression, AssignmentExpression
 */
expression
  returns [ Expression lval ]
  : a=assignmentExpression
    { $lval = $a.lval; }
  ;

/*Syntax
 * AssignmentExpression:
 * 	ConditionalExpression	
 *	LeftHandSideExpression = AssignmentExpression
 *	LeftHandSideExpression AssignmentOperator AssignmentExpression
 */
assignmentExpression
  returns [ Expression lval ]
  //: a=additiveExpression
  : a=smfConditionalExpression
    { $lval = $a.lval; }
  | l=leftHandSideExpression EQUAL r=assignmentExpression
    { checkAssignmentDestination(loc($start), $l.lval);
      $lval = buildBinaryOperator(loc($start), Binop.ASSIGN,
        $l.lval, $r.lval); }
  ;


/*
*simplified conditional expression
* complete implementation will be done later
*/
smfConditionalExpression
  returns  [Expression lval]
  :e=equalityExpression
    {$lval = $e.lval;}
  ;


/*Syntax
*LefHandSideExpression:
*	NewExpression
*	CallExpression
* 	complete implementation will be done later
*/
leftHandSideExpression
  returns [ Expression lval ]
//  : p=primaryExpression
  : c=callExpression
  { $lval = $c.lval; }
  | n=newExpression
  {$lval = $n.lval;}
  ;


/* CallExpression :
 *     MemberExpression Arguments
 *     CallExpression Arguments
 *     CallExpression [ Expression ] (wont implement this)
 *     CallExpression . IdentifierName
 */
callExpression
  returns [Expression lval]
  : m=memberExpression a=arguments
  {$lval = buildFunctionCall(loc($start),$m.lval,$a.lval);}
  | c=callExpression ar=arguments
  {$lval = buildFunctionCall(loc($start),$c.lval,$ar.lval);}
  | ca=callExpression '.' IDENTIFIER
  {$lval = buildPropertyAccessor(loc($start),$ca.lval,$IDENTIFIER.text);}
  ; 
  
/* NewExpression :
 *     MemberExpression
 *     new NewExpression
 */
newExpression
  returns [Expression lval]
  :m=memberExpression
  {$lval = $m.lval;}
  |NEW n=newExpression
  {$lval = buildNewExpression(loc($start),$n.lval);}
  ; 
 
 
 
/* MemberExpression :
 *     PrimaryExpression
 *     FunctionExpression
 *     MemberExpression [ Expression ]
 *     MemberExpression . IdentifierName
 *     new MemberExpression Arguments
 */
memberExpression
  returns [Expression lval]
  :p=primaryExpression
  {$lval = $p.lval;}
  |f=functionExpression
  {$lval = $f.lval;}
  |m=memberExpression '.' IDENTIFIER
  {$lval = buildPropertyAccessor(loc($start),$m.lval,$IDENTIFIER.text);}
  |NEW m=memberExpression a=arguments
  {$lval = buildMemberExpression(loc($start),$m.lval,$a.lval);}
  ; 
 
  
/* FunctionExpression :
 *     function Identifier? ( FormalParameterList? ) { FunctionBody }
 */
functionExpression
  returns [Expression lval]
  : FUNCTION LPAREN RPAREN '{' f=functionBody '}'
  {$lval = buildFunctionExpression(loc($start),null,null,$f.lval);}
  | FUNCTION IDENTIFIER LPAREN RPAREN '{' f=functionBody '}'
  {$lval = buildFunctionExpression(loc($start),$IDENTIFIER.text,null,$f.lval);}
  | FUNCTION LPAREN fo=formalParameterList RPAREN '{' f=functionBody '}'
  {$lval = buildFunctionExpression(loc($start),null,$fo.lval,$f.lval);}
  | FUNCTION IDENTIFIER LPAREN fo=formalParameterList RPAREN '{' f=functionBody '}'
  {$lval = buildFunctionExpression(loc($start),$IDENTIFIER.text,$fo.lval,$f.lval);}
  ;
  

/* FormalParameterList :
 *     Identifier
 *    FormalParameterList , Identifier
 */
formalParameterList
  returns [List<String> lval]
  : IDENTIFIER
  {$lval = new ArrayList<String>();
   $lval.add($IDENTIFIER.text);}
  | f=formalParameterList COMMA IDENTIFIER
  {$f.lval.add($IDENTIFIER.text);
   $lval = $f.lval;}
  ;

functionBody
  returns [List<Statement> lval]
  ://
  {$lval = new ArrayList<Statement>();}
  | s=statementList
  {$lval = $s.lval;}
  ;  



      
/* ArgumentList :
 *     AssignmentExpression
 *     ArgumentList , AssignmentExpression  
 */ 
argumentList
  returns [List<Expression> lval]
  : a=assignmentExpression
  {$lval = new ArrayList<Expression>();
   $lval.add($a.lval);}
  | ar=argumentList COMMA a=assignmentExpression
  {$ar.lval.add($a.lval);
   $lval = $ar.lval;}
  ;

/* Arguments :
 *     ( )
 *     ( ArgumentList )
 */
arguments
  returns [Arguments lval]
  : LPAREN RPAREN
  {$lval = buildArguments(loc($start),null);}
  | LPAREN a=argumentList RPAREN
  {$lval = buildArguments(loc($start),$a.lval);}
  ;
 
 





/*Syntax
*AdditiveExpression:
*	MultiplicativeExpression
*	AdditiveExpression + MultiplicativeExpression
*	AdditiveExpression - MultiplicativeExpression
*/
additiveExpression
  returns [ Expression lval ]
  : m=multiplicativeExpression
    { $lval = $m.lval; }
  | l=additiveExpression PLUS r=multiplicativeExpression
    { $lval = buildBinaryOperator(loc($start), Binop.ADD,
        $l.lval, $r.lval); }
  | l=additiveExpression MINUS r=multiplicativeExpression
    {$lval = buildBinaryOperator(loc($start),Binop.SUBTRACT,$l.lval,$r.lval);}
  ;


/*Syntax
*MultiplicativeExpresison:
*	UnaryExpression
*	MultiplicativeExpression (x/%) UnaryExpression 
*/
multiplicativeExpression
  returns [ Expression lval ]
    :u=unaryExpression
    { $lval = $u.lval; }
  | l=multiplicativeExpression ASTERISK r=unaryExpression
    { $lval = buildBinaryOperator(loc($start), Binop.MULTIPLY,
      $l.lval, $r.lval); }
  | l=multiplicativeExpression SLASH r=unaryExpression
    {$lval = buildBinaryOperator(loc($start),Binop.DIVID,$l.lval,$r.lval);}
  ;


/*Syntax
* RelationalExpression :
*     ShiftExpression (we only have AdditiveExpression now, complete implementation may be done later)
*     RelationalExpression < ShiftExpression
*     RelationalExpression > ShiftExpression
*     RelationalExpression <= ShiftExpression
*     RelationalExpression >= ShiftExpression
*     RelationalExpression instanceof ShiftExpression 
*     RelationalExpression in ShiftExpression
*/
relationalExpression
  returns [ Expression lval]
  :a=additiveExpression
   {$lval = $a.lval;}
  | l=relationalExpression LT r=additiveExpression
   {$lval = buildBinaryOperator(loc($start),Binop.LESS_THAN,$l.lval,$r.lval);}
  | l=relationalExpression GT r=additiveExpression
   {$lval = buildBinaryOperator(loc($start),Binop.GREATER_THAN,$l.lval,$r.lval);}
  ;


/*Syntax
* EqualityExpression :
*     RelationalExpression
*     EqualityExpression == RelationalExpression
*     EqualityExpression != RelationalExpression
*     EqualityExpression === RelationalExpression
*     EqualityExpression !== RelationalExpression
*/
equalityExpression
  returns [ Expression lval ]
  : r=relationalExpression
    {$lval = $r.lval;}
  | l=equalityExpression EQUALS r=relationalExpression
    {$lval = buildBinaryOperator(loc($start),Binop.LOGICAL_EQUAL,$l.lval,$r.lval);}
  ;


/*Syntax
*UnaryExpression:
*	PostfixExpression (we only have LeftHandSideExpression now, complete implementation may be done later)
*	(delete,void,typeof,++,--,+,-,~,!) UnaryExpression
*/
unaryExpression
  returns [ Expression lval ]
  : l=leftHandSideExpression
    {$lval = $l.lval;}
  | NOT u=unaryExpression
    {$lval = buildUnaryOperator(loc($start),Unaop.NOT,$u.lval);}
  | MINUS u=unaryExpression
    {$lval = buildUnaryOperator(loc($start),Unaop.MINUS,$u.lval);}
  ; 


/* Syntax
 * PrimaryExpression:
 *	this
 *	Identifier
 *	Literal
 *	ArrayLiteral
 *	ObjecrtLiteral
 *	(Expression)
 *complete implementation will be done later
 */
primaryExpression
  returns [ Expression lval ]
  : IDENTIFIER
    { $lval = buildIdentifier(loc($start), $IDENTIFIER.text); }
  | THIS
    { $lval = buildThis(loc($start));}
  | NUMERIC_LITERAL
    { $lval = buildNumericLiteral(loc($start), $NUMERIC_LITERAL.text); }
  | BOOLEAN_LITERAL
    { $lval = buildBooleanLiteral(loc($start), $BOOLEAN_LITERAL.text);} 
  | NULL_LITERAL
    { $lval = buildNullLiteral(loc($start));}
  | STRING_LITERAL
    { $lval = buildStringLiteral(loc($start), $STRING_LITERAL.text);}
  | LPAREN e=expression RPAREN
    { $lval = $e.lval; }
  | a=arrayLiteral
  	{$lval = $a.lval;}
  ;
  

  
elementList
  returns [ List<Expression> lval ]
  : a=assignmentExpression
  	{$lval = new ArrayList<Expression>();
  	 $lval.add($a.lval); }
  | e=elementList COMMA a=assignmentExpression
  	{$e.lval.add($a.lval);
  	 $lval = $e.lval;}  
  ;
  

arrayLiteral
  returns [ Expression lval]
  : LBRACK RBRACK
  	{$lval = buildArrayLiteral(loc($start),null);}
  | LBRACK e=elementList RBRACK
  	{$lval = buildArrayLiteral(loc($start),$e.lval);}
  ; 
 
 
 
 
// fragments to support the lexer rules

fragment DIGIT : [0-9];

fragment IdentifierCharacters : [a-zA-Z_$] [a-zA-Z0-9_$]*;

fragment SpaceTokens : SpaceChars | LineTerminator | EndOfLineComment;

fragment SpaceChars : ' ' | '\t' | '\f';

fragment EndOfLineComment : '//' ( ~[\n\r] )* (LineTerminator | EOF);

fragment LineTerminator : '\r' '\n' | '\r' | '\n';


//Decimal Digits
fragment DecimalDigits : DIGIT+;

//Exponent Part
fragment ExponentPart : [Ee] [+-]? DecimalDigits;

//Integer syntax
fragment DecimalInteger : '0' | ([1-9] DIGIT*);

//Float syntax
fragment DecimalFloat : DecimalInteger '.' DecimalDigits?
		      | '.' DecimalDigits
		      ;

//Decimal Literal
fragment DecimalLiteral : DecimalFloat ExponentPart?
			| DecimalInteger ExponentPart?
			;
//Hex Integer Literal
fragment HexDigit : [0-9a-fA-F];

fragment HexIntegerLiteral : '0' [xX] HexDigit+;


fragment UnicodeEscapeSequence : 'u' HexDigit HexDigit HexDigit HexDigit;

fragment HexEscapeSequence : 'x' HexDigit HexDigit;

fragment EscapeCharacter : SingleEscapeCharacter
			 | DIGIT  
			 | [xu]
			 ;

fragment NonEscapeCharacter : ~['"\\bfnrtv0-9xu\r\n];

fragment SingleEscapeCharacter : ['"\\bfnrtv];

fragment CharacterEscapeSequence : SingleEscapeCharacter
				 | NonEscapeCharacter
				 ;

fragment EscapeSequence : CharacterEscapeSequence
			| [0] //
			| HexEscapeSequence
			| UnicodeEscapeSequence
			;

fragment LineContinuation : [\\] LineTerminatorSequence;

fragment LineTerminatorSequence : '\r' '\n'
				| LineTerminator
				; 

fragment SingleStringCharacter : ~['\\\r\n]
			       | [\\] EscapeSequence
			       | LineContinuation
			       ;

fragment DoubleStringCharacter : ~["\\\r\n]
			       | [\\] EscapeSequence
			       | LineContinuation
			       ;

		





// lexer rules
//   keywords must appear before IDENTIFIER

// cannot have a leading 0 unless the literal is just 0
NUMERIC_LITERAL : DecimalLiteral
		| HexIntegerLiteral
//	        | SPECIAL_VALUE
		;

//Boolean type have two values, true and false
BOOLEAN_LITERAL : 'true'
	     	| 'false'
	     	;

STRING_LITERAL : '"' DoubleStringCharacter* '"'
	       | '\'' SingleStringCharacter* '\''
	       ;


NULL_LITERAL : 'null'
	     ;

//SPECIAL_VALUE: 'NaN'
//             | 'Infinity'
//             ;


LPAREN : 	[(];
RPAREN :	[)];
LBRACK :	[\[];
RBRACK :	[\]];
SEMICOLON :	[;];
EQUAL : 	[=];
PLUS : 		[+];
MINUS : 	[-];
ASTERISK : 	[*];
SLASH:	 	[/];
COMMA:		[,];

NOT : 		[!];
EQUALS : 	[=][=];
LT : 		[<];
GT : 		[>];



// keywords start here
PRINT : 'print';
VAR : 'var';
WHILE: 'while';
IF : 'if';
ELSE : 'else';
BREAK : 'break';
CONTINUE : 'continue';
THROW: 'throw';
TRY: 'try';
CATCH: 'catch';
FINALLY : 'finally';
FUNCTION : 'function';
RETURN : 'return';
NEW : 'new';
THIS : 'this';

IDENTIFIER : IdentifierCharacters;

// skip whitespace and comments

WhiteSpace : SpaceTokens+ -> skip;

