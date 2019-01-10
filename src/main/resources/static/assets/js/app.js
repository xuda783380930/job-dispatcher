$(function() {
    // 读取body data-type 判断是哪个页面然后执行相应页面方法，方法在下面。
    var dataType = $('body').attr('data-type');
    console.log(dataType);
    for (key in pageData) {
        if (key == dataType) {
            pageData[key]();
        }
    }

    autoLeftNav();
    $(window).resize(function() {
        autoLeftNav();
    });

})


function updateStatus(status){
    var jobCode = GetQueryString("jobcode");
    var eventtime = GetQueryString("eventtime");
    var taskCode = GetQueryString("taskcode");
    $.ajax({
         type: "GET",
         url: "jobbox/updatestatus?jobcode="+jobCode+"&eventtime="+eventtime+"&taskcode="+taskCode+"&status="+status,
         success: function(data){
            alert(data.flag);
            location.reload();
         }
    });
};
function GetQueryString(name){
	         var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	         var r = window.location.search.substr(1).match(reg);//search,查询？后面的参数，并匹配正则
	         if(r!=null)return  unescape(r[2]); return null;
};	         
function stopJob(){
	     alert("运行中的 Task不会被暂停");
		 var jobCode = GetQueryString("jobcode");
		 var eventtime = GetQueryString("eventtime");
	   	 $.ajax({
	            type: "GET",
	            url: "jobbox/stopjob?jobcode="+jobCode+"&eventtime="+eventtime,
	            success: function(data){
	           	 alert(data);
	           	location.reload();
	            }
	   	 });
};
function startJob(){
	     alert("job中所有的暂停的Task会被重新启动");
		 var jobCode = GetQueryString("jobcode");
		 var eventtime = GetQueryString("eventtime");
	   	 $.ajax({
	            type: "GET",
	            url: "jobbox/startjob?jobcode="+jobCode+"&eventtime="+eventtime,
	            success: function(data){
	           	 alert(data);
	           	location.reload();
	            }
	   	 });
}; 
function formatDate(dateStr){
	 var dt = new Date(dateStr.slice(0,4), dateStr.slice(4,6), dateStr.slice(6,8),
			 dateStr.slice(8,10), dateStr.slice(10,12), dateStr.slice(12,14));
	 return [dt.getFullYear(), dt.getMonth() + 1, dt.getDate()].join('/') + " " 
	      + [dt.getHours(),dt.getMinutes(),dt.getSeconds()].join(':')

};
function openUrl(URL) {
    window.location.href=URL;
}
function processNull(data) {
   return null==data?"":data;
}
// 页面数据
var pageData = {
    'index': function indexData() {
        $('#example-r').DataTable({
            bInfo: false, //页脚信息
            dom: 'ti'
        });
        //首页已配置的QuartzJob列表
        $.ajax({
           type: "GET",
           url: "quartz/jobs",
           success: function(data){
           	var htmlStr = "";
           	for(var idx in data){
           		var quartzJob = data[idx];
           		htmlStr = htmlStr+'<tr>';
           		htmlStr = htmlStr+"<td>" + processNull(quartzJob.jobCode) +"</td>";
           		htmlStr = htmlStr+"<td>" + processNull(quartzJob.jobCronExpression) +"</td>";
           		htmlStr = htmlStr+"<td>" + processNull(quartzJob.filterCalendarName) +"</td>";
           		htmlStr = htmlStr+"<td>" + processNull(quartzJob.monitorParas) +"</td>";
           		var jobCode = quartzJob.jobCode;
           		htmlStr = htmlStr+ '<td>'+
           						   '<button type="button" class="am-btn am-btn-primary  am-btn-block tpl-btn-bg-color-success"'+
           						   'onclick="javascript:openUrl(\'jobbuilder.html?jobcode='+ jobCode+'\')">查看配置拓扑结构</button>'+
           		                   '</td>';
           		htmlStr = htmlStr+"</tr>";
           	}
           	$("#quartz-jobs").html(htmlStr);
           }
   	 	});

      //Ajax调用处理
        $.ajax({
           type: "GET",
           url: "graph/unfinishedqueues",
           success: function(data){
        	    // 基于准备好的dom，初始化echarts实例
               var echartsA = echarts.init(document.getElementById('tpl-echarts'));
        	        if(null!=data.axisData&&null!=data.nodes){
        	        	var axisData = data.axisData;
    					var nodes  = data.nodes;
            			var links = data.links;
    					option = {
    					    title: {
   	                         	textStyle:{ //设置主标题风格
   	                         		color:'white'//设置主标题字体颜色
	                    		},
    					        text: '调度引擎任务队列'
    					    },
    					    toolbox: {
    					        feature: {
    					            dataZoom: {
    					            	borderColor: 'white'
    					            },
    					            restore: {},
    					            saveAsImage: {}
    					        }
    					    },
    					    tooltip: {
	                            formatter: function(params){
                                    return params.value[0]+" "+formatDate(params.value[1])+" "+params.value[3];
                                 } 
    					    },
    					    xAxis: {
    					        type : 'category',
    					        boundaryGap: ['5%', '5%'],
    					        data : axisData,
    	                        axisLine:{
    	                            lineStyle:{
    	                                color:'white'
    	                            }
    	                        }
    					    },
    					    yAxis: {
    					        type : 'value',
    					        axisLabel: {
    					        		formatter: function (value, index) {
    					        			return formatDate(value.toString());
    					        		},
    					        		rotate:40
    					        	},
    					        boundaryGap: ['5%', '5%'],
    					        scale:true,
    	                        axisLine:{
    	                            lineStyle:{
    	                                color:'white'
    	                            }
    	                        }
    					    },
    					    grid: {
    					    	left: 100,
    					    	bottom:80
    					    },
    					    series: [
    					        {
    					            type: 'graph',
    					            layout: 'none',
    					            coordinateSystem: 'cartesian2d',
    					            symbolSize: 20,
    					            label: {
    	                                  normal: {
    	                                	    show:true,                  //是否显示标签。
    	                                	    position:"inside",          //标签的位置。// 绝对的像素值[10, 10],// 相对的百分比['50%', '50%'].'top','left','right','bottom','inside','insideLeft','insideRight','insideTop','insideBottom','insideTopLeft','insideBottomLeft','insideTopRight','insideBottomRight'
    	                                	    color: 'black',
    	                                	    offset:[0, 0],             //是否对文字进行偏移。默认不偏移。例如：[30, 40] 表示文字在横向上偏移 30，纵向上偏移 40。
    	                                	    formatter: function(params){
    	                                            return formatDate(params.value[1])+" "+params.value[3];
    	                                        }       //标签内容格式器。模板变量有 {a}、{b}、{c}，分别表示系列名，数据名，数据值。
    	                                	}
    	                             },
    					            itemStyle: {
    					               		 normal: {
    					                  			  color: function(params) {
    					                  				  		return params.value[2];
    					                  			  		 }
    					               				 }
    					           	},
    					            edgeSymbol: ['circle', 'arrow'],
    					            edgeSymbolSize: [4, 10],
    					            data: nodes,
    					            links:links,
    					            lineStyle: {
    					                normal: {
    					                    color: '#5eb95e',
    					                    width: 2
    					                }
    					            }
    					        }
    					    ]
    					};
    					  //实现节点点击事件
                        function focus(param) {
                      	  var option = echartsA.getOption();
                       	  var data = param.value;
                       	  //判断节点的相关数据是否正确
                       	 window.location.href="jobdetail.html?jobcode="+data[0]+"&eventtime="+data[1];
                        };

                        //绑定图表节点的点击事件
                        echartsA.on("click", focus);
                        // 使用刚指定的配置项和数据显示图表。
                        echartsA.setOption(option);
    					}
        	        }
					
            });
    },
'jobbuilder': function jobBuilderData() { 
    	   var jobCode = GetQueryString("jobcode");
    		//Ajax调用处理
    	       $.ajax({
    	          type: "GET",
    	          url: "graph/quartzjob?jobcode="+jobCode,
    	          success: function(data){
    	       	    // 基于准备好的dom，初始化echarts实例
    	       	    var myChart = echarts.init(document.getElementById('jobbuilder'));
    					if(null!=data&&null!=data.nodes&&null!=data.links){
    						  var myData = data.nodes;
    						  var myLinks = data.links;
    	                  // 指定图表的配置项和数据
    	                     option = {
    	                     title: {
    	                         textStyle:{ //设置主标题风格
    	                        	 color:'white'//设置主标题字体颜色
	                    		  },
    	                         text: 'Job:'+jobCode+'拓扑结构图'
    	                     },
    	                     tooltip: {},
    	                     animationDurationUpdate: 1500,
    	                     animationEasingUpdate: 'quinticInOut',
    	                     formatter: function(params){//触发之后返回的参数，这个函数是关键
    	                    	 if("node"==params.dataType){
    	                    		 if (typeof(params.data.overTime) != undefined){
    	                    			 return "任务接口地址为"+ params.data.taskUrl +",设定的超时提醒时间为"+params.data.overTime+"分钟";
    	                    		 }
    	                    	   	return "任务接口地址为"+ params.data.taskUrl +",设定的超时提醒时间未设定";
    	                     	 }
    	                        if("edge"==params.dataType){
    	                        	return "从"+ params.data.source + "到"+ params.data.target;
	                     	    }
    	                      },
    	                     series : [
    	                         {
    	                             type: 'graph',
    	                             layout: 'none',
    	                             symbolSize: 20,
    	                             barwidth:100,
    	                             roam: true,
    	                             label: {
    	                                 normal: {
    	                               	    show:true,                  //是否显示标签。
    	                               	    color:'black',
    	                               	    position:"inside",          //标签的位置。// 绝对的像素值[10, 10],// 相对的百分比['50%', '50%'].'top','left','right','bottom','inside','insideLeft','insideRight','insideTop','insideBottom','insideTopLeft','insideBottomLeft','insideTopRight','insideBottomRight'
    	                               	    offset:[0, 0],             //是否对文字进行偏移。默认不偏移。例如：[30, 40] 表示文字在横向上偏移 30，纵向上偏移 40。
    	                               	    formatter: function(params){
    	                                           return params.data.name;
    	                                       }       //标签内容格式器。模板变量有 {a}、{b}、{c}，分别表示系列名，数据名，数据值。
    	                               	}
    	                             },
    	                             edgeSymbol: ['circle', 'arrow'],
    	                             edgeSymbolSize: [4, 10],
    	                             edgeLabel: {
    	                                 normal: {
    	                                     textStyle: {
    	                                         fontSize: 20
    	                                     }
    	                                 }
    	                             },
    	                             data: myData,
    	                             links: myLinks,
    	                             lineStyle: {
    	                                 normal: {
    	                                     opacity: 0.9,
    	                                     width: 2,
    	                                     curveness: 0
    	                                 }
    	                             }
    	                         }
    	                     ]
    	                 	};
    	                  
    	                   // 使用刚指定的配置项和数据显示图表。
    	                   myChart.setOption(option);
    						}
    	                 }
    	           });
    	},
'jobdetail': function jobdetailData() { 
	    var jobCode = GetQueryString("jobcode");
	    var eventtime = GetQueryString("eventtime");
	    if(GetQueryString("ifhis")==1){
		    $("#job-opt-btn").hide();
	    }
		//Ajax调用处理
	    $.ajax({
	           type: "GET",
	           url: "graph/tasksinjob?jobcode="+jobCode+"&eventtime="+eventtime,
	           success: function(data){
	        	    // 基于准备好的dom，初始化echarts实例
	        	    var myChart = echarts.init(document.getElementById('jobdetail'));
					if(null!=data&&null!=data.nodes&&null!=data.links){
						  var myData = data.nodes;
						  var myLinks = data.links;
	                   // 指定图表的配置项和数据
	                      option = {
	                      title: {
	                    	  textStyle:{ //设置主标题风格
 	                         		color:'white'//设置主标题字体颜色
	                    	  },
	                          text: 'Job:'+jobCode+',批次日期:'+eventtime+'具体运行情况'
	                      },
	                      tooltip: {},
	                      animationDurationUpdate: 1500,
	                      animationEasingUpdate: 'quinticInOut',
	                      formatter: function(params){//触发之后返回的参数，这个函数是关键
	                            return params.data.label;
	                       },
	                      series : [
	                          {
	                              type: 'graph',
	                              layout: 'none',
	                              symbolSize: 20,
	                              roam: true,
	                              itemStyle: {
					               		 normal: {
					                  			  color: function(params) {
					                        	   			return params.data.color;
					                         		 }
					               				 }
					           	  },
	                              label: {
	                                  normal: {
	                                	    show:true,                  //是否显示标签。
	                                	    color:'black',
	                                	    position:"inside",          //标签的位置。// 绝对的像素值[10, 10],// 相对的百分比['50%', '50%'].'top','left','right','bottom','inside','insideLeft','insideRight','insideTop','insideBottom','insideTopLeft','insideBottomLeft','insideTopRight','insideBottomRight'
	                                	    offset:[0, 0],             //是否对文字进行偏移。默认不偏移。例如：[30, 40] 表示文字在横向上偏移 30，纵向上偏移 40。
	                                	    formatter: function(params){
	                                            return params.data.name;
	                                        }       //标签内容格式器。模板变量有 {a}、{b}、{c}，分别表示系列名，数据名，数据值。
	                                	}
	                              },
	                              edgeSymbol: ['circle', 'arrow'],
	                              edgeSymbolSize: [4, 10],
	                              edgeLabel: {
	                                  normal: {
	                                      textStyle: {
	                                          fontSize: 20
	                                      }
	                                  }
	                              },
	                              data: myData,
	                              links: myLinks,
	                              lineStyle: {
	                                  normal: {
	                                      opacity: 0.9,
	                                      width: 2,
	                                      curveness: 0
	                                  }
	                              }
	                          }
	                      ]
	                  	};
	                   
	                      //实现节点点击事件
	                     function focus(param) {
	                    	  var option = myChart.getOption();
	                     	  var data = param.data;
	                     	  //判断节点的相关数据是否正确
	                     	  if (data != null && data != undefined) {
	                          //根据节点的扩展属性url打开新页面
	                          window.location.href="taskdetail.html?taskcode="+data.name+"&jobcode="+jobCode+"&eventtime="+eventtime+"&ifhis="+GetQueryString("ifhis");
	                         }
	                     }

	                    //绑定图表节点的点击事件
	                     myChart.on("click", focus)
	                    // 使用刚指定的配置项和数据显示图表。
	                    myChart.setOption(option);
						}
	                  }
	            });
     	},
'taskdetail': function taskdetailData() { 
    var jobCode = GetQueryString("jobcode");
    var eventtime = GetQueryString("eventtime");
    var taskCode = GetQueryString("taskcode");
    
    if(GetQueryString("ifhis")==1){
	    $("#task-opt-btn").hide();
    }
    
    $.ajax({
        type: "POST",
        url: "task/cons?url=tasks/"+taskCode,
        success: function(info){
        	if(info.adapterpara!=undefined){
        		var htmlStr = "";
            	htmlStr = htmlStr+"<tr>";
            	htmlStr = htmlStr+"<td>任务Url</td><td>" + processNull(info.adapterpara)+"</td>";
            	htmlStr = htmlStr+"</tr>";
            	htmlStr = htmlStr+"<tr>";
            	htmlStr = htmlStr+"<td>Job代码</td><td>" + processNull(info.jobcode)+"</td>";
            	htmlStr = htmlStr+"</tr>";
            	htmlStr = htmlStr+"<tr>";
            	htmlStr = htmlStr+"<td>任务代码</td><td>" + processNull(info.taskcode)+"</td>";
            	htmlStr = htmlStr+"</tr>";
            	htmlStr = htmlStr+"<tr>";
            	htmlStr = htmlStr+"<td>任务名</td><td>" + processNull(info.taskname)+"</td>";
            	htmlStr = htmlStr+"</tr>";
            	htmlStr = htmlStr+"<tr>";
            	htmlStr = htmlStr+"<td>任务描述</td><td>" + processNull(info.taskdesc)+"</td>";
            	htmlStr = htmlStr+"</tr>";
            	htmlStr = htmlStr+"<tr>";
            	htmlStr = htmlStr+"<td>任务类型</td><td>" + processNull(info.tasktype)+"</td>";
            	htmlStr = htmlStr+"</tr>";
            	htmlStr = htmlStr+"<tr>";
            	htmlStr = htmlStr+"<td>任务参数列表</td><td>" + processNull(info.paramlist)+"</td>";
            	htmlStr = htmlStr+"</tr>";
            	htmlStr = htmlStr+"<tr>";
            	htmlStr = htmlStr+"<td>上游代码</td><td>" + processNull(info.upstreamtaskcodes)+"</td>";
            	htmlStr = htmlStr+"</tr>";
            	htmlStr = htmlStr+'<tr>';
            	htmlStr = htmlStr+'<td>任务脚本</td><td>'+
            			          '<textarea style="width:100%;background:transparent;border-style:none;" rows="8" readonly="readonly">' + 
            			          processNull(info.script)+'</textarea></td>';
            	htmlStr = htmlStr+"</tr>";
            	$("#task-base-info").html(htmlStr);
        	}
        }
	 });
    
    var queryJson = {
    		"url":"tasklogs?taskcode="+taskCode+"&eventtime="+eventtime
    }
    $.ajax({
        type: "POST",
        url: "task/cons",
        data:queryJson,
        success: function(logs){
        	var htmlStr = "";
        	for(var idx in logs){
        		var log = logs[idx];
        		htmlStr = htmlStr+"<tr>";
        		htmlStr = htmlStr+"<td>" + log.id+"</td>";
        		htmlStr = htmlStr+"<td>" + log.eventtime+"</td>";
        		htmlStr = htmlStr+"<td>" + log.starttime+"</td>";
        		htmlStr = htmlStr+"<td>" + log.endtime+"</td>";
        		htmlStr = htmlStr+"<td>" + log.executiontime+"</td>";
        		htmlStr = htmlStr+"<td>" + log.inserttime+"</td>";
        		htmlStr = htmlStr+"<td>" + log.retrytimes+"</td>";
        		htmlStr = htmlStr+'<td>' + '<textarea style="width:100%;background:transparent;border-style:none;" rows="8" readonly="readonly">' + 
        		                  log.content + '</textarea></td>';
        		htmlStr = htmlStr+"</tr>";
        	}

        	
        	$("#task-log").html(htmlStr);
        }
	 });
    
	 $.ajax({
           type: "GET",
           url: "graph/task?jobcode="+jobCode+"&eventtime="+eventtime+"&taskcode="+taskCode,
           success: function(data){
        	    // 基于准备好的dom，初始化echarts实例
        	    var myChart = echarts.init(document.getElementById('taskdetail-chart'));
				if(null!=data&&null!=data.nodes){
					  var nodes = data.nodes;
					  var axisData = data.axisData;
          			  var links = data.links;
                   // 指定图表的配置项和数据
                     option = {
    					    title: {
   	                         	textStyle:{ //设置主标题风格
   	                         		color:'white'//设置主标题字体颜色
	                    		},
    					    	text: 'Job:'+jobCode+',批次日期:'+eventtime+',taskCode:'+taskCode+' - 运行时序'
    					    },
    					    toolbox: {
    					        feature: {
    					            dataZoom: {
    		                             borderColor: 'white'
    					            },
    					            restore: {},
    					            saveAsImage: {}
    					        }
    					    },
    					    tooltip: {
	                            formatter: function(params){
	                            	if(typeof(params.value[3]) == "undefined"){
	                            		return formatDate(params.value[1]) + params.value[0];
	                            	}
                                    return formatDate(params.value[1]) + "由" + params.value[3]+ "置为" +params.value[0];
                                 } 
    					    },
    					    xAxis: {
    					        type : 'category',
    					        boundaryGap: ['5%', '5%'],
    					        data : axisData,
    	                        axisLine:{
    	                            lineStyle:{
    	                                color:'white'
    	                            }
    	                        }
    					    },
    					    yAxis: {
    					        type : 'value',
    					        axisLabel: {
    					        		formatter: function (value, index) {
    					        			return formatDate(value.toString());
    					        		},
    					        		rotate:40
    					        	},
    					        boundaryGap: ['5%', '5%'],
    					        scale:true,
    	                        axisLine:{
    	                            lineStyle:{
    	                                color:'white'
    	                            }
    	                        }
    					    },
    					    grid: {
    					    	left: 100,
    					    	bottom:80
    					    },
    					    series: [
    					        {
    					            type: 'graph',
    					            layout: 'none',
    					            coordinateSystem: 'cartesian2d',
    					            symbolSize: 20,
    					            itemStyle: {
    					                normal: {
    					                    color: function(params) {
    					                           return params.value[2];
    					                          } 
    					               		  }
    					            },
    					            label: {
    	                                  normal: {
    	                                	    show:true,                  //是否显示标签。
    	                                	    position:"inside",          //标签的位置。// 绝对的像素值[10, 10],// 相对的百分比['50%', '50%'].'top','left','right','bottom','inside','insideLeft','insideRight','insideTop','insideBottom','insideTopLeft','insideBottomLeft','insideTopRight','insideBottomRight'
    	                                	    color: 'black',
    	                                	    offset:[0, 0],             //是否对文字进行偏移。默认不偏移。例如：[30, 40] 表示文字在横向上偏移 30，纵向上偏移 40。
    	                                	    formatter: function(params){
    	                                            return formatDate(params.value[1]);
    	                                        }       //标签内容格式器。模板变量有 {a}、{b}、{c}，分别表示系列名，数据名，数据值。
    	                                	}
    	                              },
    					            edgeSymbol: ['circle', 'arrow'],
    					            edgeSymbolSize: [4, 10],
    					            links:links,
    					            data: nodes,
    					            lineStyle: {
    					                normal: {
    					                    color: '#5eb95e',
    					                    width: 2
    					                }
    					            }
    					        }
    					    ]
    					};
                      //实现节点点击事件
                      function focus(param) {
                    	  var option = myChart.getOption();
                     	  var data = param.data;
                     	  //判断节点的相关数据是否正确
                     	  if (data != null && data != undefined) {
                      	  if (data.url != null && data.url != undefined) {
                          //根据节点的扩展属性url打开新页面
                          window.open(data.url);
                      	  }
                         }
                      }

                    //绑定图表节点的点击事件
                     myChart.on("click", focus)
                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);
					}
                  }
            });
         	},
'historyjob-list': function historyjobListData() { 
        	    var jobCode = "";
	            $("#startDate").attr("value",getNowFormatDate(24*60*60*1000));
	            $("#endDate").attr("value",getNowFormatDate(0));
        	    refreshHisJob(0);
             	}
}

//获取当前时间，格式YYYY-MM-DD
function getNowFormatDate(Time) {
    var date = new Date();
    date.setTime(date.getTime()-Time);
    var seperator1 = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + seperator1 + month + seperator1 + strDate;
    return currentdate;
}

function refreshHisJob(pageNum){
    var pageSize= 10;
    var startDate = $("#startDate").val(); 
    var endDate = $("#endDate").val(); 
    var jobCode = $("#jobCode").val();
   	var params = "startDate="+startDate+"&endDate="+endDate+"&pageNum="+pageNum+"&pageSize="+pageSize;
   	if(""!=jobCode){
   		params = params + "&jobCode="+jobCode;
   	}
   	
	$.ajax({
        type: "GET",
        url: "monitor/jobhistory?"+params,
        success: function(data){
           	var htmlStr = "";
           	for(var idx in data){
           		var quartzJob = data[idx];
           		htmlStr = htmlStr+'<tr>';
           		htmlStr = htmlStr+"<td>" + quartzJob.C_JOBCODE+"</td>";
           		htmlStr = htmlStr+"<td>" + quartzJob.C_EVENTTIME+"</td>";
           		htmlStr = htmlStr+ '<td>'+
           						   '<button type="button" class="am-btn am-btn-primary  am-btn-block tpl-btn-bg-color-success"'+
           						   'onclick="javascript:openUrl(\'jobdetail.html?ifhis=1&jobcode='+ quartzJob.C_JOBCODE +'&eventtime='+quartzJob.C_EVENTTIME+'\')">查看</button>'+
           		                   '</td>';
           		htmlStr = htmlStr+"</tr>";
           	}
           	$("#history-jobs").html(htmlStr);
           	var pageStr = '';
           	if(Number(pageNum)>1){
               	pageStr = pageStr + '<li class="am-active"><a onclick="refreshHisJob(\''+(Number(pageNum)-1)+'\')">上一页</a></li>';
           	}
           	pageStr = pageStr + '<li class="am-active"><a onclick="refreshHisJob(\''+Number(pageNum)+'\')">'+pageNum+'</a></li>';
           	pageStr = pageStr + '<li class="am-active"><a onclick="refreshHisJob(\''+(Number(pageNum)+1)+'\')">下一页</a></li>';
           	$("#history-jobs-pagetag").html(pageStr);
               }
         });
}

// 风格切换

$('.tpl-skiner-toggle').on('click', function() {
    $('.tpl-skiner').toggleClass('active');
})

$('.tpl-skiner-content-bar').find('span').on('click', function() {
    $('body').attr('class', $(this).attr('data-color'))
    saveSelectColor.Color = $(this).attr('data-color');
    // 保存选择项
    storageSave(saveSelectColor);

})


// 侧边菜单开关
function autoLeftNav() {

    $('.tpl-header-switch-button').on('click', function() {
        if ($('.left-sidebar').is('.active')) {
            if ($(window).width() > 1024) {
                $('.tpl-content-wrapper').removeClass('active');
            }
            $('.left-sidebar').removeClass('active');
        } else {

            $('.left-sidebar').addClass('active');
            if ($(window).width() > 1024) {
                $('.tpl-content-wrapper').addClass('active');
            }
        }
    })

    if ($(window).width() < 1024) {
        $('.left-sidebar').addClass('active');
    } else {
        $('.left-sidebar').removeClass('active');
    }
}


// 侧边菜单
$('.sidebar-nav-sub-title').on('click', function() {
    $(this).siblings('.sidebar-nav-sub').slideToggle(80)
        .end()
        .find('.sidebar-nav-sub-ico').toggleClass('sidebar-nav-sub-ico-rotate');
})