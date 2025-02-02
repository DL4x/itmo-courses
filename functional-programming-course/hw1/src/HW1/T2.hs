module HW1.T2
  ( N (..)
  , nplus
  , nmult
  , nsub
  , nFromNatural
  , nToNum
  , ncmp
  , nEven
  , nOdd
  , ndiv
  , nmod
  ) where

import Numeric.Natural

data N = Z | S N
  deriving Show

nplus :: N -> N -> N
nplus x Z     = x
nplus x (S y) = nplus (S x) y

nmult :: N -> N -> N
nmult _ Z     = Z
nmult x (S y) = nplus x (nmult x y)

nsub :: N -> N -> Maybe N
nsub x Z         = Just x
nsub Z (S _)     = Nothing
nsub (S x) (S y) = nsub x y

ncmp :: N -> N -> Ordering
ncmp Z Z         = EQ
ncmp Z (S _)     = LT
ncmp (S _) Z     = GT
ncmp (S x) (S y) = ncmp x y

nFromNatural :: Natural -> N
nFromNatural 0 = Z
nFromNatural n = S $ nFromNatural (n - 1)

nToNum :: Num a => N -> a
nToNum Z     = 0
nToNum (S n) = (nToNum n) + 1

nEven :: N -> Bool
nEven = undefined

nOdd :: N -> Bool
nOdd = undefined

ndiv :: N -> N -> N
ndiv = undefined

nmod :: N -> N -> N
nmod = undefined
