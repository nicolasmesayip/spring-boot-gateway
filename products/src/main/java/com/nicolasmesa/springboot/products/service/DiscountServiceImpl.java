package com.nicolasmesa.springboot.products.service;

import com.nicolasmesa.springboot.products.entity.Discount;
import com.nicolasmesa.springboot.products.exception.DiscountAlreadyExistsException;
import com.nicolasmesa.springboot.products.exception.DiscountNotFoundException;
import com.nicolasmesa.springboot.products.repository.DiscountRepository;
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
