// 查询商品
function selectGoods() {
    $(".goodsPage").fadeIn();
    const queryCondition = {
        "goodsName": document.getElementById("goodsName").value,
        "goodsCategory": document.getElementById("goodsCategory").value,
        "productionTime": document.getElementById("productionTime").value,
        "seckillStatus": document.getElementById("seckillStatus").value,
        "userId": document.getElementById("userId").value,
    };
    $.ajax({
        type: "POST",
        url: "selectGoodsList",
        data: queryCondition,
        success: function (result) {
            //将结果在body中刷新
            $("body").html(result);
            // $(".goodsManage").html(result);
            $(".goodsPage").fadeOut();
            toastr.success('查询完成');
        }
    });
}

//保存商品
function saveGoods() {
    let goodsId;
    if (document.getElementById("modalGoodsId").value !== "") {
        goodsId = document.getElementById("modalGoodsId").value;
    } else {
        goodsId = 0;
    }
    const data = {
        "goodsId": goodsId,
        "goodsName": document.getElementById("modalGoodsName").value,
        "goodsCategory": document.getElementById("modalGoodsCategory").value,
        "goodsPrice": document.getElementById("modalGoodsPrice").value,
        "goodsNumber": document.getElementById("modalGoodsNumber").value,
        "productionTime": document.getElementById("modalProductionTime").value,
        "validity": "Y",
        "status": "S"
    };
    $.ajax({
        type: "POST",
        url: "saveGoods",
        data: data,
        success: function (result) {
            if (result >= 1) {
                //关闭模态框遮罩
                $(".modal-backdrop").remove();
                //刷新表格数据
                refresh(document.getElementById("userId").value);
                toastr.success('保存成功');
            }
        }
    });
}

// 详情/编辑
function editGoods(goodsId, operation) {
    if (operation === 'add') {
        //删除模态框信息
        $('.modalForm input').val('');
        $('#goodsModalLabel').html('新增商品');
        $('.modalForm').find('input').attr('disabled', false);
        document.getElementById('save').style.display = 'inline-block';
    } else {
        if (operation === 'detail') {
            $('#goodsModalLabel').html('商品详情');
            $('.modalForm').find('input').attr('disabled', true)
            document.getElementById('save').style.display = 'none';
        } else {
            $('#goodsModalLabel').html('修改商品信息');
            $('.modalForm').find('input').attr('disabled', false)
            document.getElementById('save').style.display = 'inline-block';
        }

        $.ajax({
            type: "POST",
            url: "selectGoods",
            data: {
                "goodsId": goodsId
            },
            success: function (result) {
                $('#modalGoodsId').val(result.goodsId);
                $('#modalGoodsName').val(result.goodsName);
                $('#modalGoodsCategory').val(result.goodsCategory);
                $('#modalGoodsPrice').val(result.goodsPrice);
                $('#modalGoodsNumber').val(result.goodsNumber);
                $('#modalProductionTime').val(result.productionTime);
                //刷新表格数据
                // $(".goodsList").load(location.href + " .goodsList>*");
                refresh(document.getElementById("userId").value);
            }
        })
    }
}

// 秒杀状态修改
function seckillStatus(goodsId, status) {
    goodsLoad();
    $.ajax({
        type: "POST",
        url: "seckillStatus",
        data: {
            "goodsId": goodsId,
            "status": status
        },
        success: function (result) {
            refresh(document.getElementById("userId").value);
            $(".goodsPage").fadeOut();
            toastr.success('状态修改成功');
        }
    })
}
function refresh(userId) {
    $.ajax({
        type: "POST",
        url: "selectGoodsList",
        data: {
            "userId": userId
        },
        success: function (result) {
            $(".goodsManage").html(result);
        }
    })
}

//删除商品
function delGoods(goodsId) {
    if (goodsId > 0) {
        document.getElementById('confirm').value = goodsId;
    } else {
        const data = {
            "goodsId": document.getElementById('confirm').value
        };
        $.ajax({
            type: "POST",
            url: "deleteGoods",
            data: data,
            success: function (result) {
                if (result >= 1) {
                    //关闭模态框遮罩
                    $(".modal-backdrop").remove();
                    //刷新表格数据
                    selectGoods();
                    toastr.success('删除成功');
                } else {
                    toastr.error(' 删除失败');
                }
            }
        });
    }
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

// 页面加载动画
function goodsLoad() {
    $(".goodsPage").fadeIn();
};