package lk.dep.cisco.posbackendservlet.service;

import lk.dep.cisco.posbackendservlet.dto.CustomerDTO;
import lk.dep.cisco.posbackendservlet.exception.DuplicateIdentifierException;
import lk.dep.cisco.posbackendservlet.exception.FailedOperationException;
import lk.dep.cisco.posbackendservlet.exception.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {

    private Connection connection;

    public CustomerService(Connection connection){
        this.connection = connection;
    }

    boolean existCustomer(String id) throws SQLException {
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM customer WHERE id=?");
        pstm.setString(1,id);
        return pstm.executeQuery().next();
    }

    public void  saveCustomer(CustomerDTO customer) throws DuplicateIdentifierException, FailedOperationException {
        try{
            if(existCustomer(customer.getId())){
                throw new DuplicateIdentifierException(customer.getId()+ " already exists");
            }
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO customer (id, name, address) VALUES (?,?,?)");
            pstm.setString(1, customer.getId());
            pstm.setString(2, customer.getName());
            pstm.setString(3, customer.getAddress());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to save the customer");
        }
    }

    public CustomerDTO findCustomer(String id) throws NotFoundException, FailedOperationException {
        try {
            if(!existCustomer(id)){
                throw new NotFoundException("There is no such customer associated with " + id);
            }
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM customer WHERE id=?");
            pstm.setString(1,id);
            ResultSet rst = pstm.executeQuery();
            rst.next();
            return new CustomerDTO(id, rst.getString("name"), rst.getString("address"));
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to find the customer "+ id);
        }
    }

    public List<CustomerDTO> findAllCustomer() throws FailedOperationException {
        try{
            List<CustomerDTO> customerList = new ArrayList<>();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM customer");
            while(rst.next()){
                customerList.add(new CustomerDTO(rst.getString("id"),
                        rst.getString("name"),
                        rst.getString("address")));
            }
            return customerList;
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to find customers");
        }
    }

    public void updateCustomer(CustomerDTO customer) throws NotFoundException, FailedOperationException {
        try {
            if(!existCustomer(customer.getId())){
                throw new NotFoundException("There is no such customer associated with "+ customer.getId());
            }
            PreparedStatement pstm = connection.prepareStatement("UPDATE customer SET name=?, address=? WHERE id=?");
            pstm.setString(1, customer.getName());
            pstm.setString(2, customer.getAddress());
            pstm.setString(3, customer.getId());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to update the customer " + customer.getId());
        }
    }

    public void deleteCustomer(String id) throws NotFoundException, FailedOperationException {
        try {
            if(!existCustomer(id)){
                throw new NotFoundException("There is no such customer associated with "+ id);
            }
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM customer WHERE id=?");
            pstm.setString(1,id);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to delete the customer " + id);
        }
    }

}
