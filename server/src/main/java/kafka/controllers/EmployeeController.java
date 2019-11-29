package kafka.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController
{


    @RequestMapping(path = "/employees", method= RequestMethod.GET)
    @ResponseBody
    public String getEmployees()
    {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        String password = ((UserDetails)principal).getPassword();




        return "{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
        //return topics.toString();
    }
}