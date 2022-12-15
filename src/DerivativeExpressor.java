/**
 * An interface to define the derivate of an expression
 * for lambda functions and anonymous classes
 */
public interface DerivativeExpressor {
    public Expression derive(Expression exp1, Expression exp2);
}
