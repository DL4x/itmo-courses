#!/bin/bash

if [[ $# -ne 1 ]]; then
    echo "Invalid number of arguments"
    exit 1
fi

grep -E "$1" ~/.trash.log | while read -r line
do
    file="$(echo $line | sed 's/^\(.*\) \(.*\)$/\1/')"
    echo "Restore $file [y/n]"
    read status<&1
    case "$status" in
        "y")
        path="$(dirname "$file")"
        file_name="$(basename "$file")"
        link_name="$(echo $line | sed 's/^\(.*\) \(.*\)$/\2/')"
        if [ ! -d "$path" ]; then
            path="~"
            echo "File will be restored in ~/"
        fi
        while [ -f "$file_name" ]
        do
            echo "File $file_name already exists. Enter a new name:"
            read new_file_name<&1
            file_name="$new_file_name"
        done
        ln ~/.trash/$link_name "$path"/"$(echo "$file_name" | tr -d "'")"
        rm -f ~/.trash/$link_name && sed -i "\~$line~d" ~/.trash.log
        ;;
        "n")
        :
        ;;
        *)
        exit 1
        ;;
    esac
done
