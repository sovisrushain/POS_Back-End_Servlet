package lk.dep.cisco.posbackendservlet.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.dep.cisco.posbackendservlet.dto.ItemDTO;
import lk.dep.cisco.posbackendservlet.exception.DuplicateIdentifierException;
import lk.dep.cisco.posbackendservlet.exception.FailedOperationException;
import lk.dep.cisco.posbackendservlet.exception.NotFoundException;
import lk.dep.cisco.posbackendservlet.service.ItemService;
import lk.dep.cisco.posbackendservlet.util.DBConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ItemServlet", value = "/items")
public class ItemServlet extends HttpServlet {

    private final Jsonb jsonb = JsonbBuilder.create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* 1. Connect to the DB */
        try(Connection connection = DBConnection.getConnection()){
            /* 2. Fetch Items */
            ItemService itemService = new ItemService(connection);
            String code = req.getParameter("code");
            List<ItemDTO> items = new ArrayList<>();
            if(code == null){
                items = itemService.findAllItems();
            }else {
                items.add(itemService.findItem(code));
            }
            /* 3. Convert to Json */
            String json = jsonb.toJson(code == null ? items : items.get(0));
            /* 4. Send back to client */
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.println(json);
        } catch (SQLException | FailedOperationException | NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* 1. Determine weather the content type is Json */
        if(req.getContentType() == null || !req.getContentType().equals("application/json")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        /* 2. Connect to the DB */
        try(Connection connection = DBConnection.getConnection()){
            /* 3. Bind Json to Java Obj */
            ItemDTO item = jsonb.fromJson(req.getReader(), ItemDTO.class);
            /* Todo:validation */
            /* 4. Save the item */
            ItemService itemService = new ItemService(connection);
            item.setUnitPrice(item.getUnitPrice().setScale(2));
            itemService.saveItem(item);
            /* 5. Send a response to the client */
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.println(jsonb.toJson(item.getCode()));
        } catch (SQLException | DuplicateIdentifierException | FailedOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* 1. Check weather it is a valid request */
        if(req.getContentType() == null || !req.getContentType().equals("application/json")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        /* 2. Connect to the DB */
        try(Connection connection = DBConnection.getConnection()) {
            /* 3. Bind Json to Java obj */
            ItemDTO item = jsonb.fromJson(req.getReader(), ItemDTO.class);
            /* Todo:validation */
            /* 4. Update the item */
            ItemService itemService = new ItemService(connection);
            itemService.updateItem(item);
            /* 5. Send the status to client */
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.println(jsonb.toJson(item.getCode()));
        } catch (SQLException | NotFoundException | FailedOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* 1. Check weather the client has sent a valid code */
        String code = req.getParameter("code");
        if(code == null || !code.matches("I\\d{3}")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        /* 2. Connect to the DB */
        try(Connection connection = DBConnection.getConnection()) {
            /* 3. Delete the item */
            ItemService itemService = new ItemService(connection);
            itemService.deleteItem(code);
            /* 4. Send the status to the client */
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.println(jsonb.toJson("OK"));
        } catch (SQLException | NotFoundException | FailedOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
