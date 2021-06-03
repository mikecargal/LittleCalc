#! /bin/sh

for df in *.dot
do
    dot -Tsvg -O $df
done