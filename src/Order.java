import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Order {
    private static final Pattern TIME_PATTERN = Pattern.compile("([01]\\d|2[0-3]):([0-5]\\d)");
    private static final double DEFAULT_UNIT_PRICE = 100.0;

    private final String id;
    private final Customer customer;
    private final PartnerRestaurant restaurant;
    private final String requestedDeliveryTime;
    private final List<BasketItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.Draft;
    private Delivery delivery;

    public Order(String id, Customer customer, PartnerRestaurant restaurant, String requestedDeliveryTime) {
        this.id = DeliveryParticipant.requireText(id, "Ідентифікатор замовлення");
        this.customer = Objects.requireNonNull(customer, "Замовник не може бути null.");
        this.restaurant = Objects.requireNonNull(restaurant, "Ресторан не може бути null.");
        this.requestedDeliveryTime = requestedDeliveryTime;
    }

    public Order addItem(BasketItem item) {
        Objects.requireNonNull(item, "Позиція кошика не може бути null.");
        if (status != OrderStatus.Draft) {
            throw new IllegalStateException("Позиції можна додавати лише до чернетки замовлення.");
        }
        items.add(item);
        return this;
    }

    public Order addItem(String dishName, int quantity) {
        return addItem(new BasketItem(dishName, quantity, DEFAULT_UNIT_PRICE));
    }

    public void submit() {
        if (status != OrderStatus.Draft) {
            System.out.println("Замовлення не можна подати зі стану " + status + ".");
            return;
        }
        if (items.isEmpty()) {
            System.out.println("Не вдалося оформити замовлення: кошик порожній.");
            return;
        }
        if (!isAvailableDeliveryTime(requestedDeliveryTime)) {
            System.out.println("Не вдалося оформити замовлення: час доставки має бути від 10:00 до 22:00 у форматі HH:mm.");
            return;
        }
        status = OrderStatus.Submitted;
        System.out.println("Замовлення " + id + " подано на розгляд ресторану.");
    }

    public void confirm() {
        if (status == OrderStatus.Accepted) {
            System.out.println("Повторне підтвердження проігноровано; замовлення вже прийняте.");
            return;
        }
        if (status != OrderStatus.Submitted) {
            System.out.println("Замовлення можна підтвердити лише після подання.");
            return;
        }
        status = OrderStatus.Accepted;
        System.out.println("Ресторан підтвердив замовлення " + id + ".");
    }

    public void startPreparing() {
        if (status != OrderStatus.Accepted) {
            System.out.println("Приготування можна почати лише для прийнятого замовлення.");
            return;
        }
        status = OrderStatus.Preparing;
        System.out.println("Ресторан почав готувати замовлення.");
    }

    public void markReady() {
        if (status == OrderStatus.ReadyForPickup) {
            System.out.println("Повторне повідомлення про готовність проігноровано.");
            return;
        }
        if (status != OrderStatus.Preparing) {
            System.out.println("Готовим можна позначити лише замовлення, яке готують.");
            return;
        }
        status = OrderStatus.ReadyForPickup;
        System.out.println("Замовлення готове до забору кур'єром.");
    }

    public void reject(String reason) {
        String rejectionReason = DeliveryParticipant.requireText(reason, "Причина відхилення");
        if (status != OrderStatus.Submitted) {
            System.out.println("Відхилити можна лише подане замовлення.");
            return;
        }
        status = OrderStatus.Rejected;
        System.out.println("Ресторан відхилив замовлення: " + rejectionReason);
    }

    public void cancel() {
        if (status == OrderStatus.Cancelled) {
            return;
        }
        if (status != OrderStatus.Draft && status != OrderStatus.Submitted) {
            System.out.println("Замовлення вже не можна скасувати.");
            return;
        }
        status = OrderStatus.Cancelled;
        System.out.println("Замовлення скасовано.");
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<BasketItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Customer getCustomer() {
        return customer;
    }

    public PartnerRestaurant getRestaurant() {
        return restaurant;
    }

    public String getRequestedDeliveryTime() {
        return requestedDeliveryTime;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public String getId() {
        return id;
    }

    private static boolean isAvailableDeliveryTime(String time) {
        if (time == null) {
            return false;
        }
        Matcher matcher = TIME_PATTERN.matcher(time);
        if (!matcher.matches()) {
            return false;
        }
        int hour = Integer.parseInt(matcher.group(1));
        int minute = Integer.parseInt(matcher.group(2));
        int minutesSinceMidnight = hour * 60 + minute;
        return minutesSinceMidnight >= 10 * 60 && minutesSinceMidnight <= 22 * 60;
    }

    void markAwaitingCourier() {
        if (status == OrderStatus.ReadyForPickup) {
            status = OrderStatus.AwaitingCourier;
        }
    }

    void markInTransit() {
        if (status == OrderStatus.ReadyForPickup || status == OrderStatus.AwaitingCourier) {
            status = OrderStatus.InTransit;
        }
    }

    void markPickupFailed() {
        if (status == OrderStatus.ReadyForPickup || status == OrderStatus.AwaitingCourier) {
            status = OrderStatus.PickupFailed;
        }
    }

    void markDelivered() {
        if (status == OrderStatus.InTransit) {
            status = OrderStatus.Delivered;
        }
    }

    void attachDelivery(Delivery newDelivery) {
        Objects.requireNonNull(newDelivery, "Доставка не може бути null.");
        if (delivery != null) {
            throw new IllegalStateException("Для замовлення вже створено доставку.");
        }
        delivery = newDelivery;
    }
}
