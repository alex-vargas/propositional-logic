import java.io.*;
import java.util.*;

public class Solution{
    private static void mprintln(String toPrint){
    	System.out.println(toPrint);
    }
    private static void mprintln(Object toPrint){
    	mprintln(String.valueOf(toPrint));
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
    private static void generalMenu(){
        mprintln("Programming Assignment * Course: CS5303 - Logical Foundations of Computer Science");
    }
    private static void printMenu(){
        generalMenu();
        mprintln("Menu:");
        mprintln("1 Transform to CNF");
        mprintln("2 Transform to DNF");
        mprintln("3 Transform to Full CNF");
        mprintln("4 Transform to Full DNF");
        mprintln("5 Evaluate formula given truth values of the atoms");
        mprintln("6 Decide if formula is satisfiable, a tautology, or a contradiction");
        mprintln("7 Change formula");
        mprintln("8 Exit");
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
    public static void callMe() throws IOException{
        //Start of the program, user has to enter a propositional formula
        if(formula.equals("")){
            generalMenu();
            formula = inputFormula();
        }else{
            //If formula has been entered, show options
            printMenu();
            String action = readValue();
            if(true){
                //Call test cases
                callTestCases(action);
            }else
                executeAction(action);
        }
        mprintln("Press enter to go back to menu");
        readValue();
        callMe();
    }
    private static void executeAction(String action){
        switch(action){
            case "1":
                mprintln("Transforming " + formula + " to CNF:");
                transformToCNF();
                mprintln("NNF formula: " + formulaNNF);
                mprintln("CNF formula: " + formulaCNF);
                break;
            case "2":
                mprintln("Transforming " + formula + " to DNF:");
                transformToDNF();
                mprintln("NNF formula: " + formulaNNF);
                mprintln("DNF formula: " + formulaDNF);
                break;
            case "3":
                mprintln("Transforming " + formula + " to Full CNF");
                transformToFullCNF();
                mprintln("NNF formula: " + formulaNNF);
                mprintln("CNF formula: " + formulaCNF);
                mprintln("Full CNF formula: " + formulaFullCNF);
                break;
            case "7":
                clearFormulas();
                break;
            case "8":
                System.exit(0);
                break;
        }
    }
    private static void clearFormulas(){
        formula = formulaNNF = formulaCNF = formulaDNF = formulaFullDNF = formulaFullCNF = "";
    }
    private static void transformToNNF(){
        formulaNNF = formula;
        removeImplication();
        applyDeMorganLaw();
    }
    private static void transformToCNF(){
        String operator = "&";
        String negOperator = "|";
        //Following algorithm from wikipedia https://en.wikipedia.org/wiki/Conjunctive_normal_form
        if(formulaNNF.equals(""))
            transformToNNF();
        if(formulaCNF.equals(""))
            formulaCNF = distribute(negOperator, formulaNNF);

        formulaCNF = removeParentheses(formulaCNF, operator, negOperator);
        formulaCNF = removeDuplicates(formulaCNF, operator, negOperator);
        formulaCNF = addSpaces(formulaCNF);
    }
    private static void transformToDNF(){
        String operator = "|";
        String negOperator = "&";
        if(formulaNNF.equals(""))
            transformToNNF();
        if(formulaDNF.equals(""))
            formulaDNF = distribute(negOperator, formulaNNF);

        formulaDNF = removeParentheses(formulaDNF, operator, negOperator);
        formulaDNF = removeDuplicates(formulaDNF, operator, negOperator);
        formulaDNF = addSpaces(formulaDNF);
    }
    private static void transformToFullCNF(){
        String operator = "&";
        String negOperator = "|";
        if(formulaCNF.equals(""))
            transformToCNF();
        if(formulaFullCNF.equals(""))
            formulaFullCNF = completeClauses(operator, formulaCNF.concat(operator)); //Iterate all clauses adding an extra operator to end of formula
        formulaFullCNF = formulaFullCNF.substring(0, formulaFullCNF.length() - 1); //Remove extra operator
        formulaFullCNF = addSpaces(formulaFullCNF);
    }
    private static String completeClauses(String operator, String formula){
        int i, index = 0;
        String negOperator = operator.equals("&") ? "|" : "&";
        formula = formula.replaceAll("\\s+","");
        //Algorithm:
        //Find different atoms
        //Iterate for every parentheses that contains atoms, add every missing atom and its negation
        String equivalentExpression = "";
        Set<String> mAtoms = getAtoms(formula, false);
        for (i = -1; (i = formula.indexOf(operator, i + 1)) != -1; i++) {
            String clause = formula.substring(index, i);
            equivalentExpression = "";
            //iterate all atoms in formula, if missing one add it
            for(String mAtom : mAtoms){
                if(!clause.contains(mAtom)){
                    //add missing atom
                    if(equivalentExpression.length() > 0)
                        equivalentExpression = equivalentExpression.concat(operator);
                    equivalentExpression = equivalentExpression.concat(distribute(negOperator,
                        clause + negOperator + "(" + mAtom + operator + "!" + mAtom + ")"));
                    break;
                }
            }
            if(!equivalentExpression.equals("")){
                StringBuilder mStrBldr = new StringBuilder(formula);
                mStrBldr.replace(index, i, equivalentExpression);
                formula = mStrBldr.toString();
                formula = completeClauses(operator, formula.substring(0, formula.length()));
                i = index;
                break;
            }else
                index = i + 1;

        }
        return formula;
    }
    private static Set<String> getAtoms(String formula, boolean includeNegation){
        Set<String> mAtoms = new HashSet<String>();
        String mAtom = "";
        for(int i = 0; i < formula.length(); i++){
            mAtom = String.valueOf(formula.charAt(i));
            if(i > 0 && includeNegation && formula.charAt(i - 1) == '!')
                mAtom = "!".concat(mAtom);
            if((mAtom.length() > 1 && mAtom.contains("!")) || (mAtom.length() == 1 && Character.isLetter(mAtom.charAt(0))))
                mAtoms.add(mAtom);
        }
        return mAtoms;
    }
    private static String removeDuplicates(String formula, String operator, String negOperator){
        formula = formula.concat(operator);
        Set<String> mClauseSet = new HashSet<String>();
        String equivalentExpression = "";
        int index = 0;

        mprintln("removing duplicates formula " + formula + " operator  " + operator);
        for (int i = -1; (i = formula.indexOf(operator, i + 1)) != -1; i++) {
            String mClause = formula.substring(index, i);
            mClause = removeAtomsDuplicateOnClause(mClause, operator, negOperator);
            if(!mClause.isEmpty())
                mClauseSet.add(mClause);
            index = i + 1;
        }
        for(String mClause : mClauseSet){
            if(!equivalentExpression.equals(""))
                equivalentExpression = equivalentExpression.concat(operator);
            equivalentExpression = equivalentExpression.concat(mClause);
        }
        return equivalentExpression;
    }
    private static String removeAtomsDuplicateOnClause(String formula, String operator, String negOperator){
        Set<String> mAtoms = getAtoms(formula, true);
        Set<String> mAtoms2 = getAtoms(formula, true);
        String equivalentExpression = "";
        StringBuilder mStrBldr = new StringBuilder("");

        for(String mAtom : mAtoms2){
            if(mAtom.contains("!") && mAtoms.contains(mAtom.replace("!",""))){
                mAtoms.remove(mAtom.replace("!",""));
                mAtoms.remove(mAtom);
            }
        }
        mStrBldr.append("(");
        for(String mAtom : mAtoms){
            if(mStrBldr.length() != 1)
                mStrBldr.append(negOperator);
            mStrBldr.append(mAtom);
        }
        if(mStrBldr.length() != 1)
            mStrBldr.append(")");
        else
            mStrBldr = new StringBuilder("");
        equivalentExpression = mStrBldr.toString();
        equivalentExpression = removeParentheses(equivalentExpression, operator, negOperator);
        return equivalentExpression;
    }
    private static String distribute(String operator, String formula){
        int i,j;
        for (i = -1; (i = formula.indexOf(")" + operator, i + 1)) != -1; i++) {
            int indexOfEndOfParen = i;
            char candidateToNextSymbol = formula.charAt(indexOfEndOfParen + 2);
            if(candidateToNextSymbol != '('){
                String candidateToNextSymbolStr = getSymbolToDistribute(indexOfEndOfParen, formula);
                String equivalentExpression = "";
                int indexOfStartInnerClause = getIndexOfStartInnerClause(indexOfEndOfParen - 1, formula);
                for(j = indexOfStartInnerClause + 1; j < indexOfEndOfParen; j++){
                    char nextSymbol = formula.charAt(j);
                    if(Character.isLetter(nextSymbol)){
                        equivalentExpression = equivalentExpression.concat("(" + String.valueOf(nextSymbol) + 
                            candidateToNextSymbolStr + ")");
                    }else if(nextSymbol == '!'){
                        j++;
                        equivalentExpression = equivalentExpression.concat("(" + "!" + String.valueOf(formula.charAt(j)) + 
                            candidateToNextSymbolStr + ")");
                    }else if(nextSymbol == '|' || nextSymbol == '&'){
                        equivalentExpression = equivalentExpression.concat(String.valueOf(nextSymbol));
                    }else if(nextSymbol == '('){
                        int indexOfEndInnerInnerClause = getIndexOfEndInnerClause(j + 1, formula);
                        equivalentExpression = equivalentExpression.concat(formula.substring(j, indexOfEndInnerInnerClause + 1));
                        equivalentExpression = equivalentExpression.concat(candidateToNextSymbolStr);
                        j = indexOfEndInnerInnerClause;
                    }
                }
                String stringToReplace = formula.substring(indexOfStartInnerClause,
                    indexOfEndOfParen + candidateToNextSymbolStr.length() + 1);
                formula = formula.replace(stringToReplace, equivalentExpression);
            }else{
                String negOperator = operator.equals("&") ? "|" : "&";
                char candidatePreviousSymbol = formula.charAt(indexOfEndOfParen - 1);
                int indexOfStartOfP = getIndexOfStartInnerClause(indexOfEndOfParen - 1, formula);
                String stringToReplace = formula.substring(indexOfStartOfP + 1, indexOfEndOfParen);
                int indexOfEndInnerClause = getIndexOfEndInnerClause(indexOfEndOfParen + 3, formula);
                String stringMixing = formula.substring(indexOfEndOfParen + 3, indexOfEndInnerClause);

                Set<String> mAtoms = getAtoms(stringMixing, true);
                if(candidatePreviousSymbol == ')'){
                    String equivalentExpression = "";
                    for (j = -1; (j = stringToReplace.indexOf("(", j + 1)) != -1; j++) {
                        int indexOfStartInnerInnerClause = j;
                        int indexOfEndInnerInnerClause = getIndexOfEndInnerClause(indexOfStartInnerInnerClause + 1, stringToReplace);
                        equivalentExpression = equivalentExpression.concat(equivalentExpression.length() > 0 ? negOperator : "");
                        equivalentExpression = equivalentExpression.concat("(" + stringToReplace
                            .substring(indexOfStartInnerInnerClause, indexOfEndInnerInnerClause + 1) + operator 
                            + formula.substring(indexOfEndOfParen + 2, indexOfEndInnerClause + 1) + ")");
                    }
                    formula = formula.replace("(" + stringToReplace.concat(")" + operator + "("
                        + stringMixing + ")"), equivalentExpression);
                    i = -1;
                }else{
                    if(stringToReplace.contains(operator)){
                        String equivalentExpression = "";
                        Set<String> mAtomsToReplace = getAtoms(stringMixing, true);
                        for(String mAtom : mAtomsToReplace){
                            if(equivalentExpression.length() > 1)
                                equivalentExpression = equivalentExpression.concat(negOperator);
                            equivalentExpression = equivalentExpression.concat("(" + stringToReplace + operator + mAtom + ")");
                        }
                        formula = formula.replace((formula.substring(indexOfStartOfP, indexOfEndInnerClause + 1)), equivalentExpression);
                    }else{
                        Set<String> mAtomsToReplace = getAtoms(stringToReplace, true);
                        String equivalentExpression = "(";
                        for(String mAtomToReplace : mAtomsToReplace){
                            for(String mAtom : mAtoms){
                                if(equivalentExpression.length() > 1)
                                    equivalentExpression = equivalentExpression.concat(negOperator);
                                equivalentExpression = equivalentExpression.concat("(" + mAtomToReplace + operator + mAtom + ")");
                            }
                        }
                        equivalentExpression = equivalentExpression.concat(")");
                        formula = formula.replace((formula.substring(indexOfStartOfP, indexOfEndOfParen + 1))
                            .concat(operator + formula.substring(indexOfEndOfParen + 2, indexOfEndInnerClause + 1)), equivalentExpression);
                    }
                }
            }
        }
        //Will move letter to right of parentheses and iterate distribute
        for (i = -1; (i = formula.indexOf(operator + "(", i + 1)) != -1; i++) {
            int indexOfStartOfP = i;
            char previousSymbol = formula.charAt(i - 1);
            if(Character.isLetter(previousSymbol)){
                String equivalentExpression = "";
                //find end of innerclause
                int indexOfEndInnerClause = getIndexOfEndInnerClause(i + 2, formula);
                equivalentExpression = equivalentExpression.concat(formula.substring(i + 1, indexOfEndInnerClause + 1));

                //getting atoms to distribute
                String atomsToDistribute = getSymbolToDistributeBackwards(indexOfStartOfP, formula);
                String stringToReplace = formula.substring(indexOfStartOfP - atomsToDistribute.length() + 1, indexOfEndInnerClause + 1);
                equivalentExpression = equivalentExpression.concat(atomsToDistribute);
                equivalentExpression = distribute(operator, equivalentExpression);
                formula = formula.replace(stringToReplace, equivalentExpression);
            }
        }
        for (i = -1; (i = formula.indexOf(")" + operator, i + 1)) != -1; i++) {
            if(Character.isLetter(formula.charAt(i + 2)))
                formula = distribute(operator, formula);
        }
        return formula;
    }
    private static String getSymbolToDistributeBackwards(int index, String formula){
        String symbolToDistribute = "";
        char symbol = formula.charAt(index);
        while(symbol != '('){
            if(symbol == '!')
                symbolToDistribute = symbolToDistribute.substring(0, symbolToDistribute.length() - 1)
                    .concat(String.valueOf(symbol))
                    .concat(String.valueOf(symbolToDistribute.charAt(symbolToDistribute.length() - 1)));
            else
                symbolToDistribute = symbolToDistribute.concat(String.valueOf(symbol));
            index--;
            if(index == -1)
                break;
            symbol = formula.charAt(index);
        }
        return symbolToDistribute;

    }
    private static String getSymbolToDistribute(int index, String formula){
        String symbolToDistribute = "";
        index++;
        char symbol = formula.charAt(index);
        while(symbol != ')'){
            symbolToDistribute = symbolToDistribute.concat(String.valueOf(symbol));
            index++;
            if(index == formula.length())
                break;
            symbol = formula.charAt(index);
        }
        return symbolToDistribute;
    }
    /*
    *   Remove double parentheses
    */
    private static String addSpaces(String formula){
        //add spaces between letters and operators
        String formulaSpaces = "";
        for(int i = 0; i < formula.length(); i++){
            char symbol = formula.charAt(i);
            if(symbol == '&' || symbol == '|')
                formulaSpaces = formulaSpaces.concat(" ");
            formulaSpaces = formulaSpaces.concat(String.valueOf(symbol));
            if(symbol == '&' || symbol == '|')
                formulaSpaces = formulaSpaces.concat(" ");
        }
        return formulaSpaces;
    }
    private static String removeParentheses(String formula, String operator, String negOperator){
        if(formula.startsWith("(")){
            int indexOfEndOfParen = getIndexOfEndInnerClause(1, formula);
            if(indexOfEndOfParen == formula.length() - 1 && !formula.contains(negOperator))
                formula = formula.substring(1, formula.length() - 1);
        }
        for(int i = -1; (i = formula.indexOf("((", i + 1)) != -1; i++) {
            int indexOfEndOfParen = getIndexOfEndInnerClause(i + 1, formula);
            if(i == 0 || formula.charAt(i - 2) == ')'){
                formula = formula.replace("((", "(").replace("))", ")");
                i = -1;
            }
        }
        formula = removeParenthesesClause(formula, operator, negOperator);
        return formula;
    }
    //Remove parentheses inside a clause
    private static String removeParenthesesClause(String formula, String operator, String negOperator){
        formula = formula.concat(operator);
        int index = 0;
        for(int i = -1; (i = formula.indexOf("(", i + 1)) != -1; i++) {
            int indexOfEndOfParen = getIndexOfEndInnerClause(i + 1, formula);
            String clause = formula.substring(i, indexOfEndOfParen + 1);
            String equivalentExpression = clause;
            if(equivalentExpression.contains("(") && !equivalentExpression.contains(negOperator))
                equivalentExpression = equivalentExpression.replaceAll("[()]", "");
            if(equivalentExpression.contains(operator)){
                StringBuilder mStr = new StringBuilder(equivalentExpression);
                mStr.deleteCharAt(equivalentExpression.length() - 1);
                mStr.deleteCharAt(0);
                equivalentExpression = mStr.toString();
            }
            if(!equivalentExpression.equals(""))
                formula = formula.replace(clause, equivalentExpression);
        }
        formula = formula.substring(0, formula.length() - 1);
        return formula;
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
                        int indexOfEndInnerNegatedClause = getIndexOfEndInnerClause(i + 1, formulaNNF);
                        equivalentExpression = equivalentExpression.concat("!");
                        equivalentExpression = equivalentExpression.concat(formulaNNF.substring(i - 1, indexOfEndInnerNegatedClause + 1));
                        i = indexOfEndInnerNegatedClause;
                    }
                }else if(nextSymbol == '('){
                    int indexOfEndInnerNegatedClause = getIndexOfEndInnerClause(i + 1, formulaNNF);
                    equivalentExpression = equivalentExpression.concat("!");
                    equivalentExpression = equivalentExpression.concat(formulaNNF.substring(i, indexOfEndInnerNegatedClause + 1));
                    i = indexOfEndInnerNegatedClause;
                }
            }
            // equivalentExpression = equivalentExpression.concat(")");
            String stringToReplace = formulaNNF.substring(indexOfStartNegationParen, indexOfEndNegationParen);
            formulaNNF = formulaNNF.replace(stringToReplace, equivalentExpression);
            applyDeMorganLaw();
        }
    }
    private static int getIndexOfStartInnerClause(int index, String formula){
        int numberOfParentheses = 1;
        char nextSymbol;
        while(numberOfParentheses > 0){
            if(index >= formula.length())
                return -1;
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
            if(index >= formula.length())
                return -1;
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

    private static void callTestCases(String action){
        if(formulaNNF.equals(""))
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
            ms = "(a|b)&(a|(c&d))";
            fomulaTestCase.add(ms);
            ms = "a&(b&c)";
            fomulaTestCase.add(ms);
            ms = "a&(b|c)";
            fomulaTestCase.add(ms);
            ms = "(b&c)&a";
            fomulaTestCase.add(ms);
            ms = "(b|c)&a";
            fomulaTestCase.add(ms);
            ms = "a&(b|(c|d))";
            fomulaTestCase.add(ms);
            ms = "(a&b)|(a&(c|d))";
            fomulaTestCase.add(ms);
            ms = "!!a&(b|c)";
            fomulaTestCase.add(ms);
            ms = "!(!a|!b|!c)";
            fomulaTestCase.add(ms);
            ms = "!((a&b)->c)";
            fomulaTestCase.add(ms);
            ms = "!((a&b)->!c)";
            fomulaTestCase.add(ms);
            ms = "!(a)";
            fomulaTestCase.add(ms);
            ms = "(a&b&(c|d))";
            fomulaTestCase.add(ms);
            ms = "a&(b|(c|b))";
            fomulaTestCase.add(ms);
            ms = "a&b";
            fomulaTestCase.add(ms);
            ms = "a|b";
            fomulaTestCase.add(ms);
            ms = "(a&(b|!b))&(b&(a|!a))";
            fomulaTestCase.add(ms);
            ms = "(b&a)|(c&a)";
            fomulaTestCase.add(ms);
            ms = "(a&b)|(a&c)|(b&c)";
            fomulaTestCase.add(ms);
            ms = "(a|b)&(a|(c&d))";
            fomulaTestCase.add(ms);
        }
        for(int i = 0; i < fomulaTestCase.size();i++){
            clearFormulas();
            formula = fomulaTestCase.get(i);
            mprintln("");
            mprintln("Testing formula " + i + " = " + formula);
            executeAction(action);
        }
    }
}