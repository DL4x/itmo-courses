module HW1.T3
  ( Tree (..)
  , tsize
  , tdepth
  , tmember
  , tinsert
  , tFromList
  ) where

data Meta = M Int Int
  deriving Show

data Tree a = Leaf | Branch Meta (Tree a) a (Tree a)
  deriving (Show)

tsize :: Tree a -> Int
tsize Leaf                      = 0
tsize (Branch (M size _) _ _ _) = size

tdepth :: Tree a -> Int
tdepth Leaf                       = 0
tdepth (Branch (M _ depth) _ _ _) = depth

tmember :: Ord a => a -> Tree a -> Bool
tmember _ Leaf = False
tmember x (Branch _ left y right) =
  case compare x y of
    EQ -> True
    LT -> tmember x left
    GT -> tmember x right

mkMeta :: Tree a -> Tree a -> Meta
mkMeta x y = (M (tsize x + tsize y + 1) (tdepth x `max` tdepth y + 1))

mkBranch :: Tree a -> a -> Tree a -> Tree a
mkBranch left x right = (Branch (mkMeta left right) left x right)

tinsert :: Ord a => a -> Tree a -> Tree a
tinsert x Leaf = (Branch (M 1 1) Leaf x Leaf)
tinsert x branch@(Branch _ left y right) =
  case compare x y of
    EQ -> branch
    LT -> mkBranch (tinsert x left) y right
    GT -> mkBranch left y (tinsert x right)

tFromList :: Ord a => [a] -> Tree a
tFromList list = foldl (flip tinsert) Leaf list
