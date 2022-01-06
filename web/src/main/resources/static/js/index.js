// 登录
function login() {
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
            if (result.length>0&&result[0].userName === 'admin') {
                toastr.success('登录成功');
                location.href = "selectGoodsList";
            } else {
                toastr.success('用户名或密码错误请重新输入!')
            }
        }
    });
}