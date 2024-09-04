#!/bin/bash

function f() {
	grep -E "\[    \d{2}.\d{3}\] \($1\)" /var/log/anaconda/X.log | sed "s/($1)/$2/" >> full.log
}

f "WW" "Warning:"
f "II" "Information:"
cat full.log
