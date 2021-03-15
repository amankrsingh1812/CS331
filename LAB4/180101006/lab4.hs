-- Part A
testfunc :: Integer -> IO [Integer]
testfunc y = do
    {x <- getChar;
     if x == '\n'
        then return [y];
     else if x == ',' then
        do
             xs <- testfunc 0;
             return  ( y : xs )
     else
        do
            let z = charToInt [x] ;
            xs <- testfunc (10*y+z);
            return  xs
    }

charToInt y = (read :: String -> Integer) y

getList = do
            x <- testfunc 0
            return x


-- Part B
gcdn :: Integer -> Integer -> Integer
gcdn a b
         | a == 0 = b
         | b == 0 = a
         | a == b = a
         | a > b = gcdn b (a `rem` b)
         | b > a = gcdn a (b `rem` a)

lcmn :: [Integer] -> Integer
lcmn [] = 1
lcmn ( x : xs ) = (x * l) `div` (gcdn x l) where l = lcmn xs


-- Part C
data Tree a = Nil | Node (Tree a) a (Tree a) deriving Show

insert Nil x = Node Nil x Nil
insert (Node lst v rst) x 
                        | v == x = Node lst v rst
                        | v > x = Node (insert lst x) v rst
                        | v < x = Node lst v (insert rst x)

newTree root [] = root
newTree root (x : xs) = newTree (insert root x) xs

makeTree [] = Nil
makeTree (x : xs) = newTree (Node Nil x Nil) xs

preorder Nil = []
preorder (Node lst v rst) = [v] ++ preorder (lst) ++ preorder (rst)

postorder Nil = []
postorder (Node lst v rst) = postorder (lst) ++ postorder (rst) ++ [v]

inorder Nil = []
inorder (Node lst v rst) = inorder (lst) ++ [v] ++ inorder (rst)

-- main function
main = do
         inp <- getList;
         putStrLn ("PartA: The input array is " ++ show(inp));
         putStrLn ("PartB: The lcm of all numbers is " ++ show(lcmn (inp)));
         let tree = makeTree (inp);
         putStrLn ("PartC: The inorder traversl is " ++ show(inorder (tree)))
         putStrLn ("PartC: The preorder traversl is " ++ show(preorder (tree)))
         putStrLn ("PartC: The postorder traversl is " ++ show(postorder (tree)))
