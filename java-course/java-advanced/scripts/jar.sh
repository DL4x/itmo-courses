#!/bin/bash

javac -cp ../../java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.implementor.jar \
../java-solutions/info/kgeorgiy/ja/shulpin/implementor/Implementor.java

# shellcheck disable=SC2164
cd ../java-solutions/

jar cfvm ../scripts/Implementor.jar \
../scripts/MANIFEST.MF \
info/kgeorgiy/ja/shulpin/implementor/Implementor.class

rm -f info/kgeorgiy/ja/shulpin/implementor/Implementor.class
