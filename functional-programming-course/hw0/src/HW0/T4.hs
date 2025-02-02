module HW0.T4
  ( fac
  , fib
  , map'
  , repeat'
  ) where

import Data.Function (fix)
import Numeric.Natural (Natural)

repeat' :: a -> [a]
repeat' x = fix (x:)

map' :: (a -> b) -> [a] -> [b]
map' = fix (\rec f arr ->
  case arr of
    []     -> []
    (x:xs) -> f x : rec f xs)

fib :: Natural -> Natural
fib = fib' 0 1
  where
    fib' = fix (\rec x y n ->
      if n == 0
        then x
        else rec y (x + y) (n - 1))

fac :: Natural -> Natural
fac = fix (\rec n ->
  if n <= 1
    then 1
    else n * rec (n - 1))
