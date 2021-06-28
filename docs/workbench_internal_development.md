## 4. Internal Development

### 4.1 Workbench Style Guide

[comment]: # (TODO: write introduction)

#### 4.1.1 Java

Java code style for Workbench is based primarily on the [Code Conventions for the Java™ Programming Language](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html) (last revised 20 April 1999) from Sun/Oracle. The most notable exception is that Workbench uses a 100-column line width rather than 80-columns to prevent excessive line wrapping.

Javadoc style for Workbench follows the [Javadoc guidelines](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html) from Oracle.

Below is a summary of the Java style guide. For full details, see the Checkstyle [configuration file](WorkbenchProject/checkstyle.xml) and Checkstyle [documentation](https://checkstyle.sourceforge.io/checks.html).

* **White space**: One space after control statements (`if (isFoo)`, not `if(isFoo)`), between arguments (`myFunc(foo, bar, baz)`, not `myFunc(foo,bar,baz)`), and around operators (`x += 1`, not `x+=1`). No spaces after method names (i.e. `public void myMethod()`).
* **Indentation**: Always use 4-space indents and **never** use tabs! 
* **Blocks**: Open brace "{" should appear at the end of the same line as the declaration statement and be preceded by a single space.
* **Line wrapping**: Always use a 100-column line width for Java code and Javadoc.
* **Naming**: Class and interface names should use `PascalCase`. Methods and variables should use `camelCase`. Constants (i.e. static final members) should use `UPPER_CASE`. Trailing underscores (i.e. `someVariable_`) should never be used.

#### 4.1.2 XML


### 4.2 Tools and Plugins

#### 4.2.1 Apache Maven


#### 4.2.2 Checkstyle

