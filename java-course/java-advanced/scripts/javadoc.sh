#!/bin/bash

javadoc -d ../javadoc \
 -private \
 -cp ../../java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.implementor.jar \
 ../java-solutions/info/kgeorgiy/ja/shulpin/implementor/Implementor.java \
 ../../java-advanced-2024/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/Impler.java \
 ../../java-advanced-2024/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/JarImpler.java
