module HW3.T3
  ( joinOption
  , joinExcept
  , joinAnnotated
  , joinList
  , joinFun
  ) where

import HW3.T1

joinOption :: Option (Option a) -> Option a
joinOption None     = None
joinOption (Some x) = x

joinExcept :: Except e (Except e a) -> Except e a
joinExcept (Error e)   = Error e
joinExcept (Success x) = x

joinAnnotated :: Semigroup e => Annotated e (Annotated e a) -> Annotated e a
joinAnnotated ((x :# y) :# y') = x :# y' <> y

joinList :: List (List a) -> List a
joinList Nil      = Nil
joinList (x :. y) = f x (joinList y)
  where
    f Nil acc        = acc
    f (x' :. y') acc = x' :. f y' acc

joinFun :: Fun i (Fun i a) -> Fun i a
joinFun (F fun) = F (\x -> let F f = fun x in f x)
