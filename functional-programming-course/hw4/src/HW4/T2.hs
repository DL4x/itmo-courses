{-# LANGUAGE DerivingStrategies         #-}
{-# LANGUAGE GeneralisedNewtypeDeriving #-}

module HW4.T2
  ( ParseError (..)
  , runP
  , pChar
  , parseError
  , parseExpr
  ) where

import Control.Applicative
import Control.Monad
import qualified Data.Char (digitToInt, isDigit, isSpace)
import qualified Data.Maybe (fromMaybe)
import Numeric.Natural (Natural)

import HW4.T1 (ExceptState (..))
import HW4.Types

data ParseError = ErrorAtPos Natural
  deriving Show

newtype Parser a = P (ExceptState ParseError (Natural, String) a)
  deriving newtype (Functor, Applicative, Monad)

runP :: Parser a -> String -> Except ParseError a
runP (P es) s = runP' $ runES es (0, s)
  where
    runP' (Error e)          = Error e
    runP' (Success (x :# _)) = Success x

pChar :: Parser Char
pChar = P $ ES $ \(pos, s) ->
  case s of
    []     -> Error (ErrorAtPos pos)
    (c:cs) -> Success (c :# (pos + 1, cs))

parseError :: Parser a
parseError = P $ ES $ \(pos, _) -> Error (ErrorAtPos pos)

instance Alternative Parser where
  empty = parseError
  (P esP) <|> (P esQ) = P $ ES $ \t ->
    case runES esP t of
      Error _ -> runES esQ t
      success -> success

-- No methods
instance MonadPlus Parser

pEof :: Parser ()
pEof = P $ ES $ \t@(pos, s) ->
  case s of
    [] -> Success (() :# t)
    _  -> Error (ErrorAtPos pos)

pExpected :: Char -> Parser Char
pExpected ch = mfilter (== ch) pChar

pDigits :: Parser String
pDigits = some $ mfilter Data.Char.isDigit pChar

pWhitespace :: Parser ()
pWhitespace = void $ many (mfilter Data.Char.isSpace pChar)

pVal :: Parser Expr
pVal = do
  decimal <- pDigits
  fractional <- optional $ pExpected '.' *> pDigits
  pure $ Val $ toDouble decimal (Data.Maybe.fromMaybe "" fractional)
  where
    digitsToInt = foldl (\x ch -> 10 * x + toInteger (Data.Char.digitToInt ch)) 0
    toDouble decimal fractional =
      fromRational $ fromIntegral (digitsToInt $ decimal ++ fractional) / (10 ^ length fractional)

pBrackets :: Parser Expr
pBrackets = do
  pExpected '('
  pWhitespace
  x <- pMinPrior
  pWhitespace
  pExpected ')'
  return x

pAbstractExpr
  :: Parser Char
  -> Parser Expr
  -> Parser Expr
pAbstractExpr expOp expParser = do
  x <- expParser
  pRestExpr x
  where
    pRestExpr x = (do
        pWhitespace
        op <- expOp
        case toOp op of
          Just f -> do
            pWhitespace
            y <- expParser
            pRestExpr $ f x y
          _ -> parseError
      ) <|> pure x
    toOp op = case op of
      '+' -> Just (+)
      '-' -> Just (-)
      '*' -> Just (*)
      '/' -> Just (/)
      _   -> Nothing

pMinPrior :: Parser Expr
pMinPrior = pPrior5

pMaxPrior :: Parser Expr
pMaxPrior = pVal <|> pBrackets

pPrior4 :: Parser Expr
pPrior4 = pAbstractExpr pOperators4 pExpectedPrior4
  where
    pOperators4
      =   pExpected '*'
      <|> pExpected '/'
    pExpectedPrior4 = pMaxPrior

pPrior5 :: Parser Expr
pPrior5 = pAbstractExpr pOperators5 pExpectedPrior5
  where
    pOperators5
      =   pExpected '+'
      <|> pExpected '-'
    pExpectedPrior5 = pPrior4

parseExpr :: String -> Except ParseError Expr
parseExpr = runP parseExpr'
  where
    parseExpr' = do
      pWhitespace
      result <- pMinPrior
      pWhitespace
      pEof
      return result
