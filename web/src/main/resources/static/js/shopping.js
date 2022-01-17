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
function payment(goodsId, type) {
    $.ajax({
        type: 'POST',
        url: "payment",
        data: {
            "goodsId": goodsId,
            "type": type
        },
        success: function (result) {
            $(".goodsList").load("../selectShoppingGoods" + "?userId=" + document.getElementById("userId").value + " .goodsList>*");
            if (type === 'P') {
                toastr.success('付款成功');
            } else if (type === 'R') {
                toastr.success('退款成功');
            }
        }
    })
}

//删除记录
function delRecord(goodsId) {
    $.ajax({
        type: "POST",
        url: "delRecord",
        data: {
            "goodsId": goodsId,
            "userId": document.getElementById("userId").value
        },
        success: function (result) {
            //关闭模态框遮罩
            $(".modal-backdrop").remove();
            $(".goodsList").load("../selectShoppingGoods" + "?userId=" + document.getElementById("userId").value + " .goodsList>*");
            toastr.success('删除成功');
        }
    })
}

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