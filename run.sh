#!/bin/bash

# write a bash script that runs optimizer in this file.
# script takes one argument, a path to the input ir file 
# script outputs an optimized ir file named out.ir
java -cp build IRCompiler ${1} > out.ir
