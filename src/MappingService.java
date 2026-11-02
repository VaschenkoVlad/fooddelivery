public final class MappingService {
    public MappingService() {
    }

    public String getRouteDetails(String origin, String destination) {
        String from = DeliveryParticipant.requireText(origin, "Початкова адреса");
        String to = DeliveryParticipant.requireText(destination, "Адреса призначення");
        String details = "Імітований маршрут: " + from + " → " + to;
        System.out.println(details + ". Зовнішня картографічна служба не використовується.");
        return details;
    }
}
