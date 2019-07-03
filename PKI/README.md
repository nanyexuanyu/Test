# PKI DEMO
### 实现功能： 
使用RSA加密算法 实现前端JS加密 后端JAVA解密
### 配置： 
在项目目录/WEB-INF/key.xml中配置RSA的pubkey（公钥）和prikey（私钥），项目启动时自动读取密钥对； 
也可以不做配置，项目启动时将自动生成一对密钥并写入文件
