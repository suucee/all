
$(document).ready(function(){

	$.jsonRPC.request("sessionService.getQuestions",{
		params:[],
		success:function(data){
			if (data!=null) {
				$(".id").html(data.id);
				$(".name").val(data.userName);
				$(".email").val(data.userEmail);
				$(".phone").val(data.userPhone);
				$(".state").val(data.state);
				$(".questionType").val(data.questionType);
				$(".questionContext").val(data.questionContext);
				$(".addtime").val(toDate(data.creatTime.time));
				$(".donetime").val(toDate(data.doneTime.time));
			}
		}
    });
	
});

