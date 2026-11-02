import java.util.Objects;

public final class Courier extends DeliveryParticipant implements IDeliveryStatus {
    private final boolean availability;
    private Delivery assignedDelivery;

    public Courier(String id, String name, boolean availability) {
        super(id, name);
        this.availability = availability;
    }

    public void acceptDelivery(Delivery delivery) {
        Objects.requireNonNull(delivery, "Доставка не може бути null.");
        if (!availability) {
            System.out.println(getName() + " зараз недоступний; замовлення очікує кур'єра.");
            delivery.awaitCourier();
            return;
        }
        if (assignedDelivery != null && assignedDelivery != delivery) {
            throw new IllegalStateException("Кур'єр уже призначений для іншої доставки.");
        }
        delivery.assignCourier(this);
        assignedDelivery = delivery;
        System.out.println(getName() + " прийняв доставку.");
    }

    public void updateDeliveryStatus(String status) {
        if (assignedDelivery == null) {
            throw new IllegalStateException("Кур'єру ще не призначено доставку.");
        }
        assignedDelivery.updateStatus(status);
    }

    public boolean isAvailable() {
        return availability;
    }

    @Override
    public String getDeliveryStatus() {
        return availability ? "Кур'єр доступний." : "Кур'єр недоступний.";
    }
}
