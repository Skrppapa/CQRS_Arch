package org.fastfood.ordering.cqrs.command;

import org.fastfood.ordering.cqrs.domain.OrderEvent;
import org.fastfood.ordering.cqrs.domain.OrderStatus;

import java.util.*;

public class OrderCommandHandler {
    private final Map<UUID, OrderAggregate> repository = new HashMap<>();
    private final List<OrderEvent> eventLog = new ArrayList<>();

    public UUID handleCreateOrder() {
        UUID id = UUID.randomUUID();
        repository.put(id, new OrderAggregate(id));
        publish(id, "CREATED", "Заказ создан");
        return id;
    }

    public void handleAddItem(UUID id, String item) {
        OrderAggregate order = repository.get(id);
        if (order == null) throw new IllegalArgumentException("Заказ не найден!");
        order.addItem(item);
        publish(id, "ITEM_ADDED", "Добавлено блюдо: " + item);
    }

    public void handleRemoveItem(UUID id, int index) {
        OrderAggregate order = repository.get(id);
        if (order == null) throw new IllegalArgumentException("Заказ не найден!");

        // Получаем имя блюда перед удалением для записи в событие
        String itemName = order.getItems().get(index);
        order.removeItem(index);
        publish(id, "ITEM_REMOVED", "Удалено блюдо: " + itemName);
    }

    public List<String> getOrderItems(UUID id) {
        OrderAggregate order = repository.get(id);
        return (order != null) ? order.getItems() : Collections.emptyList();
    }

    public void handleUpdateStatus(UUID id, OrderStatus newStatus) {
        OrderAggregate order = repository.get(id);
        if (order == null) throw new IllegalArgumentException("Заказ не найден!");
        order.setStatus(newStatus);
        publish(id, "STATUS_CHANGED", "Статус изменен на: " + newStatus);
    }

    private void publish(UUID id, String type, String desc) {
        eventLog.add(new OrderEvent(id, type, desc, System.currentTimeMillis()));
    }

    public List<OrderEvent> getNewEvents() {
        List<OrderEvent> events = new ArrayList<>(eventLog);
        eventLog.clear();
        return events;
    }
}