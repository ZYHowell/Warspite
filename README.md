# Warspite

$\mathcal {Belli\ dura\ despicio}$

This is my solution for MS208 homework in ACM class, a compiler of Mx* language which is alike simplified C++ and Java. In testbench it performs very close to O2(except for a testcase requiring tail call optimization). 

This document contains two parts: details of the project and my bad experience of java&IDEA. 

The front end of the compiler uses ANTLR4. 

*java is a language that, at every place, shows how lazy its designer is and how much its user has to consider.*

#### Frontend Design

Commits of "I look to you". 

HIR is mainly an AST. In order to make semantic check, symbol table is also needed and I combine it with the scope. 

There are three kinds of type: primitive, class and function. They represent abstract "def" nodes. (defined but not used)

A naïve print optimization is made in HIR level. 

#### MIR

Commits of "12 days of Christmas". 

It is simply a LLVM IR. (and that's why this homework is not interesting: simply follow the LLVM is easy and sufficient)

My optim already contains: 

* Function inline(include force inline)
* ADCE with a precise side effect analysis(including a rough alias analysis)
* SCCP
* CFG simplification(only small part of it)
* bounded global CSE(unbounded version pressures register allocation)
* LICM based on Anderson alias
* Memory CSE based on Anderson alias
* strength reduction

I intend to realize: 

 * Algebraic simplification
 * scalar replacement of aggregate(maybe on HIR)
 * Tail call

 No partial redundant elimination, since it equals CSE+LICM but is hard to realize. 

#### Backend Design

Commits of "Young and beautiful"

Graph coloring. 

Implement naïve peephole optimization. 

#### Assembler

see another repo: Unicorn. 

#### References

1. SSA book, Lots of authors, http://ssabook.gforge.inria.fr/latest/book.pdf ;
2. Engineering a Compiler ed.2, Keith.D.Cooper & Linda Torczon;
3. Advanced Compiler Design and Implementation, Steven.S.Muchnick;
4. Witnessing Control Flow Graph Optimizations, Dario Casula;

**天灭Java**

2月16日，Java有时候传引用有时候传值的设定让人费解

3月4日，IDEA给没有加Nullable标记的参数赋值null报错

3月7日，IDEA不让我debug，一次debug结束后再次开始时显示无法连接到target VM

3月25日，IDEA提示下述代码有Null Pointer Exception，必须把while放前面

```
queue.offer(x);
do {y = queue.poll(); use y then} 
while (!queue.isEmpty());
```

3月29日，IDEA给markdown里的Java代码片段查错

4月23日，不特地打开时assert默认无效

4月24日，单线程代码，IDEA在run时产生null Pointer Exception，在debug时正常运行，跑出了多线程的既视感

4月28日，单线程代码，IDEA在windows下build，可以通过所有测试，在Linux下使用脚本build，某些测试的输出结果会RE或WA

4月30日，在Linux下，先new Print stream再将其传入时可能出现bug导致传入的Print stream并没能成功输出

5月25日，Java不支持typedef和unsigned

