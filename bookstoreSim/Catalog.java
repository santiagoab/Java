
package practica4;

import java.util.LinkedList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Currency;


public class Catalog extends BookCollection{
    
public Catalog(){
LinkedList<String[]> list = readCatalog("books.xml");
for ( String [] element : list ) {
System.out.println( element );

String title = element[0];   
String author = element[1];
Date date = new Date();
try { date = new SimpleDateFormat().parse( element[2] ); } 
catch( Exception e ) {}
String place = element[3];
long isbn = Long.parseLong( element[4] ); 
double price = Double.parseDouble( element[5] ); 
Currency currency = Currency.getInstance( element[6] ); 
int copies = Integer.parseInt( element[7] );

Book book = new Book(title,author,date,place,isbn);
Stock stock = new Stock (book, copies, price, currency);

collection.add(stock);






}

}
}

