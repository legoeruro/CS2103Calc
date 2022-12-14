import java.util.function.*;
import java.math.*;

public class SimpleExpressionParser implements ExpressionParser {

	/*
	 * Define different expressions with different sign, evaluation, and differentitations.
	 * We define its sign, evaluation, and differentiation here: third, forth, and fifth parameters respectively  
	 */

	/**
	 * Create an Additive Expression of the type g(x) + h(x), with 
	 * "+" sign
	 * evaluation of 2 doubles
	 * and differentiation = f'(x)+g'(x)
	 */
	protected Expression AExpression(Expression leftChild, Expression rightChild){
		return new DoubleSidedExpression(leftChild, rightChild, 
				"+",
				(a, b) -> a+b, 
				(exp1, exp2) -> AExpression(exp1.differentiate(), exp2.differentiate()));
	}

	/**
	 * Create a Subtractive Expression of the type g(x) - h(x), with 
	 * "-" sign
	 * evaluation of 2 doubles
	 * and differentiation = f'(x)-g'(x)
	 */
	protected Expression SExpression(Expression leftChild, Expression rightChild){
		return new DoubleSidedExpression(leftChild, rightChild, 
				"-",
				(a, b) -> a-b, 
				(exp1, exp2) -> SExpression(exp1.differentiate(), exp2.differentiate()));
	}
			
	/**
	 * Create a Multiplicative Expression of the type g(x)*h(X), with 
	 * "*" sign
	 * evaluation of 2 doubles
	 * and differentiation = leftDiff + rightDiff
	 * with leftDiff = g(x)*h'(x), rightDiff = g'(x)*h(x)
	 */
	protected Expression MExpression(Expression leftChild, Expression rightChild){
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
	protected Expression DExpression(Expression leftChild, Expression rightChild){
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
	protected Expression E1Expression(Expression leftChild, Expression rightChild){
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
	protected Expression E2Expression(Expression leftChild, Expression rightChild){
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
	 * Create an Logarithmic Expression of the type log g(x), with 
	 * "log()" sign
	 * evaluation of 1 double, the other is null
	 * and differentiation = g'(x)/g(x)
	 */
	protected Expression LExpression(Expression child){
		return new OneSidedExpression(child,  
		"log()",
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
         * Attempts to create an expression tree from the specified String.
         * Throws a ExpressionParseException if the specified string cannot be parsed.
	 * Grammar:
	 * S -> A | P
	 * A -> M+A | M-A | M
	 * M -> E*M | E/M | E
	 * E -> P^E | P | log(P)
	 * P -> (S) | L | V
	 * L -> <float>
	 * V -> x
         * @param str the string to parse into an expression tree
         * @return the Expression object representing the parsed expression tree
         */
	public Expression parse (String str) throws ExpressionParseException {
		str = str.replaceAll(" ", "");
		Expression expression = parseAdditiveExpression(str);
		if (expression == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		return expression;
	}


	
	protected Expression parseAdditiveExpression (String str) {
		Expression expression;

		// TODO: implement me
		
		return null;
	}

	// TODO: once you implement a VariableExpression class, fix the return-type below.
	protected /*Variable*/Expression parseVariableExpression (String str) {
			if (str.equals("x")) {
					// TODO implement the VariableExpression class and uncomment line below
					// return new VariableExpression();
			}
			return null;
	}

        // TODO: once you implement a LiteralExpression class, fix the return-type below.
	protected /*Literal*/Expression parseLiteralExpression (String str) {
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
			return null;
			// TODO: Once you implement LiteralExpression, replace the line above with the line below:
			// return new LiteralExpression(str);
		}
		return null;
	}

	public static void main (String[] args) throws ExpressionParseException {
		final ExpressionParser parser = new SimpleExpressionParser();
		System.out.println(parser.parse("10*2+12-4.").convertToString(0));
	}
}
