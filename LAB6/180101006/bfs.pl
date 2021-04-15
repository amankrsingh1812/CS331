:- set_prolog_flag(answer_write_options,[max_depth(0)]).
consult('Mazedata.pl').
:- ensure_loaded('Mazedata.pl').
:- dynamic faultynode/1.

addfaultynode(X):- \+ faultynode(X), assert(faultynode(X)),!.
addfaultynode(X):- faultynode(X), write("node "),write(X),write(" is already a faultynode"),nl.

removefaultynode(X):- retract(faultynode(X)).

isneighbour(X,Y,Vis) :- mazelink(X,Y) , \+ faultynode(Y), \+ member((Y,_),Vis).

genNeighbour(X,Vis,R):- findall(Y,isneighbour(X,Y,Vis),R).

createNewVis([],_,[]).
createNewVis([T|End],X,[(T,X)|Vis]):- createNewVis(End,X,Vis).

genPath([(S,S)],[S],S).
genPath([(X,P)| Tail1],[X|Tail2],X):- genPath(Tail1,Tail2,P).
genPath([(_,_)| Tail1],[Y|Tail2],X):- genPath(Tail1,[Y|Tail2],X).


bfs([X|_],Vis,E,Ans):- X =:= E, genPath(Vis,Ans,E),!.
bfs([X|Queue],Vis,E,Ans):- genNeighbour(X,Vis,R),createNewVis(R,X,NVis),append(NVis,Vis,Cvis),append(Queue,R,NQueue),bfs(NQueue,Cvis,E,Ans).

shortest_path(Src,Dst,Result):- \+ faultynode(Src), \+ faultynode(Dst) ,bfs([Dst],[(Dst,Dst)],Src,Result),length(Result,A),write("The length of shortest path is "),write(A),nl,!.
shortest_path(Src,_,_):- faultynode(Src),write("Error Input!! Source is faulty node."),nl,!.
shortest_path(_,Dst,_):- faultynode(Dst),write("Error Input!! Destination is faulty node."),nl,!.
