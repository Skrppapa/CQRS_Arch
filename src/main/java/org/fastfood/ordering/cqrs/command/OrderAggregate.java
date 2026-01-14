package org.fastfood.ordering.cqrs.command;

import lombok.Data;
import org.fastfood.ordering.cqrs.domain.*;
import java.util.*;

@Data
class OrderAggregate {
    private final UUID id;
    private final List<String> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.CREATED;

    public OrderAggregate(UUID id) { this.id = id; }

    public void addItem(String item) {
        if (status != OrderStatus.CREATED) throw new IllegalStateException("Заказ уже в работе!");
        items.add(item);
    }

    public void removeItem(int index) {
        if (status != OrderStatus.CREATED)
            throw new IllegalStateException("Нельзя менять состав заказа в процессе готовки!");
        if (index < 0 || index >= items.size())
            throw new IllegalArgumentException("Неверный индекс блюда!");

        items.remove(index);
    }

    public void setStatus(OrderStatus newStatus) {
        if (this.status == OrderStatus.COMPLETED && newStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Нельзя отменить уже завершенный заказ!");
        }
        this.status = newStatus;
    }

    public List<String> getItems() { return new ArrayList<>(items); }
}
