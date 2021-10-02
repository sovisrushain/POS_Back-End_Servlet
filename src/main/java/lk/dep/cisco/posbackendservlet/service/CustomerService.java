package lk.dep.cisco.posbackendservlet.service;

import lk.dep.cisco.posbackendservlet.dto.CustomerDTO;

import java.sql.Connection;
import java.util.List;

public class CustomerService {

    private Connection connection;

    public CustomerService(Connection connection){
        this.connection = connection;
    }

    boolean existCustomer(String id){
        return false;
    }

    public void  saveCustomer(CustomerDTO customer){

    }

    public CustomerDTO findCustomer(String id){
        return null;
    }

    public List<CustomerDTO> findAllCustomer(){
        return null;
    }

    public void updateCustomer(CustomerDTO customer){

    }

    public void deleteCustomer(String id){}

}
