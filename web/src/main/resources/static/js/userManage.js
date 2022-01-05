// 查询用户
function selectUser() {
    $(".userPage").fadeIn();
    const queryCondition = {
        "userName": document.getElementById("userName").value,
        "createTime": document.getElementById("createTime").value,
    };
    $.ajax({
        type: "GET",
        url: "selectUserList",
        data: queryCondition,
        success: function (result) {
            //将结果在body中刷新
            $("body").html(result);
            toastr.success('查询完成');
        }
    });
}

//保存用户
function saveUser() {
    let userId;
    if (document.getElementById("modalUserId").value !== "") {
        userId = document.getElementById("modalUserId").value;
    } else {
        userId = 0;
    }
    const data = {
        "userId": userId,
        "userName": document.getElementById("modalUserName").value,
        "password": document.getElementById("modalPassword").value,
        "validity": "Y"
    };
    $.ajax({
        type: "POST",
        url: "saveUser",
        data: data,
        success: function (result) {
            if (result >= 1) {
                //关闭模态框遮罩
                $(".modal-backdrop").remove();
                //刷新表格数据
                $(".userList").load(location.href + " .userList>*");
                toastr.success('保存成功');
            }
        }
    });
}

// 详情/编辑
function editUser(userId, operation) {
    if (operation === 'add') {
        //删除模态框信息
        $('.modalForm input').val('');
        $('#userModalLabel').html('新增用户');
        $('.modalForm').find('input').attr('disabled', false)
    } else {
        if (operation === 'detail') {
            $('#userModalLabel').html('用户详情');
            $('.modalForm').find('input').attr('disabled', true)
        } else {
            $('#userModalLabel').html('修改用户信息');
            $('.modalForm').find('input').attr('disabled', false)
        }

        $.ajax({
            type: "POST",
            url: "selectUser",
            data: {
                "userId": userId
            },
            success: function (result) {
                $('#modalUserId').val(result.userId);
                $('#modalUserName').val(result.userName);
                $('#modalUserCategory').val(result.userCategory);
                $('#modalUserPrice').val(result.userPrice);
                $('#modalUserNumber').val(result.userNumber);
                $('#modalCreateTime').val(result.createTime);
                //刷新表格数据
                $(".userList").load(location.href + " .userList>*");
            }
        })
    }
}

//删除用户
function delUser(userId) {
    const data = {
        "userId": userId
    };
    $.ajax({
        type: "get",
        url: "deleteUser",
        data: data,
        success: function (result) {
            if (result >= 1) {
                //关闭模态框遮罩
                $(".modal-backdrop").remove();
                //刷新表格数据
                $(".userList").load(location.href + " .userList>*");
                toastr.success('删除成功');
            } else {
                toastr.error(' 删除失败');
            }
        }
    });
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
function  userLoad() {
        $(".userPage").fadeIn();
};