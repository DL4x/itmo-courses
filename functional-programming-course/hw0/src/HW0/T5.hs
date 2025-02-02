module HW0.T5
  ( Nat
  , nFromNatural
  , nmult
  , nplus
  , ns
  , nToNum
  , nz
  ) where

import Numeric.Natural

type Nat a = (a -> a) -> a -> a

nz :: Nat a
nz _ x = x

ns :: Nat a -> Nat a
ns nat f x = f (nat f x)

nplus :: Nat a -> Nat a -> Nat a
nplus nat nat' f x = nat f (nat' f x)

nmult :: Nat a -> Nat a -> Nat a
nmult nat nat' f = nat (nat' f)

nFromNatural :: Natural -> Nat a
nFromNatural 0 = nz
nFromNatural x = ns (nFromNatural (x - 1))

nToNum :: Num a => Nat a -> a
nToNum x = x (+1) 0
