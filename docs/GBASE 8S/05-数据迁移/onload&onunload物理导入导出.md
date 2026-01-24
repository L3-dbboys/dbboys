### 说明
onunload和onload工具提供在相同平台上使用相同数据库服务器的计算机之间移动数据的最快方法。  
onunload导出库或库中单表到指定设备，默认导出到TAPEDEV指定的设备，导出文件为二进制文件，不可跨版本或跨平台。

### 示例1-使用onunload导出表test及索引到test.dat
```
touch test.dat
onunload -t test.dat gbasedb:test
Please mount tape and press Return to continue ... #回车
Please label this as tape number 1 in the tape sequence.
```
### 示例2-使用onload导入表及该表索引到gbasedb，数据存储于datadbs01
```
onload -t test.dat gbasedb:test -d datadbs01
Please mount tape and press Return to continue ... #回车
The load has successfully completed.
```
**注意：onunload/onload不支持集群，不支持blob、clob数据类型的导入导出**  
参考文档【[GBase 8s 导入导出工具指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/Migration_Guide.pdf)】