import java.util.Objects;

public final class BasketItem {
    private final String dishName;
    private final int quantity;
    private final double unitPrice;

    public BasketItem(String dishName, int quantity, double unitPrice) {
        Objects.requireNonNull(dishName, "Назва страви не може бути null.");
        if (dishName.isBlank()) {
            throw new IllegalArgumentException("Назва страви не може бути порожньою.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Кількість має бути додатною.");
        }
        if (!Double.isFinite(unitPrice) || unitPrice <= 0) {
            throw new IllegalArgumentException("Ціна має бути скінченним додатним числом.");
        }
        this.dishName = dishName.trim();
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getDishName() {
        return dishName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
