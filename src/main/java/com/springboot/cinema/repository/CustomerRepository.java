package com.springboot.cinema.repository;

import com.springboot.cinema.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {
}
