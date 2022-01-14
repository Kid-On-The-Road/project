// 查询商品
function selectGoods() {
    $(".goodsPage").fadeIn();
    const queryCondition = {
        "goodsName": document.getElementById("goodsName").value,
        "goodsCategory": document.getElementById("goodsCategory").value,
        "payStatus": document.getElementById("payStatus").value,
        "userId": document.getElementById("userId").value,
    };
    $.ajax({
        type: "POST",
        url: "selectShoppingGoods",
        data: queryCondition,
        success: function (result) {
            //将结果在body中刷新
            $("body").html(result);
            $(".goodsPage").fadeOut();
            toastr.success('查询完成');
        }
    });
}
//支付
function payment(goodsId,type){
    $.ajax({
        type : 'POST',
        url: "payment",
        data:{
            "goodsId":goodsId,
            "type":type
        },
        success : function(result) {
            $(".goodsList").load("../selectShoppingGoods"+"?userId="+document.getElementById("userId").value+ " .goodsList>*");
        }
    })
}
//删除记录
function delRecord(goodsId){
    $.ajax({
        type : "POST",
        url:"delRecord",
        data:{
        "goodsId":goodsId
        },
        success : function(result) {

        }
    })
}
//配置日期控件
$('.date').datetimepicker({
    format: 'yyyy-mm-dd',
    weekStart: 1,
    autoclose: true,
    startView: 2,
    minView: 2,
    forceParse: false,
    language: 'zh-CN'
});

toastr.options = {
    closeButton: false,
    debug: false,
    progressBar: true,
    positionClass: "toast-bottom-center",
    onclick: null,
    showDuration: "300",
    hideDuration: "1000",
    timeOut: "2000",
    extendedTimeOut: "1000",
    showEasing: "swing",
    hideEasing: "linear",
    showMethod: "fadeIn",
    hideMethod: "fadeOut"
};