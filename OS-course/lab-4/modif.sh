#!/bin/bash

declare -A array_map

while read -r file
do
    if [ ! -f $file ]; then
        continue
    fi
    sum=$(sha256sum $file | awk '{print $1}')
    array_map[$sum]+="$file "
done < <(ls -a)

count=0
for key in ${!array_map[@]};
do
    let count=$count+1
    echo "$count) ${array_map[$key]}"
done
