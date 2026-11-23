// ConcreteSyntax.java

// Implementation of the Recursive Descent Parser algorithm

//  Each method corresponds to a concrete syntax grammar rule, 
// which appears as a comment at the beginning of the method.

// This code DOES NOT implement a parser for KAY. You have to complete
// the code and also make sure it implements a parser for KAY - not something
// else.

public class ConcreteSyntax {

	// READ THE COMPLETE FILE FIRST
	
	// Instance variables
	public Token token; // current token that is considered from the input stream
	public TokenStream input; // stream of tokens generated in by the lexical analysis

	// Constructor
	public ConcreteSyntax(TokenStream ts) { // Open the source program
		input = ts; // as a TokenStream, and
		token = input.nextToken(); // retrieve its first Token
	}

	private boolean tokenHasValue(String val) {
		return token != null && token.getValue() != null && token.getValue().equals(val);
	}

	private boolean tokenHasType(String type) {
		return token != null && token.getType() != null && token.getType().equals(type);
	}

	// Method that prints a syntax error message
	private String SyntaxError(String tok) {
		String sawType = (token != null && token.getType() != null) ? token.getType() : "EOF";
		String sawVal = (token != null && token.getValue() != null) ? token.getValue() : "";
		String s = "Syntax error - Expecting: " + tok + " But saw: "
				+ sawType + " = " + sawVal;
		System.out.println(s);
		return s;
		// System.exit(0);
	}

	// Match a string with the value of a token. If no problem, go to the next
	// token otherwise generate an error message
	private void match(String s) {
		if (token.getValue().equals(s))
			token = input.nextToken();
		else //don't do several match if you have ==. If you have match () do match () {}.
			throw new RuntimeException(SyntaxError(s));
	}

	// Implementation of the Recursive Descent Parser

	public Program program() {	
		// Program --> main '{' Declarations Statements '}'
		String[] header = {"void", "main", "(", ")", "{"};
		Program p = new Program();
		for (int i = 0; i < header.length; i++)
			// bypass " main { "
			match(header[i]);
		p.decpart = declarations();
		p.body = statements();
		match("}");
		return p;
	}

	private Declarations declarations() {
		// Declarations --> { Declaration }*
		Declarations ds = new Declarations();
		while (tokenHasValue("int") || tokenHasValue("boolean")) {
			declaration(ds);
		}
		return ds;
	}

	private void declaration(Declarations ds) {
		// Declaration --> Type Identifiers ;
		Type t = type();
		identifiers(ds, t);
		match(";");
	}

	private Type type() {
		
		// Type --> integer | bool
		Type t = null;
		if (tokenHasValue("int"))
			t = new Type(token.getValue());
		else if (tokenHasValue("boolean"))
			t = new Type(token.getValue());
		else
			throw new RuntimeException(SyntaxError("int | boolean"));
		token = input.nextToken(); // pass over the type
		return t;
	}

	private void identifiers(Declarations ds, Type t) {
		// Identifiers --> Identifier { , Identifier }*
		Declaration d = new Declaration(); // first declaration
		d.t = t; // its type
		if (tokenHasType("Identifier")) {
			d.v = new Variable();
			d.v.id = token.getValue(); // its value
			ds.addElement(d);
			token = input.nextToken();
			while (tokenHasValue(",")) {
				d = new Declaration(); // next declaration
				d.t = t; // its type
				token = input.nextToken();
				if (tokenHasType("Identifier")) {
					d.v = new Variable(); // its value
					d.v.id = token.getValue();
					ds.addElement(d);
					token = input.nextToken(); // get "," or ";"
				} else
					throw new RuntimeException(SyntaxError("Identifier"));
			}
		} else
			throw new RuntimeException(SyntaxError("Identifier"));
	}

	private Statement statement() {
		// Statement --> ; | Block | Assignment | IfStatement | WhileStatement
		Statement s = new Skip();
		if (tokenHasValue(";")) { // Skip
			token = input.nextToken();
			return s;
		} else if (tokenHasValue("{")) { // Block
			token = input.nextToken();
			s = statements();
			match("}");
		} else if (tokenHasValue("if")) // IfStatement
			s = ifStatement();
		else if (tokenHasValue("while")) {
			// WhileStatement	
			s = whileStatement();
		} else if (tokenHasType("Identifier")) { // Assignment
			
			s = assignment();
			
		} else
			throw new RuntimeException(SyntaxError("Statement"));
		return s;
	}

	private Block statements() {
		// Block --> '{' Statements '}'
		Block b = new Block();
		while (token != null && !tokenHasValue("}")) {
			b.blockmembers.addElement(statement());
		}
		return b;
	}

	private Assignment assignment() {
		// Assignment --> Identifier := Expression ;
		Assignment a = new Assignment();
		if (tokenHasType("Identifier")) {
			a.target = new Variable();
			a.target.id = token.getValue();
			token = input.nextToken();
			match("=");
			a.source = expression();
			match(";");
		} else
			throw new RuntimeException(SyntaxError("Identifier"));
		return a;
	}

	private Expression expression() {
		// Expression --> Conjunction { || Conjunction }*
		Binary b;
		Expression e;
		e = conjunction();
		while (tokenHasValue("||")) {
			b = new Binary();
			b.term1 = e;
			b.op = new Operator(token.getValue());
			token = input.nextToken();
			b.term2 = conjunction();
			e = b;
		}
		return e;
	}

	private Expression conjunction() {
		// Conjunction --> Relation { && Relation }*
		Binary b;
		Expression e;
		e = relation();
		while (tokenHasValue("&&")) {
			b = new Binary();
			
			b.term1 = e;
			b.op = new Operator(token.getValue());
			token = input.nextToken();
			b.term2 = relation();
			e = b;
		}
		return e;
	}

	private Expression relation() {
		// Relation --> Addition [ < | <= | > | >= | == | <> ] Addition }*
		Binary b;
		Expression e;
		e = addition();
			
		while (tokenHasValue("<") || tokenHasValue("<=")
				|| tokenHasValue(">")
				|| tokenHasValue(">=")
			    || tokenHasValue("==")
				|| tokenHasValue("!=")) {
			b = new Binary();
			b.term1 = e;
			b.op = new Operator(token.getValue());
			token = input.nextToken();
			b.term2 = addition();
			e = b;
		}
		return e;
	}

	private Expression addition() {
		// Addition --> Term { [ + | - ] Term }*
		Binary b;
		Expression e;
		e = term();
		while (tokenHasValue("+") || tokenHasValue("-")) {
			b = new Binary();
			b.term1 = e;
			b.op = new Operator(token.getValue());
			token = input.nextToken();
			b.term2 = term();
			e = b;
		}
		return e;
	}

	private Expression term() {
		// Term --> Negation { [ '*' | / ] Negation }*
		Binary b;
		Expression e;
		e = negation();
		while (tokenHasValue("*") || tokenHasValue("/")) {
			b = new Binary();
			
			b.term1 = e;
			b.op = new Operator(token.getValue());
			token = input.nextToken();
			b.term2 = negation();
			e = b;
		}
		return e;
	}

	private Expression negation() {
		// Negation --> { ! }opt Factor
		Unary u;
		if (tokenHasValue("!")) {
			u = new Unary();
			u.op = new Operator(token.getValue());
			token = input.nextToken();
			u.term = factor();
			return u;
		} else
			return factor();
	}

	private Expression factor() {
		// Factor --> Identifier | Literal | ( Expression )
		Expression e = null;
		if (tokenHasType("Identifier")) {
			Variable v = new Variable();
			v.id = token.getValue();
			e = v;
			token = input.nextToken();
		} else if (tokenHasType("Literal")) {
			Value v = null;
			if (isInteger(token.getValue()))
				v = new Value((new Integer(token.getValue())).intValue());
			else if (tokenHasValue("true"))
				v = new Value(true);
			else if (tokenHasValue("false"))
				v = new Value(false);
			else
				throw new RuntimeException(SyntaxError("Literal"));
			e = v;
			token = input.nextToken();
		} else if (tokenHasValue("(")) {
			token = input.nextToken();
			e = expression();
			match(")");
		} else
			throw new RuntimeException(SyntaxError("Identifier | Literal | ("));
		return e;
	}

	private Conditional ifStatement() {
		// IfStatement --> if ( Expression ) Statement { else Statement }opt
		Conditional c = new Conditional();

		match("if"); 
		match("(");
		c.test = expression();
		match(")");
		c.thenbranch = statement();
		if (token.getValue().equals("else")) {
			token = input.nextToken();
			c.elsebranch = statement();
		}
		return c;
	}

	private Loop whileStatement() {
		// WhileStatement --> while ( Expression ) Statement
		Loop l = new Loop();	
		match("while"); 
		match("(");
		l.test = expression();
		match(")");
		l.body = statement();
		return l;
	}

	private boolean isInteger(String s) {
		if (s == null || s.isEmpty())
			return false;
		boolean result = true;
		for (int i = 0; i < s.length(); i++)
			if ('0' > s.charAt(i) || '9' < s.charAt(i))
				result = false;
		return result;
	}
}
