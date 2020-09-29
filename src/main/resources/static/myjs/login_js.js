
    $(function() {
        $("#b1").click(function () {
            $(".m-item").toggleClass("m-mobile-hide");
        });


        //弹出用户登录窗口
        $('#loginDiv').click(function () {
            $('.ui.modal')
                .modal('show');
        });

        $('.ui.dropdown').dropdown({
            on: 'hover'
        });

        $('#loginBtn').click(function () {
            var targetUrl = $("#loginForm").attr("action");
            var data = $("#loginForm").serialize();
            $.ajax({
                type: 'post',
                url: targetUrl,
                cache: false,
                data: data,
                // dataType: 'json',
                success: function (data) {
                    // alert('success:' + data);
                    //登录成功
                    if (data.success) {
                        // if(data.message == "manager"){
                        //     $('#admin').attr('class','m-item right item m-mobile-hide');
                        //     $('#admin').text('去后台');
                        // }
                        $('.ui.modal')
                            .modal('hide');
                        $('#loginDiv').remove();
                        //刷新局部，但不起作用
                        // $("#userMessageDiv").load("http://localhost:8090/blog/index#index.html");
                        //刷新整个页面
                        window.location.reload();
                        //登录失败
                    } else {
                        $('#message').text('用户名或密码有误');
                    }
                }
            })
        });
    });
