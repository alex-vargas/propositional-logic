import java.io.*;
import java.util.*;

public class Solution{
    private static void mprintln(String toPrint){
    	System.out.println(toPrint);
    }
    private static void mprintln(Object toPrint){
    	mprintln(String.valueOf(toPrint));
    }
    private static void generalMenu(){
        mprintln("Programming Assignment * Course: CS5303 - Logical Foundations of Computer Science");
    }
    private static void printMenu(){
        generalMenu();
        mprintln("Menu:");
        mprintln("1 Transform to CNF");
        mprintln("2 Transform to DNF");
        mprintln("3 Transform to full CNF");
        mprintln("4 Transform to full DNF");
        mprintln("5 Evaluate formula given truth values of the atoms");
        mprintln("6 Decide if formula is satisfiable, a tautology, or a contradiction");
        mprintln("7 Change formula");
        mprintln("8 Exit");
    }
    public static void main(String[] args) throws IOException{
    	callMe();
    }
    //Declaration of required formulas
    private static String formula = "";
    private static String formulaCNF = "";
    private static String formulaFullCNF = "";
    private static String formulaDNF = "";
    private static String formulaFullDNF = "";
    private static String formulaNNF = "";

    private static ArrayList<String> fomulaTestCase = new ArrayList<String>();

    public static void callMe() throws IOException{
        //Start of the program, user has to enter a propositional formula
        if(formula.equals("")){
            generalMenu();
            formula = inputFormula();
        }else{
            //If formula has been entered, show options
            printMenu();
            String action = readValue();
            switch(action){
                case "1":
                    transformToCNF();
                    break;
                case "7":
                    clearFormulas();
                    break;
                case "8":
                    System.exit(0);
                    break;
            }
        }
        mprintln("Press enter to go back to menu");
        readValue();
        callMe();
    }
    private static void clearFormulas(){
        formula = formulaNNF = formulaCNF = formulaDNF = formulaFullDNF = formulaFullCNF = "";
    }
    private static void transformToCNF(){
        mprintln("Transforming " + formula + " to CNF");
        //Following algorithm from wikipedia https://en.wikipedia.org/wiki/Conjunctive_normal_form
        if(formulaNNF.equals(""))
            // transformToNNF();
            //test NNF
        {
            fomulaTestCase.add("a");
            fomulaTestCase.add("a->b");
            fomulaTestCase.add("a->!b");
            fomulaTestCase.add("a->!!b");
            fomulaTestCase.add("!a");
            fomulaTestCase.add("!a->b");
            fomulaTestCase.add("!a->!b");
            fomulaTestCase.add("!a->!!b");
            fomulaTestCase.add("a->(b&c)");
            fomulaTestCase.add("a->!(b&c)");
            fomulaTestCase.add("a->!!(b&c)");
            fomulaTestCase.add("a->!(!b&!c)");
            fomulaTestCase.add("!a->(b&c)");
            fomulaTestCase.add("!a->!(b&c)");
            fomulaTestCase.add("!a->!!(b&c)");
            fomulaTestCase.add("!a->!(!b&!c)");
            fomulaTestCase.add("a->!c");
            String ms = "(a&b)->c";
            fomulaTestCase.add(ms);
            ms = "(a&b)->!c";
            fomulaTestCase.add(ms);
            ms = "(a->b)&(a->c)";
            fomulaTestCase.add(ms);
            ms = "a|(b&c)";
            fomulaTestCase.add(ms);
            ms = "a&(b|c)";
            fomulaTestCase.add(ms);
            ms = "a|(b&(c&D))";
            fomulaTestCase.add(ms);
            ms = "(a|b)&a|(c&d)";
            fomulaTestCase.add(ms);

            for(int i = 0; i < fomulaTestCase.size();i++){
                clearFormulas();
                formula = fomulaTestCase.get(i);
                mprintln("Testing formula " + i + " = " + formula);
                transformToNNF();
                mprintln("NNF= " + formulaNNF);
                mprintln("After distributing or");
                distributeOR();
                mprintln(formulaCNF);
            }
        }
        // if(formulaCNF.equals(""))
        //     distributeOR();
        // formulaCNF;
    }
    private static void distributeOR(){
        int i, j;
        if(formulaCNF.equals(""))
            formulaCNF = formulaNNF;
        for (i = -1; (i = formulaCNF.indexOf("|(", i + 1)) != -1; i++) {
            int indexOfStartOrParen = i;
            char previousSymbol = formulaCNF.charAt(indexOfStartOrParen - 1);
            if(previousSymbol != ')'){
                String previousSymbolStr = String.valueOf(previousSymbol);
                int negationPrevSymbol = 0;
                if(indexOfStartOrParen > 1 && formulaCNF.charAt(indexOfStartOrParen - 2) == '!')
                {
                    negationPrevSymbol++;
                    indexOfStartOrParen--;
                    previousSymbolStr = "!".concat(String.valueOf(previousSymbol));
                }
                String equivalentExpression = "";
                int indexOfEndInnerClause = getIndexOfEndInnerClause(indexOfStartOrParen + 2 + negationPrevSymbol, formulaCNF);
                boolean negatedNextSymbol = false;
                String negatedNextSymbolStr = "";

                for(j = indexOfStartOrParen + 2 + negationPrevSymbol; j < indexOfEndInnerClause; j++){
                    char nextSymbol = formulaCNF.charAt(j);
                    if(Character.isLetter(nextSymbol)){
                        if(equivalentExpression.endsWith(")"))
                            if(negatedNextSymbol)
                                equivalentExpression = equivalentExpression.concat(String.valueOf(formulaCNF.charAt(j - 2)));
                            else
                                equivalentExpression = equivalentExpression.concat(String.valueOf(formulaCNF.charAt(j - 1)));
                        if(negatedNextSymbol)
                            negatedNextSymbolStr = "!";
                        equivalentExpression = equivalentExpression.concat("(" + previousSymbolStr + "|"
                            + negatedNextSymbolStr + String.valueOf(nextSymbol) + ")");
                        negatedNextSymbolStr = "";
                        negatedNextSymbol = false;
                    }else if(nextSymbol == '('){
                        if(equivalentExpression.endsWith(")"))
                            equivalentExpression = equivalentExpression.concat(String.valueOf(formulaCNF.charAt(j - 1)));
                        equivalentExpression = equivalentExpression.concat("(" + previousSymbolStr + "|(");
                        int indexOfEndInnerInnerClause = getIndexOfEndInnerClause(j + 1, formulaCNF);
                        equivalentExpression = equivalentExpression.concat(formulaCNF.substring(j + 1, indexOfEndInnerInnerClause));
                        equivalentExpression = equivalentExpression.concat(")");
                        j = indexOfEndInnerInnerClause;
                    }else if(nextSymbol == '!')
                        negatedNextSymbol = true;
                }
                // equivalentExpression = equivalentExpression.concat(")");
                String stringToReplace = formulaCNF.substring(indexOfStartOrParen - 1 - negationPrevSymbol, j+1);
                formulaCNF = formulaCNF.replace(stringToReplace, equivalentExpression);
            }
        }
        for (i = -1; (i = formulaCNF.indexOf("|(", i + 1)) != -1; i++) {
            if(Character.isLetter(formulaCNF.charAt(i - 1)))
                distributeOR();
        }
        for (i = -1; (i = formulaCNF.indexOf(")|", i + 1)) != -1; i++) {
            int indexOfEndOfParen = i;
            char candidateToNextSymbol = formulaCNF.charAt(indexOfEndOfParen + 2);
            if(candidateToNextSymbol != '('){
                String candidateToNextSymbolStr = String.valueOf(candidateToNextSymbol);
                if(candidateToNextSymbol == '!')
                {
                    indexOfEndOfParen++;
                    candidateToNextSymbolStr = "!".concat(String.valueOf(formulaCNF.charAt(indexOfEndOfParen + 2)));
                }
                String equivalentExpression = "";
                int indexOfStartInnerClause = getIndexOfStartInnerClause(indexOfEndOfParen - 2, formulaCNF);
                mprintln("index of start: " + indexOfStartInnerClause + " - formula: " + formulaCNF);
                for(j = indexOfStartInnerClause + 1; j < indexOfEndOfParen; j++){
                    char nextSymbol = formulaCNF.charAt(j);
                    if(Character.isLetter(nextSymbol)){
                        equivalentExpression = equivalentExpression.concat("(" + candidateToNextSymbolStr + "|"
                            + String.valueOf(nextSymbol) + ")");
                    }else if(nextSymbol == '!'){
                        j++;
                        equivalentExpression = equivalentExpression.concat("(" + candidateToNextSymbolStr + "|"
                            + "!" + String.valueOf(formulaCNF.charAt(j)) + ")");
                    }else if(nextSymbol == '|' || nextSymbol == '&'){
                        equivalentExpression = equivalentExpression.concat(String.valueOf(nextSymbol));
                    }else if(nextSymbol == '('){
                        int indexOfEndInnerInnerClause = getIndexOfEndInnerClause(j + 1, formulaCNF);
                        equivalentExpression = equivalentExpression.concat(formulaCNF.substring(j, indexOfEndInnerInnerClause + 1));
                        j = indexOfEndInnerInnerClause;
                    }
                }
                String stringToReplace = formulaCNF.substring(indexOfStartInnerClause, indexOfEndOfParen + 3);
                formulaCNF = formulaCNF.replace(stringToReplace, equivalentExpression);
            }
        }
        for (i = -1; (i = formulaCNF.indexOf(")|", i + 1)) != -1; i++) {
            if(Character.isLetter(formulaCNF.charAt(i + 2)))
                distributeOR();
        }
        
    }
    /*
    *   Remove double parentheses
    */
    private static String cleanFormula(String formulaToClean){
        formulaToClean = formulaToClean.replace("((", "(");
        formulaToClean = formulaToClean.replace("))", ")");
        return formulaToClean;
    }
    private static void transformToNNF(){
        formulaNNF = formula;
        removeImplication();
        applyDeMorganLaw();
    }
    private static void applyDeMorganLaw(){
        //removeDoubleNegation()
        formulaNNF = formulaNNF.replace("!!" , "");
        //applyNegationToParentheses
        applyNegationToParentheses();
    }
    private static void applyNegationToParentheses(){
        if(formulaNNF.contains("!(")){
            int indexOfStartNegationParen = formulaNNF.indexOf("!(");
            int indexOfEndNegationParen = indexOfStartNegationParen + 2;
            char nextSymbol;
            //get clause affected by negation
            indexOfEndNegationParen = getIndexOfEndInnerClause(indexOfEndNegationParen, formulaNNF);
            String equivalentExpression = "(";
            //apply negations
            for(int i = indexOfStartNegationParen + 2; i < indexOfEndNegationParen; i++){
                nextSymbol = formulaNNF.charAt(i);
                if(Character.isLetter(nextSymbol))
                    equivalentExpression = equivalentExpression.concat("!" + String.valueOf(nextSymbol));
                else if(nextSymbol == '&')
                    equivalentExpression = equivalentExpression.concat("|");
                else if(nextSymbol == '|')
                    equivalentExpression = equivalentExpression.concat("&");
                else if(nextSymbol == '!'){
                    i++;
                    nextSymbol = formulaNNF.charAt(i);
                    if(Character.isLetter(nextSymbol))
                        equivalentExpression = equivalentExpression.concat(String.valueOf(nextSymbol));
                    else if(nextSymbol == '('){
                        int indexOfEndInnerNegatedClause = getIndexOfEndInnerClause(i, formulaNNF);
                        equivalentExpression = equivalentExpression.concat(formulaNNF.substring(i - 1, indexOfEndInnerNegatedClause));
                    }
                }
            }
            equivalentExpression = equivalentExpression.concat(")");
            String stringToReplace = formulaNNF.substring(indexOfStartNegationParen, indexOfEndNegationParen + 1);
            formulaNNF = formulaNNF.replace(stringToReplace, equivalentExpression);
            applyDeMorganLaw();
        }
    }
    private static int getIndexOfStartInnerClause(int index, String formula){
        int numberOfParentheses = 1;
        char nextSymbol;
        while(numberOfParentheses > 0){
            nextSymbol = formula.charAt(index);
            if(nextSymbol == '(')
                numberOfParentheses--;
            else if(nextSymbol == ')')
                numberOfParentheses++;
            index--;
        }
        index++;
        return index;
    }
    private static int getIndexOfEndInnerClause(int index, String formula){
        int numberOfParentheses = 1;
        char nextSymbol;
        while(numberOfParentheses > 0){
            nextSymbol = formula.charAt(index);
            if(nextSymbol == ')')
                numberOfParentheses--;
            else if(nextSymbol == '(')
                numberOfParentheses++;
            index++;
        }
        index--;
        return index;
    }
    private static void removeImplication(){
        //removing p implies q
        if(formulaNNF.contains("->")){
            int indexOfImpl = formulaNNF.indexOf("-");
            int indexOfStartOfP = indexOfImpl - 1;
            char previousSymbol = formulaNNF.charAt(indexOfStartOfP);
            String p = "";
            //if previous symbol is closing parentheses, find corresponding opening parentheses and use it as 'p'
            if(previousSymbol == ')'){
                while(previousSymbol != '('){
                    indexOfStartOfP--;
                    if(indexOfStartOfP == -1)
                        break;
                    previousSymbol = formulaNNF.charAt(indexOfStartOfP);
                }
            }
            //If before previousSymbol there is a not, find where the first 'negation' is
            if(indexOfStartOfP != 0)
                previousSymbol = formulaNNF.charAt(indexOfStartOfP - 1);
            if(previousSymbol == '!'){
                while(previousSymbol == '!'){
                    indexOfStartOfP--;
                    if(indexOfStartOfP == -1){
                        indexOfStartOfP ++;
                        break;
                    }
                    previousSymbol = formulaNNF.charAt(indexOfStartOfP);
                }
            }
            p = formulaNNF.substring(indexOfStartOfP, indexOfImpl);
            int indexOfEndOfQ = indexOfImpl + 2;
            char nextSymbol = formulaNNF.charAt(indexOfEndOfQ );
            String q = "";
            //If next symbol is negation
            if(nextSymbol == '!'){
                while(nextSymbol == '!'){
                    indexOfEndOfQ++;
                    nextSymbol = formulaNNF.charAt(indexOfEndOfQ);
                }
            }
            //if next symbol is opening parentheses, find corresponding closing parentheses and use it as 'q'
            if(nextSymbol == '('){
                while(nextSymbol != ')'){
                    indexOfEndOfQ++;
                    nextSymbol = formulaNNF.charAt(indexOfEndOfQ);
                }
            }
            q = formulaNNF.substring(indexOfImpl + 2, indexOfEndOfQ + 1);
            String implicationEquivalency = "(!" + p + "|" + q + ")";
            formulaNNF = formulaNNF.replace(p + "->" + q, implicationEquivalency);
            removeImplication();
        }
    }
    private static String inputFormula()throws IOException{
        mprintln("Instructions: Please enter a propositional formula");
        mprintln("Atom: Letter from the roman alphabet A = {a,...,z}");
        mprintln("Negation: not (p) is represented using !p");
        mprintln("Conjunction: p and q is represented using (p&q)");
        mprintln("Disjunction: p or q is represented using (p|q)");
        mprintln("Implication: p implies q is represented using (p->q)");
        mprintln("Note: Parentheses are allowed");
        mprintln("Your formula: ");
        return readValue();
    }
    private static String readValue() throws IOException{        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }

}