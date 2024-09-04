#!/bin/bash

N=1000

# Let's calculate the Maclaurin series for the function 1 / (1 + x), |x| < 1
function foo() {
    x=1
    result=0
    for ((i=0; i<N; i++));
    do
        result=$(bc <<< "scale=20; $result + $x")
        x=$(bc <<< "scale=20; $x * $1")
    done
    echo "$result"
}

foo "$(bc <<< "scale=20; 1/$1")"
