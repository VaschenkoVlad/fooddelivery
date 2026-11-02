import java.util.Objects;

public final class Delivery {
    private final String id;
    private final Order order;
    private final MappingService mappingService;
    private DeliveryStatus status = DeliveryStatus.AwaitingCourier;
    private String trackingDetails = "Очікується призначення кур'єра.";
    private boolean obstacleReported;
    private Courier courier;
    private Review review;

    public Delivery(String id, Order order, MappingService mappingService) {
        this.id = DeliveryParticipant.requireText(id, "Ідентифікатор доставки");
        this.order = Objects.requireNonNull(order, "Замовлення не може бути null.");
        this.mappingService = Objects.requireNonNull(mappingService, "Сервіс маршруту не може бути null.");
        if (order.getStatus() != OrderStatus.ReadyForPickup
                && order.getStatus() != OrderStatus.AwaitingCourier) {
            throw new IllegalStateException("Доставку можна створити лише для готового до забору замовлення.");
        }
        order.attachDelivery(this);
    }

    void assignCourier(Courier assignedCourier) {
        Objects.requireNonNull(assignedCourier, "Кур'єр не може бути null.");
        if (status == DeliveryStatus.Delivered || status == DeliveryStatus.PickupFailed) {
            System.out.println("До завершеної доставки не можна призначити кур'єра.");
            return;
        }
        if (courier != null && courier != assignedCourier) {
            throw new IllegalStateException("Для доставки вже призначено іншого кур'єра.");
        }
        if (courier == assignedCourier) {
            return;
        }
        courier = assignedCourier;
        trackingDetails = "Кур'єра призначено: " + assignedCourier.getName() + ".";
        System.out.println(trackingDetails);
    }

    public void start() {
        if (status == DeliveryStatus.InTransit) {
            System.out.println("Повторну подію забору проігноровано; доставка вже триває.");
            return;
        }
        if (status != DeliveryStatus.AwaitingCourier
                || (order.getStatus() != OrderStatus.ReadyForPickup
                && order.getStatus() != OrderStatus.AwaitingCourier)) {
            System.out.println("Доставку не можна почати в поточному стані.");
            return;
        }
        if (courier == null) {
            awaitCourier();
            return;
        }
        status = DeliveryStatus.InTransit;
        trackingDetails = "Кур'єр забрав замовлення.";
        order.markInTransit();
        System.out.println(trackingDetails);
        mappingService.getRouteDetails(order.getRestaurant().getName(), order.getCustomer().getAddress());
    }

    public void updateStatus(String statusDetails) {
        String details = DeliveryParticipant.requireText(statusDetails, "Статус відстеження");
        if (status != DeliveryStatus.InTransit) {
            System.out.println("Оновлювати відстеження можна лише під час доставки.");
            return;
        }
        if (obstacleReported && "InTransit".equals(details)) {
            obstacleReported = false;
            trackingDetails = "Доставку відновлено після перешкоди.";
            System.out.println(trackingDetails);
            return;
        }
        trackingDetails = details;
        System.out.println("Оновлення доставки: " + details);
    }

    public void complete() {
        if (status == DeliveryStatus.Delivered) {
            System.out.println("Повторне завершення проігноровано; доставка вже виконана.");
            return;
        }
        if (status != DeliveryStatus.InTransit || obstacleReported) {
            System.out.println("Доставку не можна завершити: вона не триває або перешкоду ще не усунуто.");
            return;
        }
        status = DeliveryStatus.Delivered;
        trackingDetails = "Замовлення доставлено.";
        order.markDelivered();
        System.out.println(trackingDetails);
    }

    public void reportObstacle(String reason) {
        String obstacle = DeliveryParticipant.requireText(reason, "Опис перешкоди");
        if (status == DeliveryStatus.Delivered || status == DeliveryStatus.PickupFailed) {
            System.out.println("Для завершеної доставки не можна зареєструвати перешкоду.");
            return;
        }
        if (status == DeliveryStatus.InTransit) {
            obstacleReported = true;
            trackingDetails = "Перешкода під час доставки: " + obstacle;
            System.out.println(trackingDetails + ". Доставку не позначено завершеною.");
            return;
        }
        if (courier == null) {
            awaitCourier();
            return;
        }
        status = DeliveryStatus.PickupFailed;
        trackingDetails = "Забір не відбувся: " + obstacle;
        order.markPickupFailed();
        System.out.println(trackingDetails + ". Замовлення не доставлено.");
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public String getTrackingDetails() {
        return trackingDetails;
    }

    public Order getOrder() {
        return order;
    }

    public Courier getCourier() {
        return courier;
    }

    public Review getReview() {
        return review;
    }

    public MappingService getMappingService() {
        return mappingService;
    }

    public String getId() {
        return id;
    }

    void awaitCourier() {
        if (status == DeliveryStatus.AwaitingCourier) {
            order.markAwaitingCourier();
            trackingDetails = "Очікується призначення кур'єра.";
            return;
        }
        if (status != DeliveryStatus.Delivered && status != DeliveryStatus.PickupFailed) {
            status = DeliveryStatus.AwaitingCourier;
            order.markAwaitingCourier();
            trackingDetails = "Очікується призначення кур'єра.";
        }
    }

    void registerReview(Review newReview) {
        Objects.requireNonNull(newReview, "Відгук не може бути null.");
        if (status != DeliveryStatus.Delivered || order.getStatus() != OrderStatus.Delivered) {
            throw new IllegalStateException("Відгук дозволено залишити лише після успішної доставки.");
        }
        if (review != null) {
            throw new IllegalStateException("Для цієї доставки вже залишено відгук.");
        }
        review = newReview;
    }
}
