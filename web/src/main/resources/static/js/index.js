// 登录
function login() {
    if (document.getElementById('userName').value===''||document.getElementById('passWord').value ==='') {
        toastr.warning('用户名或密码为空!')
    }else {
        const userInfo = {
            "userName": document.getElementById("userName").value,
            "password": document.getElementById("passWord").value,
            "role":document.getElementById("role").value
        };
        $.ajax({
            type: "GET",
            url: "login",
            data: userInfo,
            success: function (result) {
                if (result.length>0&&result[0].role === 'S') {
                    toastr.success('登录成功');
                    location.href = "selectGoodsList";
                } else if(result.length>0&&result[0].role === 'A'){
                    toastr.success('登录成功');
                    location.href = "selectUserList";
                }  else if(result.length>0&&result[0].role === 'B'){
                    toastr.success('登录成功');
                    location.href = "selectSeckillGoodsList";
                } else {
                    toastr.warning('用户名或密码错误请重新输入!')
                }
            }
        });
    }
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