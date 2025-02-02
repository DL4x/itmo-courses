{-# LANGUAGE DataKinds            #-}
{-# LANGUAGE TypeFamilies         #-}
{-# LANGUAGE TypeOperators        #-}
{-# LANGUAGE UndecidableInstances #-}

module HW6.T2
  ( TSet

  , Contains
  , Add
  , Delete
  ) where

import GHC.TypeLits

type TSet = [Symbol]

type family Contains (name :: Symbol) (set :: TSet) :: Bool
  where
    Contains _ '[] = 'False
    Contains name (name ': xs) = 'True
    Contains name (_ ': xs) = Contains name xs

type family Delete (name :: Symbol) (set :: TSet) :: TSet
  where
    Delete _ '[] = '[]
    Delete name (name ': xs) = xs
    Delete name (x ': xs) = x ': Delete name xs

type family Add (v :: Symbol) (set :: TSet) :: TSet
  where
    Add v set = v ': Delete v set
