#!/usr/bin/env bash

FILENAME=$1
javac Comp.java
javac Eval.java
javac IA32.java
javac Lexer.java
javac Machine.java
javac Symbol.java
javac Token.java
java Comp ${FILENAME} > ${FILENAME}.S
gcc -c ${FILENAME}.S -o ${FILENAME}.o
gcc ${FILENAME}.o -o ${FILENAME}
