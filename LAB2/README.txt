The program testCaseGenerator.java is used to generate random test cases in file
The program assignment2.java contains main logic for execution of transactions given in test case file

To generate TestCase file:
1. Compile the test case generator using: javac -d . testCaseGenerator.java
2. Run using the command: java testCaseGenerator.generateTestCase N
    N = number of transactions per Upadater
3. File name testCase.txt will be genrated

To Simulate the TestCase file:
1. Compile the program using: javac -d . assignment2.java
2. Run using the command: java assignment2.exectuteTransactions N
    N = number of transactions per Upadater (Keep it same as Test Case File)
3. The program runtime depends on number of transactions per Upadater
    Example: For 10^4 transactions per Upadater it takes 10 to 20 sec
             For 10^5 transactions per Upadater it takes 300 to 400 sec

The testCase.txt file that is provided with the source files contains 10^4 transactions per Updater            