import java.util.Objects;

public final class Review {
    private final String id;
    private final Customer customer;
    private final Delivery delivery;
    private final int rating;
    private final String comment;

    public Review(String id, Customer customer, Delivery delivery, int rating, String comment) {
        this.id = DeliveryParticipant.requireText(id, "Ідентифікатор відгуку");
        this.customer = Objects.requireNonNull(customer, "Замовник не може бути null.");
        this.delivery = Objects.requireNonNull(delivery, "Доставка не може бути null.");
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Оцінка має бути цілим числом від 1 до 5.");
        }
        Objects.requireNonNull(comment, "Коментар не може бути null.");
        if (delivery.getOrder().getCustomer() != customer) {
            throw new IllegalArgumentException("Відгук може залишити лише замовник цієї доставки.");
        }
        this.rating = rating;
        this.comment = comment.trim();
        delivery.registerReview(this);
        System.out.println("Відгук прийнято. Дякуємо за оцінку " + rating + " з 5.");
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Delivery getDelivery() {
        return delivery;
    }
}
