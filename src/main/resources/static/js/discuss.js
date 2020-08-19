$ (function () {
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
    $("#untopBtn").click(setUntop);
    $("#unwonderfulBtn").click(setUnwonderful);

});
function like(btn,entityType,entityId,entityUserId,postId) {

    //异步请求
    $.post(
        CONTEXT_PATH+"/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function (data) {
            data=$.parseJSON(data);
            if(data.code==0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?"已赞":"赞");
            }else{
                alert(data.msg);
            }

        }

    )

}
//置顶
function setTop() {
    $.post(
        CONTEXT_PATH+"/discuss/top",
        {"id":$("#postId").val()},
        function (data) {
            data=$.parseJSON(data);
            if(data.code==0){
                alert("置顶成功");
                setTimeout(function(){
                    //刷新页面
                    window.location.reload();

                }, 2000);
            }else{
                alert(data.msg)
            }

        }
    )
}
function setUntop() {
    $.post(
        CONTEXT_PATH+"/discuss/untop",
        {"id":$("#postId").val()},
        function (data) {
            data=$.parseJSON(data);
            if(data.code==0){
                alert("取消成功");
                setTimeout(function(){
                    //刷新页面
                        window.location.reload();

                }, 2000);
            }
            else{
                alert(data.msg);
            }
        }
    )
}
//置顶
function setWonderful() {
    $.post(
        CONTEXT_PATH+"/discuss/wonderful",
        {"id":$("#postId").val()},
        function (data) {
            data=$.parseJSON(data);
            if(data.code==0){
                alert("加精成功");
                setTimeout(function(){
                    //刷新页面
                    window.location.reload();

                }, 2000);
            }else{
                alert(data.msg)
            }

        }
    )
}
function setUnwonderful() {
    $.post(
        CONTEXT_PATH+"/discuss/unwonderful",
        {"id":$("#postId").val()},
        function (data) {
            data=$.parseJSON(data);
            if(data.code==0){
                alert("取消成功");
                setTimeout(function(){
                    //刷新页面
                    window.location.reload();

                }, 2000);
            }
            else{
                alert(data.msg);
            }
        }
    )
}

//置顶
function setDelete() {
    $.post(
        CONTEXT_PATH+"/discuss/delete",
        {"id":$("#postId").val()},
        function (data) {
            data=$.parseJSON(data);
            if(data.code==0){
                location.href=CONTEXT_PATH+"/index";
            }else{
                alert(data.msg)
            }

        }
    )
}