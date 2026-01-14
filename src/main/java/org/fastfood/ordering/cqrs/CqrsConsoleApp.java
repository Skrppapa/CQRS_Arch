package org.fastfood.ordering.cqrs;

import org.fastfood.ordering.cqrs.command.OrderCommandHandler;
import org.fastfood.ordering.cqrs.query.OrderQueryService;
import org.fastfood.ordering.cqrs.domain.OrderStatus;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class CqrsConsoleApp {
    private final OrderCommandHandler commands = new OrderCommandHandler();
    private final OrderQueryService queries = new OrderQueryService();
    private final Scanner sc = new Scanner(System.in);

    public void start() {
        while (true) {
            printMainMenu();
            String choice = sc.nextLine();
            try {
                switch (choice) {
                    case "1" -> System.out.println("Заказ создан: " + commands.handleCreateOrder());

                    case "2" -> {
                        System.out.print("Введите ID заказа: "); UUID id = UUID.fromString(sc.nextLine());
                        System.out.print("Название блюда: "); String item = sc.nextLine();
                        commands.handleAddItem(id, item);
                        System.out.println("Блюдо добавлено.");
                    }

                    case "3" -> {
                        System.out.print("Введите ID заказа: ");
                        UUID id = UUID.fromString(sc.nextLine());

                        List<String> items = commands.getOrderItems(id);

                        if (items.isEmpty()) {
                            System.out.println("Заказ пуст, не найден или уже находится в работе.");
                        } else {
                            System.out.println("\nТЕКУЩИЙ СОСТАВ ЗАКАЗА:");
                            for (int i = 0; i < items.size(); i++) {
                                System.out.printf("%d. %s\n", i + 1, items.get(i));
                            }
                            System.out.println("0. Назад"); // Добавили визуальный пункт

                            System.out.print("Выберите НОМЕР для удаления (или 0 для отмены): ");
                            int input = Integer.parseInt(sc.nextLine());

                            if (input == 0) {
                                System.out.println("Возврат в главное меню...");
                            } else {
                                try {
                                    int itemIdx = input - 1; // Корректируем индекс для List
                                    commands.handleRemoveItem(id, itemIdx);
                                    System.out.println("Блюдо успешно удалено.");
                                } catch (Exception e) {
                                    System.out.println("Ошибка при удалении: " + e.getMessage());
                                }
                            }
                        }
                    }

                    case "4" -> {
                        System.out.print("Введите ID заказа: "); UUID id = UUID.fromString(sc.nextLine());
                        commands.handleUpdateStatus(id, OrderStatus.COOKING);
                        System.out.println("Статус изменен: COOKING.");
                    }

                    case "5" -> {
                        System.out.print("Введите ID заказа: "); UUID id = UUID.fromString(sc.nextLine());
                        commands.handleUpdateStatus(id, OrderStatus.COMPLETED);
                        System.out.println("Статус изменен: COMPLETED.");
                    }

                    case "6" -> {
                        System.out.print("Введите ID заказа для ОТМЕНЫ: ");
                        UUID id = UUID.fromString(sc.nextLine());
                        commands.handleUpdateStatus(id, OrderStatus.CANCELLED);
                        System.out.println("Заказ отменен (CANCELLED).");
                    }

                    case "7" -> {
                        queries.project(commands.getNewEvents());
                        System.out.println("Данные успешно синхронизированы");
                    }

                    case "8" -> showReportsMenu();

                    case "0" -> {
                        System.out.println("Завершение работы...");
                        return;
                    }

                    default -> System.out.println("Ошибка: Неверный пункт меню.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка бизнес-логики: " + e.getMessage());
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n=== УПРАВЛЕНИЕ ЗАКАЗАМИ КЛИЕНТОВ ===");
        System.out.println("--- Команды ---");
        System.out.println("1. Создать новый заказ");
        System.out.println("2. Добавить блюдо");
        System.out.println("3. Удалить блюдо");
        System.out.println("4. Отправить на кухню (COOKING)");
        System.out.println("5. Завершить заказ (COMPLETED)");
        System.out.println("6. Отменить заказ (CANCELLED)");
        System.out.println("--- Синхронизация ---");
        System.out.println("7. Синхронизировать модели");
        System.out.println("--- Запросная ---");
        System.out.println("8. Отчетность");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private void showReportsMenu() {
        System.out.println("\n--- ВЫБЕРИТЕ ТИП ОТЧЕТА ---");
        System.out.println("1. Краткая статистика (Обзор всех заказов)");
        System.out.println("2. Отчет по составу блюд (Текущее наполнение)");
        System.out.println("3. Полная история событий заказа");
        System.out.println("0. Назад");
        System.out.print("Выбор: ");

        String sub = sc.nextLine();
        try {
            switch (sub) {
                case "1" -> queries.printSimpleStats();
                case "2" -> queries.printCompositionReport();
                case "3" -> {
                    System.out.print("Введите ID заказа для просмотра истории: ");
                    queries.printOrderHistory(UUID.fromString(sc.nextLine()));
                }
                case "0" -> { /* Просто выход в главное меню */ }
                default -> System.out.println("Неверный подпункт.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при чтении данных: " + e.getMessage());
        }
    }
}