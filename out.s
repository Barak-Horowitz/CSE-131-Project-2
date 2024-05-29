.text
j main
quicksort:
addi $at, $sp, 0
sw $ra, ($at)
addi $at, $sp, -4
sw $a0, ($at)
addi $at, $sp, -12
sw $a1, ($at)
addi $at, $sp, -8
sw $a2, ($at)
addi $s0, $0, 0
addi $at, $sp, -40
sw $s0, ($at)
addi $s0, $0, 0
addi $at, $sp, -44
sw $s0, ($at)
addi $at, $sp, -12
lw $s1, ($at)
addi $at, $sp, -8
lw $s2, ($at)
sub $at, $s1, $s2
bge $at, $0, quicksortend
addi $at, $sp, -12
lw $s1, ($at)
addi $at, $sp, -8
lw $s2, ($at)
add $s0, $s1, $s2
addi $at, $sp, -32
sw $s0, ($at)
addi $at, $sp, -32
lw $s1, ($at)
addi $s0, $0, 2
div $s0, $s1, $s0
addi $at, $sp, -32
sw $s0, ($at)
addi $at, $sp, -36
lw $s0, ($at)
addi $at, $sp, -4
lw $s1, ($at)
addi $at, $sp, -32
lw $s2, ($at)
move $at, $s2
sll $at, $at, 2
add $at, $at, $s1
lw $s0, ($at)
addi $at, $sp, -36
sw $s0, ($at)
addi $at, $sp, -12
lw $s1, ($at)
addi $s0, $s1, -1
addi $at, $sp, -40
sw $s0, ($at)
addi $at, $sp, -8
lw $s1, ($at)
addi $s0, $s1, 1
addi $at, $sp, -44
sw $s0, ($at)
quicksortloop0:
quicksortloop1:
addi $at, $sp, -40
lw $s1, ($at)
addi $s0, $s1, 1
addi $at, $sp, -40
sw $s0, ($at)
addi $at, $sp, -28
lw $s0, ($at)
addi $at, $sp, -4
lw $s1, ($at)
addi $at, $sp, -40
lw $s2, ($at)
move $at, $s2
sll $at, $at, 2
add $at, $at, $s1
lw $s0, ($at)
addi $at, $sp, -28
sw $s0, ($at)
addi $at, $sp, -28
lw $s1, ($at)
move $s0, $s1
addi $at, $sp, -16
sw $s0, ($at)
addi $at, $sp, -16
lw $s1, ($at)
addi $at, $sp, -36
lw $s2, ($at)
sub $at, $s1, $s2
blt $at, $0, quicksortloop1
quicksortloop2:
addi $at, $sp, -44
lw $s1, ($at)
addi $s0, $s1, -1
addi $at, $sp, -44
sw $s0, ($at)
addi $at, $sp, -28
lw $s0, ($at)
addi $at, $sp, -4
lw $s1, ($at)
addi $at, $sp, -44
lw $s2, ($at)
move $at, $s2
sll $at, $at, 2
add $at, $at, $s1
lw $s0, ($at)
addi $at, $sp, -28
sw $s0, ($at)
addi $at, $sp, -28
lw $s1, ($at)
move $s0, $s1
addi $at, $sp, -20
sw $s0, ($at)
addi $at, $sp, -20
lw $s1, ($at)
addi $at, $sp, -36
lw $s2, ($at)
sub $at, $s1, $s2
bgt $at, $0, quicksortloop2
addi $at, $sp, -40
lw $s1, ($at)
addi $at, $sp, -44
lw $s2, ($at)
sub $at, $s1, $s2
bge $at, $0, quicksortexit0
addi $at, $sp, -16
lw $s0, ($at)
addi $at, $sp, -4
lw $s1, ($at)
addi $at, $sp, -44
lw $s2, ($at)
move $at, $s2
sll $at, $at, 2
add $at, $at, $s1
sw $s0, ($at)
addi $at, $sp, -16
sw $s0, ($at)
addi $at, $sp, -20
lw $s0, ($at)
addi $at, $sp, -4
lw $s1, ($at)
addi $at, $sp, -40
lw $s2, ($at)
move $at, $s2
sll $at, $at, 2
add $at, $at, $s1
sw $s0, ($at)
addi $at, $sp, -20
sw $s0, ($at)
j quicksortloop0
quicksortexit0:
addi $at, $sp, -44
lw $s1, ($at)
addi $s0, $s1, 1
addi $at, $sp, -24
sw $s0, ($at)
addi $at, $sp, -4
lw $s1, ($at)
move $a0, $s1
addi $at, $sp, -12
lw $s1, ($at)
move $a1, $s1
addi $at, $sp, -44
lw $s1, ($at)
move $a2, $s1
addi $sp, $sp, -48
jal quicksort
addi $sp, $sp, 48
addi $at, $sp, -44
lw $s1, ($at)
addi $s0, $s1, 1
addi $at, $sp, -44
sw $s0, ($at)
addi $at, $sp, -4
lw $s1, ($at)
move $a0, $s1
addi $at, $sp, -44
lw $s1, ($at)
move $a1, $s1
addi $at, $sp, -8
lw $s1, ($at)
move $a2, $s1
addi $sp, $sp, -48
jal quicksort
addi $sp, $sp, 48
quicksortend:
addi $at, $sp, 0
lw $ra, ($at)
jr $ra
main:
addi $at, $sp, 0
sw $ra, ($at)
addi $v0, $0, 9
addi $a0, $0, 400
syscall
move $s1, $v0
addi $at, $sp, -4
sw $s1, ($at)
addi $s0, $0, 0
addi $at, $sp, -8
sw $s0, ($at)
addi $at, $sp, -16
lw $s0, ($at)
addi $v0, $0, 5
syscall
move $s0, $v0
addi $at, $sp, -16
sw $s0, ($at)
addi $at, $sp, -16
lw $s1, ($at)
addi $at, $s1, -100
bgt $at, $0, mainreturn
addi $at, $sp, -16
lw $s1, ($at)
addi $s0, $s1, -1
addi $at, $sp, -16
sw $s0, ($at)
addi $s0, $0, 0
addi $at, $sp, -12
sw $s0, ($at)
mainloop0:
addi $at, $sp, -12
lw $s1, ($at)
addi $at, $sp, -16
lw $s2, ($at)
sub $at, $s1, $s2
bgt $at, $0, mainexit0
addi $at, $sp, -8
lw $s0, ($at)
addi $v0, $0, 5
syscall
move $s0, $v0
addi $at, $sp, -8
sw $s0, ($at)
addi $at, $sp, -8
lw $s0, ($at)
addi $at, $sp, -4
lw $s1, ($at)
addi $at, $sp, -12
lw $s2, ($at)
move $at, $s2
sll $at, $at, 2
add $at, $at, $s1
sw $s0, ($at)
addi $at, $sp, -8
sw $s0, ($at)
addi $at, $sp, -12
lw $s1, ($at)
addi $s0, $s1, 1
addi $at, $sp, -12
sw $s0, ($at)
j mainloop0
mainexit0:
addi $at, $sp, -4
lw $s1, ($at)
move $a0, $s1
addi $a1, $0, 0
addi $at, $sp, -16
lw $s1, ($at)
move $a2, $s1
addi $sp, $sp, -20
jal quicksort
addi $sp, $sp, 20
addi $s0, $0, 0
addi $at, $sp, -12
sw $s0, ($at)
mainloop1:
addi $at, $sp, -12
lw $s1, ($at)
addi $at, $sp, -16
lw $s2, ($at)
sub $at, $s1, $s2
bgt $at, $0, mainexit1
addi $at, $sp, -8
lw $s0, ($at)
addi $at, $sp, -4
lw $s1, ($at)
addi $at, $sp, -12
lw $s2, ($at)
move $at, $s2
sll $at, $at, 2
add $at, $at, $s1
lw $s0, ($at)
addi $at, $sp, -8
sw $s0, ($at)
addi $at, $sp, -8
lw $s1, ($at)
addi $v0, $0, 1
move $a0, $s1
syscall
addi $v0, $0, 11
addi $a0, $0, 10
syscall
addi $at, $sp, -12
lw $s1, ($at)
addi $s0, $s1, 1
addi $at, $sp, -12
sw $s0, ($at)
j mainloop1
mainexit1:
mainreturn:
addi $v0, $0, 10
syscall
