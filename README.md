# FileHelper
based on tcp, used for file transfer in LAN by Java

#项目说明
用Socket实现的文件传输助手，在局域网内任意主机之间可以相互收发文件
全双工，多任务，断点续传

master分支是client客户端部分
server分支是服务端部分

使用时打开一个服务端，任意多个客户端
服务端负责收集通协议，保存传输记录，并支持文件上传下载
客户端负责文件收发
