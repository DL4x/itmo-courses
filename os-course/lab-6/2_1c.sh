#!/bin/bash

for ((n=1; n<=20; n++));
do 
    for ((i=0; i<10; i++));
    do
        ./create_files.sh $n
        { time -f "%E" ./2_1b.sh $n ; } 2>>log_2_1
    done
done
