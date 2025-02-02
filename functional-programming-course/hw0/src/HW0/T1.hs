{-# LANGUAGE TypeOperators #-}

module HW0.T1
  ( type (<->) (Iso)
  , assocEither
  , assocPair
  , distrib
  , flipIso
  , runIso
  ) where

data a <-> b = Iso (a -> b) (b -> a)

distrib :: Either a (b, c) -> (Either a b, Either a c)
distrib (Left x)       = (Left x, Left x)
distrib (Right (x, y)) = (Right x, Right y)

flipIso :: (a <-> b) -> (b <-> a)
flipIso (Iso f g) = Iso g f

runIso :: (a <-> b) -> (a -> b)
runIso (Iso f _) = f

assocPair :: (a, (b, c)) <-> ((a, b), c)
assocPair = Iso assocPairTo assocPairFrom
  where
    assocPairTo = \(x, (y, z)) -> ((x, y), z)
    assocPairFrom = \((x, y), z) -> (x, (y, z))

assocEither :: Either a (Either b c) <-> Either (Either a b) c
assocEither = Iso assocEitherTo assocEitherFrom
  where
    assocEitherTo (Left x)          = Left (Left x)
    assocEitherTo (Right (Left x))  = Left (Right x)
    assocEitherTo (Right (Right x)) = Right x
    assocEitherFrom (Left (Left x))  = Left x
    assocEitherFrom (Left (Right x)) = Right (Left x)
    assocEitherFrom (Right x)        = Right (Right x)
