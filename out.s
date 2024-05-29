.text
divisible:
    addi $sp, $sp, -8
    addi $sp, $sp, -4
    div $t0, $a0, $a1
    mul $t0, $t0, $a1
    bne $a0, $t0, label0_divisible
    addi $sp, $sp, 12
    jr $ra
    addi $sp, $sp, 12
    jr $ra
label0_divisible:
    addi $sp, $sp, 12
    jr $ra
    addi $sp, $sp, 12
    jr $ra
main:
    addi $sp, $sp, 0
print_main:
    addi $sp, $sp, 0
    jr $ra
