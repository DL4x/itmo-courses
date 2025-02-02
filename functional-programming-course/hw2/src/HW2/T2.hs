module HW2.T2
  ( joinWith
  , splitOn
  ) where

import Data.List.NonEmpty (NonEmpty (..), (<|))

splitOn :: Eq a => a -> [a] -> NonEmpty [a]
splitOn sep = foldr splitImpl (pure [])
  where
    splitImpl x acc@(y :| ys) =
      case x == sep of
        True  -> [] <| acc
        False -> (x : y) :| ys

joinWith :: a -> NonEmpty [a] -> [a]
joinWith sep = foldr1 (\x acc -> x ++ pure sep ++ acc)
