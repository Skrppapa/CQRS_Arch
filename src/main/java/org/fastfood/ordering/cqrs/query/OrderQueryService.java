package org.fastfood.ordering.cqrs.query;

import org.fastfood.ordering.cqrs.domain.OrderEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderQueryService {
    private final Map<UUID, OrderView> projection = new HashMap<>();

    public void project(List<OrderEvent> events) {
        for (OrderEvent event : events) {
            OrderView view = projection.computeIfAbsent(event.getOrderId(), k -> new OrderView());
            view.setId(event.getOrderId());
            view.getLog().add(new java.util.Date(event.getTimestamp()) + ": " + event.getDescription());

            switch (event.getType()) {
                case "ITEM_ADDED" -> view.getCurrentItems().add(event.getDescription().replace("Добавлено блюдо: ", ""));
                case "ITEM_REMOVED" -> view.getCurrentItems().remove(event.getDescription().replace("Удалено блюдо: ", ""));
                case "STATUS_CHANGED" -> view.setStatus(event.getDescription().replace("Статус изменен на: ", ""));
            }
        }
    }

    // Краткая статистика всех заказов
    public void printSimpleStats() {
        System.out.println("\n--- ОБЩАЯ СТАТИСТИКА ---");
        projection.values().forEach(v -> System.out.println("Заказ: " + v.getId() + " | Статус: " + v.getStatus() + " | Блюд: " + v.getCurrentItems().size()));
    }

    // Детальная история заказа
    public void printOrderHistory(UUID id) {
        if (!projection.containsKey(id)) return;
        System.out.println("\n--- ИСТОРИЯ ЗАКАЗА " + id + " ---");
        projection.get(id).getLog().forEach(System.out::println);
    }

    // Отчет по составу всех заказов
    public void printCompositionReport() {
        System.out.println("\n--- ОТЧЕТ ПО СОСТАВУ ЗАКАЗОВ ---");
        projection.values().forEach(v -> System.out.println("Заказ " + v.getId() + " содержит: " + String.join(", ", v.getCurrentItems())));
    }
}