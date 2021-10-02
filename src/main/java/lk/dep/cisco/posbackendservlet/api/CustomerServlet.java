package lk.dep.cisco.posbackendservlet.api;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "CustomerServlet", value = "/customers")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* 1. Connect to the DB */
        /* 2. Fetch Customers */
        /* 3. Convert to Json */
        /* 4. Send back to client */
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* 1. Determine weather the content type is Json */
        /* 2. Connect to the DB */
        /* 3. Bind Json to Java Obj */
        /* 4. Save the customer */
        /* 5. Send a response to the client */
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* 1. Check weather it is a valid request */
        /* 2. Bind Json to Java obj */
        /* 3. Connect to the DB */
        /* 4. Update the customer */
        /* 5. Send the status to client */
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* 1. Check weather the client has sent a valid id */
        /* 2. Connect to the DB */
        /* 3. Delete the customer */
        /* 4. Send the status to the client */
    }
}
