# Logical Foundations of Computer Science Programming Assignment

Given any propositional logic formula:
+ Transform it into CNF
+ Transform it into DNF
+ Transform it into full DNF
+ Transform it into full CNF
+ Evaluate its truth value given truth values of the atoms
+ Decide whether the formula is satisfiable, a tautology, or a contradiction

## Installation
Requirements:
+ Java JDK 1.8 or above

## Execution
For Windows:
+ Open a terminal (Command Prompt)
+ Compile the program with: javac Solution.java
+ Execute the program with: java Solution

## Instruccions
The program will require a valid propositional formula using:
+ **Atoms:** All letter from our roman alphabet A = {a, . . . , z} are atoms and are therefore
propositional formulas.
+ **NOT:** Given any propositional formula p, not (p) will be represented as !p.
+ Given any two propositional formulas p and q:
  + **Conjunction**: the conjunction of p and q will be represented as (p&q).
  + **Disjunction:** the disjunction of p with q will be represented as (p|q).
  + **Implication:** the implication of p to q will be represented as (p−>q).
  + Note that the parentheses are important, and part of the syntax to be used
  
## Try it!
Use the following propositional formula as an example
```
a->b
```
