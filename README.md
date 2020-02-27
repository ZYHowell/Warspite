# Mx-Compiler
This is my solution for MS208 homework in ACM class, a compiler of Mx* language which is alike simplified C++ and Java. The front end of the compiler uses ANTLR4. 

*java is a language that, at every place, shows how lazy its designer is and how much its user has to consider.*

### Front End Design & HIR

HIR is mainly an AST. In order to make semantic check, symbol table is also needed and I combine it with the scope. 

There are three kinds of type: primitive, class and function. They represent abstract "def" nodes. (defined but not used)

A naïve optimization is made in HIR level, that to split print(string + string) to two print commands. 

Suggested by senior students' reports, a DCE is made in HIR level. 

### MIR

It is simply a LLVM IR. (and that's why this homework is not interesting: simply follow the LLVM is easy and sufficient)

### References

1. SSA book, Lots of authous, http://ssabook.gforge.inria.fr/latest/book.pdf ;

2. Engineering a Compiler ed.2, Keith.D.Cooper & Linda Torczon;

3. Advanced Compiler Design and Implementation, Steven.S.Muchnick

**天灭Java，Rust保平安**