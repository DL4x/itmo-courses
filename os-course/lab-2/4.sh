#!/bin/bash

function divide() {
    echo $1 $2 | awk '{
        if (!$1 || !$2)
            print "";
        else
            print $1 / $2;    
    }'
}

pids=$(ls /proc | grep -E "^[[:digit:]]+$")
for pid in $pids
do
    ppid=$(grep -sE "PPid" /proc/$pid/status | awk '{print $2}')
    sum_exec_runtime=$(grep -sE "sum_exec_runtime" /proc/$pid/sched | awk '{print $3}')
    nr_switches=$(grep -sE "nr_switches" /proc/$pid/sched | awk '{print $3}')
    art=$(divide $sum_exec_runtime $nr_switches)
    if ! [[ -z $ppid && -z $art ]]; then
        echo "ProcessID=$pid : Parent_ProcessID=$ppid : Average_Running_Time=$art"
    fi
done | sort -k3 -t '=' -nk2 > file
