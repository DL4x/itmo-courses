#!bin/bash

pids=$(ls /proc | grep -E "^[[:digit:]]+$")

m_data=$(
    for pid in $pids
    do
        echo "$pid:$(grep -sE "read_bytes" /proc/$pid/io | awk '{print $2}')"
    done
)

sleep 60

for line in $m_data
do
    pid=$(echo $line | awk -F ':' '{print $1}')
    bytes1=$(echo $line | awk -F ':' '{print $2}')
    bytes2=$(grep -sE "read_bytes" /proc/$pid/io | awk '{print $2}')
    echo "$pid $(( bytes2-bytes1 )) $(grep -s $ /proc/$pid/comm)"
done | sort -nrk2 | head -n3 | awk '{print $1":"$3":"$2}'
