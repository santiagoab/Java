/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica4;
import java.util.Currency;
/**
 */
public class Stock implements StockInterface{
    private Book book;
    private int copies;
    private double price;
    private Currency currency;

    public Stock(Book bookinit ,int copinit, double priceinit, Currency currinit){
      book = bookinit;
      copies = copinit;
      price = priceinit;
      currency = currinit;
    }
    public Book getBook(){
       return book;
    }

    public String getBooktitle(){
       return book.getTitle();
    }

    public int numberOfCopies(){
       return copies;
    }

    public void addCopies(int newCopies){
       copies = copies + newCopies;
    }

    public void removeCopies(int newCopies){
       copies = copies - newCopies;
    }

    public double totalPrice(){
       return price*copies;
    }

    public double getPrice(){
       return price;
    }
     public Currency getCurrency(){
       return currency;
    }

}
