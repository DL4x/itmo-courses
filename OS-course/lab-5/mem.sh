#!/bin/bash

rm -f report.log

count=0
array=()
while true; do
    if [ $count -eq 100000 ]; then
        count=0
        echo "${#array[@]}" >> report.log
    fi
    let count=count+1
    array+=(1 2 3 4 5 6 7 8 9 10)
done
