#!/bin/bash

date_cur=$(date +%F)
last_version=$(ls ~/ | grep -E "Backup-[0-9]{4}-[0-9]{2}-[0-9]{2}" | sort -rn | head -n 1)
date_last_version=$(echo $last_version | cut -d '-' -f2,3,4)
[ ! $date_last_version ] && date_last_version=0
let date_dif=($(date +%s -d $date_cur)-$(date +%s -d $date_last_version))/60/60/24 
if [ ! $last_version ] || [ $date_dif -ge 7 ]; then
    backup_name="Backup-$date_cur"
    mkdir ~/$backup_name
    echo "$backup_name has been created on $date_cur" >> ~/backup-report
    for file in $(ls -a ~/source)
    do 
        cp ~/source/$file ~/$backup_name/
        echo "~/source/$file copied" >> ~/backup-report
    done
else
    echo "$last_version has been modified on $date_cur" >> ~/backup-report
    for file in $(ls -a ~/source)
    do
        if [ ! -f ~/$last_version/$file ]; then
            cp ~/source/$file ~/$last_version/$file
            echo "~/source/$file copied" >> ~/backup-report
        elif [ $(stat -c%s ~/source/$file) -ne $(stat -c%s ~/$last_version/$file) ]; then
            mv ~/$last_version/$file ~/$last_version/$file.$date_cur
            cp ~/source/$file ~/$last_version/$file
            echo "~/source/$file modified" >> ~/backup-report
        fi
    done
fi
