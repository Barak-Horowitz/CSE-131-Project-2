#!/bin/bash

<<<<<<< HEAD
# write a bash script that runs optimizer in this file.
# script takes one argument, a path to the input ir file 
# script outputs an optimized ir file named out.ir
java -cp build IRCompiler ${1} > out.ir
=======
# Write a script to run your backend
# This script must take exactly one terminal argument:
# a path to an input IR file. It should also take one
# flag, either ``--naive`` or ``--greedy`` to indicate
# either the naive or intra-block greedy allocation algorithm.
# This script should output a file ``out.s``.

# For example, if a Tiger-IR file is located at path/to/file.ir,
# then it should be possible to run run.sh as follows.

# run.sh path/to/file.ir --naive
# Produces out . s
# run.sh path/to/file.ir --greedy
# Produces out . s
>>>>>>> 0502606 (first commit)
