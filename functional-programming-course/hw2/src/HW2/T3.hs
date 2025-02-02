module HW2.T3
  ( epart
  , mcat
  ) where

mcat :: Monoid a => [Maybe a] -> a
mcat = foldMap fromMaybe
  where
    fromMaybe (Just x) = x
    fromMaybe Nothing  = mempty

epart :: (Monoid a, Monoid b) => [Either a b] -> (a, b)
epart = foldMap fromEither
  where
    fromEither (Left l)  = (l, mempty)
    fromEither (Right r) = (mempty, r)
