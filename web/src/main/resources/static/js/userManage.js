// 查询用户
function selectUser() {
    $("#loadingModal").modal('show');
    const queryCondition = {
        "userName": document.getElementById("userName").value,
        "createTime": document.getElementById("createTime").value,
        "role": document.getElementById("role").value
    };
    $.ajax({
        type: "POST",
        url: "selectUserList",
        data: queryCondition,
        success: function (result) {
            $("#modalRole option").removeAttr("selected");
            //将结果在body中刷新
            $("body").html(result);
            toastr.success('查询完成');
        }
    });
}

//验证用户名是否存在
function verifyUserName(save) {
    $.ajax({
        type: "POST",
        url: "verifyUserName",
        data: {
            "userName": document.getElementById('modalUserName').value,
            "role": document.getElementById('modalRole').value
        },
        success: function (result) {
            if (result.length !== 0 && document.getElementById('userModalLabel').innerText === '新增用户') {
                $('#modalUserName').val('');
                toastr.warning('用户名已存在,请重新输入!');
            } else if (document.getElementById('modalUserName').value === '' || document.getElementById('modalPassword').value === '') {
                toastr.warning('用户名或密码不能为空!');
            } else if (save === 'save') {
                saveUser();
            }
        }
    })
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
        "role": document.getElementById("modalRole").value,
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
                $(".userList").load(location.href+"?userId="+document.getElementById("userId").value+ " .userList>*");
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
        $('.form-control').attr('disabled', false);
        document.getElementById('save').style.display = 'inline-block';
    } else {
        if (operation === 'detail') {
            $('#userModalLabel').html('用户详情');
            $('.form-control').attr('disabled', true);
            document.getElementById('save').style.display = 'none';
        } else {
            $('#userModalLabel').html('修改用户信息');
            $('.form-control').attr('disabled', false);
            document.getElementById('save').style.display = 'inline-block';
        }

        $.ajax({
            type: "GET",
            url: "selectUser",
            data: {
                "userId": userId
            },
            success: function (result) {
                $('#modalUserId').val(result.userId);
                $('#modalUserName').val(result.userName);
                $('#modalPassword').val(result.password);
                $('#modalCreateTime').val(result.createTime);
                $("#modalRole option").removeAttr("selected");
                $('#modalRole').find("option[value=" + result.role + "]").attr("selected", "selected");
                //刷新表格数据
                $(".userList").load(location.href+"?userId="+document.getElementById("userId").value+ " .userList>*");
            }
        })
    }
}

//删除用户
function delUser(userId) {
    if (userId > 0) {
        document.getElementById('confirm').value = userId;
    } else {
        const data = {
            "userId": document.getElementById('confirm').value
        };
        $.ajax({
            type: "GET",
            url: "deleteUser",
            data: data,
            success: function (result) {
                if (result >= 1) {
                    //关闭模态框遮罩
                    $(".modal-backdrop").remove();
                    //刷新表格数据
                    $(".userList").load(location.href+"?userId="+document.getElementById("userId").value+ " .userList>*");
                    toastr.success('删除成功');
                } else {
                    toastr.error(' 删除失败');
                }
            }
        });
    }
}

function changeStatus() {
    $('#save').attr('disabled', false);
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