#!/bin/bash

for ((i=1; i<=$1; i++));
do
    rm -f file$i
    for ((j=1; j<=25000; j++));
    do
        echo "$j" >> file$i
    done
done
