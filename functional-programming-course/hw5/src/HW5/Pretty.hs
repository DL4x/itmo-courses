module HW5.Pretty
  ( prettyValue
  , prettyError
  ) where

import HW5.Base (HiError (..), HiFun (..), HiValue (..))

import Prettyprinter (Doc, pretty, viaShow, (<+>))
import Prettyprinter.Render.Terminal (AnsiStyle)

import Data.Foldable (toList)
import Data.List (intersperse)
import Data.Ratio (denominator, numerator, (%))
import Data.Scientific (FPFormat (Fixed), formatScientific, fromRationalRepetendUnlimited)
import Data.Sequence (Seq)
import GHC.Num (integerIsNegative)
import Text.Printf (printf)

import qualified Data.ByteString as BS

prettyValue' :: Rational -> Doc AnsiStyle
prettyValue' x =
  if denominator x == 1
    then pretty $ numerator x
    else prettyValueFraction x

prettyValueFraction :: Rational -> Doc AnsiStyle
prettyValueFraction frac =
  case fromRationalRepetendUnlimited frac of
    (frac', Nothing) -> pretty (formatScientific Fixed Nothing frac')
    _                -> prettyValueFraction' frac
    where
      prettyValueFraction' x =
        if abs (numerator x) > denominator x
          then let (whole, fractional) = quotRem (numerator x) (denominator x)
            in prettyValueCombineFractional x whole fractional
          else pretty (numerator x) <> pretty "/" <> pretty (denominator x)
      prettyValueCombineFractional x whole fractional
        = pretty whole
        <+> pretty sign
        <+> prettyValueFraction' (abs fractional % denominator x)
        where
          sign = if integerIsNegative fractional then "-" else "+"

prettyValueList :: Seq HiValue -> Doc AnsiStyle
prettyValueList l = pretty "[" <+> mconcat (intersperse (pretty ", ") (toList (fmap prettyValue l))) <+> pretty "]"

prettyValueBytes :: BS.ByteString -> Doc AnsiStyle
prettyValueBytes b = pretty $ "[# " ++ mconcat (intersperse " " (fmap (printf "%02x")  (BS.unpack b))) ++ " #]"

prettyValue :: HiValue -> Doc AnsiStyle
prettyValue HiValueNull = pretty "null"
prettyValue (HiValueBool b) = pretty $
  if b then "true" else "false"
prettyValue (HiValueNumber x) = prettyValue' x
prettyValue (HiValueString s) = viaShow s
prettyValue (HiValueList l) = prettyValueList l
prettyValue (HiValueBytes b) = prettyValueBytes b
prettyValue (HiValueFunction f) = pretty $
  case f of
    HiFunAdd            -> "add"
    HiFunSub            -> "sub"
    HiFunMul            -> "mul"
    HiFunDiv            -> "div"
    HiFunAnd            -> "and"
    HiFunOr             -> "or"
    HiFunLessThan       -> "less-than"
    HiFunGreaterThan    -> "greater-than"
    HiFunEquals         -> "equals"
    HiFunNotLessThan    -> "not-less-than"
    HiFunNotGreaterThan -> "not-greater-than"
    HiFunNotEquals      -> "not-equals"
    HiFunNot            -> "not"
    HiFunIf             -> "if"
    HiFunLength         -> "length"
    HiFunToUpper        -> "to-upper"
    HiFunToLower        -> "to-lower"
    HiFunReverse        -> "reverse"
    HiFunTrim           -> "trim"
    HiFunList           -> "list"
    HiFunRange          -> "range"
    HiFunFold           -> "fold"
    HiFunPackBytes      -> "pack-bytes"
    HiFunUnpackBytes    -> "unpack-bytes"
    HiFunZip            -> "zip"
    HiFunUnzip          -> "unzip"
    HiFunEncodeUtf8     -> "encode-utf8"
    HiFunDecodeUtf8     -> "decode-utf8"
    HiFunSerialise      -> "serialise"
    HiFunDeserialise    -> "deserialise"

prettyError :: HiError -> Doc AnsiStyle
prettyError = viaShow
