<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
	String realPath1 = "http://" + request.getServerName() + ":" + request.getServerPort() +"/surmax/"; 
 %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>修改明细表</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	
	<jsp:include page="/inc/easyuiLocation.jsp"></jsp:include>
	<jsp:include page="../../../ValidationEngine/Validation.jsp"></jsp:include>
	<link rel="stylesheet" type="text/css" href="./css/main.css">
	<link rel="stylesheet" href="./js/select2/select2.css" type="text/css"></link>
	<script type="text/javascript" src="./js/select2/select2.js"></script>
	<script type="text/javascript" src="./js/select2/select2_locale_zh-CN.js"></script>
	
  
  </head>
  <%String flg = (String) request.getAttribute("flg"); %>
  <script type="text/javascript">
	  	var dialog;
 		var grid;
  		$(function(){
  			$("#Regform").validationEngine('attach', { 
				 promptPosition:'topRight:-15,0'
				 });
		var pid='<%=flg%>';
		$.ajax({
			url : 'detailed.do?method=getUpdateDetailed',
			data : {
				id : pid
			},
			success : function(data) {
				$('#Regform').form('load', data);
			}
		});

		$("#carid").select2({
		    placeholder: "请输入车牌号",	//默认文本框显示信息
		    minimumInputLength: 1,	//最小输入字符长度
		    allowClear:true,
		    multiple: false,				//设置多项选择，false则只能单选
		    maximumSelectionSize: 5, //最大选择个数
			query: function (query) {
		    	$.ajax({
				   type: "POST",
				   url: "incomedp.do?method=getPlateNumber",
				   data: {phones:query.term},   //传递你输入框中的值
				   success: function(msg){
		 		   	var msg=$.parseJSON(msg);		
		 			var data = {results: []};		
					$.each(msg,function(x,y){
						data.results.push({id: y.id, text: y.name});;
					});
			        query.callback(data);
				   }
				});
		    }
		}).on("select2-selecting", function(f) {
			$("#car").val(f.object.id);
			$("#carnum").val(f.object.text);				
		});	 		  	
  	}); 		
  var submitFormEdit = function($dialog, $grid, $pjq, $mainMenu) {
		if($("#Regform").validationEngine('validate')){
	  			//可提交
		$pjq.messager.confirm('编辑费用', '确定要编辑吗?', function(r) {
			if (r) {
				$.ajax({
					type : "POST",
					url : 'detailed.do?method=addDetailed',
					data : $('#Regform').serialize(),
					success : function(msg) {
						if (msg.flag) {
							$pjq.messager.alert('费用编辑添加', '费用编辑成功', 'info');
							$dialog.dialog('close');
							$grid.datagrid('reload');
						};
					}
				});  
			} 
		});
		} else{
			    $pjq.messager.alert('费用添加', '必填信息不可为空', 'info');
			  }
	};
  </script>
  <body class="easyui-layout">
    <form action="" method="post" id="Regform">
  <div region="west" border="true" split="true">
    	
					<table class="tableclass">
						<tr>
							<th colspan="4">信息修改</th>
						</tr>
					<tr>
					<td><font color="red" style="margin-right:10px">*</font>订单号:</td>
							<td class="td2"><input type="text" readonly="true"
								class="validate[required]" id="num" name="shiping_order_num" >
							</td>
					
						<td>送货费：</td>
							<td class="td2"><input id="send_pay" type="text"
								class="validate[custom[number],min[0]max[99999],custom[dd]]" placeholder="0.00"
								value="0.00" name="send_pay" />
							</td>
					</tr>
					<tr>
							<td>落地费：</td>
							<td class="td2"><input id="land_total" type="text"
								class="validate[custom[number],min[0],max[99999],custom[dd]]" placeholder="0.00"
								value="0.00" name="land_total" />
							</td>
						<input type="hidden" name="detailed_id"  >
						<input type="hidden" name="shiping_order_id"  >
							<td>中转费：</td>
							<td class="td2"><input id="change_fee10" type="text"
								class="validate[custom[number],min[0]max[99999],custom[dd]]" placeholder="0.00"
								value="0.00" name="change_fee10" />
							</td>
						</tr>
					</table>
					</div>
		  </form>	
  </body>
</html>