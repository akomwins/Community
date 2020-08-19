$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	//发送ajax请求前，将csrf令牌设置到请求的信息头中
	// var token=$("meta[name='_csrf']").attr("content");
	// var header=$("meta[name='_csrf_header']").attr("content");
	// $(document).ajaxSend(function (e,xhr,options) {
	// 	xhr.setRequestHeader(header,token);
	// });

	//获取标题和内容
	var title=$("#recipient-name").val();
	var content=$("#message-text").val();
	//发送异步请求 （post)
	$.post(
		CONTEXT_PATH+"/discuss/add",
		{"title":title,"content":content},
		function (data) {
			data=$.parseJSON(data);
			//提示框中显示返回消息
			$("#hintBody").text(data.msg);
			//在提示酷返回消息后显示提示框
			$("#hintModal").modal("show");
			//2秒后自动隐藏
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//刷新页面
				if(data.code==0){
					window.location.reload();
				}
			}, 2000);
		}
	);

}