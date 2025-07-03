#!/bin/bash

last_version=$(ls ~/ | grep -E "Backup-[0-9]{4}-[0-9]{2}-[0-9]{2}" | sort -rn | head -n 1)

if [ ! $last_version ]; then
    echo "No existing backup"
    exit 1
fi

if [ ! -d ~/restore ]; then
    mkdir ~/restore
fi

for file in $(ls ~/$last_version | grep -vE ".[0-9]{4}-[0-9]{2}-[0-9]{2}")
do
    cp ~/$last_version/$file ~/restore/
done
