package io.github.abujaki.minestock;

import java.util.List;

public class TransactionEngine {

	private List<StockOrder> buyOrders, sellOrders;
	private MemoryCard m;
	/*Private list of stocks*/

	TransactionEngine(/*Stock list of some sort*/){
		/*set up stock list*/
		m = new MemoryCard();
	}

	public void match(StockOrder order, boolean buy){
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
						//Validate that they still have enough to sell
						if(validateAmount(sellOrder.getPlayer(), sellOrder.getStock(), sellOrder.getAmount())){

							//Transfer ownership of as many as possible
							transfer = Math.min(order.getAmount(), sellOrder.getAmount());
							//Adjust the orders
							order.decreaseStock(transfer);
							sellOrder.decreaseStock(transfer);
							//Give the stock to the buyer
							m.moveStocks(sellOrder.getPlayer(), order.getPlayer(), order.getStock(), transfer);
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
						} else { //The scoundrel!
							//Remove faulty sell order
							sellOrders.remove(index);
							index--;

							//TODO: Contemplate the wisdom of this decision.
							/** Other Option:
							 *	sellOrder.setAmount(MineStock.memoryCard.checkStockAmount(sellOrder.getPlayer(),
							 *		sellOrder.getStock()));
							 *
							 *Slightly more evil, in which the sell order is set to however many
							 *stocks of that type is remaining in their account.
							 *If 0, will be removed the next time there's a match, or we can
							 *remove it right here like above.
							 **/
							//--Also we can check during submission time looking through all the sell
							//orders to ensure they're not selling more stocks than they have
							
							//We can also just take the stock during submission time.
							// -- Issue, owner no longer can claim the benefits of the stock when it's
							// in the process of being sold.
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
						//Transfer ownership of the stock
						m.moveStocks(order.getPlayer(), buyOrder.getPlayer(), buyOrder.getStock(), transfer);
						//Pay the seller for the stock
						MineStock.econ.depositPlayer(order.getPlayer(), transfer * order.getPrice());
						//Refund the difference to the buyer
						MineStock.econ.depositPlayer(buyOrder.getPlayer(),
								(transfer * buyOrder.getPrice())-(transfer * order.getPrice()));
					}
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
				//Increase the index, and start again
				index++;
			}
			//Nothing else to do, add the remainder of the order to the sell orders list
			sellOrders.add(order);
		}
	}
	private boolean validateAmount(String player, String stock, int amount){
		return(m.checkStockAmount(player, stock) >= amount);
	}

	//Passthrough methods
	public int checkStockAmount(String name, String stock) {
		return m.checkStockAmount(name, stock);
	}
}