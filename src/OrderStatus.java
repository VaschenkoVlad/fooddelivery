public enum OrderStatus {
    Draft,
    Submitted,
    Accepted,
    Preparing,
    ReadyForPickup,
    AwaitingCourier,
    InTransit,
    Delivered,
    Rejected,
    Cancelled,
    PickupFailed;

    public String getDisplayName() {
        return switch (this) {
            case Draft -> "Чернетка";
            case Submitted -> "Подано";
            case Accepted -> "Прийнято";
            case Preparing -> "Готується";
            case ReadyForPickup -> "Готове до забору";
            case AwaitingCourier -> "Очікує кур'єра";
            case InTransit -> "У дорозі";
            case Delivered -> "Доставлено";
            case Rejected -> "Відхилено";
            case Cancelled -> "Скасовано";
            case PickupFailed -> "Забір не відбувся";
        };
    }
}
