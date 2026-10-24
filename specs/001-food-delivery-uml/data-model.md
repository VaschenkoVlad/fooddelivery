# Модель даних UML

Це предметна модель для узгодження п'яти артефактів із `spec.md`. Назви програмних елементів подано англійською, пояснення — українською.

## Класи та інтерфейс

| Тип | Атрибути / операції | Відповідальність і правила |
|---|---|---|
| `DeliveryParticipant` (абстрактний клас) | `id`, `name`; `getId()` (`final`), `getDeliveryStatus()`; конструктор `DeliveryParticipant(id, name)` | Спільна поведінка учасника; базовий метод отримання ідентифікатора є `final`. |
| `Customer` | `address`; `placeOrder(order)`; `submitReview(delivery, rating, comment)`; конструктор `Customer(id, name, address)` | Формує й надсилає замовлення, переглядає статус та залишає відгук лише після успішної доставки. Перевизначає `getDeliveryStatus()`. |
| `Courier` | `availability`; `acceptDelivery(delivery)`; `updateDeliveryStatus(status)`; конструктор `Courier(id, name, availability)` | Приймає готове замовлення, оновлює перебіг доставки та фіксує неможливість забору/перешкоду. Перевизначає `getDeliveryStatus()`. |
| `PartnerRestaurant` | `confirmOrder(order)`, `rejectOrder(order, reason)`, `markReady(order)`; конструктор `PartnerRestaurant(id, name)` | Підтверджує або відхиляє замовлення, відмічає готовність. Перевизначає `getDeliveryStatus()`. |
| `Order` | `id`, `requestedDeliveryTime`, `status`, `items`; `addItem(item)`, `submit()`, `confirm()`, `reject(reason)`; конструктор `Order(id, customer, restaurant, requestedDeliveryTime)` | Містить одну чи більше позицій, належить одному ресторану й проходить послідовні стани. Не може бути оформлене з порожнім кошиком або недійсним часом. |
| `BasketItem` | `dishName`, `quantity`, `unitPrice`; конструктор `BasketItem(dishName, quantity, unitPrice)` | Окрема доступна страва й кількість; входить до складу рівно одного `Order`. `quantity` є додатним числом. |
| `Delivery` | `status`, `trackingDetails`; `start()`, `updateStatus(status)`, `complete()`, `reportObstacle(reason)`; конструктор `Delivery(id, order, mappingService)` | Відповідає за виконання та відстеження доставки замовлення. Отримує `MappingService` через конструктор; маршрут лише імітується. |
| `MappingService` | `getRouteDetails(origin, destination)`; конструктор `MappingService()` | Надає імітовані маршрутні відомості; не встановлює зовнішнє з'єднання. |
| `Review` | `rating`, `comment`; конструктор `Review(id, customer, delivery, rating, comment)` | Єдиний відгук замовника на успішно завершену доставку. `rating` — ціле число 1–5 включно; відгук до завершення доставки або повторний відгук не приймається. |

Інтерфейс `IDeliveryStatus` визначає операцію `getDeliveryStatus()`. `Customer`, `Courier` та `PartnerRestaurant` реалізують інтерфейс відповідно до FR-006. Інтерфейс не потребує окремої CRC-картки, якщо на діаграмі не моделюється як клас.

Усі класи діаграми мають атрибут або операцію та конструктор ініціалізації; конкретні класи мають придатний для створення екземпляра конструктор. Діаграма позначає `getId()` як `final`, `getDeliveryStatus()` у конкретних класах як перевизначений метод, а перевантаження `addItem(item)` і `addItem(dishName, quantity)` — як два варіанти однієї операції. Через обмеження синтаксису Mermaid `final` і перевизначення пояснюються українською приміткою при діаграмі, де назви методів залишаються англійськими.

## Зв'язки

- `Customer`, `Courier` і `PartnerRestaurant` наслідують `DeliveryParticipant` та реалізують `IDeliveryStatus`.
- `Order` композиційно містить одну або більше `BasketItem`.
- `Order` пов'язане з одним `Customer`, одним `PartnerRestaurant` і однією `Delivery`.
- `Delivery` призначене одному `Courier` після підтвердження замовлення та позначення його готовим; `Delivery` залежить від `MappingService`.
- `Review` належить одній `Delivery` і одному `Customer`; для завершеної доставки допускається не більше одного відгуку.
- Повторні або недоречні події статусу не створюють суперечливих переходів.

## Життєвий цикл

Основний порядок: `Draft` → `Submitted` → `Accepted` → `Preparing` → `ReadyForPickup` → `InTransit` → `Delivered`. Ресторан може перевести `Submitted` у завершальний `Rejected`; замовлення може завершитися як `Cancelled`, а неможливість забору — як `PickupFailed`. Відхилення, скасування та невдалий забір не є успішною доставкою. `Review` створюється лише після `Delivered`.

## Узгодження артефактів

- Діаграма варіантів використання містить чотири бізнес-операції та акторів Замовник, Кур'єр і Ресторан-партнер.
- Послідовність взаємодії використовує лише екземпляри класів цієї моделі: `Customer`, `Order`, `PartnerRestaurant`, `Courier`, `Delivery`, `MappingService`, `Review`.
- Діаграма станів показує той самий життєвий цикл, завершальні стани й події, не позначаючи помилковий сценарій успішним.
- Книга CRC містить рівно одну картку для кожного з дев'яти класів, зокрема `DeliveryParticipant`; картка для `IDeliveryStatus` необов'язкова.
