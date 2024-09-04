#!/bin/bash

let N=$1+2
for ((i=2; i<$N; i++));
do
    ./function.sh $i &
done
wait
