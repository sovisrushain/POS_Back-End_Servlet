package lk.dep.cisco.posbackendservlet.service;

import lk.dep.cisco.posbackendservlet.dto.ItemDTO;
import lk.dep.cisco.posbackendservlet.exception.DuplicateIdentifierException;
import lk.dep.cisco.posbackendservlet.exception.FailedOperationException;
import lk.dep.cisco.posbackendservlet.exception.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemService {

    private Connection connection;

    public ItemService(Connection connection){
        this.connection = connection;
    }

    private boolean existsItem(String code) throws SQLException {
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE code=?");
        pstm.setString(1, code);
        return pstm.executeQuery().next();
    }

    public void saveItem(ItemDTO item) throws DuplicateIdentifierException, FailedOperationException {
        try{
            if(existsItem(item.getCode())){
                throw new DuplicateIdentifierException(item.getCode() + " already exists");
            }
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO item (code, description, unit_price, qty_on_hand) " +
                    "VALUES (?,?,?,?)");
            pstm.setString(1, item.getCode());
            pstm.setString(2, item.getDescription());
            pstm.setBigDecimal(3, item.getUnitPrice());
            pstm.setInt(4, item.getQtyOnHand());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to save the item");
        }
    }

    public ItemDTO findItem(String code) throws NotFoundException, FailedOperationException {
        try {
            if(!existsItem(code)){
                throw new NotFoundException("There is no code associated with "+ code);
            }
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE code=?");
            pstm.setString(1, code);
            ResultSet rst = pstm.executeQuery();
            rst.next();
            return new ItemDTO(code, rst.getString("description"),
                    rst.getBigDecimal("unit_price"),
                    rst.getInt("qty_on_hand"));
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to find the item " + code);
        }
    }

    public List<ItemDTO> findAllItems() throws FailedOperationException {
        try{
            List<ItemDTO> itemList = new ArrayList<>();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM item");
            while (rst.next()){
                itemList.add(new ItemDTO(rst.getString("code"),
                        rst.getString("description"),
                        rst.getBigDecimal("unit_price"),
                        rst.getInt("qty_on_hand")));
            }
            return itemList;
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to find the items");
        }
    }

    public void updateItem(ItemDTO item) throws NotFoundException, FailedOperationException {
        try {
            if(!existsItem(item.getCode())){
                throw new NotFoundException("There is no item associated with " + item.getCode());
            }
            PreparedStatement pstm = connection.prepareStatement("UPDATE item SET description=?, unit_price=?, qty_on_hand=? WHERE code=?");
            pstm.setString(1, item.getDescription());
            pstm.setBigDecimal(2, item.getUnitPrice());
            pstm.setInt(3, item.getQtyOnHand());
            pstm.setString(4, item.getCode());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to update the item");
        }
    }

    public void deleteItem(String code) throws NotFoundException, FailedOperationException {
        try {
            if (!existsItem(code)) {
                throw new NotFoundException("There is no item associated with " + code);
            }
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM item WHERE code=?");
            pstm.setString(1, code);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to delete the item " + code);
        }
    }
}
