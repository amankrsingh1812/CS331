member(X, [X|T]).
member(X, [H|T]) :- member(X, T).

sublst([],T).
sublst([H|T],[H|T1]):- sublst(T,T1).
sublst([H|T],[H1|T1]):- sublst([H|T],T1).
