#! /bin/sh
source ./ANTLRSetup.sh
./clean.sh
antlr4 -atn LittleCalc.g4 

for df in *.dot
do
    dot -Tsvg -O $df
done

for svg in *.svg
do
   echo "<h2>$svg</h2>" | sed 's/.dot.svg//' >> graphs_images.html
    echo "<img src='$svg' />\n" >> graphs_images.html
done

cat graphs_prelude.html graphs_images.html graphs_prologue.html > graphs.html
open graphs.html