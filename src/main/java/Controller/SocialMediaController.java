package Controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import Service.ServiceException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {


    // Service instances to handle business logic
    private final AccountService accountService;
    private final MessageService messageService;

    // Constructor initializing services
    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    // Method to start Javalin app and define routes
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // Define API endpoints
        app.post("/register", this::registerAccount);
        app.post("/login", this::loginAccount);
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/{message_id}", this::getMessageById);
        app.delete("/messages/{message_id}", this::deleteMessageById);
        app.patch("/messages/{message_id}", this::updateMessageById);
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountId);

        return app;
    }

    // Endpoint to register a new account
    private void registerAccount(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        try {
            Account registeredAccount = accountService.createAccount(account);
            ctx.json(mapper.writeValueAsString(registeredAccount));
        } catch (ServiceException e) {
            ctx.status(400); // Bad request if registration fails
        }
    }

    // Endpoint to handle user login
    private void loginAccount(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);

        try {
            java.util.Optional<Account> loggedInAccount = accountService.validateLogin(account);
            if (loggedInAccount.isPresent()) {
                ctx.sessionAttribute("logged_in_account", loggedInAccount.get());
                ctx.json(loggedInAccount.get());
            } else {
                ctx.status(401); // Unauthorized if login fails
            }
        } catch (ServiceException e) {
            ctx.status(401); // Unauthorized if login fails
        }
    }

    // Endpoint to create a new message
    private void createMessage(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message mappedMessage = mapper.readValue(ctx.body(), Message.class);
        try {
            java.util.Optional<Account> account = accountService.getAccountById(mappedMessage.getPosted_by());
            Message message = messageService.createMessage(mappedMessage, account);
            ctx.json(message);
        } catch (ServiceException e) {
            ctx.status(400); // Bad request if message creation fails
        }
    }

    // Endpoint to retrieve all messages
    private void getAllMessages(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    // Endpoint to retrieve a message by its ID
    private void getMessageById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("message_id"));
            java.util.Optional<Message> message = messageService.getMessageById(id);
            if (message.isPresent()) {
                ctx.json(message.get());
            } else {
                ctx.status(200); // Respond with empty result if not found
                ctx.result("");
            }
        } catch (NumberFormatException e) {
            ctx.status(400); // Bad request if ID is not a number
        } catch (ServiceException e) {
            ctx.status(200); // Handle service exceptions gracefully
            ctx.result("");
        }
    }

    // Endpoint to delete a message by its ID
    private void deleteMessageById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("message_id"));

            java.util.Optional<Message> message = messageService.getMessageById(id);
            if (message.isPresent()) {
                messageService.deleteMessage(message.get());
                ctx.json(message.get());
            } else {
                ctx.status(200); // Idempotent response for non-existing message
            }
        } catch (ServiceException e) {
            ctx.status(200); // Handle service exceptions gracefully
        }
    }

    // Endpoint to update a message by its ID
    private void updateMessageById(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message mappedMessage = mapper.readValue(ctx.body(), Message.class);
        try {
            int id = Integer.parseInt(ctx.pathParam("message_id"));
            mappedMessage.setMessage_id(id);

            Message messageUpdated = messageService.updateMessage(mappedMessage);
            ctx.json(messageUpdated);
        } catch (ServiceException e) {
            ctx.status(400); // Bad request if update fails
        }
    }

    // Endpoint to retrieve messages by account ID
    private void getMessagesByAccountId(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));
            List<Message> messages = messageService.getMessagesByAccountId(accountId);
            ctx.json(messages);
        } catch (ServiceException e) {
            ctx.status(400); // Bad request if retrieval fails
        }
    }

}