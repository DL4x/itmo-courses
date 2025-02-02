{-# LANGUAGE FlexibleContexts #-}
{-# OPTIONS_GHC -Wno-unrecognised-pragmas #-}

module HW6.T1
  ( BucketsArray
  , CHT (..)

  , newCHT
  , getCHT
  , putCHT
  , sizeCHT

  , initCapacity
  , loadFactor
  ) where

import Control.Concurrent.Classy (MonadConc (readTVarConc), MonadSTM, STM, atomically)
import Control.Concurrent.Classy.STM (TArray, TVar, modifyTVar', newTVar, readTVar, writeTVar)
import Control.Monad (when)
import Data.Array.Base (MArray (getNumElements))
import Data.Array.MArray (getElems, newArray, readArray, writeArray)
import Data.Hashable (Hashable, hash)

initCapacity :: Int
initCapacity = 16

loadFactor :: Double
loadFactor = 0.75

type Bucket k v = [(k, v)]
type BucketsArray stm k v = TArray stm Int (Bucket k v)

data CHT stm k v = CHT
  { chtBuckets :: TVar stm (BucketsArray stm k v)
  , chtSize    :: TVar stm Int
  }

ensureCapacity
  :: MArray stm (Bucket k v) m
  => Int
  -> m (stm Int (Bucket k v))
ensureCapacity capacity = newArray (0, capacity - 1) []

newCHT :: MonadConc m => m (CHT (STM m) k v)
newCHT = atomically $ do
  size <- newTVar 0
  array <- ensureCapacity initCapacity
  buckets <- newTVar array
  return $ CHT buckets size

getBucketByKey
  :: ( MonadSTM m
     , Hashable k
     )
  => TArray m Int (Bucket k v)
  -> k
  -> m (Bucket k v, Int)
getBucketByKey buckets key = do
    capacity <- getNumElements buckets
    let bucketIndex = getBucketIndex key capacity
    bucket <- readArray buckets bucketIndex
    return (bucket, bucketIndex)
  where
    getBucketIndex :: Hashable k => k -> Int -> Int
    getBucketIndex k capacity = hash k `mod` capacity

getCHT
  :: ( MonadConc m
     , Hashable k
     )
  => k
  -> CHT (STM m) k v
  -> m (Maybe v)
getCHT k cht = atomically $ do
  buckets <- readTVar $ chtBuckets cht
  (bucket, _) <- getBucketByKey buckets k
  return $ lookup k bucket

resizeBucketsArray
  :: ( MonadSTM m
     , Hashable k
     )
  => TArray m Int (Bucket k v)
  -> CHT m k v
  -> m ()
resizeBucketsArray buckets cht = do
  capacity <- getNumElements buckets
  elements <- mconcat <$> getElems buckets
  let newCapacity = capacity * 2
  newBuckets <- ensureCapacity newCapacity
  mapM_ (rehashElement newBuckets) elements
  writeTVar (chtBuckets cht) newBuckets
  where
    rehashElement newBuckets' (key, value) = do
      (bucket, bucketIndex) <- getBucketByKey newBuckets' key
      writeArray newBuckets' bucketIndex ((key, value) : bucket)

putCHT
  :: ( MonadConc m
     , Hashable k
     )
  => k
  -> v
  -> CHT (STM m) k v
  -> m ()
putCHT k v cht = atomically $ do
  buckets <- readTVar $ chtBuckets cht
  (bucket, bucketIndex) <- getBucketByKey buckets k
  case lookup k bucket of
    Just _ -> writeArray buckets bucketIndex (map replaceValue bucket)
    Nothing -> do
      let size = chtSize cht
      modifyTVar' size (+ 1)
      writeArray buckets bucketIndex ((k, v) : bucket)
      resizeCondition <- isBacketsArrayLoaded buckets
      when resizeCondition $ resizeBucketsArray buckets cht
  where
    replaceValue (key, value) =
      if key == k then (key, v) else (key, value)
    isBacketsArrayLoaded buckets' = do
      size <- readTVar $ chtSize cht
      capacity <- getNumElements buckets'
      return $ fromIntegral size >= fromIntegral capacity * loadFactor

sizeCHT :: MonadConc m => CHT (STM m) k v -> m Int
sizeCHT = readTVarConc . chtSize
