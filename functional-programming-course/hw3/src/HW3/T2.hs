module HW3.T2
  ( distOption
  , wrapOption
  , distPair
  , wrapPair
  , distQuad
  , wrapQuad
  , distAnnotated
  , wrapAnnotated
  , distExcept
  , wrapExcept
  , distPrioritised
  , wrapPrioritised
  , distStream
  , wrapStream
  , distList
  , wrapList
  , distFun
  , wrapFun
  ) where

import HW3.T1

distOption :: (Option a, Option b) -> Option (a, b)
distOption (_, None)        = None
distOption (None, _)        = None
distOption (Some x, Some y) = Some (x, y)

wrapOption :: a -> Option a
wrapOption = Some

distPair :: (Pair a, Pair b) -> Pair (a, b)
distPair (P x y, P x' y') = P (x, x') (y, y')

wrapPair :: a -> Pair a
wrapPair x = P x x

distQuad :: (Quad a, Quad b) -> Quad (a, b)
distQuad (Q x y z w, Q x' y' z' w') = Q (x, x') (y, y') (z, z') (w, w')

wrapQuad :: a -> Quad a
wrapQuad x = Q x x x x

distAnnotated :: Semigroup e => (Annotated e a, Annotated e b) -> Annotated e (a, b)
distAnnotated (x :# e, y :# e') = (x, y) :# e <> e'

wrapAnnotated :: Monoid e => a -> Annotated e a
wrapAnnotated x = x :# mempty

distExcept :: (Except e a, Except e b) -> Except e (a, b)
distExcept (Error e, _)           = Error e
distExcept (_, Error e)           = Error e
distExcept (Success x, Success y) = Success (x, y)

wrapExcept :: a -> Except e a
wrapExcept = Success

distPrioritised :: (Prioritised a, Prioritised b) -> Prioritised (a, b)
distPrioritised (Low x, Low y)       = Low (x, y)
distPrioritised (Low x, Medium y)    = Medium (x, y)
distPrioritised (Low x, High y)      = High (x, y)
distPrioritised (Medium x, Low y)    = Medium (x, y)
distPrioritised (Medium x, Medium y) = Medium (x, y)
distPrioritised (Medium x, High y)   = High (x, y)
distPrioritised (High x, Low y)      = High (x, y)
distPrioritised (High x, Medium y)   = High (x, y)
distPrioritised (High x, High y)     = High (x, y)

wrapPrioritised :: a -> Prioritised a
wrapPrioritised = Low

distStream :: (Stream a, Stream b) -> Stream (a, b)
distStream (x :> y, x' :> y') = (x, x') :> distStream (y, y')

wrapStream :: a -> Stream a
wrapStream x = x :> wrapStream x

distList :: (List a, List b) -> List (a, b)
distList (list, list') = distList' (list, list')
  where
    distList' (Nil, _)               = Nil
    distList' (_ :. y, Nil)          = distList (y, list')
    distList' (l@(x :. _), x' :. y') = (x, x') :. distList' (l, y')

wrapList :: a -> List a
wrapList x = x :. Nil

distFun :: (Fun i a, Fun i b) -> Fun i (a, b)
distFun (F fun, F fun') = F (\x -> (fun x, fun' x))

wrapFun :: a -> Fun i a
wrapFun x = F (\_ -> x)
