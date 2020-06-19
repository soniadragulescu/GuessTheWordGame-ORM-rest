package users;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repos.UserRepo;

@CrossOrigin
@RestController
@RequestMapping("/words/users")
public class UsersController {
    private static final String template="TemplatePaper";

    @Autowired
    private UserRepo userRepo;

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public ResponseEntity<?> getByUsername(@PathVariable String username){
        User user=userRepo.findOneByUsername(username);
        if(user == null){
            return new ResponseEntity<String>("User not found!", HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

}

