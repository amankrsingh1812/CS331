:- dynamic faultynode/1.

isneighbour(X,Y,Vis) :- mazelink(X,Y) , \+ faultynode(Y), \+ member((Y,_),Vis).

genNeighbour(X,Vis,R):- findall(Y,isneighbour(X,Y,Vis),R).

createNewVis([],_,[]).
createNewVis([T|End],X,[(T,X)|Vis]):- createNewVis(End,X,Vis).

genPath([(S,S)],[S],S).
genPath([(X,P)| Tail1],[X|Tail2],X):- genPath(Tail1,Tail2,P).
genPath([(Z,P)| Tail1],[Y|Tail2],X):- genPath(Tail1,[Y|Tail2],X).


bfs([X|_],Vis,E,Ans):- X =:= E, genPath(Vis,Ans,E),!.
bfs([X|Queue],Vis,E,Ans):- genNeighbour(X,Vis,R),createNewVis(R,X,NVis),append(NVis,Vis,Cvis),append(Queue,R,NQueue),bfs(NQueue,Cvis,E,Ans).

findShortestPath(S,E):- \+ faultynode(S), \+ faultynode(E) ,bfs([E],[(E,E)],S,Ans),write(Ans).
