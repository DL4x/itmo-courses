module Main (main) where

import HW5.Evaluator (eval)
import HW5.Parser (parse)
import HW5.Pretty (prettyValue, prettyError)
import HW5.Base (HiValue (..), HiError)

import System.Console.Haskeline
import Control.Monad.IO.Class (liftIO)
import Text.Megaparsec (errorBundlePretty)

process :: String -> InputT IO()
process input = do 
  let parseResult = parse input
  case parseResult of
    Left parseErr -> outputStrLn 
      $ errorBundlePretty parseErr
    Right expr -> do
      evalResult <- eval expr
      case evalResult of
        Left evalErr -> printPrettyError evalErr
        Right result -> printPrettyValue result

printPrettyError :: HiError -> InputT IO()
printPrettyError err = liftIO $ print $ prettyError err

printPrettyValue :: HiValue -> InputT IO()
printPrettyValue val = liftIO $ print $ prettyValue val

main :: IO ()
main = runInputT defaultSettings loop
   where
       loop :: InputT IO ()
       loop = do
         minput <- getInputLine "hi> "
         case minput of 
            Nothing -> return ()
            Just "quit" -> return ()
            Just input -> do 
                process input
                loop
