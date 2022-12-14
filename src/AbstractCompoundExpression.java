import java.util.*;

public abstract class AbstractCompoundExpression implements Expression {
    protected Expression _leftChild;
    protected Expression _rightChild;

    protected String _sign;
    protected EvaluateExpressor _evaluateExpressor;
    protected DerivativeExpressor _derivativeExpressor;

    public AbstractCompoundExpression(Expression leftChild, Expression rightChild, String sign, EvaluateExpressor evaluateExpressor, DerivativeExpressor derivativeExpressor){
        _leftChild = leftChild;
        _rightChild = rightChild;
        
        _sign = sign;
        _evaluateExpressor = evaluateExpressor;
        _derivativeExpressor = derivativeExpressor;
    }

    public String convertToString(int indentLevel){
        String conv = "";
        for (int i = 0; i<indentLevel; ++i){
            conv += "\t";
        }
        conv += _sign + "\n";
        conv += _leftChild.convertToString(indentLevel + 1);
        if (_rightChild != null) conv += _rightChild.convertToString(indentLevel + 1);
        return conv;
    }

    public Expression differentiate(){
        Expression diffExpression = _derivativeExpressor.derive(_leftChild, _rightChild);
        return diffExpression;
    }


}
