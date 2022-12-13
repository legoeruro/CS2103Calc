public class LiteralExpression implements Expression {
    private double _value;

    public LiteralExpression(double value){
        _value = value;
    }

    public Expression deepCopy(){
        return new LiteralExpression(_value);
    }

    public String convertToString(int indentLevel){
        String conv = "";
        for (int i = 0; i<indentLevel; ++i){
            conv += "\t";
        }
        conv += _value + "\n";
        return conv;
    }

    public double evaluate(double x){
        return _value;
    }
    
    public Expression differentiate(){
        return new LiteralExpression(0);
    }
}
