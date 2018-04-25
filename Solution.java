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
    private static String formulaEvaluated = "";
    private static String satResult = "";

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
            //change to true to execute test cases
            if(false){
                //Call test cases
                callTestCases(action);
            }else
                executeAction(action);
        }
        mprintln("Press enter to go back to menu");
        readValue();
        callMe();
    }
    private static void executeAction(String action) throws IOException{
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
            case "4":
                mprintln("Transforming " + formula + " to Full DNF");
                transformToFullDNF();
                mprintln("NNF formula: " + formulaNNF);
                mprintln("DNF formula: " + formulaDNF);
                mprintln("Full DNF formula: " + formulaFullDNF);
                break;
            case "5":
                mprintln("Evaluating formula: " + formula);
                evaluateFormula();
                mprintln("NNF formula: " + formulaNNF);
                mprintln("CNF formula: " + formulaCNF);
                mprintln("After evaluation: " + formulaEvaluated);
                break;
            case "6":
                mprintln("Evaluating if formula: " + formula + " is satisfiable, tautology or contradiction");
                satEvaluate();
                mprintln("NNF formula: " + formulaNNF);
                mprintln("CNF formula: " + formulaCNF);
                mprintln("Formula is " + satResult);
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
        formula = formulaNNF = formulaCNF = formulaDNF = formulaFullDNF = formulaFullCNF = formulaEvaluated = "";
    }
    private static void transformToNNF(){
        formulaNNF = formula;
        removeImplication();
        applyDeMorganLaw();
        formulaNNF = removeParenthesesClause(formulaNNF, "&", "|");
        if(!formulaNNF.startsWith("(") || !formulaNNF.endsWith(")"))
            formulaNNF = "(" + formulaNNF + ")";
    }
    private static void transformToCNF(){
        String operator = "&";
        String negOperator = "|";
        //Following algorithm from wikipedia https://en.wikipedia.org/wiki/Conjunctive_normal_form
        if(formulaNNF.equals(""))
            transformToNNF();
        if(formulaCNF.equals(""))
            formulaCNF = iterateInnerSymbols(operator, negOperator, formulaNNF);
        if(!validFormula(operator, negOperator, formulaCNF))
            formulaCNF = iterateInnerSymbols(operator, negOperator, formulaCNF);
        formulaCNF = removeParenthesesClause(formulaCNF, operator, negOperator);
        formulaCNF = removeDuplicates(formulaCNF, operator, negOperator);
        formulaCNF = addSpaces(formulaCNF);
    }
    private static void transformToDNF(){
        String operator = "|";
        String negOperator = "&";
        if(formulaNNF.equals(""))
            transformToNNF();
        if(formulaDNF.equals(""))
            formulaDNF = iterateInnerSymbols(operator, negOperator, formulaNNF);
        if(!validFormula(operator, negOperator, formulaDNF))
            formulaDNF = iterateInnerSymbols(operator, negOperator, formulaDNF);
        formulaDNF = removeParenthesesClause(formulaDNF, operator, negOperator);
        formulaDNF = removeDuplicates(formulaDNF, operator, negOperator);
        formulaDNF = addSpaces(formulaDNF);
    }
    private static void transformToFullCNF(){
        String operator = "&";
        String negOperator = "|";
        if(formulaCNF.equals(""))
            transformToCNF();
        if(formulaFullCNF.equals(""))
        {
            formulaFullCNF = completeClauses(operator, formulaCNF.concat(operator)); //Iterate all clauses adding an extra operator to end of formula
            formulaFullCNF = formulaFullCNF.substring(0, formulaFullCNF.length() - 1); //Remove extra operator
            formulaFullCNF = addSpaces(formulaFullCNF);
        }
    }
    private static void transformToFullDNF(){
        String operator = "|";
        String negOperator = "&";
        if(formulaDNF.equals(""))
            transformToDNF();
        if(formulaFullDNF.equals("")){
            formulaFullDNF = completeClauses(operator, formulaDNF.concat(operator)); //Iterate all clauses adding an extra operator to end of formula
            formulaFullDNF = formulaFullDNF.substring(0, formulaFullDNF.length() - 1); //Remove extra operator
            formulaFullDNF = addSpaces(formulaFullDNF);
        }
    }
    private static void evaluateFormula() throws IOException{
        formulaEvaluated = "";
        if(formulaCNF.equals(""))
            transformToCNF();
        if(formulaEvaluated.equals(""))
            formulaEvaluated = removeSpaces(formulaCNF);
        requestValues(); //Get atoms and request user to enter 0 or 1 per atom, replace values on formula
        formulaEvaluated = getValueOfGivenFormula(formulaEvaluated);
        formulaEvaluated = addSpaces(formulaEvaluated);
    }
    private static void satEvaluate(){
        if(formulaCNF.equals(""))
            transformToCNF();
        satResult = "not evaluated";
        //Get atoms of formula
        //initialize atoms at 0
        //call special function
        Set<String> mAtoms = getAtoms(formulaCNF, false);
        LinkedHashMap<String, String> mAtomsLHM = new LinkedHashMap<String, String>();

        for(String mAtom : mAtoms)
            mAtomsLHM.put(mAtom, "0");
        executeTruthValues(mAtomsLHM);
    }
    private static void executeTruthValues(LinkedHashMap<String, String> mAtomsLHM){
        String satFormula = formulaCNF;
        for(String mAtom : mAtomsLHM.keySet())
            satFormula = satFormula.replaceAll(mAtom, mAtomsLHM.get(mAtom));
        String mResult = getValueOfGivenFormula(satFormula);
        satResult = satResult.concat(mResult);
        if(!mAtomsLHM.containsValue("0")){
            if(satResult.contains("1")){
                if(satResult.contains("0"))
                    satResult = "satisfiable";
                else
                    satResult = "tautology";
            }else
                satResult = "contradiction";
            return;
        }

        for(String mAtom : mAtomsLHM.keySet()){
            if(mAtomsLHM.get(mAtom).equals("0")){
                mAtomsLHM.put(mAtom, "1");
                break;
            }else{
                mAtomsLHM.put(mAtom, "0");
            }
        }
        executeTruthValues(mAtomsLHM);
    }
    private static String applyGeneralFormula(String mFormula){
        if(mFormula.contains("0"))
            mFormula = "0";
        else if(mFormula.contains("1")){
            mFormula = mFormula.replaceAll("\\(1\\)\\&", "");
            mFormula = mFormula.replaceAll("\\&\\(1\\)", "");
        }
        if(mFormula.isEmpty())
            mFormula = "1";
        return mFormula;
    }
    private static String applyOrToParentheses(String mFormula){
        StringBuilder mStrBldr = new StringBuilder(mFormula);
        int i;
        //Iterate all parentheses
        for(i = -1; (i = mStrBldr.indexOf("(", i + 1)) != -1; i++) {
            int indexOfEndOfParen = getIndexOfEndInnerClause(i + 1, mStrBldr.toString());
            String mClause = mStrBldr.substring(i + 1, indexOfEndOfParen);
            if(mClause.contains("1"))
                mClause = "1";
            else{
                if(getAtoms(mClause, false).isEmpty())
                    mClause = "0";
                else{
                    mClause = mClause.replaceAll("0\\|", "");
                    mClause = mClause.replaceAll("\\|0", "");
                }
            }
            mStrBldr.replace(i + 1, indexOfEndOfParen, mClause);
        }
        return mStrBldr.toString();
    }
    /**
     * Replace !0 with 1 and !1 with 0
     */
    private static String applyNegationToAtoms(String formula){
        return formula.replaceAll("!0", "1").replaceAll("!1", "0");
    }
    /**
     * Evaluate formula that has values, forula has to be cnf
     * @param  mFormula [description]
     * @return          [description]
     */
    private static String getValueOfGivenFormula(String mFormula){
        mFormula = applyNegationToAtoms(mFormula); //!0 with 1, and !1 with 0
        //Replace 0|1 with 1, 
        mFormula = applyOrToParentheses(mFormula);
        //0&1 with 0,
        mFormula = applyGeneralFormula(mFormula);
        return mFormula;
    }
    /**
     * Get atoms and request user to enter 0 or 1 per atom, replace values on formula
     */
    private static void requestValues() throws IOException{
        mprintln("Please give a 0 for false, 1 for true, or just 'enter' to not specify a value to the following atoms.");
        Set<String> mAtoms = getAtoms(formulaEvaluated, false);
        for(String mAtom : mAtoms){
            mprintln("Enter value for atom: " + mAtom);
            String mValue = readValue();
            if(mValue.equals("0") || mValue.equals("1"))
                formulaEvaluated = formulaEvaluated.replaceAll(mAtom, mValue);
        }
    }
    private static String removeSpaces(String formula){
        return formula.replaceAll("\\s+","");
    }
    private static String completeClauses(String operator, String formula){
        int i, index = 0;
        String negOperator = operator.equals("&") ? "|" : "&";
        formula = removeSpaces(formula);
        //Algorithm:
        //Find different atoms
        //Iterate for every parentheses that contains atoms, add every missing atom and its negation
        String equivalentExpression = "";
        Set<String> mAtoms = getAtoms(formula, false);
        for(i = -1; (i = formula.indexOf(operator, i + 1)) != -1; i++) {
            String clause = formula.substring(index, i);
            equivalentExpression = "";
            //iterate all atoms in formula, if missing one add it
            for(String mAtom : mAtoms){
                if(!clause.contains(mAtom)){
                    //add missing atom
                    if(equivalentExpression.length() > 0)
                        equivalentExpression = equivalentExpression.concat(operator);
                    equivalentExpression = equivalentExpression.concat(distribute(operator, negOperator,
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
        // (HashSet)newset.clone();
        String equivalentExpression = "";
        StringBuilder mStrBldr = new StringBuilder("");

        for(String mAtom : mAtoms2){
            if(mAtom.contains("!") && mAtoms.contains(mAtom.replace("!",""))){
                mAtoms2.clear();
                mAtoms.clear();
                if(negOperator.equals("|")){
                    mAtoms.add("1");
                }else{
                    mAtoms.add("0");
                }
                break;
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
        equivalentExpression = removeParenthesesClause(equivalentExpression, operator, negOperator);
        return equivalentExpression;
    }
    /**
     * Check if formula is in required logic family
     * @param  operator    How the formula should be (CNF or DNF)
     * @param  negOperator [description]
     * @param  formula     [description]
     * @return             true if formula is in desired logic
     */
    private static boolean validFormula(String operator, String negOperator, String formula){
        //Look for parentheses, if they have operator then return false
        int i;
        formula = formula.concat(operator);
        for(i = -1; (i = formula.indexOf("(", i + 1)) != -1; i++) {
            int indexOfStartOfP = i;
            int indexOfEndOfParen = getIndexOfEndInnerClause(indexOfStartOfP + 1, formula);
            String mClause = formula.substring(indexOfStartOfP, indexOfEndOfParen + 1);
            if(mClause.contains(operator))
                return false;
        }
        return true;
    }
    /**
     * Distribute symbols inside inner clauses, remove double parentheses
     * @param  operator    Operator that indicates type of formula being created
     * @param  negOperator Operator to be distributed
     * @param  formula     Formula to be transformed
     * @return String Formula after being distributed
     */
    private static String distributeSymbols(String operator, String negOperator, String formula){
        //if formula contains (( or ))
            //if clause contains | xor &
                //remove all inner parentheses
            //if index of parentheses is 0
                //get symbol after inner opening parentheses
            //else
                //get symbol before inner opening parentheses
            //if index of parentheses is 0
                //get symbol after general opening parentheses
            //else
                //get symbol before general opening parentheses
            //if symbol inner parentheses is not equal to general symbol
                //distribute symbol inner opening parentheses
            //remove general parentheses
        //distribute negoperator
        int i, index = 0;
        if(formula.startsWith("(") 
            && getIndexOfEndInnerClause(1, formula) == formula.length() - 1)
            formula = formula.substring(1, formula.length() - 1);
        
        for(i = -1; (i = formula.indexOf("((", i + 1)) != -1; i++) {
            int indexOfEndOfParen = getIndexOfEndInnerClause(i + 1, formula);
            int indexOfEndInnerClause = getIndexOfEndInnerClause(i + 2, formula);
            String mClause = formula.substring(i + 1, indexOfEndOfParen);
            //Remove inner parentheses if clause contains same symbols
            if(mClause.contains("|")^mClause.contains("&"))
                mClause = mClause.replaceAll("[()]", "");
            String symbolInnerParen = formula.substring(indexOfEndInnerClause + 1, indexOfEndInnerClause + 2);
            //if theres no symbol to the right of formula check if there's one to the left
            //if not cotinue
            String symbolGeneralParen = "";
            if(indexOfEndOfParen != (formula.length() - 1))
                symbolGeneralParen = formula.substring(indexOfEndOfParen + 1, indexOfEndOfParen + 2);
            else if(i > 0)
                symbolGeneralParen = formula.substring(i - 1, i);
            else
                continue;
            if(!symbolInnerParen.equals(symbolGeneralParen))
                mClause = distribute(symbolGeneralParen, symbolInnerParen, mClause);
            if(i != 0)
                formula = formula.substring(0, i) + mClause
                    + formula.substring(indexOfEndOfParen + 1, formula.length());
            else
                formula = mClause + formula.substring(indexOfEndOfParen + 1, formula.length());
        }
        for(i = -1; (i = formula.indexOf("))", i + 1)) != -1; i++) {
            int indexOfStartOfP = getIndexOfStartInnerClause(i - 1, formula);
            index = indexOfStartOfP - 2;
            String mAtomToDistribute = formula.substring(indexOfStartOfP - 2, indexOfStartOfP);
            mAtomToDistribute = new StringBuilder(mAtomToDistribute).reverse().toString();
            if(formula.charAt(indexOfStartOfP - 3) == '!'){
                mAtomToDistribute = String.valueOf(mAtomToDistribute.charAt(0)) + "!"
                    + String.valueOf(mAtomToDistribute.charAt(1));
                index--;
            }
            formula = formula.substring(0, index) 
                + formula.substring(indexOfStartOfP, i + 1)
                + mAtomToDistribute
                + formula.substring(i + 1, formula.length());
            formula = distributeSymbols(operator, negOperator, formula);
        }
        return formula;
    }
   /** 
    * Iterate inner clause and distribute negOperator
    * @param operator Operator that indicates type of formula being created
    * @param negOperator Operator that should be distributed
    * @param formula Formula that will be treated
    * @return String Formula after being distributed
    */
    private static String iterateInnerSymbols(String operator, String negOperator, String formula){
        return distribute(operator, negOperator, distributeSymbols(operator, negOperator, formula));
        //The following code is deprecated Apr. 22 2018
        // StringBuilder mStrBldr = new StringBuilder(formula);
        // int indexOfStartOfP = mStrBldr.indexOf("(");
        // if(indexOfStartOfP == -1)
        //     return mStrBldr.toString();
        // int indexOfEndOfParen = getIndexOfEndInnerClause(indexOfStartOfP + 1, mStrBldr.toString());
        // StringBuilder mClause = new StringBuilder(mStrBldr.substring(indexOfStartOfP + 1, indexOfEndOfParen));
        // //If clause contains parentheses
        // if(mClause.toString().contains("(")){
        //     int indexOfStartInnerInnerClause = mClause.indexOf("(");
        //     int indexOfEndInnerInnerClause = getIndexOfEndInnerClause(indexOfStartInnerInnerClause + 1, mClause.toString());
        //     StringBuilder mInnerClause = new StringBuilder(mClause.substring(indexOfStartInnerInnerClause + 1,
        //                                                                         indexOfEndInnerInnerClause));
        //     //If clause contains just one symbol, not two, we just remove parentheses
        //     if((mClause.toString().contains(operator) && !mClause.toString().contains(negOperator)) ||
        //     (mClause.toString().contains(negOperator) && !mClause.toString().contains(operator)))
        //         mClause.replace(0, mClause.length(),
        //             mClause.toString().replaceAll("[()]", ""));
        //     else{
        //         //Iterate innerinner clause
        //         mClause.replace(indexOfStartInnerInnerClause + 1, indexOfEndInnerInnerClause,
        //             iterateInnerSymbols(operator, negOperator, mInnerClause.toString()));
        //         mClause = new StringBuilder(distribute(negOperator, operator, mClause.toString()));
        //     }
        // }
        // if(mClause.indexOf("(") > 0)
        //     mClause = new StringBuilder(distribute(mClause.charAt(mClause.indexOf("(") - 1) == '|' ? "&" : "|",
        //         mClause.substring(mClause.indexOf("(") - 1, mClause.indexOf("(")), mClause.toString()));
        // if(!mStrBldr.substring(indexOfStartOfP + 1, indexOfEndOfParen).equals(mClause.toString())){
        //     mStrBldr.replace(indexOfStartOfP + 1, indexOfEndOfParen, mClause.toString());
        //     if(indexOfStartOfP > 0)
        //         mStrBldr = new StringBuilder(distribute(mStrBldr.charAt(indexOfStartOfP - 1) == '|' ? "&" : "|",
        //             mStrBldr.substring(indexOfStartOfP - 1, indexOfStartOfP), mStrBldr.toString()));
        //     mStrBldr = new StringBuilder(iterateInnerSymbols(operator, negOperator, mStrBldr.toString()));
        // }else if(indexOfEndOfParen != (mStrBldr.length() - 1))
        //     mStrBldr.replace(indexOfEndOfParen + 2, mStrBldr.length(),
        //         iterateInnerSymbols(operator, negOperator, mStrBldr.substring(indexOfEndOfParen + 2)));
        // //Check if formula needs another iteration
        
        // formula = mStrBldr.toString();
        // formula = removeParenthesesClause(formula, operator, negOperator);
        // if(formula.indexOf(")" + negOperator + "(") != -1)
        //     formula = distribute(operator, negOperator, formula);
        // return formula;
    }
    /**
     * Gernerate permutations given a list of list of atoms
     * obtained from: https://stackoverflow.com/questions/17192796/generate-all-combinations-from-multiple-lists
     * @param Lists   List of list of atoms
     * @param result  List of required permutations
     * @param depth   [description]
     * @param current [description]
     */
    private static void generatePermutations(List<List<String>> Lists, List<String> result, int depth, 
        String current, String operator, String negOperator){
        if(depth == Lists.size()){
           result.add("(" + current.substring(0, current.length() - 1) + ")");
           return;
         }
        for(int i = 0; i < Lists.get(depth).size(); ++i)
            generatePermutations(Lists, result, depth + 1, current + Lists.get(depth).get(i) + negOperator, 
                operator, negOperator);
    }
    /**
    *   Distribute given symbol in formula
    *
    **/
    private static String distribute(String operator, String negOperator, String formula){
        if(formula.startsWith("(") 
            && getIndexOfEndInnerClause(1, formula) == formula.length() - 1)
            formula = formula.substring(1, formula.length() - 1);
        //Only distribute negoperator
        if(formula.contains("|")^formula.contains("&"))
            return formula;
        if(formula.contains("(")){
            int indexOfStartOfP = formula.indexOf("(");
            int indexOfEndOfParen = getIndexOfEndInnerClause(indexOfStartOfP + 1, formula);
            if((indexOfStartOfP != 0 && String.valueOf(formula.charAt(indexOfStartOfP - 1)).equals(operator))
              || (indexOfStartOfP == 0 && String.valueOf(formula.charAt(indexOfEndOfParen + 1)).equals(operator)))
                return formula;
        }

        StringBuilder mFormulaBldr = new StringBuilder(formula);
        int i, j, index = 0;

        //Listing clauses
        //Get clauses
        List<String> mClauses = new ArrayList<String>();
        for(i = 0; i < mFormulaBldr.length(); i++){
            char mSymbol = mFormulaBldr.charAt(i);
            if(Character.isLetter(mSymbol))//If symbol is letter, thats the clause
                mClauses.add(String.valueOf(mSymbol));
            else if(mSymbol == '!'){ //If the symbol is negation, use it plus atom next to neg
                mClauses.add(String.valueOf(mSymbol) + String.valueOf(mFormulaBldr.charAt(i + 1)));
                i++;
            }else if(mSymbol == '('){ //If it is parentheses, search for end of it and it is clause
                int indexOfEndOfParen = getIndexOfEndInnerClause(i + 1, mFormulaBldr.toString());
                mClauses.add(mFormulaBldr.substring(i + 1, indexOfEndOfParen)); //will not get parentheses
                i = indexOfEndOfParen;
            }
        }
        List<List<String>> mListOfAtomsPerClause = new ArrayList<List<String>>();
        for(String mClause : mClauses){
            List<String> mAtoms = new ArrayList<String>();
            index = 0;
            mClause = mClause.concat(operator);
            for(i = 0; i < mClause.length(); i++){
                char mSymbol = mClause.charAt(i);
                if(!String.valueOf(mSymbol).equals(operator))
                    continue;
                mAtoms.add(mClause.substring(index, i));
                index = i + 1;
            }
            mListOfAtomsPerClause.add(mAtoms);
        }
        List<String> mNewFormula = new ArrayList<String>();
        generatePermutations(mListOfAtomsPerClause, mNewFormula, 0, "", operator, negOperator);
        mFormulaBldr = new StringBuilder();
        for(String permu : mNewFormula){
            if(mFormulaBldr.length() != 0)
                mFormulaBldr.append(operator);
            mFormulaBldr.append(permu);
        }
        return mFormulaBldr.toString();
    }
    /**
    *   Move atom to distribute to the right of parentheses
    *
    **/
    private static String moveAtomToDistributeRightParentheses(String formula, String operator, String negOperator){
        for (int i = -1; (i = formula.indexOf(negOperator + "(", i + 1)) != -1; i++) {
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
                equivalentExpression = distribute(operator, negOperator, equivalentExpression);
                formula = formula.replace(stringToReplace, equivalentExpression);
            }
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
    //deprecated
    private static String removeParentheses(String formula, String operator, String negOperator){
        if(formula.startsWith("(")){
            int indexOfEndOfParen = getIndexOfEndInnerClause(1, formula);
            if(indexOfEndOfParen == formula.length() - 1 && !formula.contains(negOperator)){
                formula = formula.substring(1, formula.length() - 1);
            }
        }
        formula = removeParenthesesClause(formula, operator, negOperator);
        mprintln("This function is deprectaed! -----------------------");
        return formula;
    }
    //Remove parentheses inside a clause
    private static String removeParenthesesClause(String formula, String operator, String negOperator){
        StringBuilder mStrBldr = new StringBuilder(formula);
        int index = 0;
        for(int i = mStrBldr.length() - 1; i >= 0; i--){
            char mSymbol = mStrBldr.charAt(i);
            if(mSymbol != '(')
                continue;
            int indexOfEndOfParen = getIndexOfEndInnerClause(i + 1, mStrBldr.toString());
            String mStr = mStrBldr.substring(i + 1, indexOfEndOfParen);
            if(mStr.contains("(") && 
                ((mStr.contains(operator) && !mStr.contains(negOperator)) ||
                (!mStr.contains(operator) && mStr.contains(negOperator))))
                mStr = mStr.replaceAll("[()]", "");
            mStrBldr.replace(i + 1, indexOfEndOfParen, mStr);
        }
        formula = mStrBldr.toString();
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
    /**
     * Get index that belongs to the ending parentheses (end of clause)
     * @param index Index to start reading symbols (not the opening parentheses)
     * @param formula Propositional formula to read symbols
     * @return int The index of the closing parentheses
     */
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

    private static void callTestCases(String action) throws IOException{
        if(fomulaTestCase.isEmpty())
        {
            String ms = "";
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
            ms = "(a&b)->c";
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
            ms = "(a&(b&(c|d)))";
            fomulaTestCase.add(ms);
            ms = "a&(b|(c|b))";
            fomulaTestCase.add(ms);
            ms = "a&b";
            fomulaTestCase.add(ms);
            ms = "a|b";
            fomulaTestCase.add(ms);
            // ms = "(a&(b|(c|(a&d))))|(a&b)";
            // fomulaTestCase.add(ms);
            ms = "(b&a)|(c&a)";
            fomulaTestCase.add(ms);
            ms = "(a|b)&(a|(c&d))";
            fomulaTestCase.add(ms);
            ms = "(a&b)|(a&c)|(b&c)";
            fomulaTestCase.add(ms);
            ms = "(a|b|c)&(d|e|f)";
            fomulaTestCase.add(ms);
            ms = "(a&b&c)|(d&e&f)";
            fomulaTestCase.add(ms);
            ms = "(a&(b|!b))";
            fomulaTestCase.add(ms);
            ms = "(a|b)&(c|d)";
            fomulaTestCase.add(ms);
            ms = "(!a|b)|(!b|a)";
            fomulaTestCase.add(ms);
            ms = "(!a&a)";
            fomulaTestCase.add(ms);
            ms = "(a->b)|(b->a)";
            fomulaTestCase.add(ms);
            ms = "(P|!P)";
            fomulaTestCase.add(ms);
            ms = "c|(b&a)";
            fomulaTestCase.add(ms);
            ms = "((b&a)|!c)&a";
            fomulaTestCase.add(ms);
            ms = "((b&a)|c)&((b&a)|c)&a";
            fomulaTestCase.add(ms);
            ms = "(b|c)&(a|c)&a";
            fomulaTestCase.add(ms);
            ms = "(a&b)|(c&(d|f))";
            fomulaTestCase.add(ms);
            ms = "((d|f)&c)|(a&b)";
            fomulaTestCase.add(ms);
            ms = "((c|e)&(d|f))|(a&b)";
            fomulaTestCase.add(ms);
            ms = "((a&b)|(c&d))";
            fomulaTestCase.add(ms);
            ms = "((a|b)|(c|d))";
            fomulaTestCase.add(ms);
            ms = "(a&b)|((c|d)&(e|f))";
            fomulaTestCase.add(ms);
            ms = "((b&a)|c)&a";
            fomulaTestCase.add(ms);
            ms = "c&(d|f)";
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
//Test cases to check:
//8
//10
//11
//12
//14
//20
//22
//23
//27
//40
// a&(b|c|b)
// (a&(b|!b))