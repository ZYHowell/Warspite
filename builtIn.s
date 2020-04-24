	.text
	.attribute	4, 16
	.attribute	5, "rv32i2p0_m2p0"
	.file	"builtIn.c"
	.globl	g_print                 # -- Begin function g_print
	.p2align	2
	.type	g_print,@function
g_print:                                # @g_print
.Lg_print$local:
	.cfi_startproc
# %bb.0:                                # %entry
	lui	a1, %hi(.L.str)
	addi	a1, a1, %lo(.L.str)
	mv	a2, a0
	mv	a0, a1
	mv	a1, a2
	tail	printf
.Lfunc_end0:
	.size	g_print, .Lfunc_end0-g_print
	.cfi_endproc
                                        # -- End function
	.globl	g_println               # -- Begin function g_println
	.p2align	2
	.type	g_println,@function
g_println:                              # @g_println
.Lg_println$local:
	.cfi_startproc
# %bb.0:                                # %entry
	tail	puts
.Lfunc_end1:
	.size	g_println, .Lfunc_end1-g_println
	.cfi_endproc
                                        # -- End function
	.globl	g_printInt              # -- Begin function g_printInt
	.p2align	2
	.type	g_printInt,@function
g_printInt:                             # @g_printInt
.Lg_printInt$local:
	.cfi_startproc
# %bb.0:                                # %entry
	lui	a1, %hi(.L.str.2)
	addi	a1, a1, %lo(.L.str.2)
	mv	a2, a0
	mv	a0, a1
	mv	a1, a2
	tail	printf
.Lfunc_end2:
	.size	g_printInt, .Lfunc_end2-g_printInt
	.cfi_endproc
                                        # -- End function
	.globl	g_printlnInt            # -- Begin function g_printlnInt
	.p2align	2
	.type	g_printlnInt,@function
g_printlnInt:                           # @g_printlnInt
.Lg_printlnInt$local:
	.cfi_startproc
# %bb.0:                                # %entry
	lui	a1, %hi(.L.str.3)
	addi	a1, a1, %lo(.L.str.3)
	mv	a2, a0
	mv	a0, a1
	mv	a1, a2
	tail	printf
.Lfunc_end3:
	.size	g_printlnInt, .Lfunc_end3-g_printlnInt
	.cfi_endproc
                                        # -- End function
	.globl	g_getString             # -- Begin function g_getString
	.p2align	2
	.type	g_getString,@function
g_getString:                            # @g_getString
.Lg_getString$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	sw	s0, 8(sp)
	.cfi_offset ra, -4
	.cfi_offset s0, -8
	addi	a0, zero, 1000
	mv	a1, zero
	call	malloc
	mv	s0, a0
	lui	a0, %hi(.L.str)
	addi	a0, a0, %lo(.L.str)
	mv	a1, s0
	call	__isoc99_scanf
	mv	a0, s0
	lw	s0, 8(sp)
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end4:
	.size	g_getString, .Lfunc_end4-g_getString
	.cfi_endproc
                                        # -- End function
	.globl	g_getInt                # -- Begin function g_getInt
	.p2align	2
	.type	g_getInt,@function
g_getInt:                               # @g_getInt
.Lg_getInt$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	lui	a0, %hi(.L.str.2)
	addi	a0, a0, %lo(.L.str.2)
	addi	a1, sp, 8
	call	__isoc99_scanf
	lw	a0, 8(sp)
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end5:
	.size	g_getInt, .Lfunc_end5-g_getInt
	.cfi_endproc
                                        # -- End function
	.globl	g_toString              # -- Begin function g_toString
	.p2align	2
	.type	g_toString,@function
g_toString:                             # @g_toString
.Lg_toString$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -96
	.cfi_def_cfa_offset 96
	sw	ra, 92(sp)
	sw	s0, 88(sp)
	sw	s1, 84(sp)
	sw	s2, 80(sp)
	sw	s3, 76(sp)
	sw	s4, 72(sp)
	sw	s5, 68(sp)
	sw	s6, 64(sp)
	sw	s7, 60(sp)
	sw	s8, 56(sp)
	sw	s9, 52(sp)
	sw	s10, 48(sp)
	sw	s11, 44(sp)
	.cfi_offset ra, -4
	.cfi_offset s0, -8
	.cfi_offset s1, -12
	.cfi_offset s2, -16
	.cfi_offset s3, -20
	.cfi_offset s4, -24
	.cfi_offset s5, -28
	.cfi_offset s6, -32
	.cfi_offset s7, -36
	.cfi_offset s8, -40
	.cfi_offset s9, -44
	.cfi_offset s10, -48
	.cfi_offset s11, -52
	beqz	a0, .LBB6_4
# %bb.1:                                # %if.end
	mv	s5, a0
	srai	a0, a0, 31
	add	a1, s5, a0
	xor	a0, a1, a0
	srli	s3, s5, 31
	beqz	a0, .LBB6_5
# %bb.2:                                # %while.body.preheader
	mv	a5, zero
	addi	a6, sp, 16
	lui	a2, 838861
	addi	a2, a2, -819
	addi	a3, zero, 10
	addi	a4, zero, 9
.LBB6_3:                                # %while.body
                                        # =>This Inner Loop Header: Depth=1
	mv	s0, a0
	addi	s1, a5, 1
	slli	a0, a5, 16
	srai	a0, a0, 16
	slli	a0, a0, 1
	add	a5, a6, a0
	mulhu	a0, s0, a2
	srli	a0, a0, 3
	mul	a1, a0, a3
	sub	a1, s0, a1
	sh	a1, 0(a5)
	mv	a5, s1
	bltu	a4, s0, .LBB6_3
	j	.LBB6_6
.LBB6_4:                                # %if.then
	addi	a0, zero, 2
	mv	a1, zero
	call	malloc
	addi	a1, zero, 48
	sb	a1, 0(a0)
	sb	zero, 1(a0)
	j	.LBB6_24
.LBB6_5:
	mv	s1, zero
.LBB6_6:                                # %while.end
	slli	a0, s1, 16
	srai	s4, a0, 16
	add	s2, s3, s4
	addi	a0, s2, 1
	srai	a1, a0, 31
	call	malloc
	addi	a1, zero, -1
	blt	a1, s5, .LBB6_8
# %bb.7:                                # %if.then17
	addi	a1, zero, 45
	sb	a1, 0(a0)
.LBB6_8:                                # %if.end19
	addi	a1, zero, 1
	blt	s4, a1, .LBB6_23
# %bb.9:                                # %while.body25.preheader
	lui	a1, 16
	addi	a1, a1, -1
	and	a1, s1, a1
	addi	a2, zero, 16
	bgeu	a1, a2, .LBB6_11
# %bb.10:
	mv	a7, zero
	mv	a1, zero
	j	.LBB6_17
.LBB6_11:                               # %vector.scevcheck
	sub	a3, s4, a1
	mv	a7, zero
	bge	a3, s4, .LBB6_16
# %bb.12:                               # %vector.scevcheck
	addi	a3, a1, -1
	sltu	a1, a3, a1
	addi	a3, a1, -1
	mv	a1, a7
	bnez	a3, .LBB6_17
# %bb.13:                               # %vector.ph
	sw	s2, 12(sp)
	mv	a1, zero
	lui	a2, 16
	addi	a2, a2, -16
	and	a7, s1, a2
	addi	a6, sp, 16
	mv	a5, zero
	mv	a4, zero
.LBB6_14:                               # %vector.body
                                        # =>This Inner Loop Header: Depth=1
	not	s0, a5
	add	s0, s0, s4
	slli	s0, s0, 1
	add	s0, a6, s0
	lh	t0, -14(s0)
	lh	t1, -12(s0)
	lh	t2, -10(s0)
	lh	t3, -8(s0)
	lh	t4, -6(s0)
	lh	t5, -4(s0)
	lh	a3, -2(s0)
	lh	a2, 0(s0)
	lh	t6, -30(s0)
	lh	s5, -28(s0)
	lh	s6, -26(s0)
	lh	s7, -24(s0)
	lh	s8, -22(s0)
	lh	s9, -20(s0)
	lh	s10, -18(s0)
	lh	s0, -16(s0)
	addi	s11, a2, 48
	addi	ra, a3, 48
	addi	t5, t5, 48
	addi	t4, t4, 48
	addi	t3, t3, 48
	addi	t2, t2, 48
	addi	s2, t1, 48
	addi	a3, t0, 48
	addi	t0, s0, 48
	addi	t1, s10, 48
	addi	s9, s9, 48
	addi	s8, s8, 48
	addi	s7, s7, 48
	addi	s6, s6, 48
	addi	s5, s5, 48
	addi	s0, t6, 48
	or	a2, a5, s3
	add	a2, a0, a2
	sb	a3, 7(a2)
	sb	s2, 6(a2)
	sb	t2, 5(a2)
	sb	t3, 4(a2)
	sb	t4, 3(a2)
	sb	t5, 2(a2)
	sb	ra, 1(a2)
	sb	s11, 0(a2)
	sb	s0, 15(a2)
	sb	s5, 14(a2)
	sb	s6, 13(a2)
	sb	s7, 12(a2)
	sb	s8, 11(a2)
	sb	s9, 10(a2)
	sb	t1, 9(a2)
	addi	a3, a5, 16
	sltu	a5, a3, a5
	add	a4, a4, a5
	xor	a5, a3, a7
	or	s0, a5, a4
	sb	t0, 8(a2)
	mv	a5, a3
	bnez	s0, .LBB6_14
# %bb.15:                               # %middle.block
	lui	a2, 16
	addi	a2, a2, -1
	and	a2, s1, a2
	xor	a2, a7, a2
	lw	s2, 12(sp)
	bnez	a2, .LBB6_17
	j	.LBB6_23
.LBB6_16:
	mv	a1, a7
.LBB6_17:                               # %while.body25.preheader92
	not	a5, a7
	andi	a2, s1, 1
	not	s0, a1
	bnez	a2, .LBB6_19
# %bb.18:
	mv	a3, a7
	j	.LBB6_20
.LBB6_19:                               # %while.body25.prol
	not	a2, a7
	add	a2, a2, s4
	slli	a2, a2, 1
	addi	a3, sp, 16
	add	a2, a3, a2
	lb	a2, 0(a2)
	addi	a2, a2, 48
	or	a3, a7, s3
	add	a3, a0, a3
	sb	a2, 0(a3)
	ori	a3, a7, 1
	addi	a7, a7, 1
.LBB6_20:                               # %while.body25.prol.loopexit
	lui	a2, 16
	addi	a2, a2, -1
	and	a4, s1, a2
	snez	a2, a4
	neg	a2, a2
	neg	s1, a4
	xor	a5, a5, s1
	xor	a2, s0, a2
	or	a2, a5, a2
	beqz	a2, .LBB6_23
# %bb.21:                               # %while.body25.preheader1
	sub	a2, s4, a7
	addi	a2, a2, -2
	addi	a6, sp, 16
.LBB6_22:                               # %while.body25
                                        # =>This Inner Loop Header: Depth=1
	slli	s1, a2, 1
	add	s1, a6, s1
	lb	s0, 2(s1)
	addi	s0, s0, 48
	lb	s1, 0(s1)
	add	a5, a3, s3
	add	a5, a0, a5
	sb	s0, 0(a5)
	addi	s1, s1, 48
	sb	s1, 1(a5)
	addi	a5, a3, 2
	sltu	a3, a5, a3
	add	a1, a1, a3
	xor	a3, a5, a4
	or	s1, a3, a1
	addi	a2, a2, -2
	mv	a3, a5
	bnez	s1, .LBB6_22
.LBB6_23:                               # %while.end41
	add	a1, a0, s2
	sb	zero, 0(a1)
.LBB6_24:                               # %return
	lw	s11, 44(sp)
	lw	s10, 48(sp)
	lw	s9, 52(sp)
	lw	s8, 56(sp)
	lw	s7, 60(sp)
	lw	s6, 64(sp)
	lw	s5, 68(sp)
	lw	s4, 72(sp)
	lw	s3, 76(sp)
	lw	s2, 80(sp)
	lw	s1, 84(sp)
	lw	s0, 88(sp)
	lw	ra, 92(sp)
	addi	sp, sp, 96
	ret
.Lfunc_end6:
	.size	g_toString, .Lfunc_end6-g_toString
	.cfi_endproc
                                        # -- End function
	.globl	l_string_length         # -- Begin function l_string_length
	.p2align	2
	.type	l_string_length,@function
l_string_length:                        # @l_string_length
.Ll_string_length$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strlen
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end7:
	.size	l_string_length, .Lfunc_end7-l_string_length
	.cfi_endproc
                                        # -- End function
	.globl	l_string_substring      # -- Begin function l_string_substring
	.p2align	2
	.type	l_string_substring,@function
l_string_substring:                     # @l_string_substring
.Ll_string_substring$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	sw	s0, 8(sp)
	sw	s1, 4(sp)
	sw	s2, 0(sp)
	.cfi_offset ra, -4
	.cfi_offset s0, -8
	.cfi_offset s1, -12
	.cfi_offset s2, -16
	mv	s2, a2
	mv	s1, a1
	mv	s0, a0
	addi	a0, zero, 1000
	mv	a1, zero
	call	malloc
	add	a1, s0, s1
	sub	a2, s2, s1
	lw	s2, 0(sp)
	lw	s1, 4(sp)
	lw	s0, 8(sp)
	lw	ra, 12(sp)
	addi	sp, sp, 16
	tail	memcpy
.Lfunc_end8:
	.size	l_string_substring, .Lfunc_end8-l_string_substring
	.cfi_endproc
                                        # -- End function
	.globl	l_string_parseInt       # -- Begin function l_string_parseInt
	.p2align	2
	.type	l_string_parseInt,@function
l_string_parseInt:                      # @l_string_parseInt
.Ll_string_parseInt$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	lui	a1, %hi(.L.str.2)
	addi	a1, a1, %lo(.L.str.2)
	addi	a2, sp, 8
	call	__isoc99_sscanf
	lw	a0, 8(sp)
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end9:
	.size	l_string_parseInt, .Lfunc_end9-l_string_parseInt
	.cfi_endproc
                                        # -- End function
	.globl	l_string_ord            # -- Begin function l_string_ord
	.p2align	2
	.type	l_string_ord,@function
l_string_ord:                           # @l_string_ord
.Ll_string_ord$local:
	.cfi_startproc
# %bb.0:                                # %entry
	add	a0, a0, a1
	lb	a0, 0(a0)
	ret
.Lfunc_end10:
	.size	l_string_ord, .Lfunc_end10-l_string_ord
	.cfi_endproc
                                        # -- End function
	.globl	g_stringAdd             # -- Begin function g_stringAdd
	.p2align	2
	.type	g_stringAdd,@function
g_stringAdd:                            # @g_stringAdd
.Lg_stringAdd$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -32
	.cfi_def_cfa_offset 32
	sw	ra, 28(sp)
	sw	s0, 24(sp)
	sw	s1, 20(sp)
	sw	s2, 16(sp)
	sw	s3, 12(sp)
	.cfi_offset ra, -4
	.cfi_offset s0, -8
	.cfi_offset s1, -12
	.cfi_offset s2, -16
	.cfi_offset s3, -20
	mv	s2, a1
	mv	s3, a0
	addi	a0, zero, 1000
	mv	a1, zero
	call	malloc
	mv	s0, a0
	mv	a0, s3
	call	strlen
	mv	s1, a0
	mv	a0, s0
	mv	a1, s3
	mv	a2, s1
	call	memcpy
	add	a0, s0, s1
	sb	zero, 0(a0)
	mv	a0, s0
	mv	a1, s2
	lw	s3, 12(sp)
	lw	s2, 16(sp)
	lw	s1, 20(sp)
	lw	s0, 24(sp)
	lw	ra, 28(sp)
	addi	sp, sp, 32
	tail	strcat
.Lfunc_end11:
	.size	g_stringAdd, .Lfunc_end11-g_stringAdd
	.cfi_endproc
                                        # -- End function
	.globl	g_stringLT              # -- Begin function g_stringLT
	.p2align	2
	.type	g_stringLT,@function
g_stringLT:                             # @g_stringLT
.Lg_stringLT$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strcmp
	srli	a0, a0, 31
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end12:
	.size	g_stringLT, .Lfunc_end12-g_stringLT
	.cfi_endproc
                                        # -- End function
	.globl	g_stringGT              # -- Begin function g_stringGT
	.p2align	2
	.type	g_stringGT,@function
g_stringGT:                             # @g_stringGT
.Lg_stringGT$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strcmp
	sgtz	a0, a0
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end13:
	.size	g_stringGT, .Lfunc_end13-g_stringGT
	.cfi_endproc
                                        # -- End function
	.globl	g_stringLE              # -- Begin function g_stringLE
	.p2align	2
	.type	g_stringLE,@function
g_stringLE:                             # @g_stringLE
.Lg_stringLE$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strcmp
	slti	a0, a0, 1
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end14:
	.size	g_stringLE, .Lfunc_end14-g_stringLE
	.cfi_endproc
                                        # -- End function
	.globl	g_stringGE              # -- Begin function g_stringGE
	.p2align	2
	.type	g_stringGE,@function
g_stringGE:                             # @g_stringGE
.Lg_stringGE$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strcmp
	not	a0, a0
	srli	a0, a0, 31
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end15:
	.size	g_stringGE, .Lfunc_end15-g_stringGE
	.cfi_endproc
                                        # -- End function
	.globl	g_stringEQ              # -- Begin function g_stringEQ
	.p2align	2
	.type	g_stringEQ,@function
g_stringEQ:                             # @g_stringEQ
.Lg_stringEQ$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strcmp
	seqz	a0, a0
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end16:
	.size	g_stringEQ, .Lfunc_end16-g_stringEQ
	.cfi_endproc
                                        # -- End function
	.globl	g_stringNE              # -- Begin function g_stringNE
	.p2align	2
	.type	g_stringNE,@function
g_stringNE:                             # @g_stringNE
.Lg_stringNE$local:
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strcmp
	snez	a0, a0
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end17:
	.size	g_stringNE, .Lfunc_end17-g_stringNE
	.cfi_endproc
                                        # -- End function
	.type	.L.str,@object          # @.str
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str:
	.asciz	"%s"
	.size	.L.str, 3

	.type	.L.str.2,@object        # @.str.2
.L.str.2:
	.asciz	"%d"
	.size	.L.str.2, 3

	.type	.L.str.3,@object        # @.str.3
.L.str.3:
	.asciz	"%d\n"
	.size	.L.str.3, 4

	.ident	"clang version 11.0.0 (https://github.com/llvm/llvm-project.git 688ac00bb225d72a53ad3c77471363183a4f8b06)"
	.section	".note.GNU-stack","",@progbits
