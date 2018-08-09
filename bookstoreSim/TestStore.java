
package practica4;

public class TestStore {
    public static void main(String[] args) {
    Catalog catalog = new Catalog();
    ShoppingCart shopcart = new ShoppingCart(catalog);
    BookStore bookstore = new BookStore(catalog,shopcart);
        
    }
    
}
