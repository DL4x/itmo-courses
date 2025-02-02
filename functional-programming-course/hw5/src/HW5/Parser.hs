module HW5.Parser
  ( parse
  ) where

import HW5.Base (HiExpr (..), HiFun (..), HiValue (..))

import Control.Applicative (optional)
import Control.Monad.Combinators.Expr (Operator (InfixL, InfixN, InfixR), makeExprParser)
import Data.Text (pack)
import Data.Void (Void)
import Data.Word (Word8)
import Text.Megaparsec (Parsec, between, choice, count, empty, eof, manyTill, notFollowedBy,
                        runParser, sepBy, sepEndBy, try, (<|>))
import Text.Megaparsec.Char (char, hexDigitChar, space1, string)
import Text.Megaparsec.Error (ParseErrorBundle)
import Text.Read (readMaybe)

import qualified Data.ByteString as BS
import qualified Text.Megaparsec.Char.Lexer as L

type Parser = Parsec Void String

sc :: Parser ()
sc = L.space space1 empty empty

symbol :: String -> Parser String
symbol = L.symbol sc

lexeme :: Parser a -> Parser a
lexeme = L.lexeme sc

parse :: String -> Either (ParseErrorBundle String Void) HiExpr
parse = runParser (parseHiExpr <* eof) ""

parseHiExpr :: Parser HiExpr
parseHiExpr = parseHiExpr' >>= chainHiExpr
  where
    chainHiExpr x = do
      args <- chainHiExpr'
      case args of
        Nothing    -> pure x
        Just args' -> chainHiExpr (HiExprApply x args')
    chainHiExpr' = optional (parseParens (sepBy parseHiExpr (symbol ",")))

parseHiExpr' :: Parser HiExpr
parseHiExpr' = makeExprParser parseHiExprTerm table

parseHiExprTerm :: Parser HiExpr
parseHiExprTerm = sc *> (parseParens parseHiExpr' <|> try parseHiExprApply <|> try parseHiValue) <* sc

parseHiValue :: Parser HiExpr
parseHiValue = HiExprValue
  <$> parseHiValue'
  <|> parseHiValueBytes
  <|> parseHiValueList
  where
    parseHiValue' = choice
      [ try parseHiValueNull
      , try parseHiValueBool
      , try parseHiValueNumber
      , try parseHiValueString
      , try parseHiValueFunction
      ]

parseHiValueNull :: Parser HiValue
parseHiValueNull = HiValueNull <$ symbol "null"

parseHiValueBool :: Parser HiValue
parseHiValueBool = HiValueBool <$> choice
  [ True <$ symbol "true"
  , False <$ symbol "false"
  ]

parseBetween :: String -> String -> Parser a -> Parser a
parseBetween start end = between (symbol start) (symbol end)

parseParens :: Parser a -> Parser a
parseParens = parseBetween "(" ")"

parseHiValueList :: Parser HiExpr
parseHiValueList = HiExprApply (HiExprValue (HiValueFunction HiFunList)) <$> parseBetween "[" "]" (sepBy parseHiExpr (symbol ","))

parseHiValueBytes :: Parser HiExpr
parseHiValueBytes = do
  args <- parseBetween "[#" "#]" (sepEndBy parseHiValueByte space1)
  return $ HiExprValue $ HiValueBytes $ BS.pack args
  where
    parseHiValueByte :: Parser Word8
    parseHiValueByte = do
      str <- count 2 hexDigitChar
      maybe empty pure (readMaybe ("0x" ++ str))

parseHiValueNumber :: Parser HiValue
parseHiValueNumber = HiValueNumber . toRational <$> L.signed (pure()) L.scientific

parseHiValueString :: Parser HiValue
parseHiValueString = HiValueString . pack <$> (char '"' >> manyTill L.charLiteral (char '"'))

parseHiValueFunction :: Parser HiValue
parseHiValueFunction = HiValueFunction <$> choice
  [ HiFunAdd <$ string "add"
  , HiFunSub <$ string "sub"
  , HiFunMul <$ string "mul"
  , HiFunDiv <$ string "div"
  , HiFunAnd <$ string "and"
  , HiFunOr <$ string "or"
  , HiFunLessThan <$ string "less-than"
  , HiFunGreaterThan <$ string "greater-than"
  , HiFunEquals <$ string "equals"
  , HiFunNotLessThan <$ string "not-less-than"
  , HiFunNotGreaterThan <$ string "not-greater-than"
  , HiFunNotEquals <$ string "not-equals"
  , HiFunNot <$ string "not"
  , HiFunIf <$ string "if"
  , HiFunLength <$ string "length"
  , HiFunToUpper <$ string "to-upper"
  , HiFunToLower <$ string "to-lower"
  , HiFunReverse <$ string "reverse"
  , HiFunTrim <$ string "trim"
  , HiFunList <$ string "list"
  , HiFunRange <$ string "range"
  , HiFunFold <$ string "fold"
  , HiFunPackBytes <$ string "pack-bytes"
  , HiFunUnpackBytes <$ string "unpack-bytes"
  , HiFunZip <$ string "zip"
  , HiFunUnzip <$ string "unzip"
  , HiFunEncodeUtf8 <$ string "encode-utf8"
  , HiFunDecodeUtf8 <$ string "decode-utf8"
  , HiFunSerialise <$ string "serialise"
  , HiFunDeserialise <$ string "deserialise"
  ]

parseHiExprApply :: Parser HiExpr
parseHiExprApply = do
  hiVal <- parseHiValue
  HiExprApply hiVal <$> parseHiExprApplyArgs
  where
    parseHiExprApplyArgs = parseParens $ sepBy parseHiExpr (symbol ",")

table :: [[Operator Parser HiExpr]]
table =
  [ [ binaryL "*"  HiFunMul
    , binaryL "/"  HiFunDiv             ]
  , [ binaryL "+"  HiFunAdd
    , binaryL "-"  HiFunSub             ]
  , [ binaryN "<"  HiFunLessThan
    , binaryN ">"  HiFunGreaterThan
    , binaryN ">=" HiFunNotLessThan
    , binaryN "<=" HiFunNotGreaterThan
    , binaryN "==" HiFunEquals
    , binaryN "/=" HiFunNotEquals       ]
  , [ binaryR "&&" HiFunAnd             ]
  , [ binaryR "||" HiFunOr              ] ]
  where
    binaryL name fun = InfixL $ binary' name fun
    binaryN name fun = InfixN $ binary' name fun
    binaryR name fun = InfixR $ binary' name fun
    binary' name fun = (\x y -> HiExprApply (HiExprValue (HiValueFunction fun)) [x, y])
      <$ (lexeme . try) (string name <* notFollowedBy (string "="))
