import Text.Printf

sqrt1 l r eps n
             | (r - l) <= eps = m
             | (m*m) <= n = sqrt1 m r eps n
             | otherwise = sqrt1 l m eps n
               where m = (l+r)/2


cusSqrt n = printf "The square root of %f is %0.6f\n" (n) (sqrt1 0 n 0.000001 n)
