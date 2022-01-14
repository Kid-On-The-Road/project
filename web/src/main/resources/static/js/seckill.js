// 查询商品
function selectSeckillGoods(finish) {
    if (finish==='Y') {
        document.getElementById("seckillGoodsButton").style.display = "none";
    }else {
        document.getElementById("seckillGoodsButton").style.display = "inline-block";
    }
    $.ajax({
        type: "POST",
        url: "selectSeckillGoodsList",
        data: {
            "userId":document.getElementById("userId").value
        },
        success: function (result) {
            //将结果在body中刷新
            $("body").html(result);
        }
    });
}

//开始秒杀
function startSeckill() {
    if(document.getElementById("seckillGoodsName").value===''||document.getElementById("seckillGoodsName").value===null){
        toastr.success('暂无可秒杀商品');
        location.reload();
    }else {
        $('#loading').modal('show');
        $.ajax({
            type: "GET",
            url: "startSeckill",
            data: {
                "goodsId": document.getElementById("seckillGoodsName").value,
                "userId": document.getElementById("userId").value
            },
            success: function (result) {
                if (result=== 0) {
                    $('#seckillModal').modal();
                    toastr.success('秒杀已结束!');
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