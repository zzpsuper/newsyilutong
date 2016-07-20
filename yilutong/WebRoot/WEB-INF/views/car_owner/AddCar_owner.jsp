<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML>
<html>
<head>
<base href="<%=basePath%>">

<title>车主信息</title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<jsp:include page="/inc/easyuiLocation.jsp"></jsp:include>
<jsp:include page="../../../ValidationEngine/Validation.jsp"></jsp:include>
	<link rel="stylesheet" type="text/css" href="./css/main.css">
	<link rel="stylesheet" href="./js/select2/select2.css" type="text/css"></link>
	<script type="text/javascript" src="./js/select2/select2.js"></script>
	<script type="text/javascript" src="./js/select2/select2_locale_zh-CN.js"></script>
</head>
<script type="text/javascript">
	$(function(){
		 $("#Regform").validationEngine('attach', { 
				 promptPosition:'topRight:-15,0'
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
				   url: "car_owner.do?method=getPlateNumberLength",
				   data: {number:query.term},   //传递你输入框中的值
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
		});	 		 
 		
	});
	//   		function saveDriver(){
	//   			var myform=document.forms[0];
	//   			myform.action="driver.do?method=saveDriver";
	//   			myform.submit();
	//   		}
	var submitForm = function($dialog, $grid, $pjq, $mainMenu) {
		if($("#Regform").validationEngine('validate')&&($("#carid").val()!="")){
	  			//可提交
		$pjq.messager.confirm('新增车主','确定要新增吗?',function(r){   
	    	if (r){    
				$.ajax({
					type: "POST",
		  			url:'car_owner.do?method=saveCar_owner',
		  			data:$('#Regform').serialize(),
		  			success:function(msg){
		  				if(msg.flag){
		  					$pjq.messager.alert('新增车主','车主添加成功','info'); 
									   $dialog.dialog('close');
									   $grid.datagrid('reload');
		  				};
		  			}
				});
			}
		});
		 }else if($("#carid").val()==""){
			    $pjq.messager.alert('新增车主', '车牌号不可为空', 'info');
			  }
		 else{
			    $pjq.messager.alert('新增车主', '必填信息不可为空', 'info');
			  }
	};
</script>
<body >
	<form action="" method="post" id="Regform">
		<input type="hidden" name="carOwnerId">
		<fieldset>
			<table class="tableclass">
				<tr>
				<th colspan="4">基本信息</th>
				</tr>
				<tr>
					<td><font color="red" style="margin-right:10px">*</font>车主姓名:</td>
					<td class="td2"><input type="text" class="validate[required]" id=""
						name="carOwnerName"></td>

					<td>性别:</td>
					<td class="td2"><select id='sect' class="easyui-combobox" name="gender" style="width:150px;height:22px"
						 data-options="panelHeight : 'auto',editable:false">
							<option id="" value="0">男</option>
							<option id="" value="1">女</option>
					</select></td>
					</tr>
				<tr>
					<td>出生日期:</td>
					<td class="td2"><input id="fxz"  class="Wdate"  readonly="readonly" onfocus="WdatePicker({isShowWeek:true})"
						 name="birthday" /></td>
					<td><font color="red" style="margin-right:10px">*</font>车牌号:</td>
					<td class="td2"><input type="hidden"  id="carid" name="carId"style="width:150px">
					</td>
						</tr>
				<tr>
					<td><font color="red" style="margin-right:10px">*</font>联系方式:</td>
					<td class="td2"><input type="text" class="validate[custom[phone],required,custom[number]]" id="" 
						name="phoneNumber" /></td>

					<td>住址:</td>
					<td class="td2"><input id="fxz" type="text"
						editable="" name="address" /> <input type="hidden" name="" id="">
					</td>
				</tr>
			</table>
		</fieldset>

		<fieldset >
			<table class="tableclass">
			<tr>
					<th colspan="4">身份证信息</th>
				</tr>
				<tr>
					<td>民族:</td>
					<td class="td2"><input type="text" id=""
						name="cardNation" />
					</td>
					<td><font color="red" style="margin-right:10px">*</font>身份证号:</td>
					<td class="td2"><input type="text" class=" validate[required]" id=""
						name="cardNumber" />
					</td>
					</tr>
				<tr>
					<td>签发机关:</td>
					<td class="td2"><input type="text"" id=""
						name="cardIssuingAuthorityAuthority" />
					</td>
					<td>有效日期:</td>
					<td class="td2"><input type="text" id=""
						name="cardValidityDate" />
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset >
			<table class="tableclass">
			<tr>
					<th colspan="4">上岗证信息</th>
				</tr>
				<tr>
					<td>从业资格证件号:</td>
					<td class="td2"><input type="text" id=""
						name="employmentCertifiedCard" />
					</td>
					<td>从业资格类别:</td>
					<td class="td2"><input type="text" id=""
						name="employmentType" />
					</td>
				</tr>
				<tr>
					<td>初次领证日期:</td>
					<td class="td2"><input  class="Wdate "  readonly="readonly" onfocus="WdatePicker({isShowWeek:true})" id=""
						name="employmentLicensingDate" />
					</td>
					<td>发证机关:</td>
					<td class="td2"><input type="text"" id=""
						name="employmentIssuingAuthority" />
					</td>
				</tr>
				<tr>
					<td>发证日期:</td>
					<td class="td2"><input  class="Wdate "  readonly="readonly" onfocus="WdatePicker({isShowWeek:true})" id=""
						name="employmentLicenseDate" />
					</td>
					<td>有效日期:</td>
					<td class="td2"><input type="text" id=""
						name="employmentValidityDate" />
					</td>
				</tr>
			</table>
		</fieldset>

		<fieldset>
			<table class="tableclass">
			<tr>
					<th colspan="4">驾驶证信息</th>
				</tr>	
				<tr>
					<td>国籍:</td>
					<td class="td2"><input type="text" id=""
						name="driverNationality">
					</td>

					<td>驾驶证号:</td>
					<td class="td2"><input id="" type="text"
						editable="false" name="drivingLicense" />
					</td>
					</tr>
				<tr>
					<td>初次领证日期:</td>
					<td class="td2"><input id="fxz"   class="Wdate" readonly="readonly" onfocus="WdatePicker({isShowWeek:true})"
						editable="false" name="drvingLicensingDate" /></td>
					<td>有效日期:</td>
					<td class="td2"><input type="text" id=""
						name="driverLicenseDate" data-options=""></td>
						</tr>
				<tr>
					<td>准驾车型:</td>
					<td class="td2"><input type="text" id=""
						name="drivingType" /></td>

					<td>档案编号:</td>
					<td class="td2"><input id="fxz" type="text"
						editable="" name="driverLicenseArchivesnumber"
						 />
				</tr>
				<tr>
					<td>记录:</td>
					<td class="td2" colspan="3"><textarea class="easyui-textarea" cols="45" rows="2" name="drivingRecord" ></textarea></td>
				</tr>
			</table>
		</fieldset>
	</form>
</body>
</html>
