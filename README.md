# job-dispatcher

#### 介绍
一个工作流编排，任务调度引擎

欢迎加入QQ讨论群:558192118

GtiHub地址:[https://github.com/xuda783380930/job-dispatcher.git](https://github.com/xuda783380930/job-dispatcher.git)

基本模型:

基于事件或者定时生成一个job,每个job由若干个task组成，task之间存在串行或并行的依赖关系

task的具体实现引由客户端（用户自己编程实现），调度引擎只负责按照拓扑顺序发出HTTP信号触发任务

由客户端发出HTTP通知引擎任务任务的完成状态，调度引擎据此继续发出后续调度信号

使用场景:

任务编排，事件驱动或时间驱动的任务调度，如企业级ETL调度等

特点:

调度引擎只做调度和任务编排，不参与具体任务实现和执行，可看作一个HTTP信号发射和接收器，具有极大的适用性，天生支持异构系统，引擎端和任务实现端彻底的解耦。

job-dispatcher是微服务中的一个独立组件，而非作业平台，因此它并不去实现各类任务的运行环境的适配工作，任何实现了配置中子任务HTTP启动接口的程序都可以作为job-dispatche的客户端，无论是何种语言何种数据库的程序。

相似项目: _Linkedin公司推出的Azkaban_ ， _国产软件TASKCTL_， _流程引擎 Flowable /Activiti_ ，欢迎大家对比提出意见

#### 软件架构
软件架构说明

1.时间调度引擎:基于Quartz，按时触发对应的Job

2.Job引擎:可注册JobBuilder（包含子任务的依赖关系），Job引擎收到触发信号后，根据builder生成JobBox实例（包含批次时间和任务执行状态）
          按照时间顺序JobBox被Put到队列中，队首的JobBox会被消费，即按照拓扑关系依次发出信号，直到所有子任务执行完毕，继续执行下一个
          JobBox。

![输入图片说明](https://images.gitee.com/uploads/images/2019/0109/101119_f27af5bf_1466151.png "调度引擎任务队列.png")

                                                       调度引擎任务队列样例

![通过单点的任务依赖关系形成的Job拓扑图](https://images.gitee.com/uploads/images/2019/0108/220256_bb1bc412_1466151.png "任务拓扑图样例.png")

                                                       任务拓扑图样例


3.使用的框架:JDK1.8,SpringBoot2.0,Quartz,JPA,Echarts,Amazeui,Swagger2

#### 安装教程

1.修改对应的jdbc数据源配置和pom里面的jdbc驱动依赖 

2.基于Springboot,用maven打包成Jar后启动即可

#### 使用说明

1. JDK1.8环境，通过默认端口浏览器访问就可以看到主页

2. 通过quartz/registerjob接口，传入Json格式的Job配置文件注册任务即可,具体如下

    1.任务端向引擎注册一个定时Job
    http://ip:port/quartz/register?json={} 

    //通过下列的json配置样例

     {

	"jobBoxBuilder": {

		"jobCode": "job1",

                "monitorParas":{mobileNos:[ "18621711111","18616152222"]} //短信收信人

                "taskBuilderMap": [ //Job内的task组成列表

			{
				"taskCode": "task1",

				"adapterPara": "http://172.11.33.44/trigger?jobCode=jos1&taskCode=task1", 

				"upstreamTaskCodes": ["task2"], //依赖task2，task2完成后task1才可执行

				"overTime":"60" //超时时间60分钟则报警
			},

			{
				"taskCode": "task2",

				"adapterPara": "http://172.11.33.44/trigger? jobCode=jos2&taskCode=task2",

				//"upstreamTaskCodes":[],//不依赖任何任务可以不填该字段

                                //"overTime":"60" //可不填

			}

		]

	},

	"jobCronExpression": "0 0/1 11 * * ?", //每天11点每隔一分钟执行一次

	"filterCalendarName": "nonTradingDays" //过滤nonTradingDays日历组

    }



    2.任务端向引擎通知任务执行情况

    UNSTART("未开始", 0),TRIGGERED("正在触发", 1),STARTED("正在执行", 2),NEED_RESTART("需重新执行", 3),
    PAUSED("已暂停", 4),FINISHED("已完毕", 5);

    http://ip:port/task/call?jobcode=job1&taskcode=task2&status=0

    3.查看job的等待队列的情况，查看正在执行的job的情况(通过前端页面关系图查看)

    

#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request


#### 码云特技

1. 使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2. 码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3. 你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4. [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5. 码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6. 码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)