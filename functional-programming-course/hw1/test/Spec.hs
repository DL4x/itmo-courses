{- import HW1.T1 
    ( Day(..)
    , nextDay
    , afterDays
    , isWeekend
    , daysToParty
    )

import HW1.T2
    ( N (..)
    , nplus
    , nmult
    , nsub
    , nFromNatural
    , nToNum
    , ncmp
    , nEven
    , nOdd
    , ndiv
    , nmod
    )

import HW1.T3 
    ( Tree (..)
    , tsize
    , tdepth
    , tmember
    , tinsert
    , tFromList
    )

--TESTS
testTreeOne = tFromList [5, 0, 7, 2, 1]
testTreeTwo = tFromList [5, 3, 10, 1, 4, 8, 2, 9, 6, 7]

main :: IO()
main = do 
    putStrLn "T1"
    putStrLn "Fun #1"
    putStrLn $ show $ nextDay Monday
    putStrLn $ show $ nextDay Tuesday
    putStrLn $ show $ nextDay Wednesday
    putStrLn $ show $ nextDay Thursday
    putStrLn $ show $ nextDay Friday
    putStrLn $ show $ nextDay Saturday
    putStrLn $ show $ nextDay Sunday
    putStrLn "Fun #2"
    putStrLn $ show $ afterDays 0 Monday
    putStrLn $ show $ afterDays 1 Monday
    putStrLn $ show $ afterDays 2 Monday
    putStrLn $ show $ afterDays 3 Monday
    putStrLn $ show $ afterDays 4 Monday
    putStrLn $ show $ afterDays 5 Monday
    putStrLn $ show $ afterDays 6 Monday
    putStrLn $ show $ afterDays 7 Monday
    putStrLn "Fun #3"
    putStrLn $ show $ isWeekend Monday
    putStrLn $ show $ isWeekend Tuesday
    putStrLn $ show $ isWeekend Wednesday
    putStrLn $ show $ isWeekend Thursday
    putStrLn $ show $ isWeekend Friday
    putStrLn $ show $ isWeekend Saturday
    putStrLn $ show $ isWeekend Sunday
    putStrLn "Fun #4"
    putStrLn $ show $ daysToParty Monday
    putStrLn $ show $ daysToParty Tuesday
    putStrLn $ show $ daysToParty Wednesday
    putStrLn $ show $ daysToParty Thursday
    putStrLn $ show $ daysToParty Friday
    putStrLn $ show $ daysToParty Saturday
    putStrLn $ show $ daysToParty Sunday
    putStrLn "T2"
    putStrLn "Fun #1"
    putStrLn $ show $ nplus Z (S Z)
    putStrLn $ show $ nplus (S Z) (S (S Z))
    putStrLn $ show $ nplus (S (S Z)) (S (S (S Z)))
    putStrLn "Fun #2"
    putStrLn $ show $ nmult Z Z
    putStrLn $ show $ nmult Z (S Z)
    putStrLn $ show $ nmult (S Z) Z
    putStrLn $ show $ nmult (S Z) (S (S Z))
    putStrLn $ show $ nmult (S (S Z)) (S (S (S Z)))
    putStrLn "Fun #3"
    putStrLn $ show $ nsub Z Z
    putStrLn $ show $ nsub Z (S Z)
    putStrLn $ show $ nsub (S Z) Z
    putStrLn $ show $ nsub (S (S (S (S Z)))) (S (S Z))
    putStrLn "Fun #4"
    putStrLn $ show $ ncmp Z Z
    putStrLn $ show $ ncmp Z (S Z)
    putStrLn $ show $ ncmp (S Z) Z
    putStrLn $ show $ ncmp (S (S (S (S Z)))) (S (S Z))
    putStrLn $ show $ ncmp (S (S Z)) (S (S (S (S Z))))
    putStrLn $ show $ ncmp (S (S Z)) (S (S Z))
    putStrLn "Fun #5"
    putStrLn $ show $ nFromNatural 0
    putStrLn $ show $ nFromNatural 1
    putStrLn $ show $ nFromNatural 2
    putStrLn $ show $ nFromNatural 3
    putStrLn $ show $ nFromNatural 4
    putStrLn $ show $ nFromNatural 5
    putStrLn "Fun #5"
    putStrLn $ show $ nToNum $ Z
    putStrLn $ show $ nToNum $ S Z
    putStrLn $ show $ nToNum $ S (S Z)
    putStrLn $ show $ nToNum $ S (S (S Z))
    putStrLn $ show $ nToNum $ S (S (S (S Z)))
    putStrLn $ show $ nToNum $ S ( S ( S (S (S Z))))
    putStrLn "T3"
    putStrLn "Tree #1"
    -- putStrLn $ show $ testTreeOne
    putStrLn $ show $ tsize testTreeOne
    putStrLn $ show $ tdepth testTreeOne
    putStrLn $ show $ tmember 0 testTreeOne
    putStrLn $ show $ tmember 1 testTreeOne
    putStrLn $ show $ tmember 2 testTreeOne
    putStrLn $ show $ tmember 3 testTreeOne
    putStrLn "Tree #2"
    -- putStrLn $ show $ testTreeTwo
    putStrLn $ show $ tsize testTreeTwo
    putStrLn $ show $ tdepth testTreeTwo
    putStrLn $ show $ tmember 5 testTreeTwo
    putStrLn $ show $ tmember 10 testTreeTwo
    putStrLn $ show $ tmember 15 testTreeTwo
    putStrLn $ show $ tmember 20 testTreeTwo -}
