import java.util.*;
import java.math.*;


public class OneSidedExpression extends AbstractCompoundExpression implements Expression {

    public OneSidedExpression(Expression child, String sign, SignExpressor signExpressor, DerivativeExpressor derivativeExpressor){
        super(child, null, sign, signExpressor, derivativeExpressor);
    }

    public Expression deepCopy(){
        return new OneSidedExpression(_leftChild.deepCopy(), _sign, _signExpressor, _derivativeExpressor);
    }

    public double evaluate(double x){
        double accu = _signExpressor.signMethod(_leftChild.evaluate(x), 0);
        return accu;
    }
    


}
