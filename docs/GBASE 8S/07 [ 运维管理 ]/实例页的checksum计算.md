## 页的checksum计算
如下是一个页oncheck -pP输出信息     
```text
addr             stamp    chksum nslots flag type         frptr frcnt next     prev
6:27000          68995002 aad8   5      801  DATA         92    16268 0        0     
```
以下将说明chksum的值来源：  
addr包含 chunk 及 offset 两部分，位于页头部，chunk是2字节，offset是4字节  
stamp位于页尾部，4字节长度  

chksum的值等于 chunk 位异或 offset前两字节 位异或 offset后两字节 位异或 stamp前两字节 位异或 stamp后两字节  

计算过程示例：
```python
chunk = 6
offset = 27000
stamp = 68995002

“”“
offset>>16 右移2字节，获取前两字节
offset&65535 屏蔽前两字节，获取后两字节
”“”
chksum = chunk ^ (offset>>16) ^ (offset&65535) ^ (stamp>>16) ^ (stamp&65535)

print(f"chksum: {chksum} {chksum:04x}")
```
运行结果与chksum一致  
```text
chksum: 43736 aad8
```
