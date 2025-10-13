// 1. 현재 연도 가져오기
const currentYear = new Date().getFullYear();

// 2. 년도 범위 설정
const startYear = 1950; // 시작 연도
const endYear = currentYear; // 종료 연도

// 3. select 요소 가져오기
const selectYear = document.getElementById('selectYear');

// 4. 반복문으로 option 생성 및 추가
for (let i = startYear; i <= endYear; i++) {
    // 새로운 option 요소 생성
    const option = document.createElement('option');

    // option의 value와 화면에 표시될 텍스트 설정
    option.value = i;
    option.text = i + '년';

    // select 요소에 option 추가
    selectYear.appendChild(option);
}

// 월 생성 12월까지
const selectMonth = document.getElementById('selectMonth');
for (let i = 1; i <= 12; i++) {
    const option = document.createElement('option');
    // 앞에 0 붙이기
    const displayValue = String(i).padStart(2, '0');

    option.value = displayValue;
    option.text = displayValue + '월';
    selectMonth.appendChild(option);
}

// 일 생성 31일까지
const selectDay = document.getElementById('selectDay');
for (let i = 1; i <= 31; i++) {
    const option = document.createElement('option');
    // 앞에 0 붙이기
    const displayValue = String(i).padStart(2, '0');

    option.value = displayValue;
    option.text = displayValue + '일';
    selectDay.appendChild(option);
}


function submit_check() {
    document.join_form.submit();
}

function id_check() {
    var userid = $('#userid').val();

    if(userid.length < 4)
    {
        var msg = "<font color='black'>아이디 : 4글자 이상</font>";
        $('#id_msg').html(msg);
        return;
    }

    $.ajax({
        url : "/idCheck",
        type : "get",
        data : {userid: userid},
        dataType : 'html',
        success : function(result){

            var data = result.trim();

            if(data == "y")
            {
                var msg = "<font color='blue'>아이디 사용 가능</font>";
                $('#id_msg').html(msg);
            }
            else
            {
                var msg = "<font color='red'>아이디 사용 불가</font>";
                $('#id_msg').html(msg);
            }
        },
        error: function (request, status, error) {
            console.log("code: " + request.status)
            console.log("message: " + request.responseText)
            console.log("error: " + error);
        }
    });
    return;
}

function pwd_check() {
    var pwd1 = $('#pwd1').val();
    var pwd2 = $('#pwd2').val();

    if(pwd1.length < 4 || pwd2.length < 4)
    {
        $('#pass_msg').html("<font color='black'>비밀번호 : 4자리 이상</font>");
        return;
    }

    if (pwd1 == pwd2) {
        $('#pass_msg').html("<font color='blue'>비밀번호 확인됨</font>");
    }else {
        $('#pass_msg').html("<font color='red'>비밀번호 서로 다름</font>");
    }
}
