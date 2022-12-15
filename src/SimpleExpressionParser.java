import java.util.function.*;
import java.math.*;

public class SimpleExpressionParser implements ExpressionParser {

	/*
	 * Define different expressions with different sign, evaluation, and differentitations.
	 * We define its sign, evaluation, and differentiation here: third, forth, and fifth parameters respectively  
	 * Additional operations (such as sin, floor, etc.) that is not included can be defined the same way.
	 */

	/**
	 * Create an Additive Expression of the type g(x) + h(x), with 
	 * "+" sign
	 * evaluation of 2 doubles
	 * and differentiation = f'(x)+g'(x)
	 */
	protected DoubleSidedExpression AExpression(Expression leftChild, Expression rightChild){
		return new DoubleSidedExpression(leftChild, rightChild, 
				"+",
				(a, b) -> a+b, 
				(f, g) -> AExpression(f.differentiate(), g.differentiate()));
	}

	/**
	 * Create a Subtractive Expression of the type g(x) - h(x), with 
	 * "-" sign
	 * evaluation of 2 doubles
	 * and differentiation = f'(x)-g'(x)
	 */
	protected DoubleSidedExpression SExpression(Expression leftChild, Expression rightChild){
		return new DoubleSidedExpression(leftChild, rightChild, 
				"-",
				(a, b) -> a-b, 
				(f, g) -> SExpression(f.differentiate(), g.differentiate()));
	}
			
	/**
	 * Create a Multiplicative Expression of the type g(x)*h(X), with 
	 * "*" sign
	 * evaluation of 2 doubles
	 * and differentiation = leftDiff + rightDiff
	 * with leftDiff = g(x)*h'(x), rightDiff = g'(x)*h(x)
	 */
	protected DoubleSidedExpression MExpression(Expression leftChild, Expression rightChild){
		return new DoubleSidedExpression(leftChild, rightChild, 
				"*",
				(a, b) -> a*b, 
				new DerivativeExpressor() {
					public Expression derive (Expression g, Expression h){
						Expression gCopy = g.deepCopy();
						Expression hCopy = h.deepCopy();

						Expression leftDiff = MExpression(gCopy, hCopy.differentiate());
						Expression rightDiff = MExpression(gCopy.differentiate(), hCopy);
						return AExpression(leftDiff, rightDiff);
					}
				});
	}

	/**
	 * Create a Division Expression of the type g(x)/h(X), with 
	 * "*" sign
	 * evaluation of 2 doubles
	 * and differentiation = diff1 - diff2
	 * with gCopy = g(x), hCopy = h(x), 
	 * gDiff = g'(x), hDiff =  h'(x)
	 * hSq = h^2(x)
	 * diff1 = g'(x)/h(x)
	 * diff2 = g(x)*h'(x)/h^2(x)
	 */
	protected DoubleSidedExpression DExpression(Expression leftChild, Expression rightChild){
		return new DoubleSidedExpression(leftChild, rightChild, 
				"/",
				(a, b) -> a/b, 
				new DerivativeExpressor() {
					public Expression derive (Expression g, Expression h){
						Expression gCopy = g.deepCopy();
						Expression hCopy = h.deepCopy();
						Expression gDiff = g.differentiate();
						Expression hDiff = h.differentiate();
						Expression hSq = E2Expression(h.deepCopy(), new LiteralExpression(2));

						Expression diff1 = DExpression(gDiff, hCopy);
						Expression diff2 = MExpression(gCopy, DExpression(hDiff, hSq));
						return SExpression(diff1, diff2);
					}
				});
	}

	/**
	 * Create an Exponential Expression of the type c^h(x), with 
	 * "^" sign
	 * evaluation of 2 doubles
	 * and differentiation = cLog*cCopy^hCopy*hDiff
	 * with cLog = log(c), cCopy=c, hCopy=h(x), hDiff=h'(x)
	 */
	protected DoubleSidedExpression E1Expression(Expression leftChild, Expression rightChild){
		return new DoubleSidedExpression(leftChild, rightChild, 
		"^",
		(a, b) -> Math.pow(a, b), 
		new DerivativeExpressor(){
			public Expression derive (Expression c, Expression h){
						Expression cLog = LExpression(c);
						Expression cCopy = c.deepCopy();
						Expression hCopy = h.deepCopy();
						Expression hDiff = h.differentiate();
						return MExpression(cLog, MExpression(E1Expression(cCopy, hCopy), hDiff));
					}
				});
	}

	/**
	 * Create an Exponential Expression of the type g(x)^c, with 
	 * "^" sign
	 * evaluation of 2 doubles
	 * and differentiation = cCopy*gCopy^cMinus*gDiff
	 * cCopy = c, gCopy = g(x), cMinus = C-1, gDiff = g'(x)
	 */
	protected DoubleSidedExpression E2Expression(Expression leftChild, Expression rightChild){
		return new DoubleSidedExpression(leftChild, rightChild, 
		"^",
		(a, b) -> Math.pow(a, b), 
		new DerivativeExpressor(){
			public Expression derive (Expression g, Expression c){
								Expression cCopy = c.deepCopy();
								Expression gCopy = g.deepCopy();
								Expression cMinus = new LiteralExpression(c.evaluate(0) - 1);
								Expression gDiff = g.differentiate();
								return MExpression(cCopy, MExpression(E2Expression(gCopy, cMinus), gDiff));
							}
						});
	}

	/**
	 * Create an Exponential Expression of the type g(x)^h(x), with 
	 * "^" sign
	 * evaluation of 2 doubles
	 * and differentiation = null (cannot be differentiated)
	 */
	protected DoubleSidedExpression ENullExpression(Expression leftChild, Expression rightChild){
		return new DoubleSidedExpression(leftChild, rightChild, 
		"^",
		(a, b) -> Math.pow(a, b), 
		new DerivativeExpressor(){
			public Expression derive (Expression g, Expression c){
								return null;
							}
						});
	}

	/**
	 * Create an Logarithmic Expression of the type log g(x), with 
	 * "log()" sign
	 * evaluation of 1 double, the other is null
	 * and differentiation = g'(x)/g(x)
	 */
	protected OneSidedExpression LExpression(Expression child){
		return new OneSidedExpression(child,  
		"log",
		(a, nullValue) -> Math.log(a), 
		new DerivativeExpressor(){
			public Expression derive (Expression g, Expression nullExpression){
								Expression gCopy = g.deepCopy();
								Expression gDiff = g.differentiate();
								return DExpression(gDiff, gCopy);
							}
						});
	}

	/**
	 * Create an Parentheses Expression of the type (f(x)), with 
	 * "()" sign
	 * evaluation of 1 double, the other is null
	 * and differentiation = (f'(x))
	 */
	protected OneSidedExpression PExpression(Expression child){
		return new OneSidedExpression(child,  
		"()",
		(a, nullValue) -> a, 
		new DerivativeExpressor(){
			public Expression derive (Expression g, Expression nullExpression){
								Expression gDiff = g.differentiate();
								return PExpression(gDiff);
							}
						});
	}
	
	        /**
         * Attempts to create an expression tree from the specified String.
         * Throws a ExpressionParseException if the specified string cannot be parsed.
	 * Grammar:
	 * S -> A | P
	 * A -> A+M | A-M | M
	 * M -> M*E | M/E | E
	 * E -> P^E | P | log(P)
	 * P -> (S) | L | V
	 * L -> <float>
	 * V -> x
         * @param str the string to parse into an expression tree
         * @return the Expression object representing the parsed expression tree
         */
	public Expression parse (String str) throws ExpressionParseException {
		str = str.replaceAll(" ", "");
		Expression expression = parseAdd(str);
		if (expression == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		return expression;
	}

	private int checkParenthesis(char c){
		if (c == '(') return 1;
		if (c == ')') return -1;
		return 0;
	}

	//A -> A+M | A-M | M
	protected Expression parseAdd (String str) throws ExpressionParseException {
		int len = str.length();
		if (len == 0) throw new ExpressionParseException("Cannot parse expression");
		System.out.println("parseAdd, str: " + str);
		Expression expression;

		// TODO: implement me
		//Count parenthesis; do not evaluate if +/- is inside a parenthesis
		int pCount = 0;
		for (int index = str.length() - 1; index >= 1; --index){
			pCount += checkParenthesis(str.charAt(index));
			if (pCount > 0) return null;
			if (str.charAt(index) == '+' && pCount == 0){
				return AExpression(parseAdd(str.substring(0, index)), parseMulti(str.substring(index + 1, str.length())));
			}
			if (str.charAt(index) == '-' && pCount == 0){
				return SExpression(parseAdd(str.substring(0, index)), parseMulti(str.substring(index + 1, str.length())));
			}
		}
		return parseMulti(str);
	}

	//M -> M*E | M/E | E
	protected Expression parseMulti (String str) throws ExpressionParseException {
		int len = str.length();
		if (len == 0) throw new ExpressionParseException("Cannot parse expression");
		System.out.println("parseMulti, str: " + str);
		Expression expression;

		// TODO: implement me
		int pCount = 0;
		for (int index = str.length() - 1; index >= 0; --index){
			pCount += checkParenthesis(str.charAt(index));
			if (pCount > 0) return null;
			if (str.charAt(index) == '*' && pCount == 0){
				return MExpression(parseMulti(str.substring(0, index)), parseExp(str.substring(index + 1, str.length())));
			}
			if (str.charAt(index) == '/' && pCount == 0){
				return DExpression(parseMulti(str.substring(0, index)), parseExp(str.substring(index + 1, str.length())));
			}
		}
		if (pCount != 0) return null;
		return parseExp(str);
	}

	//E -> log(P) | P^E | P | 
	protected Expression parseExp (String str) throws ExpressionParseException {
		int len = str.length();
		if (len == 0) throw new ExpressionParseException("Cannot parse expression");
		System.out.println("parseExp, str: " + str);
		Expression expression;

		//log(P)
		if (len > 5 && (str.substring(0, 4) + str.charAt(len - 1)).equals("log()")) {
			System.out.println("this is log h");
			return LExpression(parseParen(str.substring(3, len)));
		}

		//P^E
		int pCount = 0;
		for (int index = 0; index < len; ++index){
			pCount += checkParenthesis(str.charAt(index));
			if (pCount < 0) return null;

			if (str.charAt(index) == '^' && pCount == 0){
				
				Expression left = parseParen(str.substring(0, index));
				Expression right = parseExp(str.substring(index + 1, str.length()));

				if (left instanceof LiteralExpression) return E1Expression(left, right);
				if (right instanceof LiteralExpression) return E2Expression(left, right);
				return ENullExpression(left, right);
			}
		}
		return parseParen(str);
	}

	//P -> (S) | L | V
	protected Expression parseParen (String str) throws ExpressionParseException {
		int len = str.length();
		if (len == 0) throw new ExpressionParseException("Cannot parse expression");
		System.out.println("parseParen, str: " + str + "bbb");
		Expression expression;


		if (str.charAt(0) == '(' && str.charAt(len - 1) == ')') 
			return PExpression(parseAdd(str.substring(1, len - 1)));
		
		Expression L = parseLiteralExpression(str);
		if (L != null) return L;

		Expression V = parseVariableExpression(str);
		return V;
	}

	//L -> <float>
	//V -> x
	// TODO: once you implement a VariableExpression class, fix the return-type below.
	protected VariableExpression parseVariableExpression (String str) {
			if (str.equals("x")) {
					// TODO implement the VariableExpression class and uncomment line below
					return new VariableExpression();
			}
			return null;
	}


        // TODO: once you implement a LiteralExpression class, fix the return-type below.
	protected LiteralExpression parseLiteralExpression (String str) {
		// From https://stackoverflow.com/questions/3543729/how-to-check-that-a-string-is-parseable-to-a-double/22936891:
		final String Digits     = "(\\p{Digit}+)";
		final String HexDigits  = "(\\p{XDigit}+)";
		// an exponent is 'e' or 'E' followed by an optionally 
		// signed decimal integer.
		final String Exp        = "[eE][+-]?"+Digits;
		final String fpRegex    =
		    ("[\\x00-\\x20]*"+ // Optional leading "whitespace"
		    "[+-]?(" +         // Optional sign character
		    "NaN|" +           // "NaN" string
		    "Infinity|" +      // "Infinity" string

		    // A decimal floating-point string representing a finite positive
		    // number without a leading sign has at most five basic pieces:
		    // Digits . Digits ExponentPart FloatTypeSuffix
		    // 
		    // Since this method allows integer-only strings as input
		    // in addition to strings of floating-point literals, the
		    // two sub-patterns below are simplifications of the grammar
		    // productions from the Java Language Specification, 2nd 
		    // edition, section 3.10.2.

		    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
		    "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

		    // . Digits ExponentPart_opt FloatTypeSuffix_opt
		    "(\\.("+Digits+")("+Exp+")?)|"+

		    // Hexadecimal strings
		    "((" +
		    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
		    "(0[xX]" + HexDigits + "(\\.)?)|" +

		    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
		    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

		    ")[pP][+-]?" + Digits + "))" +
		    "[fFdD]?))" +
		    "[\\x00-\\x20]*");// Optional trailing "whitespace"

		if (str.matches(fpRegex)) {
			// TODO: Once you implement LiteralExpression, replace the line above with the line below:
			return new LiteralExpression(Double.valueOf(str));
		}
		return null;
	}

	public static void main (String[] args) throws ExpressionParseException {
		final ExpressionParser parser = new SimpleExpressionParser();
		System.out.println(parser.parse("x*x*x*2").convertToString(0));
	}
}
