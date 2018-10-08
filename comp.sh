#!/usr/bin/env bash

FILENAME=$1
javac Comp.java
javac Eval.java
javac IA32_ATT.java
javac Lexer.java
javac Machine.java
javac Symbol.java
javac Token.java
java Comp ${FILENAME}.txt > ${FILENAME}.S
gcc -c ${FILENAME}.S -o ${FILENAME}.o
gcc ${FILENAME}.o -o ${FILENAME}
