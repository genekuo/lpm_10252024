package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.web;

import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.messaging.common.OrderDetails;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.CreateOrderRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.CreateOrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.GetOrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.GetOrdersResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class OrderController {

  private final OrderService orderService;
  private final OrderRepository orderRepository;

  @Autowired
  public OrderController(OrderService orderService, OrderRepository orderRepository) {
    this.orderService = orderService;
    this.orderRepository = orderRepository;
  }

  @RequestMapping(value = "/orders", method = RequestMethod.POST)
  public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
    Order order = orderService.createOrder(new OrderDetails(createOrderRequest.getCustomerId(), createOrderRequest.getOrderTotal()));
    return new CreateOrderResponse(order.getId());
  }

  @RequestMapping(value="/orders", method= RequestMethod.GET)
  public ResponseEntity<GetOrdersResponse> getAll() {
    return ResponseEntity.ok(new GetOrdersResponse(StreamSupport.stream(orderRepository.findAll().spliterator(), false)
            .map(o -> new GetOrderResponse(o.getId(), o.getState(), o.getRejectionReason())).collect(Collectors.toList())));
  }

  @RequestMapping(value="/orders/{orderId}", method= RequestMethod.GET)
  public ResponseEntity<GetOrderResponse> getOrder(@PathVariable Long orderId) {
    return orderRepository
            .findById(orderId)
            .map(o -> new ResponseEntity<>(new GetOrderResponse(o.getId(), o.getState(), o.getRejectionReason()), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequestMapping(value="/orders/customer/{customerId}", method= RequestMethod.GET)
  public ResponseEntity<List<GetOrderResponse>> getOrdersByCustomerId(@PathVariable Long customerId) {
    return new ResponseEntity<>(orderRepository
            .findAllByOrderDetailsCustomerId(customerId)
            .stream()
            .map(o -> new GetOrderResponse(o.getId(), o.getState(), o.getRejectionReason()))
            .collect(Collectors.toList()), HttpStatus.OK);
  }
}
