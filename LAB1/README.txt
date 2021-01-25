PART A:
1. To compile use the command "javac -d . A.java"
2. To run use the command "java PartA.MonteCarloSimulation Nthreads" where Nthreads is required number of threads(range 4-16)

PART B:
1. To compile use the command "javac -d . B.java"
2. To run use the command "java PartB.SimpsonRule Nthreads" where Nthreads is required number of threads(range 4-16)

PART C:
1. To compile use the command "javac -d . C.java"
2. To run use the command "java PartC.MatrixMultiplication Nthreads Flag" where Nthreads is required number of threads(range 4-16).
   Flag(0 or 1) is an integer indicating whether to write the Matrices A, B, C in a file. 
   Passing flag as 1 increases the runtime of program due to the slow FILE I/O. 
   Example Command to run:- java PartC.MatrixMultiplication 4 0