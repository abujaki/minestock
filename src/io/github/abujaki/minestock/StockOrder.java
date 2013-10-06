package io.github.abujaki.minestock;

//Stock order object

public class StockOrder {
	private String stk, pl;
	private int amt; 
	private double price;
	
	//Stock order has stock code, amount, price per stock, and controlling user; 
	
	StockOrder(String stock, int amount, int priceEach, String player){
		stk = stock; amt = amount; price = priceEach; pl = player;
	}
	
	//Decrease amount of stocks in the order
	public boolean decreaseStock(){return decreaseStock(1);}
	public boolean decreaseStock(int by){
		//Due to code in Transaction Engine doing the checking,
		//This check may be removed shortly
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
	public int getAmount(){return amt;}
	public String getStock(){return stk;}
	public double getPrice(){return price;}
	public String getPlayer(){return pl;}
	
}
