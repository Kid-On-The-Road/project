// 查询商品
function selectOrderGoods(finish) {
    $("#loadingModal").modal('show');
    if (finish === 'Y') {
        document.getElementById("orderGoodsButton").style.display = "none";
    } else {
        document.getElementById("orderGoodsButton").style.display = "inline-block";
    }
    $.ajax({
        type: "POST",
        url: "selectOrderGoodsList",
        data: {
            "userId": document.getElementById("userId").value,
            "goodsName": document.getElementById("goodsName").value,
            "goodsCategory": document.getElementById("goodsCategory").value,
            "productionTime": document.getElementById("productionTime").value
        },
        success: function (result) {
            //将结果在body中刷新
            $("body").html(result);
        }
    });
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
//查看是否下单
function confirmOrder(goodsId){
    $.ajax({
        type:'POST',
        url : ' selectOrderByIdFromRedis',
        data : {
            "goodsId":goodsId,
            "userId":document.getElementById("userId").value
        },
        success : function(result) {
            if (result===1) {
                toastr.warning("商品已添加购物车")
            }else{
                $('#goodsModal').modal();
                addOrder(goodsId)
            }
        }
    })
}
// 编辑订单信息
function addOrder(goodsId) {
    $('.modalForm input').val('');
    $('#goodsModalLabel').html('加入购物车');
    $.ajax({
        type: "POST",
        url: "selectOrderGoods",
        data: {
            "goodsId": goodsId
        },
        success: function (result) {
            $('#modalGoodsId').val(result.goodsId);
            $('#modalGoodsName').val(result.goodsName);
            $('#modalGoodsCategory').val(result.goodsCategory);
            $('#modalGoodsPrice').val(result.goodsPrice);
            $('#modalProductionTime').val(result.productionTime);
            //刷新表格数据
            $(".goodsList").load(location.href + "?userId=" + document.getElementById("userId").value + " .goodsList>*");
        }
    })
}
//加入购物车
function addShoppingCar() {
    const data = {
        "goodsId": document.getElementById("modalGoodsId").value,
        "userId": document.getElementById("userId").value,
        "orderNumber": document.getElementById("modalGoodsNumber").value
    };
    $.ajax({
        type: "POST",
        url: "addShoppingCar",
        data: data,
        success: function (result) {
            if (result === 1) {
                $(".goodsList").load(location.href + "?userId=" + document.getElementById("userId").value + " .goodsList>*");
                toastr.success('添加购物车成功');
            } else {
                toastr.warning('库存不足');
            }
        }
    });
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