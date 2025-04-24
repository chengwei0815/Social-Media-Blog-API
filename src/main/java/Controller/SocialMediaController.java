package Controller;

import Model.Account;
import Model.Message;

import Service.AccountService;
import Service.MessageService;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;
import io.javalin.http.Context;

// Control layer
public class SocialMediaController {
    // Instances to access service layer
    AccountService accountService;
    MessageService messageService;

    // Constructor
    public SocialMediaController () {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // register
        app.post("/register", this::registerAccountHandler);

        // login
        app.post("/login", this::loginAccountHandler);

        // add message
        app.post("/messages", this::createMessageHandler);

        // get messages
        app.get("/messages", this::getAllMessagesHandler);

        // get message
        app.get("/messages/{message_id}", this::getMessageHandler);

        // delete message
        app.delete("/messages/{message_id}", this::deleteMessageHandler);

        // update message
        app.patch("/messages/{message_id}", this::updateMessageHandler);

        // get messages for user
        app.get("/accounts/{account_id}/messages", this::getMessageForUserHandler);

        return app;
    }

    // Deserializes the request body into an account object and tasks the service layer to add it to the database
    // Return (success): JSON of account with status 200
    // Return (fail): status 400 (client)
    private void registerAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account addedAccount = accountService.addAccount(account);

        if (addedAccount != null) {
            ctx.json(addedAccount).status(200);
        }
        else {
            ctx.status(400);
        }
    }

    // Deserializes the request body into an account object and tasks the service layer to verify the account with the database
    // Return (success): JSON of account with status 200
    // Return (fail): status 400 (client)
    private void loginAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account addedAccount = accountService.loginAccount(account);

        if (addedAccount != null) {
            ctx.json(addedAccount).status(200);
        }
        else {
            ctx.status(401);
        }
    }

    // Deserializes the request body into an message object and tasks the service layer to add it to the database
    // Return (success): JSON of message with status 200
    // Return (fail): status 400
    private void createMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        Message addedMessage = messageService.addMessage(message);

        if (addedMessage != null) {
            ctx.json(addedMessage).status(200);
        }
        else {
            ctx.status(400);
        }
    }

    // Tasks the service layer to return all existing messages in the database
    // Return: JSON of messages
    private void getAllMessagesHandler(Context ctx) throws JsonProcessingException {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    // Extracts the message_id from the URL path into an integer and tasks the service layer to find the message in the database
    // Return (success): JSON of message
    // Return (fail): empty string
    private void getMessageHandler(Context ctx) throws JsonProcessingException {
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.getMessage(message_id);
            
        if (message != null) {
            ctx.json(message);
        }
        else {
            ctx.result("");
        }
    }

    // Extracts the message_id from the URL path into an integer and tasks the service layer to delete the message from the database
    // Return (success): JSON of message
    // Return (fail): empty string
    private void deleteMessageHandler(Context ctx) throws JsonProcessingException {
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.deleteMessage(message_id);

        if (message != null) {
            ctx.json(message);
        }
        else {
            ctx.result("");
        }
    }

    // Deserializes the request body into a message object and extracts the message_id from the URL path into an integer. Then tasks the service layer to update the message in the database
    // Return (success): JSON of message as a string status 200
    // Return (fail): status 400
    private void updateMessageHandler(Context ctx) throws JsonProcessingException {
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        Message updatedMessage = messageService.modifyMessage(message.getMessage_text(), message_id);

        if (updatedMessage != null) {
            ctx.json(mapper.writeValueAsString(updatedMessage)).status(200);
        }
        else {
            ctx.status(400);
        }
    }

    // Extracts the user_id from the URL path into an integer and tasks the service layer to get all existing messages for the user in the database
    // Return: JSON of messages
    private void getMessageForUserHandler(Context ctx) throws JsonProcessingException {
        int account_id = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> messages = messageService.getMessages(account_id);
        ctx.json(messages).status(200);
    }
}