#! /bin/zsh
source ./ANTLRSetup.sh
./clean.sh
antlr4 LittleCalc.g4
javac *.java
grun LittleCalc calcIn -tree "$PROJROOT/little.ltl"
