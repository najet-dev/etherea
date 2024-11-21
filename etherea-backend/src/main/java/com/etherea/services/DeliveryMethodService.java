package com.etherea.services;

import com.etherea.dtos.AddDeliveryMethodRequestDTO;
import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.exception.CartNotFoundException;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.DeliveryMethodNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.*;
import com.etherea.repositories.*;
import com.etherea.utils.DeliveryCostCalculator;
import com.etherea.utils.DeliveryDateCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeliveryMethodService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private DeliveryDateCalculator deliveryDateCalculator;
    @Autowired
    private HomeStandardDeliveryRepository homeStandardDeliveryRepository;
    @Autowired
    private HomeExpressDeliveryRepository homeExpressDeliveryRepository;
    @Autowired
    private PickupPointDeliveryRepository pickupPointDeliveryRepository;
    @Autowired
    private DeliveryMethodRepository deliveryMethodRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private PickupPointService pickupPointService;

    public List<DeliveryMethodDTO> getAvailableDeliveryMethods(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Shopping cart not found for user with ID: " + userId));

        double cartTotal = cart.calculateTotalAmount().doubleValue();
        LocalDate currentDate = LocalDate.now();

        DeliveryAddress userAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException("No address found for user with ID: " + userId));

        List<DeliveryMethodDTO> deliveryOptions = new ArrayList<>();

        // Add home delivery options
        deliveryOptions.add(createDeliveryMethodDTO(DeliveryOption.HOME_STANDARD, cartTotal, currentDate, 3, userAddress));
        deliveryOptions.add(createDeliveryMethodDTO(DeliveryOption.HOME_EXPRESS, cartTotal, currentDate, 1, userAddress));

        // Add relay points
        List<AddDeliveryMethodRequestDTO> pickupPoints = pickupPointService.findPickupPoints(userId);
        pickupPoints.forEach(pickupPoint ->
                deliveryOptions.add(createPickupPointDeliveryDTO(cartTotal, currentDate, pickupPoint))
        );

        return deliveryOptions;
    }
    private DeliveryMethodDTO createPickupPointDeliveryDTO(double cartTotal, LocalDate currentDate, AddDeliveryMethodRequestDTO pickupPoint) {
        return new DeliveryMethodDTO.Builder()
                .setDeliveryOption(DeliveryOption.PICKUP_POINT)
                .setExpectedDeliveryDate(currentDate.plusDays(5))
                .setCost(DeliveryCostCalculator.calculateDeliveryCost(cartTotal, DeliveryOption.PICKUP_POINT))
                .setPickupPointName(pickupPoint.getPickupPointName())
                .setPickupPointAddress(pickupPoint.getPickupPointAddress())
                .setPickupPointLatitude(pickupPoint.getPickupPointLatitude())
                .setPickupPointLongitude(pickupPoint.getPickupPointLongitude())
                .build();
    }
    public DeliveryMethodDTO getDeliveryMethod(Long userId, Long deliveryMethodId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User ID not found : " + userId));

        // Retrieve delivery method
        DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new DeliveryMethodNotFoundException("Delivery mode not found with ID : " + deliveryMethodId));

        // Retrieve delivery address if applicable
        DeliveryAddress deliveryAddress = (deliveryMethod instanceof HomeStandardDelivery)
                ? ((HomeStandardDelivery) deliveryMethod).getDeliveryAddress()
                : (deliveryMethod instanceof HomeExpressDelivery)
                ? ((HomeExpressDelivery) deliveryMethod).getDeliveryAddress()
                : null;

        // Shopping cart recovery
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Shopping cart not found for user with ID : " + userId));

        if (cart.getItems().isEmpty()) {
            throw new CartNotFoundException("The shopping cart is empty for the user with the ID : " + userId);
        }

        // Delivery cost calculation
        double cartTotal = cart.calculateTotalAmount().doubleValue();
        double deliveryCost = deliveryMethod.calculateCost(cartTotal);

        return DeliveryMethodDTO.fromDeliveryMethod(deliveryMethod, deliveryAddress, LocalDate.now(), cartTotal, deliveryDateCalculator);
    }
    public DeliveryMethodDTO addDeliveryMethod(AddDeliveryMethodRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User ID not found : " + request.getUserId()));

        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new CartNotFoundException("Shopping cart not found for user with ID : " + request.getUserId()));

        double cartTotal = cart.calculateTotalAmount().doubleValue();
        DeliveryMethod deliveryMethod = switch (request.getDeliveryOption()) {
            case HOME_STANDARD, HOME_EXPRESS -> {
                DeliveryAddress userAddress = getUserDeliveryAddress(request.getUserId());
                yield request.getDeliveryOption() == DeliveryOption.HOME_STANDARD
                        ? new HomeStandardDelivery(userAddress)
                        : new HomeExpressDelivery(userAddress);
            }
            case PICKUP_POINT -> new PickupPointDelivery(
                    request.getPickupPointName(),
                    request.getPickupPointAddress(),
                    request.getPickupPointLatitude(),
                    request.getPickupPointLongitude(),
                    user
            );
            default -> throw new DeliveryMethodNotFoundException("Delivery method not supported.");
        };

        deliveryMethodRepository.save(deliveryMethod);

        return DeliveryMethodDTO.fromDeliveryMethod(
                deliveryMethod,
                request.getDeliveryOption() != DeliveryOption.PICKUP_POINT ? getUserDeliveryAddress(request.getUserId()) : null,
                LocalDate.now(),
                cartTotal,
                deliveryDateCalculator
        );
    }
    private DeliveryAddress getUserDeliveryAddress(Long userId) {
        return deliveryAddressRepository.findTopByUserIdOrderByIdDesc(userId)
         .orElseThrow(() -> new DeliveryAddressNotFoundException("No delivery addresses found for user with ID: " ));
    }
    private DeliveryMethodDTO createDeliveryMethodDTO(
            DeliveryOption option,
            double cartTotal,
            LocalDate currentDate,
            int daysToAdd,
            DeliveryAddress address
    ) {
        double deliveryCost = DeliveryCostCalculator.calculateDeliveryCost(cartTotal, option);
        LocalDate expectedDeliveryDate = currentDate.plusDays(daysToAdd);

        DeliveryMethodDTO.Builder builder = new DeliveryMethodDTO.Builder()
                .setDeliveryOption(option)
                .setExpectedDeliveryDate(expectedDeliveryDate)
                .setCost(deliveryCost);

        if (option == DeliveryOption.HOME_STANDARD || option == DeliveryOption.HOME_EXPRESS) {
            builder.setDeliveryAddress(DeliveryAddressDTO.fromDeliveryAddress(address));
        }

        return builder.build();
    }
}
