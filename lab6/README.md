使用 Windows 系统的需要注意，远程 JVM 调试和 make check 会使用 python。

关于 python 关键字问题，在 makefile 文件中统一使用了 python3，但是在笔者主机上是 python，其他平台还有可能是 py。若关键词匹配错误可能导致调试和 make check 失效。

解决办法比较简单，在 make check 时加上参数：

make check PYTHON=<keyword for python3>

<keyword for python3> runner.py --debug

此事在本实验说明中亦有提及。