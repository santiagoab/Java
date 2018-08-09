
package practica4;

public interface ShoppingCartInterface extends BookCollectionInterface {
	public abstract double totalPrice();
	public abstract String checkout();
}
