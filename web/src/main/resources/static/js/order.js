// 查询商品
function selectOrderGoods(finish) {
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
            "goodsName": document.getElementById("userId").value,
            "goodsCategory": document.getElementById("userId").value,
            "productionTime": document.getElementById("userId").value
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
            }else if (result === 2) {
                toastr.warning('购物车中已存在相同商品!')
            }else {
                toastr.warning('添加购物车失败');
            }
        }
    });
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