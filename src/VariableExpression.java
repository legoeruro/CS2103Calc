public class VariableExpression implements Expression {
    public Expression deepCopy(){
        return new VariableExpression();
    }

    public String convertToString(int indentLevel){
        String conv = "";
        for (int i = 0; i<indentLevel; ++i){
            conv += "\t";
        }
        conv +="x" + "\n";
        return conv;
    }

    public double evaluate(double x){
        return x;
    }
    
    public Expression differentiate(){
        return new LiteralExpression(1);
    }
}
