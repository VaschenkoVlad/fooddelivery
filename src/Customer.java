import java.util.Objects;

public final class Customer extends DeliveryParticipant implements IDeliveryStatus {
    private final String address;

    public Customer(String id, String name, String address) {
        super(id, name);
        this.address = requireText(address, "Адреса замовника");
    }

    public void placeOrder(Order order) {
        Objects.requireNonNull(order, "Замовлення не може бути null.");
        if (order.getCustomer() != this) {
            throw new IllegalArgumentException("Замовлення належить іншому замовнику.");
        }
        System.out.println(getName() + " оформлює замовлення.");
        order.submit();
    }

    public Review submitReview(Delivery delivery, int rating, String comment) {
        Objects.requireNonNull(delivery, "Доставка не може бути null.");
        if (delivery.getOrder().getCustomer() != this) {
            throw new IllegalArgumentException("Це замовлення належить іншому замовнику.");
        }
        return new Review("REV-" + delivery.getId(), this, delivery, rating, comment);
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String getDeliveryStatus() {
        return "Замовник готовий отримати замовлення за адресою: " + address;
    }
}
