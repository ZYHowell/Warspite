# Mx-Compiler
This is my solution for MS208 homework in ACM class, a compiler of Mx* language which is alike simplified C++ and Java. 
This document contains two parts: details of the project and my bad experience of java&IDEA. 

The front end of the compiler uses ANTLR4. 

*java is a language that, at every place, shows how lazy its designer is and how much its user has to consider.*

### Front End Design & HIR

Commits of "I look to you". 

HIR is mainly an AST. In order to make semantic check, symbol table is also needed and I combine it with the scope. 

There are three kinds of type: primitive, class and function. They represent abstract "def" nodes. (defined but not used)

A naïve optimization is made in HIR level, that to split print(string + string) to two print commands. 

Suggested by senior students' reports, a DCE is made in HIR level. But I think its MIR version is already in ADCE, so maybe it is not co useful now? 

### MIR

Commits of "12 days of Christmas". 

It is simply a LLVM IR. (and that's why this homework is not interesting: simply follow the LLVM is easy and sufficient)

My optim already contains: 

* Function inline
* a precise side effect analysis and ADCE with it
* constant folding
* CFG simplification(part of it)

I intend to realize: 

 * Algebraic simplification
 * scalar replacement of aggregate(really? maybe on HIR)
 * value numbering
 * common subexpression elimination(I'm very interested in implementing this in EBB)
 * redundant elimination

### References

1. SSA book, Lots of authors, http://ssabook.gforge.inria.fr/latest/book.pdf ;

2. Engineering a Compiler ed.2, Keith.D.Cooper & Linda Torczon;

3. Advanced Compiler Design and Implementation, Steven.S.Muchnick

**天灭Java，Rust保平安**

2月16日，Java有时候传引用有时候传值的设定让人费解

3月4日，IDEA给没有加Nullable标记的参数赋值null报错

3月7日，IDEA不让我debug，一次debug结束后再次开始时显示无法连接到target VM