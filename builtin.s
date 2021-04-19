	.file	"builtin.c"
	.option nopic
	.attribute arch, "rv32i2p0_m2p0_a2p0_f2p0_d2p0_c2p0"
	.attribute unaligned_access, 0
	.attribute stack_align, 16
	.text
	.section	.rodata.str1.4,"aMS",@progbits,1
	.align	2
.LC0:
	.string	"%s"
	.text
	.align	1
	.globl	g_print
	.type	g_print, @function
g_print:
	mv	a1,a0
	lui	a0,%hi(.LC0)
	addi	a0,a0,%lo(.LC0)
	tail	printf
	.size	g_print, .-g_print
	.align	1
	.globl	g_println
	.type	g_println, @function
g_println:
	tail	puts
	.size	g_println, .-g_println
	.section	.rodata.str1.4
	.align	2
.LC1:
	.string	"%d"
	.text
	.align	1
	.globl	g_printInt
	.type	g_printInt, @function
g_printInt:
	mv	a1,a0
	lui	a0,%hi(.LC1)
	addi	a0,a0,%lo(.LC1)
	tail	printf
	.size	g_printInt, .-g_printInt
	.section	.rodata.str1.4
	.align	2
.LC2:
	.string	"%d\n"
	.text
	.align	1
	.globl	g_printlnInt
	.type	g_printlnInt, @function
g_printlnInt:
	mv	a1,a0
	lui	a0,%hi(.LC2)
	addi	a0,a0,%lo(.LC2)
	tail	printf
	.size	g_printlnInt, .-g_printlnInt
	.align	1
	.globl	g_getString
	.type	g_getString, @function
g_getString:
	addi	sp,sp,-16
	sw	s2,0(sp)
	lui	a0,%hi(.LC0)
	lui	s2,%hi(input)
	addi	a1,s2,%lo(input)
	addi	a0,a0,%lo(.LC0)
	sw	ra,12(sp)
	sw	s0,8(sp)
	sw	s1,4(sp)
	call	scanf
	addi	a0,s2,%lo(input)
	call	strlen
	addi	s0,a0,1
	mv	a0,s0
	call	malloc
	mv	a2,s0
	addi	a1,s2,%lo(input)
	mv	s1,a0
	call	memcpy
	lw	ra,12(sp)
	lw	s0,8(sp)
	lw	s2,0(sp)
	mv	a0,s1
	lw	s1,4(sp)
	addi	sp,sp,16
	jr	ra
	.size	g_getString, .-g_getString
	.align	1
	.globl	g_getInt
	.type	g_getInt, @function
g_getInt:
	addi	sp,sp,-32
	lui	a0,%hi(.LC1)
	addi	a1,sp,12
	addi	a0,a0,%lo(.LC1)
	sw	ra,28(sp)
	call	scanf
	lw	ra,28(sp)
	lw	a0,12(sp)
	addi	sp,sp,32
	jr	ra
	.size	g_getInt, .-g_getInt
	.align	1
	.globl	g_toString
	.type	g_toString, @function
g_toString:
	addi	sp,sp,-64
	sw	ra,60(sp)
	sw	s0,56(sp)
	sw	s1,52(sp)
	sw	s2,48(sp)
	sw	s3,44(sp)
	beq	a0,zero,.L20
	srai	a3,a0,31
	xor	a5,a3,a0
	sub	a5,a5,a3
	mv	s1,a0
	srli	s3,a0,31
	li	s0,0
	li	a3,10
.L13:
	rem	a2,a5,a3
	slli	a4,s0,1
	addi	a1,sp,32
	add	a4,a1,a4
	addi	s0,s0,1
	slli	s0,s0,16
	srai	s0,s0,16
	div	a5,a5,a3
	sh	a2,-20(a4)
	bne	a5,zero,.L13
	add	s2,s3,s0
	addi	a0,s2,1
	call	malloc
	blt	s1,zero,.L21
.L14:
	ble	s0,zero,.L17
	addi	a5,s0,-1
	addi	a4,sp,32
	slli	a5,a5,1
	add	a5,a4,a5
	lbu	a5,-20(a5)
	add	a4,a0,s3
	li	a3,1
	addi	a5,a5,48
	sb	a5,0(a4)
	beq	s0,a3,.L17
	addi	a5,s0,-2
	addi	a3,sp,32
	slli	a5,a5,1
	add	a5,a3,a5
	lbu	a5,-20(a5)
	li	a3,2
	addi	a5,a5,48
	sb	a5,1(a4)
	beq	s0,a3,.L17
	addi	a5,s0,-3
	addi	a3,sp,32
	slli	a5,a5,1
	add	a5,a3,a5
	lbu	a5,-20(a5)
	li	a3,3
	addi	a5,a5,48
	sb	a5,2(a4)
	beq	s0,a3,.L17
	addi	a5,s0,-4
	addi	a3,sp,32
	slli	a5,a5,1
	add	a5,a3,a5
	lbu	a5,-20(a5)
	li	a3,4
	addi	a5,a5,48
	sb	a5,3(a4)
	beq	s0,a3,.L17
	addi	a5,s0,-5
	addi	a3,sp,32
	slli	a5,a5,1
	add	a5,a3,a5
	lbu	a5,-20(a5)
	li	a3,5
	addi	a5,a5,48
	sb	a5,4(a4)
	beq	s0,a3,.L17
	addi	a5,s0,-6
	addi	a3,sp,32
	slli	a5,a5,1
	add	a5,a3,a5
	lbu	a5,-20(a5)
	li	a3,6
	addi	a5,a5,48
	sb	a5,5(a4)
	beq	s0,a3,.L17
	addi	a5,s0,-7
	addi	a3,sp,32
	slli	a5,a5,1
	add	a5,a3,a5
	lbu	a5,-20(a5)
	li	a3,7
	addi	a5,a5,48
	sb	a5,6(a4)
	beq	s0,a3,.L17
	addi	a5,s0,-8
	addi	a3,sp,32
	slli	a5,a5,1
	add	a5,a3,a5
	lbu	a5,-20(a5)
	li	a3,8
	addi	a5,a5,48
	sb	a5,7(a4)
	beq	s0,a3,.L17
	addi	a5,s0,-9
	addi	a3,sp,32
	slli	a5,a5,1
	add	a5,a3,a5
	lbu	a5,-20(a5)
	li	a3,9
	addi	a5,a5,48
	sb	a5,8(a4)
	beq	s0,a3,.L17
	addi	a5,s0,-10
	slli	a5,a5,1
	addi	a3,sp,32
	add	a5,a3,a5
	lbu	a5,-20(a5)
	addi	a5,a5,48
	sb	a5,9(a4)
.L17:
	add	s2,a0,s2
	sb	zero,0(s2)
	lw	ra,60(sp)
	lw	s0,56(sp)
	lw	s1,52(sp)
	lw	s2,48(sp)
	lw	s3,44(sp)
	addi	sp,sp,64
	jr	ra
.L21:
	li	a5,45
	sb	a5,0(a0)
	j	.L14
.L20:
	li	a0,2
	call	malloc
	li	a5,48
	sh	a5,0(a0)
	lw	ra,60(sp)
	lw	s0,56(sp)
	lw	s1,52(sp)
	lw	s2,48(sp)
	lw	s3,44(sp)
	addi	sp,sp,64
	jr	ra
	.size	g_toString, .-g_toString
	.align	1
	.globl	l_string_length
	.type	l_string_length, @function
l_string_length:
	tail	strlen
	.size	l_string_length, .-l_string_length
	.align	1
	.globl	l_string_substring
	.type	l_string_substring, @function
l_string_substring:
	addi	sp,sp,-16
	sw	s1,4(sp)
	sub	s1,a2,a1
	sw	s2,0(sp)
	mv	s2,a0
	addi	a0,s1,1
	sw	s0,8(sp)
	sw	ra,12(sp)
	mv	s0,a1
	call	malloc
	add	a1,s2,s0
	lw	s0,8(sp)
	lw	ra,12(sp)
	lw	s2,0(sp)
	mv	a2,s1
	lw	s1,4(sp)
	addi	sp,sp,16
	tail	memcpy
	.size	l_string_substring, .-l_string_substring
	.align	1
	.globl	l_string_parseInt
	.type	l_string_parseInt, @function
l_string_parseInt:
	addi	sp,sp,-32
	lui	a1,%hi(.LC1)
	addi	a2,sp,12
	addi	a1,a1,%lo(.LC1)
	sw	ra,28(sp)
	call	sscanf
	lw	ra,28(sp)
	lw	a0,12(sp)
	addi	sp,sp,32
	jr	ra
	.size	l_string_parseInt, .-l_string_parseInt
	.align	1
	.globl	l_string_ord
	.type	l_string_ord, @function
l_string_ord:
	add	a0,a0,a1
	lbu	a0,0(a0)
	ret
	.size	l_string_ord, .-l_string_ord
	.align	1
	.globl	g_stringAdd
	.type	g_stringAdd, @function
g_stringAdd:
	addi	sp,sp,-16
	sw	ra,12(sp)
	sw	s0,8(sp)
	sw	s1,4(sp)
	sw	s2,0(sp)
	mv	s1,a0
	mv	s2,a1
	call	strlen
	mv	s0,a0
	mv	a0,s2
	call	strlen
	add	a0,s0,a0
	call	malloc
	mv	a2,s0
	mv	a1,s1
	mv	s1,a0
	add	s0,s1,s0
	call	memcpy
	mv	a1,s2
	mv	a0,s1
	sb	zero,0(s0)
	call	strcat
	lw	ra,12(sp)
	lw	s0,8(sp)
	lw	s2,0(sp)
	mv	a0,s1
	lw	s1,4(sp)
	addi	sp,sp,16
	jr	ra
	.size	g_stringAdd, .-g_stringAdd
	.align	1
	.globl	g_stringLT
	.type	g_stringLT, @function
g_stringLT:
	addi	sp,sp,-16
	sw	ra,12(sp)
	call	strcmp
	lw	ra,12(sp)
	srli	a0,a0,31
	addi	sp,sp,16
	jr	ra
	.size	g_stringLT, .-g_stringLT
	.align	1
	.globl	g_stringGT
	.type	g_stringGT, @function
g_stringGT:
	addi	sp,sp,-16
	sw	ra,12(sp)
	call	strcmp
	lw	ra,12(sp)
	sgt	a0,a0,zero
	addi	sp,sp,16
	jr	ra
	.size	g_stringGT, .-g_stringGT
	.align	1
	.globl	g_stringLE
	.type	g_stringLE, @function
g_stringLE:
	addi	sp,sp,-16
	sw	ra,12(sp)
	call	strcmp
	lw	ra,12(sp)
	slti	a0,a0,1
	addi	sp,sp,16
	jr	ra
	.size	g_stringLE, .-g_stringLE
	.align	1
	.globl	g_stringGE
	.type	g_stringGE, @function
g_stringGE:
	addi	sp,sp,-16
	sw	ra,12(sp)
	call	strcmp
	lw	ra,12(sp)
	not	a0,a0
	srli	a0,a0,31
	addi	sp,sp,16
	jr	ra
	.size	g_stringGE, .-g_stringGE
	.align	1
	.globl	g_stringEQ
	.type	g_stringEQ, @function
g_stringEQ:
	addi	sp,sp,-16
	sw	ra,12(sp)
	call	strcmp
	lw	ra,12(sp)
	seqz	a0,a0
	addi	sp,sp,16
	jr	ra
	.size	g_stringEQ, .-g_stringEQ
	.align	1
	.globl	g_stringNE
	.type	g_stringNE, @function
g_stringNE:
	addi	sp,sp,-16
	sw	ra,12(sp)
	call	strcmp
	lw	ra,12(sp)
	snez	a0,a0
	addi	sp,sp,16
	jr	ra
	.size	g_stringNE, .-g_stringNE
	.comm	input,1000,4
	.ident	"GCC: (GNU) 9.2.0"
