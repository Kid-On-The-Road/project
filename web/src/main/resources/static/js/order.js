// 查询商品
function selectOrderGoods(finish) {
    if (finish==='Y') {
        document.getElementById("orderGoodsButton").style.display = "none";
    }else {
        document.getElementById("orderGoodsButton").style.display = "inline-block";
    }
    $.ajax({
        type: "POST",
        url: "selectOrderGoodsList",
        data: {
            "userId":document.getElementById("userId").value
        },
        success: function (result) {
            //将结果在body中刷新
            $("body").html(result);
        }
    });
}

//下单
function startOrder() {
    if(document.getElementById("orderGoodsName").value===''||document.getElementById("orderGoodsName").value===null){
        toastr.success('暂无商品');
        location.reload();
    }else {
        $('#loading').modal('show');
        $.ajax({
            type: "GET",
            url: "startOrder",
            data: {
                "goodsId": document.getElementById("orderGoodsName").value,
                "userId": document.getElementById("userId").value
            },
            success: function (result) {
                $('#orderModal').modal();
                if (result=== 0) {
                    toastr.success('成功抢到商品!');
                }else {
                    toastr.success('未能抢到商品,试试其他的!');
                }
                $('#loading').modal('hide');
            }
        });
    }
}
//购物车
function shopping() {
    $.ajax({
        type: "GET",
        url: "selectShoppingGoods",
        data: {
            "userId": document.getElementById("userId").value
        },
        success: function (result) {
            //将结果在body中刷新
            $("body").html(result);
        }
    })
}

// 页面加载动画
function goodsLoad() {
    $(".goodsPage").fadeIn();
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