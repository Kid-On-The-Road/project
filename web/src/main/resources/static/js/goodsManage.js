// 查询商品
function selectGoods() {
    $("#loadingModal").modal('show');
    const queryCondition = {
        "goodsName": document.getElementById("goodsName").value,
        "goodsCategory": document.getElementById("goodsCategory").value,
        "productionTime": document.getElementById("productionTime").value,
        "goodsStatus": document.getElementById("goodsStatus").value,
        "userId": document.getElementById("userId").value
    };
    $.ajax({
        type: "POST",
        url: "selectGoodsList",
        data: queryCondition,
        success: function (result) {
            //将结果在body中刷新
            $("body").html(result);
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
                $(".goodsList").load(location.href + "?userId=" + document.getElementById("userId").value + " .goodsList>*");
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
                $(".goodsList").load(location.href + "?userId=" + document.getElementById("userId").value + " .goodsList>*");
            }
        })
    }
}

// 商品状态修改
function goodsStatus(goodsId, status) {
    $("#loadingModal").modal('show');
    $.ajax({
        type: "POST",
        url: "goodsStatus",
        data: {
            "goodsId": goodsId,
            "status": status
        },
        success: function (result) {
            $("#loadingModal").modal('hide');
            $(".goodsList").load(location.href + "?userId=" + document.getElementById("userId").value + " .goodsList>*");
            toastr.success('状态修改成功');
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
            type: "GET",
            url: "deleteGoods",
            data: data,
            success: function (result) {
                if (result >= 1) {
                    //关闭模态框遮罩
                    $(".modal-backdrop").remove();
                    //刷新表格数据
                    $(".goodsList").load(location.href + "?userId=" + document.getElementById("userId").value + " .goodsList>*");
                    toastr.success('删除成功');
                } else {
                    toastr.error(' 删除失败');
                }
            }
        });
    }
}

//初始化头像上传插件
$("#fileinput").fileinput({
    language: 'zh', //设置语言
    allowedFileExtensions: ["xlsx", "xls"],//接收的文件后缀
    uploadUrl: "../importData",
    showUpload: true, //是否显示上传按钮
    showRemove: true, //显示移除按钮
    showPreview: true, //是否显示预览
    showCaption: true,//是否显示标题
    browseClass: "btn btn-primary", //按钮样式
    maxFileCount: 4, //表示允许同时上传的最大文件个数
    maxFileSize: 2000,
    maxFilesNum: 1,
    enctype: 'multipart/form-data',
    autoReplace: true,//替换当前文件
    previewFileIcon: '<i class="fa fa-file-excel-o text-success"></i>',
    msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}！"
}).on("fileuploaded", function (event, data, previewId, index) {
        if (data.response === 1) {
            toastr.success("上传成功");
            console.log(event);
            $(".kv-file-remove").click();  //手动调用删除文件按钮的click方法
        }
    });
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