A sample test case graph "Mazedata.pl" is provided in the folder.

The file "SampleQueries.txt" consists a list of sample queries.

The file "bfs.pl" contains main prolog code for assignment. Prolog procedure “shortest_path (src, dst, Result)” is defined which outputs shortest path between src and dst node along with its length(ie number of nodes in path). Furthermore the procedure "shortest_path" also outputs error message is src node or dst node is a faulty node. "shortest_path(src,dst,Result)" returns false if no path exists between src node and dst node.

The procedure "addfaultynode(X)" and "removefaultynode(X)" are also defined . "addfaultynode(X)" is used to make node X a faultynode and it outputs a message if node X is already a faultynode. "removefaultynode(X)" is used to remove a faultynode X ie to make it a normal node. It returns false if node X is not a faultynode during query time.

SWI-Prolog (threaded, 64 bits, version 7.6.4) is required for program to run.

Steps To Run:
1. Start SWI-Prolog using the following command in the terminal: "swipl"
2. To load "bfs.pl" use the command : "consult('bfs.pl')." 
3. To see output Copy one of the queries from the file "SampleQueries.txt"
4. To run on custom input use the command: "shortest_path (src, dst, Result)" where src and dst are source and dest node respectively. Result is an array that will contain final output, hence pass it as a variable. Also "addfaultynode(X)" can be used to add a faultynode and "removefaultynode(X)" can be used to remove a faultynode (ie make it again a normal node)


