#!/bin/bash

rm -f .mem_data

./mem.sh &

while true; do
    [ -z $(pgrep "mem.sh") ] && break
    top_data="$(top -b -n 1)"
    echo "$(echo "$top_data" | sed -n '4,5p')" >> .mem_data
    echo "$(echo "$top_data" | grep "mem.sh")" >> .mem_data
    echo "$(echo "$top_data" | sed -n '8,12p')" >> .mem_data
    echo >> .mem_data
    sleep 1
done
