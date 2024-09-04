#!/bin/bash

for ((n=1; n<=20; n++));
do
    for ((i=0; i<10; i++));
    do
        { time -f "%E" ./1_2a.sh $n ; } 2>>log_1_2
    done
done
