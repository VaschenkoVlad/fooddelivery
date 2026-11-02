import java.util.Objects;

public abstract class DeliveryParticipant implements IDeliveryStatus {
    private final String id;
    private final String name;

    public DeliveryParticipant(String id, String name) {
        this.id = requireText(id, "Ідентифікатор");
        this.name = requireText(name, "Ім'я");
    }

    public final String getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    @Override
    public abstract String getDeliveryStatus();

    protected static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " не може бути null.");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " не може бути порожнім.");
        }
        return value.trim();
    }
}
