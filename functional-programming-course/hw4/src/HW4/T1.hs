module HW4.T1
  ( EvaluationError (..)
  , ExceptState (..)
  , mapExceptState
  , wrapExceptState
  , joinExceptState
  , modifyExceptState
  , throwExceptState
  , eval
  ) where

import qualified Control.Monad (ap)
import HW4.Types

data ExceptState e s a = ES { runES :: s -> Except e (Annotated s a) }

mapExceptState :: (a -> b) -> ExceptState e s a -> ExceptState e s b
mapExceptState f (ES fun) = ES ((mapExcept . mapAnnotated) f . fun)

wrapExceptState :: a -> ExceptState e s a
wrapExceptState x = ES (wrapExcept . (x :#))

joinExceptState :: ExceptState e s (ExceptState e s a) -> ExceptState e s a
joinExceptState (ES fun) = ES (\x -> let es = fun x in joinExceptState' es)
  where
    joinExceptState' (Error e)            = Error e
    joinExceptState' (Success (x' :# y')) = runES x' y'

modifyExceptState :: (s -> s) -> ExceptState e s ()
modifyExceptState f = ES (\x -> wrapExcept (() :# f x))

throwExceptState :: e -> ExceptState e s a
throwExceptState e = ES (\_ -> Error e)

instance Functor (ExceptState e s) where
  fmap = mapExceptState

instance Applicative (ExceptState e s) where
  pure = wrapExceptState
  (<*>) = Control.Monad.ap

instance Monad (ExceptState e s) where
  s >>= f = joinExceptState (mapExceptState f s)

data EvaluationError = DivideByZero
  deriving Show

unaryEval
  :: (Double -> Double)
  -> (Double -> Prim Double)
  -> Expr
  -> ExceptState EvaluationError [Prim Double] Double
unaryEval f op x = do
  x' <- eval x
  let op' = op x'
  modifyExceptState (op' :)
  return (f x')

binaryEval
  :: (Double -> Double -> Double)
  -> (Double -> Double -> Prim Double)
  -> Expr
  -> Expr
  -> ExceptState EvaluationError [Prim Double] Double
binaryEval f op x y = do
  x' <- eval x
  y' <- eval y
  let op' = op x' y'
  modifyExceptState (op' :)
  return (f x' y')

eval :: Expr -> ExceptState EvaluationError [Prim Double] Double
eval (Val x)        = pure x
eval (Op (Abs x))   = unaryEval (abs) Abs x
eval (Op (Sgn x))   = unaryEval (signum) Sgn x
eval (Op (Add x y)) = binaryEval (+) Add x y
eval (Op (Sub x y)) = binaryEval (-) Sub x y
eval (Op (Mul x y)) = binaryEval (*) Mul x y
eval (Op (Div x y)) = do
  x' <- eval x
  y' <- eval y
  if y' == 0
    then throwExceptState DivideByZero
    else binaryEval (/) Div (Val x') (Val y')
