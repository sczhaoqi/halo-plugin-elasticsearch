## 网站全文检索

全文检索使用 Elasticsearch 实现（简称：ES），ES 是一个实时分布式搜索和分析引擎，ES 底层是基于高性能的 Lucene 实现，而对是一套简单的 RestFul API 让我们更简单去实现我们所需要的全文搜索的功能。

### 安装 Elasticsearch

如果您已经安装了 ES，请确认是否安装了中文分词插件，如果都有，可以忽略本节。

本节使用 Windows 操作系统环境为例介绍安装。

#### 下载 ES 软件

环境要求 JDK1.8 以上（建议 JDK 11）、ES 版本 7.6.2，本节使用 Windows 为例如下：

-   [https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.6.2-windows-x86\_64.zip (opens new window)](https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.6.2-windows-x86_64.zip)

更多版本：[https://www.elastic.co/cn/downloads/elasticsearch (opens new window)](https://www.elastic.co/cn/downloads/elasticsearch)

下载后解压文件到 elasticsearch-7.6.2-windows-x86\_64 目录下。

打开 elasticsearch-7.6.2-windows-x86\_64/config/elasticsearch.yml 文件，根据自己的情况进行配置：

```
cluster.name: my-application
network.host: 127.0.0.1
http.port: 9200

# 因为 elasticsearch-head 需要，一定要添加上
http.cors.enabled: true
http.cors.allow-origin: "*"
```

#### [#](https://jeesite.com/docs/cms/#%E5%8F%82%E6%95%B0%E9%85%8D%E7%BD%AE-2#下载-es-分词插件) 下载 ES 分词插件

-   [https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.7.2/elasticsearch-analysis-ik-6.7.2.zip (opens new window)](https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.7.2/elasticsearch-analysis-ik-6.7.2.zip)
-   [https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v6.7.2/elasticsearch-analysis-pinyin-6.7.2.zip (opens new window)](https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v6.7.2/elasticsearch-analysis-pinyin-6.7.2.zip)

下载后 2 个文件是源码包，需要自行编译下，手动解压，进入到目录分别执行：

```
cd elasticsearch-analysis-ik-6.7.2
mvn clean package -Dmaven.test.skip=true

cd elasticsearch-analysis-pinyin-6.7.2.zip
mvn clean package -Dmaven.test.skip=true
```

执行编译完成后分别在 target/release 目录下得到 2 个 zip 文件：

-   elasticsearch-analysis-ik-6.5.0.zip
-   elasticsearch-analysis-pinyin-6.3.0.zip

分别解压这个这 2 个 zip 文件，到 ES 插件目录下：

-   elasticsearch-7.6.2-windows-x86\_64\\plugins\\analysis-ik
-   elasticsearch-7.6.2-windows-x86\_64\\plugins\\analysis-pinyin

#### 运行 ES 服务

```
cd elasticsearch-7.6.2-windows-x86_64/bin
elasticsearch.bat
```

在启动日志里可以看到 loaded plugin 加载 analysis-ik 和 analysis-pinyin 的日志，说明插件安装成功。

### 安装可视化工具

安装目标主要是为了对 ES 中索引的数据提供搜索和数据可视化功能。

有两种工具：Kibana 或 elasticsearch-head 根据喜好任选其一。

#### 安装 Kibana

-   下载

[https://artifacts.elastic.co/downloads/kibana/kibana-7.6.2-windows-x86\_64.zip (opens new window)](https://artifacts.elastic.co/downloads/kibana/kibana-7.6.2-windows-x86_64.zip)

注意下载版本要和 ES 版本匹配，更多版本：[https://www.elastic.co/cn/downloads/kibana/ (opens new window)](https://www.elastic.co/cn/downloads/kibana/)

下载后解压文件到 kibana-7.6.2-windows-x86\_64 目录下。

-   配置

修改 config/kibana.yml 文件的语言参数：

```
i18n.locale: "zh-CN"
```


-   运行

确保 ES 服务已经启动，然后运行 `bin/kibana.bat` 即可。

-   浏览器访问

地址：[http://localhost:5601/ (opens new window)](http://localhost:5601/)

-   进入界面后，首先需要创建索引

创建索引，菜单：Management -> Kibana -> 索引模式 -> 创建索引模式

索引模式填写 `js_cms_article` 点击 “下一步” 按钮，接着点击 “创建索引模式” 按钮即可。

-   常见问题

如果提示 “Forbidden” 可能是因为磁盘空间不足导致 “索引变成只读状态”。

先保证磁盘使用率 90% 以下，菜单：Dev Tools -> 控制台填写如下内容：

```
PUT _settings
{
  "index": {
    "blocks": {
      "read_only_allow_delete": "false"
    }
  }
}
```


然后点击编辑器右侧的 “三角号” 执行发送请求即可。

#### 安装 ES-head

-   下载

```
git clone https://github.com/mobz/elasticsearch-head.git
cd elasticsearch-head
```

-   编译

```
yarn config set registry https://registry.npm.taobao.org
yarn install 
```

-   运行

```
npm run start
```


-   浏览器访问

地址：[http://localhost:9100/ (opens new window)](http://localhost:9100/)

### 引入插件


### 参数配置

```
   serverUrl: http://127.0.0.1:9200
   index: halo_es_index
   apiKey: 
```

### 功能测试

-   进入菜单 “站点管理” 在列表上点击 “重建索引” 按钮，可以对当前库里的文章信息，进行存储到 ES 中。
-   根据需要还可以根据栏目进行重建索引，进入菜单 “栏目管理” 在列表上点击 “重建索引” 按钮。
-   当新增、修改或删除文章的时候都会进行自动化更新索引，无需手动更新。
-   进入网站，在右上角，填入关键字，搜索即可查看效果。