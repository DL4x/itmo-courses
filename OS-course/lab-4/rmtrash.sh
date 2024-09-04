#!/bin/bash

if [[ $# -ne 1 ]]; then
    echo "Invalid number of arguments"
    exit 1
fi

if [[ ! -f $1 ]]; then
    echo "File $1 does not exist"
    exit 1
fi

if [[ ! -d ~/.trash/ ]]; then 
    mkdir ~/.trash
fi

link_name=$(( $([ -f ~/.trash.info ] && cat ~/.trash.info) + 1))
echo "$link_name" > ~/.trash.info
ln "$1" ~/.trash/$link_name && rm -f "$1"

echo "$PWD/'$1' $link_name" >> ~/.trash.log
