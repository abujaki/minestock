package io.github.abujaki.minestock;

public class OwnedStockRecord {
  private String pl, stk;
  int own;
  //Each set of owned stock consists of the player owning the stock, the stock code, and how many of the stocks are owned
  //player and stock are the primary key
  
  //Typical Constructor
  OwnedStockRecord(String player, String stockCode, int stockOwned){
	  pl = player; stk = stockCode; own = stockOwned;
  }
  
  //Typical Getters
  int getStocksOwned(){return own;}
  String getPlayer(){return pl;}
  String getStockCode(){return stk;}
  
  //Setters
  
 //---Delete stock from the record.
  boolean reduceStock(){return reduceStock(1);}
  boolean reduceStock(int by){
	  if((by > 0) && (own >= by)){
		  //check to see if there's enough stock to deduct
		  own -= by;
		  return true;
	  }
	  else return false;
  }
  
  //---Add Stock to the record
  boolean increaseStock(){return increaseStock(1);}
  boolean increaseStock(int by){
	  if(by > 0){ //Smartass check
		  own += by;
		  return true;
	  }
	  return false;
  }
  
}
