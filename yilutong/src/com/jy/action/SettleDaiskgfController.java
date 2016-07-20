package com.jy.action;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jy.common.ExportUtils;
import com.jy.common.SessionInfo;
import com.jy.common.UUIDUtils;
import com.jy.dao.Json;
import com.jy.model.Agencyfund;
import com.jy.model.Customer;
import com.jy.model.SettlGFSh;
import com.jy.model.Settle_historyGF;
import com.jy.model.ShippingOrder;
import com.jy.model.User;
import com.jy.service.SettleDaiskgfService;

/*
 * 运费结算
 * */
@Controller
@RequestMapping(value = "settleDaiskgf.do")
public class SettleDaiskgfController {
	
	@Resource
	private SettleDaiskgfService bid;
	//运费结算记录主页面
	@RequestMapping(params="method=setlRcordInfo")
	public String setlRcordInfo(HttpServletRequest request,String pid,String state){
		request.setAttribute("pid", pid);
		request.setAttribute("state", state);
		return "settlementGF/setlRcordInfo";
	}
	//运费结算审核历史记录主页面settlementGFSH
		@RequestMapping(params="method=settlementShRC")
		public String settlementShRC(HttpServletRequest request,String pid){
			request.setAttribute("pid", pid);
			return "settlementGFSH/shrcordInfo";
		}
	//运费结算审核主页面
	@RequestMapping(params="method=settlementGFSH")
	public String settlementGFSH(){
		return "settlementGFSH/settlementDsShInfo";
	}
	//运费结算主页面
	@RequestMapping(params="method=settlementDsPage")
	public String getSettlementDsSearchPage(HttpServletRequest request,String pid){
		request.setAttribute("pid", pid);
		return "settlementGF/settlementDsInfo";
	}
	
	//客户查询页面 customerSettlInfo.jsp
	@RequestMapping(params="method=customerSettlInfo")
	public String customerSettlInfo(HttpServletRequest request,String pid){
		request.setAttribute("pid", pid);
		return "settlementGF/customerSettlInfo";
	}
	//跳转到提审页面
	@RequestMapping(params="method=thSettlement")
	public String thSettlement(HttpServletRequest request,String []pid){
		request.setAttribute("pid", pid);
		System.out.println(pid.length);
		return "settlementGF/thSettlement";
	}
	//跳转到单个审核页面
		@RequestMapping(params="method=shSettlement")
		public String shSettlement(HttpServletRequest request,String []pid){			
			request.setAttribute("pid", pid[0]);
			return "settlementGFSH/shSettlement";
		}
	//跳转到结算运费页面
	@RequestMapping(params="method=edSettlement")
	public String edSettlement(HttpServletRequest request,String []pid){
		request.setAttribute("pid", pid);
		return "settlementGF/EditsettlementDs";
	}
	//运费结算查询
	@RequestMapping(params = "method=getSettlementYf")
	public @ResponseBody
	Map<String,Object> getSettlementYfInfo(String rows, String page,String ddId, String types, String isdelivery, String start_date,
			String end_date, String start_date1, String end_date1,String start_date2, String end_date2, String kname) {
		//当前第几页
				Integer page1 = 1;
				//当前页一共几条
				Integer rows1 = 0;
		if (rows != null && !"".equals(rows)) {
			rows1 = Integer.parseInt(rows);
		}
		if (page != null && !"".equals(page)) {
			page1 = Integer.parseInt(page);
		}
		List<ShippingOrder> list = bid.getSettlementYfInfo((page1 - 1) * rows1,rows1 , ddId, types, isdelivery, start_date, end_date, start_date1, end_date1, start_date2, end_date2, kname);
		int count = bid.getSettlementYf(ddId, types, isdelivery, start_date, end_date, start_date1, end_date1, start_date2, end_date2, kname);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", count);
		map.put("rows", list);
//		JSONObject json=new JSONObject();
//		json.put("rows", list);
//		System.out.println(json.toString());
		return map;
	}
	//审核信息保存
	public List<SettlGFSh> saveSettlyfSh(String [] orderid,String checks_type,String checks_notes,String checks_state,String checks_user,String  settlyf_sf,List<Settle_historyGF>listSet ,String did) {
		List<SettlGFSh> list=new ArrayList<SettlGFSh>();
		for(String id:orderid){
			SettlGFSh s=new SettlGFSh();
			for(int i=0;i<listSet.size();i++){
				if(listSet.get(i).getOrder_id().equals(id)){
					
					s.setSettlyf_id(listSet.get(i).getSettlements_id());
					listSet.remove(i);
				}
			}
			s.setSettlementids(UUIDUtils.getUUID());
			s.setOrderids(id);
			s.setSettlyf_type(checks_type);
			s.setSettlyf_state(checks_notes);
			s.setSettlyf_state(checks_state);
			s.setSettlyf_user(checks_user);
			s.setSettlyf_remaks(checks_notes);
			s.setSettlyf_sf(settlyf_sf);
			s.setSettle_th_did(did);
			list.add(s);
		}
		return list;
	}
	//运费结算
	public Map<String,Object> saveSettlementYf(String[] dds,String type, String remarks,String khbank,String usernames,String kahao,String zpnum,String userid,String state) {	
		List<Settle_historyGF> list=new ArrayList<Settle_historyGF>();
		List<ShippingOrder> order= bid.getSettlementYfout(null, null, null, null, null, null, null, null, null, null,dds);
		for(int i=0;i<order.size();i++){
			Settle_historyGF sh=new Settle_historyGF();
			sh.setSettlements_id(UUIDUtils.getUUID());
			if(order.get(i).getPaytypes().equals("0")){
				sh.setAllmoney(order.get(i).getTrade_agency());				
			}else{
				sh.setAllmoney(Float.parseFloat(order.get(i).getTrade_agency())-Float.parseFloat(order.get(i).getSettlgf_fu().split(",")[1])+"" );
			}
			sh.setOrder_id(order.get(i).getShiping_order_id());	
			if(order.get(i).getPaytypes().equals("0")){
				sh.setSettl_money(order.get(i).getPaidgf_fu());				
			}else{
				sh.setSettl_money(Float.parseFloat(order.get(i).getPaidgf_fu())-Float.parseFloat(order.get(i).getSettlgf_fu().split(",")[0])+"");
			}
			sh.setOver_money(order.get(i).getTrade_cha());					
			sh.setSettl_user(userid);
			sh.setSettl_username(usernames);
			sh.setSettl_kahao(kahao);
			sh.setSettl_khbank(khbank);
			sh.setSettl_notes(remarks);
			sh.setSettl_type(type);		
			sh.setSettl_order(state);
			sh.setSettl_zpnum(zpnum);
			list.add(sh);
			if(order.get(i).getPaytypes().equals("0")){
				order.get(i).setSettlgf_fu(order.get(i).getPaidgf_fu()+","+0);		
			}else{
				order.get(i).setSettlgf_fu(order.get(i).getPaidgf_fu()+","+order.get(i).getSettlgf_fu().split(",")[0]);
			}		
					}		
		Map< String, Object> map =new HashMap<String,Object>();
		map.put("list", list);
		map.put("order", order);
		return map;
	}
	
	/*
	 * 结算运费实际应收
	 */
	@RequestMapping(params = "method=changePrice")
	public @ResponseBody
	Json updateSettlementYf(String dds, String price,HttpServletRequest request) {
		User user=(User) request.getSession().getAttribute(SessionInfo.SessionName);
		Json json = new Json();
		int truck=bid.updateSettlementYf(dds,price);
		if (truck > 0) {
			json.setFlag(true);
			return json;
		}
		json.setFlag(false);
		return json;
	}
	

	/*
	 * 结算运费
	 */
	@RequestMapping(params = "method=addSettlementYf")
	public @ResponseBody
	Json addSettlementYf(String[] dds,String type, String remarks,String khbank,String usernames,String kahao,
			HttpServletRequest request,String zpnum) {
		User user=(User) request.getSession().getAttribute(SessionInfo.SessionName);
		Json json = new Json();
		//保存结算记录
		bid.saveSettlchecks((List<Settle_historyGF>) saveSettlementYf(dds, type, remarks, khbank, usernames, kahao, zpnum, user.getId(),"0").get("list"));
		bid.saveSettl_fu((List<ShippingOrder>) saveSettlementYf(dds, type, remarks, khbank, usernames, kahao, zpnum, user.getId(),"0").get("order"));		
		int truck=bid.addSettlementYf(dds, type,user.getId(), remarks,khbank, usernames, kahao, zpnum);
		if (truck > 0) {
			json.setFlag(true);
			return json;
		}
		json.setFlag(false);
		return json;
	}
	
	/*
	 * 结算运费提审
	 */
	@RequestMapping(params = "method=delSettlementYf")
	public @ResponseBody
	Json delSettlementYf(String[] dds,String did,String notes,HttpServletRequest request) {
		User user=(User) request.getSession().getAttribute(SessionInfo.SessionName);
		Json json = new Json();		
		List<Settle_historyGF> list=bid.getupdateSettle("jy_settlemengf_history", 0, dds);//最新结算记录
		System.out.println("list"+list.size());
		bid.updateSettle(list,"jy_settlemengf_history","1");//修改结算记录审核状态
		bid.saveSettlGFSh(saveSettlyfSh(dds, "0", notes,"2", user.getId(),"0",list,did));
		System.out.println("是是是"+dds.length);
		int truck=bid.delSettlementYf(dds, did, user.getId(), notes);	
		if (truck > 0) {
			
			json.setFlag(true);
			return json;
		}
		json.setFlag(false);
		return json;
	}
	
	
	//运费结算审核查询
		@RequestMapping(params = "method=getSettlementYfSHInfo")
		public @ResponseBody
		Map<String,Object> getSettlementYfSHInfo(String rows, String page,String ddId, String types, String isdelivery, String start_date,
				String end_date, String start_date1, String end_date1,String start_date2, String end_date2, String kname) {
			//当前第几页
					Integer page1 = 1;
					//当前页一共几条
					Integer rows1 = 0;
			if (rows != null && !"".equals(rows)) {
				rows1 = Integer.parseInt(rows);
			}
			if (page != null && !"".equals(page)) {
				page1 = Integer.parseInt(page);
			}
			List<ShippingOrder> list = bid.getSettlementYfSHInfo((page1 - 1) * rows1,rows1 , ddId, types, isdelivery, start_date, end_date, start_date1, end_date1, start_date2, end_date2, kname);
			int count = bid.getSettlementYfSH(ddId, types, isdelivery, start_date, end_date, start_date1, end_date1, start_date2, end_date2, kname);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("total", count);
			map.put("rows", list);
//			JSONObject json=new JSONObject();
//			json.put("rows", list);
//			System.out.println(json.toString());
			return map;
		}
	/*
	 * 结算运费审核通过
	 */
	@RequestMapping(params = "method=settlementYfTG")
	public @ResponseBody
	Json shSettlementYfTG(String[] ids,String notes,HttpServletRequest request) {
		User user=(User) request.getSession().getAttribute(SessionInfo.SessionName);
		Json json = new Json();
		List<Settle_historyGF> list=bid.getupdateSettle("jy_settlemengf_history", 0, ids);
		bid.updateSettle(list,"jy_settlemengf_history","2");
		bid.saveSettlGFSh(saveSettlyfSh(ids, "1", notes,"1", user.getId(),"0",list,user.getDid()));
		int truck=bid.shSettlementYf(ids, "2", user.getId(), notes);		
		System.out.println("返回的参数"+truck);
		if (truck > 0) {		
			json.setFlag(true);
			return json;
		}
		json.setFlag(false);
		return json;
	}
	
	/*
	 * 结算运费审核驳回
	 */
	@RequestMapping(params = "method=settlementYfBH")
	public @ResponseBody
	Json shSettlementYfBH(String[] ids,String notes,HttpServletRequest request) {
		User user=(User) request.getSession().getAttribute(SessionInfo.SessionName);
		Json json = new Json();
		//驳回
		List<ShippingOrder> order= bid.getSettlementYfout(null, null, null, null, null, null, null, null, null, null,ids);	
		for(int i=0;i<order.size();i++){			
				order.get(i).setSettlgf_fu(order.get(i).getSettlgf_fu().split(",")[1]+","+order.get(i).getSettlgf_fu().split(",")[1]);
		}
		bid.saveSettl_fu(order);
		
		List<Settle_historyGF> list=bid.getupdateSettle("jy_settlemengf_history", 0, ids);
		bid.updateSettle(list,"jy_settlemengf_history","3");
		bid.saveSettlGFSh(saveSettlyfSh(ids, "1", notes,"1", user.getId(),"0",list,user.getDid()));
		int truck=bid.shSettlementYf(ids, "3", user.getId(), notes);		
		if (truck > 0) {			
			json.setFlag(true);
			return json;
		}
		json.setFlag(false);
		return json;
	}
	
/*//车牌检索
	@RequestMapping(params = "method=getCar_idLength")
	public @ResponseBody
	String getCar_idLength(String plate_number) {
		List<CityAgreement> list = bid.getCar_idLength(plate_number);
		JSONObject jb = null;
		JSONArray jsons = new JSONArray();
		for (CityAgreement d : list) {
			jb = new JSONObject();
			jb.put("id", d.getId());
			jb.put("name", d.getCar_id());
			jsons.add(jb);
		}
		return jsons.toString();
	}*/
	
	//运费结算记录查询
			@RequestMapping(params = "method=getSettlementYfRc")
			public @ResponseBody
			Map<String,Object> getSettlementYfRc(String rows, String page,String start_date, String end_date,
					String ddids) {
				//当前第几页
						Integer page1 = 1;
						//当前页一共几条
						Integer rows1 = 0;
				if (rows != null && !"".equals(rows)) {
					rows1 = Integer.parseInt(rows);
				}
				if (page != null && !"".equals(page)) {
					page1 = Integer.parseInt(page);
				}
				List<Agencyfund> list = bid.getSettlementYfRc((page1 - 1) * rows1,rows1 , start_date, end_date, ddids);
				int count = bid.getSettlementYfRcs(start_date, end_date, ddids);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("total", count);
				map.put("rows", list);
//				JSONObject json=new JSONObject();
//				json.put("rows", list);
//				System.out.println(json.toString());
				return map;
			}
			
			//运费结算审核记录查询
			@RequestMapping(params = "method=getSettleShRc")
			public @ResponseBody
			Map<String,Object> getSettleShRc(String rows, String page,String start_date, String end_date,
					String ddids) {
				//当前第几页
						Integer page1 = 1;
						//当前页一共几条
						Integer rows1 = 0;
				if (rows != null && !"".equals(rows)) {
					rows1 = Integer.parseInt(rows);
				}
				if (page != null && !"".equals(page)) {
					page1 = Integer.parseInt(page);
				}
				List<SettlGFSh> list = bid.getSettleShRc((page1 - 1) * rows1,rows1 , start_date, end_date, ddids);
				int count = bid.getSettleShRcs(start_date, end_date, ddids);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("total", count);
				map.put("rows", list);
//				JSONObject json=new JSONObject();
//				json.put("rows", list);
//				System.out.println(json.toString());
				return map;
			}
			
			//运费导出
			@RequestMapping(params = "method=getSettlementYfout")
			public @ResponseBody List<ShippingOrder> getSettlementYfout(HttpServletResponse response,String[] headtitle, String[] fieldName,String ddId, String types, String isdelivery, String start_date,
					String end_date, String start_date1, String end_date1,String start_date2, String end_date2, String kname,String [] checkarray){
				String filename = "代收款结算信息导出";	
				List<ShippingOrder> list;
				if(checkarray!=null&&checkarray.length>0){
					list= bid.getSettlementYfout(null, null, null, null, null, null, null, null, null, null,checkarray);					
				}else{
					list= bid.getSettlementYfout(ddId, types, isdelivery, start_date, end_date, start_date1, end_date1, start_date2, end_date2, kname,null);
					
				}
					/*	for (ShippingOrder so : list) {
					if (so!=null) {				
					if (so.getSend_type()!=null&&so.getSend_type()!="null"&&so.getSend_type().equals("0")) {
						so.setSend_type("自提");
					} if (so.getSend_type()!=null&&so.getSend_type()!="null"&&so.getSend_type().equals("1")) {
						so.setSend_type("送货");
					} if (so.getShipping_order_state()!=null&&so.getShipping_order_state().equals("0")) {
						so.setShipping_order_state("受理");
					}  if(so.getShipping_order_state()!=null&&so.getShipping_order_state().equals("1")){
						so.setShipping_order_state("在途");
					}  if(so.getShipping_order_state()!=null&&so.getShipping_order_state().equals("2")){
						so.setShipping_order_state("到达");
					}  if(so.getShipping_order_state()!=null&&so.getShipping_order_state().equals("4")){
						so.setShipping_order_state("签收");
					} 		 if (so.getIs_receive().equals("0")) {
						so.setIs_receive("未接收");
					}  if (so.getIs_receive().equals("1")) {
						so.setIs_receive("已接收");
					}
				   if (so.getIs_send().equals("0")) {
						so.setIs_send("未寄出");
					} 
				   if (so.getIs_send().equals("1")) {
						so.setIs_send("已寄出");
				}
			    }	
				}*/
				try {			
					// 声明一个工作薄
					HSSFWorkbook workbook = new HSSFWorkbook();
					// 生成一个表格
					response.setContentType("application/vnd.ms-excel;");
					response.setHeader("Content-Disposition", "attachment;filename=\""
							+ new String(filename.getBytes("gbk"), "iso-8859-1")
							+ ".xls\"");
					OutputStream ouputStream = response.getOutputStream();
					// 调用工具类创建表头
					ExportUtils.outputHeaders(headtitle, workbook, "代收款结算信息导出");
					// 调用工具类生成内容
					ExportUtils.outputColums(fieldName, list, workbook, 1, "代收款结算信息导出");
					workbook.write(ouputStream);
					ouputStream.flush();
					ouputStream.close();
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;	
			}
				
				
				//客户查询
			@RequestMapping(params = "method=getCustomerSetYf")
			public @ResponseBody Map<String,Object> getCustomerSetYf(String rows, String page,String customer_name, String customer_num,String js_time,String js_times,String chayi,String order) {
					//当前第几页
				Integer page1 = 1;
							//当前页一共几条
							Integer rows1 = 0;
					if (rows != null && !"".equals(rows)) {
						rows1 = Integer.parseInt(rows);
					}
					if (page != null && !"".equals(page)) {
						page1 = Integer.parseInt(page);
					}
					List<Customer> list = bid.getCustomerSetYf((page1 - 1) * rows1,rows1,customer_name, customer_num,js_time,js_times,chayi,order);
					int count = bid.getCustomerSetYfs(customer_name, customer_num,js_time,js_times,chayi,order);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("total", count);
					map.put("rows", list);
//					JSONObject json=new JSONObject();
//					json.put("rows", list);
//					System.out.println(json.toString());
					return map;
				}
			//运费客户导出
			@RequestMapping(params = "method=getCustomerSetYfout")
			public @ResponseBody List<ShippingOrder> getCustomerSetYfout(HttpServletResponse response,String[] headtitle, String[] fieldName,String customer_name, String customer_num,String js_time,String js_times,String chayi,String order, String [] checkarray){
				String filename = "客户代收款结算信息导出";	
				List<Customer> list;
				if(checkarray!=null&&checkarray.length>0){
					list= bid.getCustomerSetYfout(null, null,null,null,chayi,order,checkarray);					
				}else{
					list= bid.getCustomerSetYfout(customer_name,customer_num,js_times,js_time,chayi,order,null);
					
				}					
				try {			
					// 声明一个工作薄
					HSSFWorkbook workbook = new HSSFWorkbook();
					// 生成一个表格
					response.setContentType("application/vnd.ms-excel;");
					response.setHeader("Content-Disposition", "attachment;filename=\""
							+ new String(filename.getBytes("gbk"), "iso-8859-1")
							+ ".xls\"");
					OutputStream ouputStream = response.getOutputStream();
					// 调用工具类创建表头
					ExportUtils.outputHeaders(headtitle, workbook, "客户代收款结算信息导出");
					// 调用工具类生成内容
					ExportUtils.outputColums(fieldName, list, workbook, 1, "客户代收款结算信息导出");
					workbook.write(ouputStream);
					ouputStream.flush();
					ouputStream.close();
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;	
			}
					
			//	查询
			@RequestMapping(params="method=getSettlout")
			public @ResponseBody ShippingOrder getMsgContent(String []id){
				List<ShippingOrder> list= bid.getSettlementYfout(null, null, null, null, null, null, null, null, null, null,id);	
				return list.get(0);
			}
			//运费审核导出
			@RequestMapping(params = "method=getSettlementYfSHout")
			public @ResponseBody List<ShippingOrder> getSettlementYfSHout(HttpServletResponse response,String[] headtitle, String[] fieldName,String ddId, String types, String isdelivery, String start_date,
					String end_date, String start_date1, String end_date1,String start_date2, String end_date2, String kname,String [] checkarray){
				String filename = "代收运费结算审核信息导出";	
				List<ShippingOrder> list;
				if(checkarray!=null&&checkarray.length>0){
					list= bid.getSettlementYfSHout(null, null, null, null, null, null, null, null, null, null,checkarray);					
				}else{
					list= bid.getSettlementYfSHout(ddId, types, isdelivery, start_date, end_date, start_date1, end_date1, start_date2, end_date2, kname,null);
					
				}				
				try {			
					// 声明一个工作薄
					HSSFWorkbook workbook = new HSSFWorkbook();
					// 生成一个表格
					response.setContentType("application/vnd.ms-excel;");
					response.setHeader("Content-Disposition", "attachment;filename=\""
							+ new String(filename.getBytes("gbk"), "iso-8859-1")
							+ ".xls\"");
					OutputStream ouputStream = response.getOutputStream();
					// 调用工具类创建表头
					ExportUtils.outputHeaders(headtitle, workbook, "代收运费结算审核信息导出");
					// 调用工具类生成内容
					ExportUtils.outputColums(fieldName, list, workbook, 1, "代收运费结算审核信息导出");
					workbook.write(ouputStream);
					ouputStream.flush();
					ouputStream.close();
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;	
			}
	
			//运费记录查询
		@RequestMapping(params = "method=getSettleRc")
		public @ResponseBody Map<String,Object> getSettleRc(String rows, String page,String order_id, String settl_order) {
				//当前第几页
			Integer page1 = 1;
						//当前页一共几条
						Integer rows1 = 0;
				if (rows != null && !"".equals(rows)) {
					rows1 = Integer.parseInt(rows);
				}
				if (page != null && !"".equals(page)) {
					page1 = Integer.parseInt(page);
				}
				List<Settle_historyGF> list = bid.getSettleRc((page1 - 1) * rows1,rows1,order_id, settl_order);
				int count = bid.getSettleRcs(order_id, settl_order);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("total", count);
				map.put("rows", list);
//				JSONObject json=new JSONObject();
//				json.put("rows", list);
//				System.out.println(json.toString());
				return map;
			}
}
