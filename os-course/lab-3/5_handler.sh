#!/bin/bash

function clean() {
    killall tail
    kill $(cat .pid)
    rm .pid
}

x=1
mode=0
(tail -f pipe) | 
while true; do
    read LINE
    case $LINE in
        "+") 
        mode=0
        ;;
        "*")
        mode=1
        ;;
        "QUIT")
        clean
        echo "The program ended successfully"
        exit 0
        ;;
        *)
        if [[ $(echo "$LINE" | awk '$1 ~ /^-?[0-9]+$/ {print $1}') ]]; then
    	    [[ $mode -eq 0 ]] && x=$(( $x + $LINE )) || x=$(( $x * $LINE ))
    	    echo "Actual result = $x"
        else
            clean
            echo "The program ended incorrectly"
            exit 1   
	    fi
        ;;
    esac
done
