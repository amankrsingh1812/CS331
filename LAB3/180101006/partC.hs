qs [] = []
qs y = concat [ qs ([x | x <- y , x < z]), [x | x <- y , x == z], qs ([x | x <-y , x > z])] 
       where z = y!!((length y)-1)

qsort y = do
      putStrLn ("Input: " ++ show(y) ++ " Output: " ++ show(qs(y)))
