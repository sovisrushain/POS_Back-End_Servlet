package lk.dep.cisco.posbackendservlet.api;

import jakarta.json.JsonArrayBuilder;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.dep.cisco.posbackendservlet.dto.CustomerDTO;
import lk.dep.cisco.posbackendservlet.exception.DuplicateIdentifierException;
import lk.dep.cisco.posbackendservlet.exception.FailedOperationException;
import lk.dep.cisco.posbackendservlet.exception.NotFoundException;
import lk.dep.cisco.posbackendservlet.service.CustomerService;
import lk.dep.cisco.posbackendservlet.util.DBConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CustomerServlet", value = "/customers")
public class CustomerServlet extends HttpServlet {

    private final Jsonb jsonb = JsonbBuilder.create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* 1. Connect to the DB */
        try(Connection connection = DBConnection.getConnection()){
            /* 2. Fetch Customers */
            CustomerService customerService = new CustomerService(connection);
            String id = req.getParameter("id");
            List<CustomerDTO> customers = new ArrayList<>();
            if(id == null){
                customers = customerService.findAllCustomer();
            }else {
                customers.add(customerService.findCustomer(id));
            }
            /* 3. Convert to Json */
            String json = jsonb.toJson(id == null ? customers : customers.get(0));
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
        if(!req.getContentType().equals("application/json") || req.getContentType() == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        /* 2. Connect to the DB */
        try(Connection connection = DBConnection.getConnection()){
            /* 3. Bind Json to Java Obj */
            CustomerDTO customer = jsonb.fromJson(req.getReader(), CustomerDTO.class);
            /* 4. Save the customer */
            /* ToDo: Validation */
            CustomerService customerService = new CustomerService(connection);
            customerService.saveCustomer(customer);
            /* 5. Send a response to the client */
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.println(jsonb.toJson(customer.getId()));
        } catch (SQLException | DuplicateIdentifierException | FailedOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* 1. Check weather it is a valid request */
        if(!req.getContentType().equals("application/json") || req.getContentType() == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        /* 2. Bind Json to Java obj */
        CustomerDTO customer = jsonb.fromJson(req.getReader(), CustomerDTO.class);
        /* 3. Connect to the DB */
        try(Connection connection = DBConnection.getConnection()){
            /* 4. Update the customer */
            /*Todo: validation*/
            CustomerService customerService = new CustomerService(connection);
            customerService.updateCustomer(customer);
            /* 5. Send the status to client */
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.println(jsonb.toJson(customer.getId()));
        } catch (SQLException | NotFoundException | FailedOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* 1. Check weather the client has sent a valid id */
        String id = req.getParameter("id");
        if(id == null || !id.matches("C\\d{3}")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        /* 2. Connect to the DB */
        try(Connection connection = DBConnection.getConnection()){
            /* 3. Delete the customer */
            CustomerService customerService = new CustomerService(connection);
            customerService.deleteCustomer(id);
            /* 4. Send the status to the client */
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.println(jsonb.toJson("OK"));
        } catch (SQLException | NotFoundException | FailedOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
