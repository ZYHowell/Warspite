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
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	sw	s0, 8(sp)
	sw	s1, 4(sp)
	.cfi_offset ra, -4
	.cfi_offset s0, -8
	.cfi_offset s1, -12
	mv	s0, a0
	addi	a0, zero, 1000
	mv	a1, zero
	call	malloc
	mv	s1, a0
	lui	a0, %hi(.L.str.2)
	addi	a1, a0, %lo(.L.str.2)
	mv	a0, s1
	mv	a2, s0
	call	sprintf
	mv	a0, s1
	lw	s1, 4(sp)
	lw	s0, 8(sp)
	lw	ra, 12(sp)
	addi	sp, sp, 16
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
