#!/bin/bash

let N=$1+1
for ((i=1; i<$N; i++));
do
    ./write.sh $i
done
