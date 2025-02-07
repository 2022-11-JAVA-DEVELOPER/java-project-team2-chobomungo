package com.itwill.chobomungo.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.itwill.chobomungo.cart.Cart;
import com.itwill.chobomungo.cart.CartDao;
import com.itwill.chobomungo.product.Product;
import com.itwill.chobomungo.product.ProductDao;
import com.itwill.chobomungo.user.User;

public class OrderService {
	OrderDao orderDao;
	CartDao cartDao;
	ProductDao productDao;
	
	public OrderService() throws Exception {
		orderDao = new OrderDao();
		productDao = new ProductDao();
		cartDao = new CartDao();
	}
	
	// 상품에서 직접주문
	public int create(int oi_qty, int p_no, String userId) throws Exception {
		Product product = productDao.findByNo(p_no);
		OrderItem orderItem = new OrderItem(0 , oi_qty , p_no , product);
		ArrayList<OrderItem> orderItemList = new ArrayList<OrderItem>();
		orderItemList.add(new OrderItem(0,oi_qty,p_no,product));
		Orders newOrder = new Orders(0,
				orderItemList.get(0).getProduct().getP_title()+" "+(oi_qty)+"권",
				oi_qty*product.getP_price(),
				new Date(),
				userId); 
		newOrder.setOrderItemList(orderItemList);
		
		return orderDao.insert(newOrder);
	}
	
	// cart에서 주문
	public int create(String userId) throws Exception {
		List<Cart> cartList = cartDao.findByUserId(userId);
		ArrayList<OrderItem> orderItemList = new ArrayList<OrderItem>();
		int o_price = 0;
		int oi_tot_qty = 0;
		String o_desc = null;
		for (Cart cart : cartList) {
			OrderItem orderItem = new OrderItem(0,cart.getCart_qty(),0,cart.getProduct());
			orderItemList.add(orderItem);
			oi_tot_qty += cart.getCart_qty();
			o_price += cart.getCart_qty()*cart.getProduct().getP_price();
		}
		if(oi_tot_qty==1) {
			o_desc = orderItemList.get(0).getProduct().getP_title();
		}else if(oi_tot_qty==0){
			
		}else {
			o_desc = orderItemList.get(0).getProduct().getP_title()+" 총 "+oi_tot_qty+" 권";
		}
		Orders newOrder = new Orders(0,o_desc,o_price,null,userId);
		newOrder.setOrderItemList(orderItemList);
		orderDao.insert(newOrder);
		cartDao.deleteByUserId(userId);
		
		return 0;
	}
	// cart에서 선택주문
	public int create(String userId,String[] cart_no_select_array) throws Exception {
		List<Cart> cartList = cartDao.findByUserId(userId);
		ArrayList<OrderItem> orderItemList = new ArrayList<OrderItem>();
		int o_price = 0;
		int oi_tot_qty = 0;
		for(int i = 0; i<cart_no_select_array.length;i++) {
			Cart cartItem = cartDao.findByCartNo(Integer.parseInt(cart_no_select_array[i]));
			OrderItem orderItem = new OrderItem(0,cartItem.getCart_qty(),0,cartItem.getProduct());
			orderItemList.add(orderItem);
			o_price += orderItem.getOi_qty()*orderItem.getProduct().getP_price();
			oi_tot_qty += orderItem.getOi_qty();
		}
		String o_desc = orderItemList.get(0).getProduct().getP_title()+"외 "+(oi_tot_qty-1)+" 개";
		Orders newOrder = new Orders(0,o_desc,o_price,null,userId);
		orderDao.insert(newOrder);
		for(int i = 0 ; i<cart_no_select_array.length ; i++) {
			cartDao.deleteByCartNo(Integer.parseInt(cart_no_select_array[i]));
		}
		
		return 0;
	}
	
	// 주문 1개 삭제
	public int deleteOrderNo(String userId, int o_no) throws Exception {
		return orderDao.deleteByOrderNo(userId, o_no);
	}
	// 주문 전체삭제
	public int deleteUserId(String userId) throws Exception {
		return orderDao.deleteByUserId(userId);
	}
	// 주문목록
	public List<Orders> orderList(String userId) throws Exception {
		return orderDao.findByUserID(userId);
	}
	//주문 상세보기
	public Orders orderDetail(String userId, int o_no) throws Exception {
		return orderDao.findByOrderNo(userId, o_no);
	}
	
	//오더 총액 계산
	public int orderTotPrice(String userId) throws Exception {
		int totPrice = 0;
		List<Orders> orderList = orderDao.findByUserID(userId);
		for (Orders order : orderList) {
			totPrice += order.getO_price();
		}
		return totPrice;
	}
	
	public int orderNoPrice(String userId,int o_no) throws Exception {
		Orders order = orderDao.findByOrderNo(userId, o_no);
		return order.getO_price();
	}

}

