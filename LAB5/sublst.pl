sublst([],_).
sublst([H|T],[H|T1]):- sublst(T,T1),!.
sublst([H|T],[_|T1]):- sublst([H|T],T1).
