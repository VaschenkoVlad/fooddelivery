public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        demonstrateSuccessfulDelivery();
        demonstrateEmptyBasket();
        demonstrateRestaurantRejection();
        demonstratePickupFailure();
    }

    private static void demonstrateSuccessfulDelivery() {
        printScenario("Успішне замовлення");
        Customer customer = new Customer("C-001", "Олена", "вул. Хрещатик, 1");
        PartnerRestaurant restaurant = new PartnerRestaurant("R-001", "Ресторан «Смак»");
        Courier courier = new Courier("D-001", "Андрій", true);
        Order order = new Order("O-001", customer, restaurant, "18:30")
                .addItem(new BasketItem("Борщ", 1, 150.0))
                .addItem("Вареники", 2);

        customer.placeOrder(order);
        restaurant.confirmOrder(order);
        restaurant.startPreparing(order);
        restaurant.markReady(order);

        Delivery delivery = new Delivery("DEL-001", order, new MappingService());
        courier.acceptDelivery(delivery);
        delivery.start();
        courier.updateDeliveryStatus("Кур'єр прямує до замовника.");
        delivery.complete();
        customer.submitReview(delivery, 5, "Смачно й доставлено вчасно.");
    }

    private static void demonstrateEmptyBasket() {
        printScenario("Невдалий сценарій: порожній кошик");
        Customer customer = new Customer("C-002", "Марія", "вул. Садова, 5");
        PartnerRestaurant restaurant = new PartnerRestaurant("R-002", "Кафе «Дім»");
        Order order = new Order("O-002", customer, restaurant, "19:00");
        customer.placeOrder(order);
        System.out.println("Поточний стан замовлення: " + order.getStatus() + ".");
    }

    private static void demonstrateRestaurantRejection() {
        printScenario("Невдалий сценарій: відхилення рестораном");
        Customer customer = new Customer("C-003", "Іван", "вул. Шевченка, 10");
        PartnerRestaurant restaurant = new PartnerRestaurant("R-003", "Піцерія «Вогонь»");
        Order order = new Order("O-003", customer, restaurant, "20:15")
                .addItem(new BasketItem("Піца", 1, 280.0));
        customer.placeOrder(order);
        restaurant.rejectOrder(order, "Страва тимчасово недоступна.");
        System.out.println("Поточний стан замовлення: " + order.getStatus() + ".");
    }

    private static void demonstratePickupFailure() {
        printScenario("Невдалий сценарій: кур'єр не може забрати замовлення");
        Customer customer = new Customer("C-004", "Оксана", "вул. Польова, 8");
        PartnerRestaurant restaurant = new PartnerRestaurant("R-004", "Кухня «Ранок»");
        Courier courier = new Courier("D-004", "Петро", true);
        Order order = new Order("O-004", customer, restaurant, "21:00")
                .addItem(new BasketItem("Суп", 1, 120.0));
        customer.placeOrder(order);
        restaurant.confirmOrder(order);
        restaurant.startPreparing(order);
        restaurant.markReady(order);

        Delivery delivery = new Delivery("DEL-004", order, new MappingService());
        courier.acceptDelivery(delivery);
        delivery.reportObstacle("Транспорт кур'єра несправний");
        System.out.println("Поточний стан замовлення: " + order.getStatus() + ".");
    }

    private static void printScenario(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
