package com.nicolasmesa.springboot.productservices.discounts.service;

import com.nicolasmesa.springboot.productservices.discounts.entity.Discount;
import com.nicolasmesa.springboot.productservices.discounts.exception.DiscountAlreadyExistsException;
import com.nicolasmesa.springboot.productservices.discounts.exception.DiscountNotFoundException;
import com.nicolasmesa.springboot.productservices.discounts.repository.DiscountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountServiceImpl(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    @Override
    public Discount getByDiscountCode(String discountCode) {
        return discountRepository.findByDiscountCode(discountCode).orElseThrow(() -> new DiscountNotFoundException(discountCode));
    }

    @Override
    public Discount createDiscount(Discount discount) {
        if (discountRepository.findByDiscountCode(discount.getDiscountCode()).isPresent())
            throw new DiscountAlreadyExistsException(discount.getDiscountCode());
        return discountRepository.save(discount);
    }

    @Override
    public void deleteDiscount(String discountCode) {
        Discount discount = discountRepository.findByDiscountCode(discountCode).orElseThrow(() -> new DiscountNotFoundException(discountCode));
        discountRepository.deleteById(discount.getId());
    }
}
