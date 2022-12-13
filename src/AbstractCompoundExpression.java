import java.util.*;

public abstract class AbstractCompoundExpression implements Expression {
    protected Expression _leftChild;
    protected Expression _rightChild;

    protected String _sign;
    protected SignExpressor _signExpressor;
    protected DerivativeExpressor _derivativeExpressor;

    public AbstractCompoundExpression(Expression leftChild, Expression rightChild, String sign, SignExpressor signExpressor, DerivativeExpressor derivativeExpressor){
        _leftChild = leftChild;
        _rightChild = rightChild;
        
        _sign = sign;
        _signExpressor = signExpressor;
        _derivativeExpressor = derivativeExpressor;
    }

    public String convertToString(int indentLevel){
        String conv = "";
        for (int i = 0; i<indentLevel; ++i){
            conv += "\t";
        }
        conv += _sign + "\n";
        return conv;
    }

    public void addChilds(Expression leftChild, Expression rightChild){
        _leftChild = leftChild;
        _rightChild = rightChild;
    }


}
