import java.util.*;
import java.math.*;


public class OneSidedExpression extends AbstractCompoundExpression implements Expression {

    public OneSidedExpression(Expression child, String sign, EvaluateExpressor evaluateExpressor, DerivativeExpressor derivativeExpressor){
        super(child, null, sign, evaluateExpressor, derivativeExpressor);
    }

    public Expression deepCopy(){
        return new OneSidedExpression(_leftChild.deepCopy(), _sign, _evaluateExpressor, _derivativeExpressor);
    }

    public double evaluate(double x){
        double accu = _evaluateExpressor.signMethod(_leftChild.evaluate(x), 0);
        return accu;
    }
    


}
