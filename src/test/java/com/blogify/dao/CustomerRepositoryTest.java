package com.blogify.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:/sql/customers.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;




}