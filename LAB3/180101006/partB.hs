fb :: Integer -> Integer
fb n
     | n==0 = 0
     | n==1 = 1
     |otherwise = f 1 0 (n-2)
                     where f a b n
                                  | n==0 = a + b 
                                  | otherwise = f (a+b) (a) (n-1)

fib n = putStrLn ("The " ++ show(n) ++ "th fibonacci number is " ++ show(fb(n)))
