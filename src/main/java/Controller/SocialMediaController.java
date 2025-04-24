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


public class SocialMediaController {

    AccountService accountService;
    MessageService messageService;

    public SocialMediaController () {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // Routes for account and message operations
        app.post("/register", this::registerAccountHandler);
        app.post("/login", this::loginAccountHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessageForUserHandler);

        return app;
    }

    // Handle user registration
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

    // Handle user login
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

    // Handle creating a new message
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

     // Handle fetching all messages
    private void getAllMessagesHandler(Context ctx) throws JsonProcessingException {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    // Handle fetching a specific message by ID
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

    // Handle deleting a message by ID
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

    // Handle updating a message by ID
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

    // Handle fetching all messages for a specific user
    private void getMessageForUserHandler(Context ctx) throws JsonProcessingException {
        int account_id = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> messages = messageService.getMessages(account_id);
        ctx.json(messages).status(200);
    }
}