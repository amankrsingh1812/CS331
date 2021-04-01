cc(V,R,A):- V*V =< R + A, V*V >= R - A.
sqrt1(Ans,V,R,A):-cc(V,R,A), Ans is V.
sqrt1(Ans,V,R,A):-E is (R/V + V)/2, sqrt1(Ans,E,R,A).

findsqrt(X,Result,Accuracy):-sqrt1(Result,X,X,Accuracy).
