package org.fastfood.ordering.cqrs.domain;

import lombok.Value;
import java.util.UUID;

@Value
public class OrderEvent {
    UUID orderId;
    String type;
    String description;
    long timestamp;
}