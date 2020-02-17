# Mx-Compiler
This is my solution for MS208 homework in ACM class, a compiler of Mx* language which is alike simplified C++ and Java. The front end of the compiler uses ANTLR4. 

*a huge but not hard work, thanks to seniors.*

### Front End Design & HIR

HIR is mainly an AST. In order to make semantic check, symbol table is also needed and I combine it with the scope. 

There are three kinds of type: primitive, class and function. They represent abstract "def" nodes. (defined but not used)

A naïve optimization is made in HIR level, that to split print(string + string) to two print commands. 

Suggested by senior students' reports, a DCE is made in HIR level. 