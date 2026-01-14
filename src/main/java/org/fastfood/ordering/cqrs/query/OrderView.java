package org.fastfood.ordering.cqrs.query;

import lombok.Data;
import java.util.*;

@Data
class OrderView {
    private UUID id;
    private List<String> currentItems = new ArrayList<>();
    private String status = "CREATED";
    private List<String> log = new ArrayList<>();
}

