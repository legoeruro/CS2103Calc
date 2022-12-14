
/*
 * Left-assosiative expressions, different from right-assosiate in
 * working with deepCopy(): instantitate a LeftAssoExpression object
 * and evaluate(): evaluate from left to right
 */
public class DoubleSidedExpression extends AbstractCompoundExpression implements Expression {

    public DoubleSidedExpression(Expression leftChild, Expression rightChild, String sign, SignExpressor signExpressor, DerivativeExpressor derivativeExpressor){
        super(leftChild, rightChild, sign, signExpressor, derivativeExpressor);
    }

    public Expression deepCopy(){
        Expression leftCopy = _leftChild.deepCopy();
        Expression rightCopy = _rightChild.deepCopy();
        AbstractCompoundExpression copy = new DoubleSidedExpression(leftCopy, rightCopy, _sign, _signExpressor, _derivativeExpressor);
        return copy;
    }

    public double evaluate(double x){
        double accu = _signExpressor.signMethod(_leftChild.evaluate(x), _rightChild.evaluate(x));
        return accu;
    }


    
}