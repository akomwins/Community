$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	//发送数据给服务端
	var toName=$("#recipient-name").val();
	var content=$("#message-text").val();
	//异步发post
	$.post(
		CONTEXT_PATH+"/letter/send",//路劲
		{"toName":toName,"content":content},
		function (data) {//返回结果
			data=$.parseJSON(data);//转化为js对象
			if(data.code==0){
				$("#hintBody").text("发送成功");

			}else{
				$("#hintBody").text(data.msg);
			}
		}
	);
	$("#hintModal").modal("show");
	setTimeout(function(){
		$("#hintModal").modal("hide");
		//刷新页面
		location.reload();
	}, 2000);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}