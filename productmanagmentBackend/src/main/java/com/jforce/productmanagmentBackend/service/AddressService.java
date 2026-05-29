package com.jforce.productmanagmentBackend.service;

import com.jforce.productmanagmentBackend.dto.request.AddressRequest;
import com.jforce.productmanagmentBackend.dto.response.AddressResponse;
import com.jforce.productmanagmentBackend.entity.Address;
import com.jforce.productmanagmentBackend.entity.User;
import com.jforce.productmanagmentBackend.exception.BadRequestException;
import com.jforce.productmanagmentBackend.exception.ResourceNotFoundException;
import com.jforce.productmanagmentBackend.repository.AddressRepository;
import com.jforce.productmanagmentBackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;

    public List<AddressResponse> getAddresses(User user) {
        return addressRepository.findByUser(user).stream()
                .map(AddressResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse addAddress(User user, AddressRequest request) {
        if (request.isDefault()) {
            addressRepository.findByUserAndIsDefaultTrue(user)
                    .ifPresent(a -> a.setDefault(false));
        }

        Address address = new Address();
        address.setUser(user);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setDefault(request.isDefault());

        return AddressResponse.from(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse updateAddress(Long id, User user, AddressRequest request) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", id));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Address does not belong to this user");
        }

        if (request.isDefault()) {
            addressRepository.findByUserAndIsDefaultTrue(user)
                    .ifPresent(a -> a.setDefault(false));
        }

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setDefault(request.isDefault());

        return AddressResponse.from(addressRepository.save(address));
    }

    public void deleteAddress(Long id, User user) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", id));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Address does not belong to this user");
        }

        if (!orderRepository.findByUserOrderByOrderDateDesc(user).isEmpty()) {
            boolean addressInUse = orderRepository.findByUserOrderByOrderDateDesc(user).stream()
                    .anyMatch(order -> order.getAddress() != null && order.getAddress().getId().equals(id));
            if (addressInUse) {
                throw new BadRequestException("Cannot delete address that is referenced by an order");
            }
        }

        addressRepository.deleteById(id);
    }
}
