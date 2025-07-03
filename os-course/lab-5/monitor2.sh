#!/bin/bash

rm -f .mem2_data

./mem.sh &
./mem2.sh &

while true; do
    [ -z $(pgrep "mem.sh") ] && [ -z $(pgrep "mem2.sh") ] && break
    top_data="$(top -b -n 1)"
    echo "$(echo "$top_data" | sed -n '4,5p')" >> .mem2_data
    echo "$(echo "$top_data" | grep "mem[2]*.sh")" >> .mem2_data
    echo "$(echo "$top_data" | sed -n '8,12p')" >> .mem2_data
    echo >> .mem2_data
    sleep 1
done
