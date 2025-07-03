#!/bin/bash

function process() {
    a=0
    while true; do
        a=$(( a * 0 ))
    done
}

# first background process
process &
pid1=$!

# second background process
process &

# third background process
process &
pid3=$!

sudo cpulimit -b -p $pid1 -l 10
kill $pid3
