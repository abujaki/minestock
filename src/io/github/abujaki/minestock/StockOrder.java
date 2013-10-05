package io.github.abujaki.minestock;

//Stock order object

public class StockOrder {
	private String stk, pl;
	private int amt, price;
	//Stock order has stock code, amount, price per stock, and controlling user; 
	
	StockOrder(String stock, int amount, int priceEach, String player){
		stk = stock; amt = amount; price = priceEach; pl = player; 
	}
	
	//Decrease amount of stocks in the order
	boolean decreaseStock(){return decreaseStock(1);}
	boolean decreaseStock(int by){
		if (amt >= by){
			amt -= by;
			return true;
		}
		else return false;
	}
	//Increase amount of stocks in the order
	//Admittedly not really needed yet
	/*boolean increaseStock(){return increaseStock(1);}
	boolean increaseStock(int by){
		amt += by;
		return true;
	}
	*/
	
	//The usual getters
	int getAmount(){return amt;}
	String getStock(){return stk;}
	int getPrice(){return price;}
	String getPlayer(){return pl;}
}
