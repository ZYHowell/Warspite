	.text
	.file	"builtIn.c"
	.globl	g_print                 # -- Begin function g_print
	.p2align	4, 0x90
	.type	g_print,@function
g_print:                                # @g_print
.Lg_print$local:
	.cfi_startproc
# %bb.0:                                # %entry
	movq	%rdi, %rsi
	movl	$.L.str, %edi
	xorl	%eax, %eax
	jmp	printf                  # TAILCALL
.Lfunc_end0:
	.size	g_print, .Lfunc_end0-g_print
	.cfi_endproc
                                        # -- End function
	.globl	g_println               # -- Begin function g_println
	.p2align	4, 0x90
	.type	g_println,@function
g_println:                              # @g_println
.Lg_println$local:
	.cfi_startproc
# %bb.0:                                # %entry
	jmp	puts                    # TAILCALL
.Lfunc_end1:
	.size	g_println, .Lfunc_end1-g_println
	.cfi_endproc
                                        # -- End function
	.globl	g_printInt              # -- Begin function g_printInt
	.p2align	4, 0x90
	.type	g_printInt,@function
g_printInt:                             # @g_printInt
.Lg_printInt$local:
	.cfi_startproc
# %bb.0:                                # %entry
	movl	%edi, %esi
	movl	$.L.str.2, %edi
	xorl	%eax, %eax
	jmp	printf                  # TAILCALL
.Lfunc_end2:
	.size	g_printInt, .Lfunc_end2-g_printInt
	.cfi_endproc
                                        # -- End function
	.globl	g_printlnInt            # -- Begin function g_printlnInt
	.p2align	4, 0x90
	.type	g_printlnInt,@function
g_printlnInt:                           # @g_printlnInt
.Lg_printlnInt$local:
	.cfi_startproc
# %bb.0:                                # %entry
	movl	%edi, %esi
	movl	$.L.str.3, %edi
	xorl	%eax, %eax
	jmp	printf                  # TAILCALL
.Lfunc_end3:
	.size	g_printlnInt, .Lfunc_end3-g_printlnInt
	.cfi_endproc
                                        # -- End function
	.globl	g_getString             # -- Begin function g_getString
	.p2align	4, 0x90
	.type	g_getString,@function
g_getString:                            # @g_getString
.Lg_getString$local:
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movl	$1000, %edi             # imm = 0x3E8
	callq	malloc
	movq	%rax, %rbx
	movl	$.L.str, %edi
	movq	%rax, %rsi
	xorl	%eax, %eax
	callq	__isoc99_scanf
	movq	%rbx, %rax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end4:
	.size	g_getString, .Lfunc_end4-g_getString
	.cfi_endproc
                                        # -- End function
	.globl	g_getInt                # -- Begin function g_getInt
	.p2align	4, 0x90
	.type	g_getInt,@function
g_getInt:                               # @g_getInt
.Lg_getInt$local:
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	leaq	4(%rsp), %rsi
	movl	$.L.str.2, %edi
	xorl	%eax, %eax
	callq	__isoc99_scanf
	movl	4(%rsp), %eax
	popq	%rcx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end5:
	.size	g_getInt, .Lfunc_end5-g_getInt
	.cfi_endproc
                                        # -- End function
	.section	.rodata.cst16,"aM",@progbits,16
	.p2align	4               # -- Begin function g_toString
.LCPI6_0:
	.short	255                     # 0xff
	.short	255                     # 0xff
	.short	255                     # 0xff
	.short	255                     # 0xff
	.short	255                     # 0xff
	.short	255                     # 0xff
	.short	255                     # 0xff
	.short	255                     # 0xff
.LCPI6_1:
	.byte	48                      # 0x30
	.byte	48                      # 0x30
	.byte	48                      # 0x30
	.byte	48                      # 0x30
	.byte	48                      # 0x30
	.byte	48                      # 0x30
	.byte	48                      # 0x30
	.byte	48                      # 0x30
	.zero	1
	.zero	1
	.zero	1
	.zero	1
	.zero	1
	.zero	1
	.zero	1
	.zero	1
	.text
	.globl	g_toString
	.p2align	4, 0x90
	.type	g_toString,@function
g_toString:                             # @g_toString
.Lg_toString$local:
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbp
	.cfi_def_cfa_offset 16
	pushq	%r15
	.cfi_def_cfa_offset 24
	pushq	%r14
	.cfi_def_cfa_offset 32
	pushq	%rbx
	.cfi_def_cfa_offset 40
	subq	$24, %rsp
	.cfi_def_cfa_offset 64
	.cfi_offset %rbx, -40
	.cfi_offset %r14, -32
	.cfi_offset %r15, -24
	.cfi_offset %rbp, -16
	testl	%edi, %edi
	je	.LBB6_4
# %bb.1:                                # %if.end
	movl	%edi, %r15d
	movl	%edi, %r14d
	shrl	$31, %r14d
	movl	%edi, %ebp
	negl	%ebp
	cmovll	%edi, %ebp
	testl	%ebp, %ebp
	je	.LBB6_5
# %bb.2:                                # %while.body.preheader
	xorl	%edx, %edx
	movl	$3435973837, %ecx       # imm = 0xCCCCCCCD
	.p2align	4, 0x90
.LBB6_3:                                # %while.body
                                        # =>This Inner Loop Header: Depth=1
	movl	%ebp, %edi
	imulq	%rcx, %rdi
	shrq	$35, %rdi
	leal	(%rdi,%rdi), %esi
	leal	(%rsi,%rsi,4), %esi
	movl	%ebp, %eax
	subl	%esi, %eax
	leal	1(%rdx), %esi
	movswq	%dx, %rdx
	movw	%ax, (%rsp,%rdx,2)
	movl	%esi, %edx
	cmpl	$9, %ebp
	movl	%edi, %ebp
	ja	.LBB6_3
	jmp	.LBB6_6
.LBB6_4:                                # %if.then
	movl	$2, %edi
	callq	malloc
	movw	$48, (%rax)
	jmp	.LBB6_23
.LBB6_5:
	xorl	%esi, %esi
.LBB6_6:                                # %while.end
	movswl	%si, %ebx
	leal	(%r14,%rbx), %eax
	addl	$1, %eax
	movslq	%eax, %rdi
	callq	malloc
	testl	%r15d, %r15d
	jns	.LBB6_8
# %bb.7:                                # %if.then17
	movb	$45, (%rax)
.LBB6_8:                                # %if.end19
	leal	(%r14,%rbx), %r8d
	testw	%bx, %bx
	jle	.LBB6_22
# %bb.9:                                # %while.body25.preheader
	movl	%r14d, %r10d
	movzwl	%bx, %r11d
	cmpw	$16, %bx
	jae	.LBB6_11
# %bb.10:
	xorl	%r9d, %r9d
.LBB6_16:
	xorl	%edx, %edx
.LBB6_17:                               # %while.body25.preheader92
	movq	%rdx, %rcx
	notq	%rcx
	testb	$1, %r11b
	je	.LBB6_19
# %bb.18:                               # %while.body25.prol
	movl	%r9d, %esi
	notl	%esi
	addl	%ebx, %esi
	movslq	%esi, %rsi
	movb	(%rsp,%rsi,2), %sil
	addb	$48, %sil
	movq	%rdx, %rdi
	orq	%r10, %rdi
	movb	%sil, (%rax,%rdi)
	orq	$1, %rdx
	addl	$1, %r9d
.LBB6_19:                               # %while.body25.prol.loopexit
	addq	%r11, %rcx
	je	.LBB6_22
# %bb.20:                               # %while.body25.preheader1
	subl	%r9d, %ebx
	addl	$-2, %ebx
	leaq	(%r10,%rax), %rdi
	addq	$1, %rdi
	.p2align	4, 0x90
.LBB6_21:                               # %while.body25
                                        # =>This Inner Loop Header: Depth=1
	leal	1(%rbx), %esi
	movslq	%esi, %rsi
	movzbl	(%rsp,%rsi,2), %ecx
	addb	$48, %cl
	movb	%cl, -1(%rdi,%rdx)
	movslq	%ebx, %rbx
	movzbl	(%rsp,%rbx,2), %ecx
	addb	$48, %cl
	movb	%cl, (%rdi,%rdx)
	addq	$2, %rdx
	addl	$-2, %ebx
	cmpq	%rdx, %r11
	jne	.LBB6_21
	jmp	.LBB6_22
.LBB6_11:                               # %vector.scevcheck
	leaq	-1(%r11), %rcx
	movl	%ecx, %edi
	notl	%edi
	addl	%ebx, %edi
	xorl	%r9d, %r9d
	cmpl	%ebx, %edi
	jge	.LBB6_16
# %bb.12:                               # %vector.scevcheck
	shrq	$32, %rcx
	movl	$0, %edx
	jne	.LBB6_17
# %bb.13:                               # %vector.ph
	movl	%r11d, %edx
	andl	$-16, %edx
	movl	%edx, %r9d
	leal	-1(%rbx), %ecx
	xorl	%esi, %esi
	movdqa	.LCPI6_0(%rip), %xmm0   # xmm0 = [255,255,255,255,255,255,255,255]
	movdqa	.LCPI6_1(%rip), %xmm1   # xmm1 = <48,48,48,48,48,48,48,48,u,u,u,u,u,u,u,u>
	.p2align	4, 0x90
.LBB6_14:                               # %vector.body
                                        # =>This Inner Loop Header: Depth=1
	movslq	%ecx, %rcx
	movdqu	-30(%rsp,%rcx,2), %xmm2
	movdqu	-14(%rsp,%rcx,2), %xmm3
	pand	%xmm0, %xmm3
	pshufd	$78, %xmm3, %xmm3       # xmm3 = xmm3[2,3,0,1]
	pshuflw	$27, %xmm3, %xmm3       # xmm3 = xmm3[3,2,1,0,4,5,6,7]
	pshufhw	$27, %xmm3, %xmm3       # xmm3 = xmm3[0,1,2,3,7,6,5,4]
	packuswb	%xmm0, %xmm3
	pand	%xmm0, %xmm2
	pshufd	$78, %xmm2, %xmm2       # xmm2 = xmm2[2,3,0,1]
	pshuflw	$27, %xmm2, %xmm2       # xmm2 = xmm2[3,2,1,0,4,5,6,7]
	pshufhw	$27, %xmm2, %xmm2       # xmm2 = xmm2[0,1,2,3,7,6,5,4]
	packuswb	%xmm0, %xmm2
	paddb	%xmm1, %xmm3
	paddb	%xmm1, %xmm2
	punpcklqdq	%xmm2, %xmm3    # xmm3 = xmm3[0],xmm2[0]
	movq	%rsi, %rbp
	orq	%r10, %rbp
	movdqu	%xmm3, (%rax,%rbp)
	addq	$16, %rsi
	addl	$-16, %ecx
	cmpq	%rsi, %rdx
	jne	.LBB6_14
# %bb.15:                               # %middle.block
	cmpq	%r11, %rdx
	jne	.LBB6_17
.LBB6_22:                               # %while.end41
	movslq	%r8d, %rcx
	movb	$0, (%rax,%rcx)
.LBB6_23:                               # %return
	addq	$24, %rsp
	.cfi_def_cfa_offset 40
	popq	%rbx
	.cfi_def_cfa_offset 32
	popq	%r14
	.cfi_def_cfa_offset 24
	popq	%r15
	.cfi_def_cfa_offset 16
	popq	%rbp
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end6:
	.size	g_toString, .Lfunc_end6-g_toString
	.cfi_endproc
                                        # -- End function
	.globl	l_string_length         # -- Begin function l_string_length
	.p2align	4, 0x90
	.type	l_string_length,@function
l_string_length:                        # @l_string_length
.Ll_string_length$local:
	.cfi_startproc
# %bb.0:                                # %entry
	jmp	strlen                  # TAILCALL
.Lfunc_end7:
	.size	l_string_length, .Lfunc_end7-l_string_length
	.cfi_endproc
                                        # -- End function
	.globl	l_string_substring      # -- Begin function l_string_substring
	.p2align	4, 0x90
	.type	l_string_substring,@function
l_string_substring:                     # @l_string_substring
.Ll_string_substring$local:
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbp
	.cfi_def_cfa_offset 16
	pushq	%r14
	.cfi_def_cfa_offset 24
	pushq	%rbx
	.cfi_def_cfa_offset 32
	.cfi_offset %rbx, -32
	.cfi_offset %r14, -24
	.cfi_offset %rbp, -16
	movl	%edx, %ebx
	movl	%esi, %ebp
	movq	%rdi, %r14
	movl	$1000, %edi             # imm = 0x3E8
	callq	malloc
	movslq	%ebp, %rcx
	leaq	(%r14,%rcx), %rsi
	subl	%ecx, %ebx
	movslq	%ebx, %rdx
	movq	%rax, %rdi
	popq	%rbx
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%rbp
	.cfi_def_cfa_offset 8
	jmp	memcpy                  # TAILCALL
.Lfunc_end8:
	.size	l_string_substring, .Lfunc_end8-l_string_substring
	.cfi_endproc
                                        # -- End function
	.globl	l_string_parseInt       # -- Begin function l_string_parseInt
	.p2align	4, 0x90
	.type	l_string_parseInt,@function
l_string_parseInt:                      # @l_string_parseInt
.Ll_string_parseInt$local:
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	leaq	4(%rsp), %rdx
	movl	$.L.str.2, %esi
	xorl	%eax, %eax
	callq	__isoc99_sscanf
	movl	4(%rsp), %eax
	popq	%rcx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end9:
	.size	l_string_parseInt, .Lfunc_end9-l_string_parseInt
	.cfi_endproc
                                        # -- End function
	.globl	l_string_ord            # -- Begin function l_string_ord
	.p2align	4, 0x90
	.type	l_string_ord,@function
l_string_ord:                           # @l_string_ord
.Ll_string_ord$local:
	.cfi_startproc
# %bb.0:                                # %entry
	movslq	%esi, %rax
	movsbl	(%rdi,%rax), %eax
	retq
.Lfunc_end10:
	.size	l_string_ord, .Lfunc_end10-l_string_ord
	.cfi_endproc
                                        # -- End function
	.globl	g_stringAdd             # -- Begin function g_stringAdd
	.p2align	4, 0x90
	.type	g_stringAdd,@function
g_stringAdd:                            # @g_stringAdd
.Lg_stringAdd$local:
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%r15
	.cfi_def_cfa_offset 16
	pushq	%r14
	.cfi_def_cfa_offset 24
	pushq	%r12
	.cfi_def_cfa_offset 32
	pushq	%rbx
	.cfi_def_cfa_offset 40
	pushq	%rax
	.cfi_def_cfa_offset 48
	.cfi_offset %rbx, -40
	.cfi_offset %r12, -32
	.cfi_offset %r14, -24
	.cfi_offset %r15, -16
	movq	%rsi, %r14
	movq	%rdi, %r15
	movl	$1000, %edi             # imm = 0x3E8
	callq	malloc
	movq	%rax, %rbx
	movq	%r15, %rdi
	callq	strlen
	movslq	%eax, %r12
	movq	%rbx, %rdi
	movq	%r15, %rsi
	movq	%r12, %rdx
	callq	memcpy
	movb	$0, (%rbx,%r12)
	movq	%rbx, %rdi
	movq	%r14, %rsi
	addq	$8, %rsp
	.cfi_def_cfa_offset 40
	popq	%rbx
	.cfi_def_cfa_offset 32
	popq	%r12
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%r15
	.cfi_def_cfa_offset 8
	jmp	strcat                  # TAILCALL
.Lfunc_end11:
	.size	g_stringAdd, .Lfunc_end11-g_stringAdd
	.cfi_endproc
                                        # -- End function
	.globl	g_stringLT              # -- Begin function g_stringLT
	.p2align	4, 0x90
	.type	g_stringLT,@function
g_stringLT:                             # @g_stringLT
.Lg_stringLT$local:
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	callq	strcmp
	shrl	$31, %eax
	popq	%rcx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end12:
	.size	g_stringLT, .Lfunc_end12-g_stringLT
	.cfi_endproc
                                        # -- End function
	.globl	g_stringGT              # -- Begin function g_stringGT
	.p2align	4, 0x90
	.type	g_stringGT,@function
g_stringGT:                             # @g_stringGT
.Lg_stringGT$local:
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	callq	strcmp
	xorl	%ecx, %ecx
	testl	%eax, %eax
	setg	%cl
	movl	%ecx, %eax
	popq	%rcx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end13:
	.size	g_stringGT, .Lfunc_end13-g_stringGT
	.cfi_endproc
                                        # -- End function
	.globl	g_stringLE              # -- Begin function g_stringLE
	.p2align	4, 0x90
	.type	g_stringLE,@function
g_stringLE:                             # @g_stringLE
.Lg_stringLE$local:
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	callq	strcmp
	xorl	%ecx, %ecx
	testl	%eax, %eax
	setle	%cl
	movl	%ecx, %eax
	popq	%rcx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end14:
	.size	g_stringLE, .Lfunc_end14-g_stringLE
	.cfi_endproc
                                        # -- End function
	.globl	g_stringGE              # -- Begin function g_stringGE
	.p2align	4, 0x90
	.type	g_stringGE,@function
g_stringGE:                             # @g_stringGE
.Lg_stringGE$local:
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	callq	strcmp
	notl	%eax
	shrl	$31, %eax
	popq	%rcx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end15:
	.size	g_stringGE, .Lfunc_end15-g_stringGE
	.cfi_endproc
                                        # -- End function
	.globl	g_stringEQ              # -- Begin function g_stringEQ
	.p2align	4, 0x90
	.type	g_stringEQ,@function
g_stringEQ:                             # @g_stringEQ
.Lg_stringEQ$local:
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	callq	strcmp
	xorl	%ecx, %ecx
	testl	%eax, %eax
	sete	%cl
	movl	%ecx, %eax
	popq	%rcx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end16:
	.size	g_stringEQ, .Lfunc_end16-g_stringEQ
	.cfi_endproc
                                        # -- End function
	.globl	g_stringNE              # -- Begin function g_stringNE
	.p2align	4, 0x90
	.type	g_stringNE,@function
g_stringNE:                             # @g_stringNE
.Lg_stringNE$local:
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	callq	strcmp
	xorl	%ecx, %ecx
	testl	%eax, %eax
	setne	%cl
	movl	%ecx, %eax
	popq	%rcx
	.cfi_def_cfa_offset 8
	retq
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
