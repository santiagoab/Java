
package practica4;

import java.util.Currency;


public class ShoppingCart extends BookCollection implements ShoppingCartInterface{
    private Catalog catalog;
    
    public ShoppingCart(Catalog catinit){
    super();
    catalog = catinit; 
    for(StockInterface stkin : catinit.collection){
            Stock stock = (Stock)stkin;
            Stock a = new Stock( stock.getBook(),0,stock.getPrice(), stock.getCurrency());
            this.collection.add(a);
        }
    
}

public void addCopies(int numberOfCopies, String booktitle){
     super.addCopies(numberOfCopies, booktitle);
     catalog.removeCopies(numberOfCopies, booktitle);
     
}

public void removeCopies(int numberOfCopies, String booktitle){
      super.removeCopies(numberOfCopies, booktitle);
      catalog.addCopies(numberOfCopies, booktitle);
}

public double totalPrice(){ 
    
double price = 0;
     for(StockInterface value: collection){
      price += value.totalPrice();
      
    }
return price;
 
}


public String checkout(){
  
        long cardNo = 478956785;
        String cardHolder = "Geralt de Rivia";
        Currency currency = Currency.getInstance("EUR");
        
        Payment pay = new Payment();
        String payment = pay.doPayment(cardNo, cardHolder, totalPrice(), currency);
        return payment;

}
}