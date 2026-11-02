import java.util.Objects;

public final class PartnerRestaurant extends DeliveryParticipant implements IDeliveryStatus {
    public PartnerRestaurant(String id, String name) {
        super(id, name);
    }

    public void confirmOrder(Order order) {
        requireOwnOrder(order);
        order.confirm();
    }

    public void rejectOrder(Order order, String reason) {
        requireOwnOrder(order);
        order.reject(reason);
    }

    public void startPreparing(Order order) {
        requireOwnOrder(order);
        order.startPreparing();
    }

    public void markReady(Order order) {
        requireOwnOrder(order);
        order.markReady();
    }

    private void requireOwnOrder(Order order) {
        Objects.requireNonNull(order, "Замовлення не може бути null.");
        if (order.getRestaurant() != this) {
            throw new IllegalArgumentException("Замовлення належить іншому ресторану.");
        }
    }

    @Override
    public String getDeliveryStatus() {
        return "Ресторан-партнер приймає замовлення.";
    }
}
