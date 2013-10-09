package io.github.abujaki.minestock;

import java.util.List;

public class TransactionEngine {

	private List<StockOrder> buyOrders, sellOrders;
	/*Private list of stocks*/
	
	TransactionEngine(/*Stock list of some sort*/){
		/*set up stock list*/
	}
	
	public void submitBuyOrder(StockOrder buyOrder){
		//Take the money
		MineStock.econ.withdrawPlayer(buyOrder.getPlayer(), buyOrder.getPrice() * buyOrder.getAmount());
		//TODO: Remember to remove the code in the MineStock.java that does the exact same thing
		//Else players will be double-billed
		
		//run
		match(buyOrder, true);
	}
	
	public void submitSellOrder(StockOrder sellOrder){
		//Take the stock
		removeStock(sellOrder.getPlayer(),sellOrder.getStock(), sellOrder.getAmount());
		//run
		match(sellOrder,false);
	}
	
	private void match(StockOrder order, boolean buy){
		int index = 0;
		int transfer = 0;
		if(buy){ //Buy stock order
			StockOrder sellOrder;
			while(!(sellOrders.isEmpty())){
				//See if we can match the buy order to a sell order
				//For all orders in sell Orders
				sellOrder = sellOrders.get(index);
				
				//Check to see if the stock matches
				if(order.getStock()==sellOrder.getStock()){
					//Check to see that the buyer is offering more than the seller wants
					if(order.getPrice() >= sellOrder.getPrice()){
						//Transfer ownership of as many as possible
						transfer = Math.min(order.getAmount(), sellOrder.getAmount());
						//Adjust the orders
						order.decreaseStock(transfer);
						sellOrder.decreaseStock(transfer);
						//Give the stock to the buyer
						addStock(order.getPlayer(), order.getStock(), transfer);
						//Pay the seller for the stock
						MineStock.econ.depositPlayer(sellOrder.getPlayer(), transfer * sellOrder.getPrice());
						//Refund the difference to the buyer
						MineStock.econ.depositPlayer(order.getPlayer(),
								(transfer * order.getPrice())-(transfer * sellOrder.getPrice()));
						
						//Clean up
						if(sellOrder.getAmount()==0){
							//Remove it from the order list
							sellOrders.remove(index);
							index--;
						}
						if(order.getAmount()==0){
							//Fantastic. We're done. Let's get a coffee.
							return;
						}
					}
				}
				//Increase the index, and start again
				index++;
			}
			//Nothing else to do, add the remainder of the order to the buy orders list
			buyOrders.add(order);
		} else { //Sell stock order
			StockOrder buyOrder;
			while(!(sellOrders.isEmpty())){
				//See if we can match the sell order to a buy order
				//For all orders in buy Orders
				buyOrder = sellOrders.get(index);
				//Check to see if the stock matches
				if(order.getStock()==buyOrder.getStock()){
					//Check to see that the buyer is offering more than the seller wants
					if(buyOrder.getPrice() >= order.getPrice()){
						//Transfer ownership of as many as possible
						transfer = Math.min(order.getAmount(), buyOrder.getAmount());
						//Adjust the orders
						order.decreaseStock(transfer);
						buyOrder.decreaseStock(transfer);
						//Give the stock to the buyer
						addStock(buyOrder.getPlayer(), buyOrder.getStock(), transfer);
						//Pay the seller for the stock
						MineStock.econ.depositPlayer(order.getPlayer(), transfer * order.getPrice());
						//Refund the difference to the buyer
						MineStock.econ.depositPlayer(buyOrder.getPlayer(),
								(transfer * buyOrder.getPrice())-(transfer * order.getPrice()));
						
						//Clean up
						if(buyOrder.getAmount()==0){
							//Remove it from the order list
							buyOrders.remove(index);
							index--;
						}
						if(order.getAmount()==0){
							//Fantastic. We're done. Let's get a coffee.
							return;
						}
					}
				}
				//Increase the index, and start again
				index++;
			}
			//Nothing else to do, add the remainder of the order to the sell orders list
			sellOrders.add(order);
		}
	}
	private void removeStock(String player, String stock, int amount){
		MineStock.memoryCard.removeStocks(player, stock, amount);
	}
	
	private void addStock(String player, String stock, int amount){
		MineStock.memoryCard.addStocks(player, stock, amount);
	}
}