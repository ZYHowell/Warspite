# Mx-Compiler
This is my solution for MS208 homework in ACM class, a compiler of Mx* language which is alike simplified C++ and Java. The front end of the compiler uses ANTLR4. 

### Front End Design & HIR

HIR is mainly an AST. 

There are three kinds of type: primitive, class and function. They represent abstract "def" nodes. (defined but not used)

Instance of primitive type should be recorded to help constants return. 

defines:

​	type: array, base;

​		array: {base, dim}

​		base: primitive(int, string, void, null), class, constructor return value(not void!!)

​			class: {name, function define, variables(local scope)}

​	function define: {name, return value type, variables(local scope)}

entity: variables; (maybe) function call;

​	variable: {name, type}

global symbol table: {type map; function map; global scope;}

local symbol table: {local scope}

scope: variable entities and some other information. 