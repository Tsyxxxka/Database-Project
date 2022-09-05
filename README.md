# 技术栈  

## 后端技术栈

后端主要采用了：  

1.SpringBoot  
2.SpringSecurity  
3.MyBatis  
4.MySQL  

## 前端技术栈

前端主要采用了：  

1.Vue  
2.axios  
3.ElementUI  
4.vue-echarts  
5.mavon-editor  
6.vue-router  

# 快速运行  

1.克隆本项目到本地  
2.配置mysql数据库
3.根据自己本地情况修改数据库配置，数据库配置在SpringBoot项目的application.properties中  
4.在IntelliJ IDEA中运行blogserver项目  

**服务端就启动成功了 

5.进入到vueblog目录中，在命令行依次输入如下命令：  

```
# 安装依赖
npm install

# 在 localhost:8080 启动项目
npm run dev
```  

6.最后可以用WebStorm等工具打开vueblog项目，继续开发，开发完成后，当项目要上线时，依然进入到vueblog目录，然后执行如下命令：  

```
npm run build
```  

该命令执行成功之后，vueblog目录下生成一个dist文件夹，将该文件夹中的两个文件static和index.html拷贝到SpringBoot项目中resources/static/目录下，然后就可以像第4步那样直接访问了。  


# 项目依赖  

1.[vue-echarts](https://github.com/Justineo/vue-echarts)  
2.[mavonEditor](https://github.com/hinesboy/mavonEditor)  
