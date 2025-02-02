module HW5.Evaluator
  ( eval
  ) where

import HW5.Base (HiError (..), HiExpr (..), HiFun (..), HiValue (..))

import Codec.Compression.Zlib (bestCompression, compressLevel, compressWith, decompress,
                               defaultCompressParams)
import Codec.Serialise (deserialiseOrFail, serialise)
import Control.Monad (foldM)
import Control.Monad.Except (ExceptT, runExceptT, throwError)
import Data.Either (fromRight)
import Data.Foldable (toList)
import Data.Ratio (denominator, numerator)
import Data.Semigroup (stimes)
import Data.Sequence (Seq, fromList)
import qualified Data.Sequence (drop, index, length, reverse, take)
import Data.Text.Encoding (decodeUtf8', encodeUtf8)
import Data.Word (Word8)

import qualified Data.ByteString as BS
import qualified Data.ByteString.Lazy as BL
import qualified Data.Text as T

eval :: Monad m => HiExpr -> m (Either HiError HiValue)
eval = runExceptT . evalImpl

evalImpl :: Monad m => HiExpr -> ExceptT HiError m HiValue
evalImpl (HiExprValue val) = pure val
evalImpl (HiExprApply constructor args) = do
  constructor' <- evalImpl constructor
  case constructor' of
    HiValueList l     -> evalImpl' evalHiListApply l
    HiValueBytes b    -> evalImpl' evalHiBytesApply b
    HiValueString s   -> evalImpl' evalHiStringApply s
    HiValueFunction f -> evalImpl' evalHiFunctionApply f
    _                 -> throwError HiErrorInvalidFunction
    where
      evalImpl' eval' obj = do
        args' <- mapM evalImpl args
        eval' obj args'

evalHiError :: Monad m
  => [HiValue]
  -> Int
  -> ExceptT HiError m HiValue
evalHiError args size =
  if length args == size
    then throwError HiErrorInvalidArgument
    else throwError HiErrorArityMismatch

evalHiErrorUnary :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiErrorUnary args = evalHiError args 1

evalHiErrorBinary :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiErrorBinary args = evalHiError args 2

evalHiNumberIndex :: Rational -> Maybe Integer
evalHiNumberIndex x =
  if denominator x /= 1
    then Nothing
    else pure $ numerator x

evalHiStringApplyUnary :: Monad m
  => T.Text
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiStringApplyUnary s [HiValueNumber x] = do
  case evalHiNumberIndex x of
    Just i -> pure $ do
      let i' = fromIntegral i
      let len = fromIntegral $ T.length s
      if i < 0 || i >= len
        then HiValueNull
        else HiValueString $ T.pack $ pure (T.index s i')
    Nothing -> throwError HiErrorInvalidArgument
evalHiStringApplyUnary _ args = evalHiErrorUnary args

evalHiStringApplyBinary :: Monad m
  => T.Text
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiStringApplyBinary s [HiValueNumber x, HiValueNumber y] = do
  case evalHiNumberIndex x of
    Just i -> case evalHiNumberIndex y of
      Just j -> pure $ do
        let i' = fromIntegral i
        let j' = fromIntegral j
        let len = fromIntegral $ T.length s
        if i < 0 || i > len || i < 0 || j > len
          then HiValueNull
          else HiValueString $ T.take (j' - i') (T.drop i' s)
      Nothing -> throwError HiErrorInvalidArgument
    Nothing -> throwError HiErrorInvalidArgument
evalHiStringApplyBinary _ args = evalHiErrorBinary args

evalHiStringApply :: Monad m
  => T.Text
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiStringApply val arg@[_]     = evalHiStringApplyUnary val arg
evalHiStringApply val args@[_, _] = evalHiStringApplyBinary val args
evalHiStringApply _ _             = throwError HiErrorArityMismatch

evalHiListApplyUnary :: Monad m
  => Seq HiValue
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiListApplyUnary l [HiValueNumber x] = do
  case evalHiNumberIndex x of
    Just i -> pure $ do
      let i' = fromIntegral i
      let len = fromIntegral $ Data.Sequence.length l
      if i < 0 || i >= len
        then HiValueNull
        else Data.Sequence.index l i'
    Nothing -> throwError HiErrorInvalidArgument
evalHiListApplyUnary _ args = evalHiErrorUnary args

evalHiListApplyBinary :: Monad m
  => Seq HiValue
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiListApplyBinary l [HiValueNumber x, HiValueNumber y] = do
  case evalHiNumberIndex x of
    Just i -> case evalHiNumberIndex y of
      Just j -> pure $ do
        let i' = fromIntegral i
        let j' = fromIntegral j
        if i < 0 || j < 0
          then HiValueNull
          else HiValueList $ Data.Sequence.take (j' - i') (Data.Sequence.drop i' l)
      Nothing -> throwError HiErrorInvalidArgument
    Nothing -> throwError HiErrorInvalidArgument
evalHiListApplyBinary _ args = evalHiErrorBinary args

evalHiListApply :: Monad m
  => Seq HiValue
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiListApply val arg@[_]     = evalHiListApplyUnary val arg
evalHiListApply val args@[_, _] = evalHiListApplyBinary val args
evalHiListApply _ _             = throwError HiErrorArityMismatch

evalHiBytesApplyUnary :: Monad m
  => BS.ByteString
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiBytesApplyUnary b [HiValueNumber x] = do
  case evalHiNumberIndex x of
    Just i -> pure $ do
      let i' = fromIntegral i
      let len = fromIntegral $ BS.length b
      if i < 0 || i >= len
        then HiValueNull
        else HiValueNumber $ fromIntegral $ BS.index b i'
    Nothing -> throwError HiErrorInvalidArgument
evalHiBytesApplyUnary _ args = evalHiErrorBinary args

evalHiBytesApplyBinary :: Monad m
  => BS.ByteString
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiBytesApplyBinary b [HiValueNumber x, HiValueNumber y] = do
  case evalHiNumberIndex x of
    Just i -> case evalHiNumberIndex y of
      Just j -> pure $ do
        let i' = fromIntegral i
        let j' = fromIntegral j
        if i < 0 || j < 0
          then HiValueNull
          else HiValueBytes $ BS.take (j' - i') (BS.drop i' b)
      Nothing -> throwError HiErrorInvalidArgument
    Nothing -> throwError HiErrorInvalidArgument
evalHiBytesApplyBinary _ args = evalHiErrorBinary args

evalHiBytesApply :: Monad m
  => BS.ByteString
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiBytesApply val arg@[_]     = evalHiBytesApplyUnary val arg
evalHiBytesApply val args@[_, _] = evalHiBytesApplyBinary val args
evalHiBytesApply _ _             = throwError HiErrorArityMismatch

evalHiFunctionApplyUnaryBool :: Monad m
  => (Bool -> Bool)
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiFunctionApplyUnaryBool f [HiValueBool x] = pure $ HiValueBool $ f x
evalHiFunctionApplyUnaryBool _ args            = evalHiErrorUnary args

evalHiFunctionApplyUnaryString :: Monad m
  => (T.Text -> T.Text)
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiFunctionApplyUnaryString f [HiValueString s] = pure $ HiValueString $ f s
evalHiFunctionApplyUnaryString _ args              = evalHiErrorUnary args

evalHiFunctionApplyBinaryBool :: Monad m
  => (Bool -> Bool -> Bool)
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiFunctionApplyBinaryBool f [HiValueBool b, HiValueBool b'] = pure $ HiValueBool $ f b b'
evalHiFunctionApplyBinaryBool _ args                            = evalHiErrorBinary args

evalHiFunctionApplyBinaryString :: Monad m
  => (T.Text -> T.Text -> T.Text)
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiFunctionApplyBinaryString f [HiValueString s, HiValueString t] = pure $ HiValueString $ f s t
evalHiFunctionApplyBinaryString _ args                               = evalHiErrorBinary args

evalHiFunctionApplyBinaryNumber :: Monad m
  => (Rational -> Rational -> Rational)
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiFunctionApplyBinaryNumber f [HiValueNumber x, HiValueNumber y] = pure $ HiValueNumber $ f x y
evalHiFunctionApplyBinaryNumber _ args                               = evalHiErrorBinary args

evalHiFunctionApplyAdd :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyAdd args = case args of
  [HiValueNumber _, HiValueNumber _] -> evalHiFunctionApplyBinaryNumber (+) args
  [HiValueList l, HiValueList l']    -> pure $ HiValueList $ l <> l'
  [HiValueBytes b, HiValueBytes b']  -> pure $ HiValueBytes $ b <> b'
  _                                  -> evalHiFunctionApplyBinaryString (<>) args

evalHiFunctionApplyMul :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyMul args = case args of
  [HiValueString s, HiValueNumber n] -> evalHiFunctionApplyMul' HiValueString s n
  [HiValueList l, HiValueNumber n]   -> evalHiFunctionApplyMul' HiValueList l n
  [HiValueBytes b, HiValueNumber n]  -> evalHiFunctionApplyMul' HiValueBytes b n
  [HiValueNumber _, HiValueNumber _] -> evalHiFunctionApplyBinaryNumber (*) args
  _                                  -> evalHiErrorBinary args
  where
    evalHiFunctionApplyMul' :: (Monad m, Semigroup a) =>
      (a -> HiValue)
      -> a
      -> Rational
      -> ExceptT HiError m HiValue
    evalHiFunctionApplyMul' cons x n =
      case evalHiNumberIndex n of
        Just i  -> pure $ cons $ stimes i x
        Nothing -> throwError HiErrorInvalidArgument

evalHiFunctionApplyDiv :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyDiv args = case args of
  [HiValueNumber _, HiValueNumber y] -> do
    if y == 0
    then throwError HiErrorDivideByZero
    else evalHiFunctionApplyBinaryNumber (/) args
  _ -> evalHiFunctionApplyBinaryString (\s t -> T.concat [s, T.pack "/", t]) args

evalHiFunctionApplyCompare :: Monad m
  => (a -> b -> Bool)
  -> a
  -> b
  -> ExceptT HiError m HiValue
evalHiFunctionApplyCompare f x y = pure $ HiValueBool $ f x y

evalHiFunctionApplyEquals :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyEquals args =
  case args of
    [HiValueBool _, HiValueNumber _]        -> pure $ HiValueBool False
    [HiValueNumber _, HiValueBool _]        -> pure $ HiValueBool False
    [HiValueNumber x, HiValueNumber y]      -> evalHiFunctionApplyCompare (==) x y
    [HiValueString s, HiValueString t]      -> evalHiFunctionApplyCompare (==) s t
    [HiValueFunction f, HiValueFunction f'] -> evalHiFunctionApplyCompare (==) f f'
    _                                       -> evalHiFunctionApplyBinaryBool (==) args

evalHiFunctionApplyLessThan :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyLessThan args =
  case args of
    [HiValueBool _, HiValueNumber _]   -> pure $ HiValueBool True
    [HiValueNumber _, HiValueBool _]   -> pure $ HiValueBool False
    [HiValueNumber x, HiValueNumber y] -> evalHiFunctionApplyCompare (<) x y
    _                                  -> evalHiFunctionApplyBinaryBool (<) args

evalHiFunctionApplyGreaterThan :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyGreaterThan = evalHiFunctionApplyLessThan . foldl (flip (:)) []

evalHiFunctionApplyNot :: Monad m
  => ([HiValue] -> ExceptT HiError m HiValue)
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiFunctionApplyNot f args = do
  args' <- f args
  evalHiFunctionApplyUnaryBool not [args']

evalHiFunctionApplyIf :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyIf [HiValueBool b, HiValueFunction f, HiValueFunction f'] = pure $
  HiValueFunction $ if b then f else f'
evalHiFunctionApplyIf [HiValueBool b, x, y]  = pure $
  if b then x else y
evalHiFunctionApplyIf args = evalHiError args 3

evalHiFunctionApplyLength :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyLength args = case args of
  [HiValueString s] -> evalHiFunctionApplyLength' $ T.length s
  [HiValueList l]   -> evalHiFunctionApplyLength' $ Data.Sequence.length l
  _                 -> evalHiError args 1
  where
    evalHiFunctionApplyLength' x = pure $ HiValueNumber $ fromIntegral x

evalHiFunctionApplyReverse :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyReverse [HiValueList l] = pure $ HiValueList $ Data.Sequence.reverse l
evalHiFunctionApplyReverse args            = evalHiFunctionApplyUnaryString T.reverse args

evalHiFunctionApplyList :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyList args = pure $ HiValueList $ fromList args

evalHiFunctionApplyRange :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyRange [HiValueNumber x, HiValueNumber y] = evalHiFunctionApplyList $ map HiValueNumber [x..y]
evalHiFunctionApplyRange args = evalHiErrorBinary args

evalHiFunctionApplyFold :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyFold [HiValueFunction f, HiValueList l] =
  case toList l of
    []     -> throwError HiErrorInvalidArgument
    (x:xs) -> foldM evalHiFunctionApplyFold' x xs
  where
    evalHiFunctionApplyFold' acc x' = evalImpl $
      HiExprApply (HiExprValue (HiValueFunction f)) [HiExprValue acc, HiExprValue x']
evalHiFunctionApplyFold args = evalHiErrorBinary args

evalHiFunctionApplyPackBytes :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyPackBytes [HiValueList l] = do
  bytes <- mapM mapByte (toList l)
  pure $ HiValueBytes $ BS.pack bytes
  where
    mapByte :: Monad m => HiValue -> ExceptT HiError m Word8
    mapByte (HiValueNumber x) =
      if byteCondition x
        then pure $ fromIntegral $ numerator x
        else throwError HiErrorInvalidArgument
    mapByte _ = throwError HiErrorInvalidArgument
    byteCondition x = 0 <= x && x <= 255 && denominator x == 1
evalHiFunctionApplyPackBytes args = evalHiErrorUnary args

evalHiFunctionApplyUnpackBytes :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyUnpackBytes [HiValueBytes b] = pure $
  HiValueList $ fromList $ map (HiValueNumber . fromIntegral) $ BS.unpack b
evalHiFunctionApplyUnpackBytes args = evalHiErrorUnary args

evalHiFunctionApplyEncodeUtf8 :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyEncodeUtf8 [HiValueString s] = pure $ HiValueBytes $ encodeUtf8 s
evalHiFunctionApplyEncodeUtf8 args              = evalHiErrorUnary args

evalHiFunctionApplyDecodeUtf8 :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyDecodeUtf8 [HiValueBytes b] = pure $
  case decodeUtf8' b of
    Left _  -> HiValueNull
    Right s -> HiValueString s
evalHiFunctionApplyDecodeUtf8 args = evalHiErrorUnary args

evalHiFunctionApplyZip :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyZip [HiValueBytes b] = pure $
  HiValueBytes $ BL.toStrict $ evalHiFunctionApplyZip' $ BS.fromStrict b
  where
    evalHiFunctionApplyZip' = compressWith defaultCompressParams {
      compressLevel = bestCompression
    }
evalHiFunctionApplyZip args = evalHiErrorUnary args

evalHiFunctionApplyUnzip :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyUnzip [HiValueBytes b] = pure $
  HiValueBytes $ BL.toStrict $ decompress $ BS.fromStrict b
evalHiFunctionApplyUnzip args = evalHiErrorUnary args

evalHiFunctionApplySerialise :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplySerialise [x] = pure $ HiValueBytes $ BS.toStrict $ serialise x
evalHiFunctionApplySerialise _   = throwError HiErrorArityMismatch

evalHiFunctionApplyDeserialise :: Monad m => [HiValue] -> ExceptT HiError m HiValue
evalHiFunctionApplyDeserialise [HiValueBytes b] = pure $
  fromRight HiValueNull (deserialiseOrFail $ BS.fromStrict b)
evalHiFunctionApplyDeserialise args = evalHiErrorUnary args

evalHiFunctionApply :: Monad m
  => HiFun
  -> [HiValue]
  -> ExceptT HiError m HiValue
evalHiFunctionApply HiFunAdd            = evalHiFunctionApplyAdd
evalHiFunctionApply HiFunSub            = evalHiFunctionApplyBinaryNumber (-)
evalHiFunctionApply HiFunMul            = evalHiFunctionApplyMul
evalHiFunctionApply HiFunDiv            = evalHiFunctionApplyDiv

evalHiFunctionApply HiFunNot            = evalHiFunctionApplyUnaryBool not
evalHiFunctionApply HiFunAnd            = evalHiFunctionApplyBinaryBool (&&)
evalHiFunctionApply HiFunOr             = evalHiFunctionApplyBinaryBool (||)
evalHiFunctionApply HiFunEquals         = evalHiFunctionApplyEquals
evalHiFunctionApply HiFunLessThan       = evalHiFunctionApplyLessThan
evalHiFunctionApply HiFunGreaterThan    = evalHiFunctionApplyGreaterThan
evalHiFunctionApply HiFunNotEquals      = evalHiFunctionApplyNot evalHiFunctionApplyEquals
evalHiFunctionApply HiFunNotLessThan    = evalHiFunctionApplyNot evalHiFunctionApplyLessThan
evalHiFunctionApply HiFunNotGreaterThan = evalHiFunctionApplyNot evalHiFunctionApplyGreaterThan
evalHiFunctionApply HiFunIf             = evalHiFunctionApplyIf

evalHiFunctionApply HiFunLength         = evalHiFunctionApplyLength
evalHiFunctionApply HiFunReverse        = evalHiFunctionApplyReverse

evalHiFunctionApply HiFunToUpper        = evalHiFunctionApplyUnaryString T.toUpper
evalHiFunctionApply HiFunToLower        = evalHiFunctionApplyUnaryString T.toLower
evalHiFunctionApply HiFunTrim           = evalHiFunctionApplyUnaryString T.strip

evalHiFunctionApply HiFunList           = evalHiFunctionApplyList
evalHiFunctionApply HiFunRange          = evalHiFunctionApplyRange
evalHiFunctionApply HiFunFold           = evalHiFunctionApplyFold

evalHiFunctionApply HiFunPackBytes      = evalHiFunctionApplyPackBytes
evalHiFunctionApply HiFunUnpackBytes    = evalHiFunctionApplyUnpackBytes
evalHiFunctionApply HiFunEncodeUtf8     = evalHiFunctionApplyEncodeUtf8
evalHiFunctionApply HiFunDecodeUtf8     = evalHiFunctionApplyDecodeUtf8
evalHiFunctionApply HiFunZip            = evalHiFunctionApplyZip
evalHiFunctionApply HiFunUnzip          = evalHiFunctionApplyUnzip
evalHiFunctionApply HiFunSerialise      = evalHiFunctionApplySerialise
evalHiFunctionApply HiFunDeserialise    = evalHiFunctionApplyDeserialise
