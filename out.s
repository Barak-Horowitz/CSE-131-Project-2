.text
j main
quicksort:
addi $t0, $sp, 0
sw $ra, ($t0)
addi $t0, $sp, -4
sw $a0, ($t0)
addi $t0, $sp, -12
sw $a1, ($t0)
addi $t0, $sp, -8
sw $a2, ($t0)
addi $s0, $0, 0
addi $t0, $sp, -40
sw $s0, ($t0)
addi $s0, $0, 0
addi $t0, $sp, -44
sw $s0, ($t0)
addi $t0, $sp, -12
lw $s1, ($t0)
addi $t0, $sp, -8
lw $s2, ($t0)
sub $t0, $s1, $s2
bge $t0, $0, quicksortend
addi $t0, $sp, -12
lw $s1, ($t0)
addi $t0, $sp, -8
lw $s2, ($t0)
add $s0, $s1, $s2
addi $t0, $sp, -32
sw $s0, ($t0)
addi $t0, $sp, -32
lw $s1, ($t0)
addi $s0, $0, 2
div $s0, $s1, $s0
addi $t0, $sp, -32
sw $s0, ($t0)
addi $t0, $sp, -36
lw $s0, ($t0)
addi $t0, $sp, -4
lw $s1, ($t0)
addi $t0, $sp, -32
lw $s2, ($t0)
move $t0, $s2
sll $t0, $t0, 2
add $t0, $t0, $s1
lw $s0, ($t0)
addi $t0, $sp, -36
sw $s0, ($t0)
addi $t0, $sp, -12
lw $s1, ($t0)
addi $s0, $s1, -1
addi $t0, $sp, -40
sw $s0, ($t0)
addi $t0, $sp, -8
lw $s1, ($t0)
addi $s0, $s1, 1
addi $t0, $sp, -44
sw $s0, ($t0)
quicksortloop0:
quicksortloop1:
addi $t0, $sp, -40
lw $s1, ($t0)
addi $s0, $s1, 1
addi $t0, $sp, -40
sw $s0, ($t0)
addi $t0, $sp, -28
lw $s0, ($t0)
addi $t0, $sp, -4
lw $s1, ($t0)
addi $t0, $sp, -40
lw $s2, ($t0)
move $t0, $s2
sll $t0, $t0, 2
add $t0, $t0, $s1
lw $s0, ($t0)
addi $t0, $sp, -28
sw $s0, ($t0)
addi $t0, $sp, -28
lw $s1, ($t0)
move $s0, $s1
addi $t0, $sp, -16
sw $s0, ($t0)
addi $t0, $sp, -16
lw $s1, ($t0)
addi $t0, $sp, -36
lw $s2, ($t0)
sub $t0, $s1, $s2
blt $t0, $0, quicksortloop1
quicksortloop2:
addi $t0, $sp, -44
lw $s1, ($t0)
addi $s0, $s1, -1
addi $t0, $sp, -44
sw $s0, ($t0)
addi $t0, $sp, -28
lw $s0, ($t0)
addi $t0, $sp, -4
lw $s1, ($t0)
addi $t0, $sp, -44
lw $s2, ($t0)
move $t0, $s2
sll $t0, $t0, 2
add $t0, $t0, $s1
lw $s0, ($t0)
addi $t0, $sp, -28
sw $s0, ($t0)
addi $t0, $sp, -28
lw $s1, ($t0)
move $s0, $s1
addi $t0, $sp, -20
sw $s0, ($t0)
addi $t0, $sp, -20
lw $s1, ($t0)
addi $t0, $sp, -36
lw $s2, ($t0)
sub $t0, $s1, $s2
bgt $t0, $0, quicksortloop2
addi $t0, $sp, -40
lw $s1, ($t0)
addi $t0, $sp, -44
lw $s2, ($t0)
sub $t0, $s1, $s2
bge $t0, $0, quicksortexit0
addi $t0, $sp, -16
lw $s0, ($t0)
addi $t0, $sp, -4
lw $s1, ($t0)
addi $t0, $sp, -44
lw $s2, ($t0)
move $t0, $s2
sll $t0, $t0, 2
add $t0, $t0, $s1
sw $s0, ($t0)
addi $t0, $sp, -16
sw $s0, ($t0)
addi $t0, $sp, -20
lw $s0, ($t0)
addi $t0, $sp, -4
lw $s1, ($t0)
addi $t0, $sp, -40
lw $s2, ($t0)
move $t0, $s2
sll $t0, $t0, 2
add $t0, $t0, $s1
sw $s0, ($t0)
addi $t0, $sp, -20
sw $s0, ($t0)
j quicksortloop0
quicksortexit0:
addi $t0, $sp, -44
lw $s1, ($t0)
addi $s0, $s1, 1
addi $t0, $sp, -24
sw $s0, ($t0)
addi $t0, $sp, -4
lw $s1, ($t0)
move $a0, $s1
addi $t0, $sp, -12
lw $s1, ($t0)
move $a1, $s1
addi $t0, $sp, -44
lw $s1, ($t0)
move $a2, $s1
addi $sp, $sp, -48
jal quicksort
addi $sp, $sp, 48
addi $t0, $sp, -44
lw $s1, ($t0)
addi $s0, $s1, 1
addi $t0, $sp, -44
sw $s0, ($t0)
addi $t0, $sp, -4
lw $s1, ($t0)
move $a0, $s1
addi $t0, $sp, -44
lw $s1, ($t0)
move $a1, $s1
addi $t0, $sp, -8
lw $s1, ($t0)
move $a2, $s1
addi $sp, $sp, -48
jal quicksort
addi $sp, $sp, 48
quicksortend:
addi $t0, $sp, 0
lw $ra, ($t0)
jr $ra
main:
addi $t0, $sp, 0
sw $ra, ($t0)
addi $v0, $0, 9
addi $a0, $0, 400
syscall
move $s1, $v0
addi $t0, $sp, -4
sw $s1, ($t0)
addi $s0, $0, 0
addi $t0, $sp, -8
sw $s0, ($t0)
addi $t0, $sp, -16
lw $s0, ($t0)
addi $v0, $0, 5
syscall
move $s0, $v0
addi $t0, $sp, -16
sw $s0, ($t0)
addi $t0, $sp, -16
lw $s1, ($t0)
addi $t0, $s1, -100
bgt $t0, $0, mainreturn
addi $t0, $sp, -16
lw $s1, ($t0)
addi $s0, $s1, -1
addi $t0, $sp, -16
sw $s0, ($t0)
addi $s0, $0, 0
addi $t0, $sp, -12
sw $s0, ($t0)
mainloop0:
addi $t0, $sp, -12
lw $s1, ($t0)
addi $t0, $sp, -16
lw $s2, ($t0)
sub $t0, $s1, $s2
bgt $t0, $0, mainexit0
addi $t0, $sp, -8
lw $s0, ($t0)
addi $v0, $0, 5
syscall
move $s0, $v0
addi $t0, $sp, -8
sw $s0, ($t0)
addi $t0, $sp, -8
lw $s0, ($t0)
addi $t0, $sp, -4
lw $s1, ($t0)
addi $t0, $sp, -12
lw $s2, ($t0)
move $t0, $s2
sll $t0, $t0, 2
add $t0, $t0, $s1
sw $s0, ($t0)
addi $t0, $sp, -8
sw $s0, ($t0)
addi $t0, $sp, -12
lw $s1, ($t0)
addi $s0, $s1, 1
addi $t0, $sp, -12
sw $s0, ($t0)
j mainloop0
mainexit0:
addi $t0, $sp, -4
lw $s1, ($t0)
move $a0, $s1
addi $a1, $0, 0
addi $t0, $sp, -16
lw $s1, ($t0)
move $a2, $s1
addi $sp, $sp, -20
jal quicksort
addi $sp, $sp, 20
addi $s0, $0, 0
addi $t0, $sp, -12
sw $s0, ($t0)
mainloop1:
addi $t0, $sp, -12
lw $s1, ($t0)
addi $t0, $sp, -16
lw $s2, ($t0)
sub $t0, $s1, $s2
bgt $t0, $0, mainexit1
addi $t0, $sp, -8
lw $s0, ($t0)
addi $t0, $sp, -4
lw $s1, ($t0)
addi $t0, $sp, -12
lw $s2, ($t0)
move $t0, $s2
sll $t0, $t0, 2
add $t0, $t0, $s1
lw $s0, ($t0)
addi $t0, $sp, -8
sw $s0, ($t0)
addi $t0, $sp, -8
lw $s1, ($t0)
addi $v0, $0, 1
move $a0, $s1
syscall
addi $v0, $0, 11
addi $a0, $0, 10
syscall
addi $t0, $sp, -12
lw $s1, ($t0)
addi $s0, $s1, 1
addi $t0, $sp, -12
sw $s0, ($t0)
j mainloop1
mainexit1:
mainreturn:
addi $v0, $0, 10
syscall
