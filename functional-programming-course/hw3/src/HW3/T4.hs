module HW3.T4
  ( State (..)
  , Prim (..)
  , Expr (..)
  , mapState
  , wrapState
  , joinState
  , modifyState
  , eval
  ) where

import qualified Control.Monad (ap)
import HW3.T1

newtype State s a = S { runS :: s -> Annotated s a }

mapState :: (a -> b) -> State s a -> State s b
mapState f (S fun) = S (mapAnnotated f . fun)

wrapState :: a -> State s a
wrapState x = S (x :#)

joinState :: State s (State s a) -> State s a
joinState (S fun) = S (\x -> let x' :# y' = fun x in runS x' y')

modifyState :: (s -> s) -> State s ()
modifyState f = S (\x -> () :# f x)

instance Functor (State s) where
  fmap = mapState

instance Applicative (State s) where
  pure = wrapState
  (<*>) = Control.Monad.ap

instance Monad (State s) where
  s >>= f = joinState (mapState f s)

data Prim a =
    Add a a
  | Sub a a
  | Mul a a
  | Div a a
  | Abs a
  | Sgn a
  deriving Show

data Expr = Val Double | Op (Prim Expr)
  deriving Show

instance Num Expr where
  x + y = Op (Add x y)
  x - y = Op (Sub x y)
  x * y = Op (Mul x y)
  abs x = Op (Abs x)
  signum x = Op (Sgn x)
  fromInteger x = Val (fromInteger x)

instance Fractional Expr where
  x / y = Op (Div x y)
  fromRational x = Val (fromRational x)

unaryEval 
  :: (Double -> Double) 
  -> (Double -> Prim Double) 
  -> Expr 
  -> State [Prim Double] Double
unaryEval f op x = do
  x' <- eval x
  let op' = op x'
  modifyState (op' :)
  return (f x')

binaryEval 
  :: (Double -> Double -> Double) 
  -> (Double -> Double -> Prim Double) 
  -> Expr 
  -> Expr 
  -> State [Prim Double] Double
binaryEval f op x y = do
  x' <- eval x
  y' <- eval y
  let op' = op x' y'
  modifyState (op' :)
  return (f x' y')

eval :: Expr -> State [Prim Double] Double
eval (Val x)        = pure x
eval (Op (Abs x))   = unaryEval (abs) Abs x
eval (Op (Sgn x))   = unaryEval (signum) Sgn x
eval (Op (Add x y)) = binaryEval (+) Add x y
eval (Op (Sub x y)) = binaryEval (-) Sub x y
eval (Op (Mul x y)) = binaryEval (*) Mul x y
eval (Op (Div x y)) = binaryEval (/) Div x y
