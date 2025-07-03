#!/bin/bash

function switch() {
    echo "Enter pipe name:" > `tty`
    while true; do
        read PIPE
        if [ ! -p $PIPE ]; then
            echo "Invalid pipe name! Try again:" > `tty`
        else
            break    
        fi
    done 
    echo "Successfully switched to $PIPE!" > `tty`
    echo $PIPE
}

echo $$ > .pid
pipe_name=$(switch)
while true; do
    read LINE
    case $LINE in
        "SWITCH")
        pipe_name=$(switch)
        ;;
        *)
        echo "$LINE" > $pipe_name
        ;;
    esac
done
