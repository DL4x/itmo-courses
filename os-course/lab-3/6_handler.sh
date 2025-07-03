#!/bin/bash

echo $$ > .pid
x=1
MODE="add"

function usr1() {
    MODE="add"
}

function usr2() {
    MODE="mult"
}

function term() {
    MODE="quit"
}

trap 'usr1' USR1
trap 'usr2' USR2
trap 'term' SIGTERM 

while true; do
    case $MODE in
        "add")
        x=$(( $x + 2 ))
        echo "$x"
        ;;
        "mult")
        x=$(( $x * 2 ))
        echo "$x"
        ;;
        "quit")
        rm .pid
        echo "The program ended successfully"
        exit 0
        ;;
    esac
    sleep 1
done
