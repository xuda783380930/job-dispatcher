$(function() {
    // 读取body data-type 判断是哪个页面然后执行相应页面方法，方法在下面。
    var dataType = $('body').attr('data-type');
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
	 return [dt.getFullYear(), dt.getMonth(), dt.getDate()].join('/') + " " 
	      + [dt.getHours(),dt.getMinutes(),dt.getSeconds()].join(':')

};
function openUrl(URL) {
    window.location.href=URL;
}
function processNull(data) {
   return null==data?"":data;
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